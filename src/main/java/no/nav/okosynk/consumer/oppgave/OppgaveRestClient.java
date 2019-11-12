package no.nav.okosynk.consumer.oppgave;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static no.nav.okosynk.consumer.oppgave.OppgaveStatus.FERDIGSTILT;
import static no.nav.okosynk.consumer.oppgave.OppgaveStatus.OPPRETTET;
import static no.nav.okosynk.consumer.util.ListeOppdeler.delOppListe;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.domain.Oppgave;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppgaveRestClient {

  private static final Logger log = LoggerFactory.getLogger(OppgaveRestClient.class);
  private static final String FAGOMRADE_OKONOMI_KODE = "OKO";
  private static final String ENHET_ID_FOR_ANDRE_EKSTERNE = "9999";

  private final IOkosynkConfiguration okosynkConfiguration;
  private final Constants.BATCH_TYPE batchType;
  private final CloseableHttpClient httpClient;
  private final UsernamePasswordCredentials credentials;

  public OppgaveRestClient(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType) {
    this.okosynkConfiguration = okosynkConfiguration;
    this.batchType = batchType;

    final String bruker = okosynkConfiguration
        .getString(batchType.getBatchBrukerKey(), batchType.getBatchBrukerDefaultValue());
    this.credentials =
        new UsernamePasswordCredentials(
            bruker,
            okosynkConfiguration
                .getString(batchType.getBatchBrukerPasswordKey())
        );

    this.httpClient = HttpClients.createDefault();
    log.info("OppgaveRestClient konfigurert for {} og bruker {}", batchType, bruker);
  }

  private static String getBatchBruker(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE  batchType) {

    final String batchBruker =
        okosynkConfiguration
            .getString(
                batchType.getBatchBrukerKey(),
                batchType.getBatchBrukerDefaultValue()
            );

    return batchBruker;
  }

  public ConsumerStatistics opprettOppgaver(final Collection<Oppgave> oppgaver) {

    final HttpPost request = new HttpPost(getOkosynkConfiguration().getRequiredString("OPPGAVE_URL"));
    request.addHeader("X-Correlation-ID", UUID.randomUUID().toString());
    request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());
    request.addHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
    try {
      request.addHeader(new BasicScheme(UTF_8).authenticate(credentials, request, null));
    } catch (AuthenticationException e) {
      throw new IllegalStateException(e);
    }

    final ObjectMapper objectMapper = new ObjectMapper();
    final List<OppgaveDto> opprettedeOppgaver = new ArrayList<>();
    final List<OppgaveDto> oppgaverSomIkkeErOpprettet = new ArrayList<>();
    final List<OppgaveDto> oppgaveDtos =
        oppgaver.stream()
          .map(this::oversettOppgave)
          .collect(Collectors.toList());
    oppgaveDtos.forEach(dto -> {
      try {
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(dto), "UTF-8"));
      } catch (JsonProcessingException e) {
        throw new IllegalStateException(
            "Klarte ikke serialisere oppgave i forkant av POST mot Oppgave", e);
      }

      try (CloseableHttpResponse response = this.httpClient.execute(request)) {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() >= 400) {
          try {
            ErrorResponse errorResponse = new ObjectMapper()
                .readValue(response.getEntity().getContent(), ErrorResponse.class);
            log.error("Feil oppsto under oppretting av oppgave: {}, Errorresponse: {}", dto,
                errorResponse);
            oppgaverSomIkkeErOpprettet.add(dto);
          } catch (JsonParseException jpe) {
            parseRawError(response);
          }
        } else {
          opprettedeOppgaver.add(
              new ObjectMapper().readValue(response.getEntity().getContent(), OppgaveDto.class));
        }
      } catch (IOException e) {
        throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
      }

    });

    return ConsumerStatistics
        .builder(getBatchType())
        .antallOppgaverSomMedSikkerhetErOpprettet(opprettedeOppgaver.size())
        .antallOppgaverSomMedSikkerhetIkkeErOpprettet(oppgaverSomIkkeErOpprettet.size())
        .build();
  }

  public ConsumerStatistics patchOppgaver(
      final Set<Oppgave> oppgaver,
      final boolean      ferdigstill) {

    if (oppgaver == null || oppgaver.isEmpty()) {
      return ConsumerStatistics.zero(getBatchType());
    }

    final HttpPatch request = new HttpPatch(getOkosynkConfiguration().getRequiredString("OPPGAVE_URL"));
    request.addHeader("X-Correlation-ID", UUID.randomUUID().toString());
    request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());
    request.addHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
    try {
      request.addHeader(new BasicScheme(UTF_8).authenticate(credentials, request, null));
    } catch (AuthenticationException e) {
      throw new IllegalStateException(e);
    }

    final List<List<Oppgave>> oppgaverLister = delOppListe(new ArrayList<>(oppgaver), 500);

    log.info(
        "Starter patching av oppgaver, sublistestørrelse: "
            + "{}, antall sublister {}, antall oppgaver totalt: {}",
        500, oppgaverLister.size(), oppgaver.size());
    final List<PatchOppgaverResponse> responses =
        oppgaverLister.stream()
            .map(list -> patchOppgaver(list, ferdigstill, request))
            .collect(Collectors.toList());

    final int suksess =
        summerAntallFraResponse(responses, PatchOppgaverResponse::getSuksess);
    final int feilet =
        summerAntallFraResponse(responses, PatchOppgaverResponse::getFeilet);

    if (ferdigstill) {
      return ConsumerStatistics
          .builder(getBatchType())
          .antallOppgaverSomMedSikkerhetErFerdigstilt(suksess)
          .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(feilet)
          .build();
    } else {
      return ConsumerStatistics
          .builder(getBatchType())
          .antallOppgaverSomMedSikkerhetErOppdatert(suksess)
          .antallOppgaverSomMedSikkerhetIkkeErOppdatert(feilet)
          .build();
    }
  }

  public Constants.BATCH_TYPE getBatchType() {
    return  this.batchType;
  }

  public ConsumerStatistics finnOppgaver(final Set<Oppgave> oppgaver) {

    final String opprettetAv =
        OppgaveRestClient.getBatchBruker(getOkosynkConfiguration(), getBatchType());

    final int limit = 20;
          int offset = 0;
    FinnOppgaveResponse finnOppgaveResponse = this.finnOppgaver(opprettetAv, limit, offset);
    log.info("Starter inkrementelt søk i oppgaver mot Oppgave API. Fant totalt {} oppgaver",
        finnOppgaveResponse.getAntallTreffTotalt());

    while (!finnOppgaveResponse.getOppgaver().isEmpty()) {
      finnOppgaveResponse = this.finnOppgaver(opprettetAv, limit, offset);
      oppgaver.addAll(finnOppgaveResponse.getOppgaver()
          .stream()
          .map(this::tilOppgave)
          .collect(Collectors.toList()));

      offset += limit;
    }

    log.info("Hentet {} unike oppgaver fra Oppgave", oppgaver.size());

    return ConsumerStatistics
        .builder(getBatchType())
        .antallOppgaverSomErHentetFraDatabasen(oppgaver.size())
        .build();
  }

  private FinnOppgaveResponse finnOppgaver(
      final String opprettetAv,
      final int    limit,
      final int    offset) {
    final URI uri;
    try {
      uri = new URIBuilder(getOkosynkConfiguration().getRequiredString("OPPGAVE_URL"))
          .addParameter("opprettetAv", opprettetAv)
          .addParameter("tema", FAGOMRADE_OKONOMI_KODE)
          .addParameter("statuskategori", "AAPEN")
          .addParameter("limit", String.valueOf(limit))
          .addParameter("offset", String.valueOf(offset))
          .build();
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Klarte ikke bygge opp Oppgave URI", e);
    }

    final HttpGet request = new HttpGet(uri);
    request.addHeader("X-Correlation-ID", UUID.randomUUID().toString());
    request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());
    try {
      request.addHeader(new BasicScheme(UTF_8).authenticate(credentials, request, null));
    } catch (AuthenticationException e) {
      throw new IllegalStateException(e);
    }

    try (CloseableHttpResponse response = this.httpClient.execute(request)) {
      final StatusLine statusLine = response.getStatusLine();
      if (statusLine.getStatusCode() >= 400) {
        try {
          final ErrorResponse errorResponse = new ObjectMapper()
              .readValue(response.getEntity().getContent(), ErrorResponse.class);
          log.error("Feil oppsto under henting av oppgaver: {}", errorResponse);
          throw illegalArgumentFrom(errorResponse);
        } catch (JsonParseException jpe) {
          parseRawError(response);
        }
      }

      return new ObjectMapper()
          .readValue(response.getEntity().getContent(), FinnOppgaveResponse.class);
    } catch (IOException e) {
      throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
    }
  }

  private PatchOppgaverResponse patchOppgaver(
      final List<Oppgave> oppgaver,
      final boolean       ferdigstill,
      final HttpPatch     request) {
    try {
      ObjectNode patchJson = createPatchrequest(oppgaver, ferdigstill);
      String jsonString = new ObjectMapper().writeValueAsString(patchJson);
      request.setEntity(new StringEntity(jsonString, "UTF-8"));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Noe gikk galt under serialisering av patch request", e);
    }

    try (CloseableHttpResponse response = this.httpClient.execute(request)) {
      final StatusLine statusLine = response.getStatusLine();
      if (statusLine.getStatusCode() >= 400) {
        try {
          final ErrorResponse errorResponse =
              new ObjectMapper().readValue(response.getEntity().getContent(), ErrorResponse.class);
          log.error("Feil oppsto under patching av oppgaver: {}", errorResponse);
          throw illegalArgumentFrom(errorResponse);
        } catch (JsonParseException jpe) {
          parseRawError(response);
        }
      }

      return new ObjectMapper()
          .readValue(response.getEntity().getContent(), PatchOppgaverResponse.class);
    } catch (IOException e) {
      throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
    }
  }

  private int summerAntallFraResponse(
      final List<PatchOppgaverResponse>          responses,
      final ToIntFunction<PatchOppgaverResponse> function) {
    return responses.stream()
        .mapToInt(function)
        .reduce(Integer::sum)
        .orElse(0);
  }

  private PatchOppgaverResponse parseRawError(
      final CloseableHttpResponse response
  ) throws IOException {

    final String responseBody = new ObjectMapper()
        .readValue(response.getEntity().getContent(), String.class);
    log.error("Feilet under parsing av oppgave error response: {}", responseBody);
    throw new IllegalStateException("Feilet under parsing av oppgave error response");
  }

  private ObjectNode createPatchrequest(
      final List<Oppgave> oppgaver,
      final boolean       ferdigstill
  ) {
    final ObjectMapper mapper = new ObjectMapper();
    final ObjectNode patchJson = mapper.createObjectNode();
    patchJson.put("endretAvEnhetsnr", ENHET_ID_FOR_ANDRE_EKSTERNE);
    if (ferdigstill) {
      patchJson.put("status", FERDIGSTILT.name());
    }
    final ArrayNode patchJsonOppgaver = patchJson.putArray("oppgaver");

    oppgaver.forEach(o -> {
      final ObjectNode node = mapper.createObjectNode();
      node.put("id", o.oppgaveId);
      node.put("versjon", o.versjon);
      node.put("beskrivelse", o.beskrivelse);
      patchJsonOppgaver.add(node);
    });

    return patchJson;
  }

  private IllegalArgumentException illegalArgumentFrom(
      final ErrorResponse errorResponse
  ) {
    return new IllegalArgumentException(
        errorResponse.getFeilmelding() + " uuid: " + errorResponse.getUuid());
  }

  private Oppgave tilOppgave(final OppgaveDto oppgaveDto) {
    return new Oppgave.OppgaveBuilder()
        .withOppgaveId(oppgaveDto.getId())
        .withAktoerId(oppgaveDto.getAktoerId())
        .withSamhandlernr(oppgaveDto.getSamhandlernr())
        .withOrgnr(oppgaveDto.getOrgnr())
        .withBnr(oppgaveDto.getBnr())
        .withOppgavetypeKode(oppgaveDto.getOppgavetype())
        .withFagomradeKode(oppgaveDto.getTema())
        .withBehandlingstema(oppgaveDto.getBehandlingstema())
        .withBehandlingstype(oppgaveDto.getBehandlingstype())
        .withPrioritetKode(oppgaveDto.getPrioritet())
        .withBeskrivelse(oppgaveDto.getBeskrivelse())
        .withAktivFra(
            isNotBlank(oppgaveDto.getAktivDato()) ? LocalDate.parse(oppgaveDto.getAktivDato())
                : null)
        .withAktivTil(isNotBlank(oppgaveDto.getFristFerdigstillelse()) ? LocalDate
            .parse(oppgaveDto.getFristFerdigstillelse()) : null)
        .withAnsvarligEnhetId(oppgaveDto.getTildeltEnhetsnr())
        .withLest(oppgaveDto.getStatus() != OPPRETTET)
        .withVersjon(oppgaveDto.getVersjon())
        .withSistEndret(
            ofNullable(oppgaveDto.getEndretTidspunkt()).orElse(oppgaveDto.getOpprettetTidspunkt()))
        .withMappeId(oppgaveDto.getMappeId())
        .withAnsvarligSaksbehandlerIdent(oppgaveDto.getTilordnetRessurs())
        .build();
  }

  private OppgaveDto oversettOppgave(final Oppgave oppgave) {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    OppgaveDto oppgaveDto = new OppgaveDto();
    oppgaveDto.setAktoerId(oppgave.aktoerId);
    oppgaveDto.setSamhandlernr(oppgave.samhandlernr);
    oppgaveDto.setOrgnr(oppgave.orgnr);
    oppgaveDto.setBnr(oppgave.bnr);
    oppgaveDto.setOppgavetype(oppgave.oppgavetypeKode);
    oppgaveDto.setTema(oppgave.fagomradeKode);
    oppgaveDto.setBehandlingstema(oppgave.behandlingstema);
    oppgaveDto.setBehandlingstype(oppgave.behandlingstype);
    oppgaveDto.setPrioritet(oppgave.prioritetKode);
    oppgaveDto.setBeskrivelse(oppgave.beskrivelse);
    oppgaveDto.setAktivDato(oppgave.aktivFra.format(formatter));
    oppgaveDto.setFristFerdigstillelse(oppgave.aktivTil.format(formatter));
    oppgaveDto.setTildeltEnhetsnr(oppgave.ansvarligEnhetId);
    oppgaveDto.setOpprettetAvEnhetsnr(ENHET_ID_FOR_ANDRE_EKSTERNE);

    return oppgaveDto;
  }

  private IOkosynkConfiguration getOkosynkConfiguration() {
    return this.okosynkConfiguration;
  }

}
