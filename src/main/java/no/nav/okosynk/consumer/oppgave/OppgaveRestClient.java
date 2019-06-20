package no.nav.okosynk.consumer.oppgave;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.*;
import no.nav.okosynk.domain.Oppgave;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static no.nav.okosynk.consumer.oppgave.OppgaveStatus.FERDIGSTILT;
import static no.nav.okosynk.consumer.oppgave.OppgaveStatus.OPPRETTET;
import static no.nav.okosynk.consumer.util.ListeOppdeler.delOppListe;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class OppgaveRestClient {
    private static final Logger log = LoggerFactory.getLogger(OppgaveRestClient.class);
    private static final String FAGOMRADE_OKONOMI_KODE = "OKO";
    private static final int ENHET_ID_FOR_ANDRE_EKSTERNE = 9999;

    private final IOkosynkConfiguration okosynkConfiguration;
    private final CloseableHttpClient httpClient;
    private final UsernamePasswordCredentials credentials;

    public OppgaveRestClient(IOkosynkConfiguration okosynkConfiguration, Constants.BATCH_TYPE batchType) {
        this.okosynkConfiguration = okosynkConfiguration;

        String bruker = okosynkConfiguration.getString(batchType.getBatchBrukerKey(), batchType.getBatchBrukerDefaultValue());
        this.credentials = new UsernamePasswordCredentials(bruker, okosynkConfiguration.getString(batchType.getBatchBrukerPasswordKey()));

        this.httpClient = HttpClients.createDefault();
        log.info("OppgaveRestClient konfigurert for {} og bruker {}", batchType, bruker);
    }

    private FinnOppgaveResponse finnOppgaver(String opprettetAv, int limit, int offset) {
        URI uri;
        try {
            uri = new URIBuilder(this.okosynkConfiguration.getRequiredString("OPPGAVE_URL"))
                    .addParameter("opprettetAv", opprettetAv)
                    .addParameter("tema", FAGOMRADE_OKONOMI_KODE)
                    .addParameter("statuskategori", "AAPEN")
                    .addParameter("limit", String.valueOf(limit))
                    .addParameter("offset", String.valueOf(offset))
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Klarte ikke bygge opp Oppgave URI", e);
        }

        HttpGet request = new HttpGet(uri);
        request.addHeader("X-Correlation-ID", UUID.randomUUID().toString());
        request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        try {
            request.addHeader(new BasicScheme(UTF_8).authenticate(credentials, request, null));
        } catch (AuthenticationException e) {
            throw new IllegalStateException(e);
        }

        try (CloseableHttpResponse response = this.httpClient.execute(request)) {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= 400) {
                try {
                    ErrorResponse errorResponse = new ObjectMapper().readValue(response.getEntity().getContent(), ErrorResponse.class);
                    log.error("Feil oppsto under henting av oppgaver: {}", errorResponse);
                    throw illegalArgumentFrom(errorResponse);
                } catch (JsonParseException jpe) {
                    parseRawError(response);
                }
            }

            return new ObjectMapper().readValue(response.getEntity().getContent(), FinnOppgaveResponse.class);
        } catch (IOException e) {
            throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
        }
    }

    public ConsumerStatistics finnOppgaver(String opprettetAv, Set<Oppgave> oppgaver ) {
        int limit = 20;
        int offset = 0;
        FinnOppgaveResponse finnOppgaveResponse = this.finnOppgaver(opprettetAv, limit, offset);
        log.info("Starter inkrementelt søk i oppgaver mot Oppgave API. Fant totalt {} oppgaver", finnOppgaveResponse.getAntallTreffTotalt());

        while(!finnOppgaveResponse.getOppgaver().isEmpty()) {
            finnOppgaveResponse = this.finnOppgaver(opprettetAv, limit, offset);
            oppgaver.addAll(finnOppgaveResponse.getOppgaver()
                            .stream()
                            .map(this::tilOppgave)
                            .collect(Collectors.toList()));

            log.info("Hentet {}/{} unike oppgaver fra Oppgave. Offset -> {}", oppgaver.size(), finnOppgaveResponse.getAntallTreffTotalt(), offset);
            offset += limit;
        }

        return ConsumerStatistics
                        .builder()
                        .antallOppgaverSomErHentetFraDatabasen(oppgaver.size())
                        .build();
    }

    public ConsumerStatistics opprettOppgaver(final Collection<Oppgave> oppgaver) {
        List<OppgaveDTO> oppgaveDTOs = oppgaver.stream()
                .map(this::oversettOppgave)
                .collect(Collectors.toList());

        HttpPost request = new HttpPost(this.okosynkConfiguration.getRequiredString("OPPGAVE_URL"));
        request.addHeader("X-Correlation-ID", UUID.randomUUID().toString());
        request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        request.addHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        try {
            request.addHeader(new BasicScheme(UTF_8).authenticate(credentials, request, null));
        } catch (AuthenticationException e) {
            throw new IllegalStateException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<OppgaveDTO> opprettedeOppgaver = new ArrayList<>();
        List<OppgaveDTO> oppgaverSomIkkeErOpprettet = new ArrayList<>();
        oppgaveDTOs.forEach(dto -> {
            try {
                request.setEntity(new StringEntity(objectMapper.writeValueAsString(dto)));
            } catch (UnsupportedEncodingException | JsonProcessingException e) {
                throw new IllegalStateException("Klarte ikke serialisere oppgave i forkant av POST mot Oppgave", e);
            }

            try (CloseableHttpResponse response = this.httpClient.execute(request)) {
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() >= 400) {
                    try {
                        ErrorResponse errorResponse = new ObjectMapper().readValue(response.getEntity().getContent(), ErrorResponse.class);
                        log.error("Feil oppsto under oppretting av oppgave: {}, Errorresponse: {}", dto, errorResponse);
                        oppgaverSomIkkeErOpprettet.add(dto);
                    } catch (JsonParseException jpe) {
                        parseRawError(response);
                    }
                } else {
                    opprettedeOppgaver.add(new ObjectMapper().readValue(response.getEntity().getContent(), OppgaveDTO.class));
                }
            } catch (IOException e) {
                throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
            }

        });

        return ConsumerStatistics
                .builder()
                .antallOppgaverSomMedSikkerhetErOpprettet(opprettedeOppgaver.size())
                .antallOppgaverSomMedSikkerhetIkkeErOpprettet(oppgaverSomIkkeErOpprettet.size())
                .build();
    }

    public ConsumerStatistics patchOppgaver(Set<Oppgave> oppgaver, boolean ferdigstill) {
        if (oppgaver == null || oppgaver.isEmpty()) {
            return ConsumerStatistics.zero();
        }

        HttpPatch request = new HttpPatch(this.okosynkConfiguration.getRequiredString("OPPGAVE_URL"));
        request.addHeader("X-Correlation-ID", UUID.randomUUID().toString());
        request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        request.addHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        try {
            request.addHeader(new BasicScheme(UTF_8).authenticate(credentials, request, null));
        } catch (AuthenticationException e) {
            throw new IllegalStateException(e);
        }

        final List<List<Oppgave>> oppgaverLister = delOppListe(new ArrayList<>(oppgaver), 1);

        log.info("Starter patching av oppgaver, sublistestørrelse: {}, antall sublister {}, antall oppgaver totalt: {}", 500, oppgaverLister.size(), oppgaver.size());
        List<PatchOppgaverResponse> responses =
                oppgaverLister.stream()
                .map(list -> patchOppgaver(list, ferdigstill, request))
                .collect(Collectors.toList());

        log.info("Ferdig med patching av oppgave");

        int suksess = summerAntallFraResponse(responses, PatchOppgaverResponse::getSuksess);
        int feilet = summerAntallFraResponse(responses, PatchOppgaverResponse::getFeilet);

        if (ferdigstill) {
            return ConsumerStatistics.builder()
                    .antallOppgaverSomMedSikkerhetErFerdigstilt(suksess)
                    .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(feilet)
                    .build();
        } else {
            return ConsumerStatistics.builder()
                    .antallOppgaverSomMedSikkerhetErOppdatert(suksess)
                    .antallOppgaverSomMedSikkerhetIkkeErOppdatert(feilet)
                    .build();
        }
    }

    private int summerAntallFraResponse(List<PatchOppgaverResponse> responses, ToIntFunction<PatchOppgaverResponse> function) {
        return responses.stream()
                .mapToInt(function)
                .reduce(Integer::sum)
                .orElse(0);
    }

    private PatchOppgaverResponse patchOppgaver(List<Oppgave> oppgaver, boolean ferdigstill, HttpPatch request) {
        try {
            ObjectNode patchJson = createPatchrequest(oppgaver, ferdigstill);
            String jsonString = new ObjectMapper().writeValueAsString(patchJson);
            log.info("Forsøker å patche oppgaver: {}", jsonString);
            request.setEntity(new StringEntity(jsonString));
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new IllegalStateException("Noe gikk galt under serialisering av patch request", e);
        }

        try (CloseableHttpResponse response = this.httpClient.execute(request)) {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= 400) {
                try {
                    ErrorResponse errorResponse = new ObjectMapper().readValue(response.getEntity().getContent(), ErrorResponse.class);
                    log.error("Feil oppsto under patching av oppgaver: {}", errorResponse);
                    throw illegalArgumentFrom(errorResponse);
                } catch (JsonParseException jpe) {
                    parseRawError(response);
                }
            }

            return new ObjectMapper().readValue(response.getEntity().getContent(), PatchOppgaverResponse.class);
        } catch (IOException e) {
            throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
        }
    }

    private PatchOppgaverResponse parseRawError(CloseableHttpResponse response) throws IOException {
        String responseBody = new ObjectMapper().readValue(response.getEntity().getContent(), String.class);
        log.error("Feilet under parsing av oppgave error response: {}", responseBody);
        throw new IllegalStateException("Feilet under parsing av oppgave error response");
    }

    private ObjectNode createPatchrequest(List<Oppgave> oppgaver, boolean ferdigstill) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode patchJson = mapper.createObjectNode();
        patchJson.put("endretAvEnhetsnr", ENHET_ID_FOR_ANDRE_EKSTERNE);
        if (ferdigstill) patchJson.put("status", FERDIGSTILT.name());
        ArrayNode patchJsonOppgaver = patchJson.putArray("oppgaver");

        oppgaver.forEach(o -> {
            ObjectNode node = mapper.createObjectNode();
            node.put("id", o.oppgaveId);
            node.put("versjon", o.versjon);
            node.put("beskrivelse", o.beskrivelse);
            patchJsonOppgaver.add(node);
        });

        return patchJson;
    }

    private IllegalArgumentException illegalArgumentFrom(ErrorResponse errorResponse) {
        return new IllegalArgumentException(errorResponse.getFeilmelding() + " uuid: " + errorResponse.getUuid());
    }

    private Oppgave tilOppgave(OppgaveDTO oppgaveDTO) {
        return new Oppgave.OppgaveBuilder()
                .withOppgaveId(oppgaveDTO.getId())
                .withAktoerId(oppgaveDTO.getAktoerId())
                .withSamhandlernr(oppgaveDTO.getSamhandlernr())
                .withOrgnr(oppgaveDTO.getOrgnr())
                .withBnr(oppgaveDTO.getBnr())
                .withOppgavetypeKode(oppgaveDTO.getOppgavetype())
                .withFagomradeKode(oppgaveDTO.getTema())
                .withBehandlingstema(oppgaveDTO.getBehandlingstema())
                .withBehandlingstype(oppgaveDTO.getBehandlingstype())
                .withPrioritetKode(oppgaveDTO.getPrioritet())
                .withBeskrivelse(oppgaveDTO.getBeskrivelse())
                .withAktivFra(isNotBlank(oppgaveDTO.getAktivDato()) ? LocalDate.parse(oppgaveDTO.getAktivDato()) : null)
                .withAktivTil(isNotBlank(oppgaveDTO.getFristFerdigstillelse()) ? LocalDate.parse(oppgaveDTO.getFristFerdigstillelse()) : null)
                .withAnsvarligEnhetId(oppgaveDTO.getTildeltEnhetsnr())
                .withLest(oppgaveDTO.getStatus() != OPPRETTET)
                .withVersjon(oppgaveDTO.getVersjon())
                .withSistEndret(ofNullable(oppgaveDTO.getEndretTidspunkt()).orElse(oppgaveDTO.getOpprettetTidspunkt()))
                .withMappeId(oppgaveDTO.getMappeId())
                .withAnsvarligSaksbehandlerIdent(oppgaveDTO.getTilordnetRessurs())
                .build();
    }

    private OppgaveDTO oversettOppgave(Oppgave oppgave) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            OppgaveDTO oppgaveDTO = new OppgaveDTO();
            oppgaveDTO.setAktoerId(oppgave.aktoerId);
            oppgaveDTO.setSamhandlernr(oppgave.samhandlernr);
            oppgaveDTO.setOrgnr(oppgave.orgnr);
            oppgaveDTO.setBnr(oppgave.bnr);
            oppgaveDTO.setOppgavetype(oppgave.oppgavetypeKode);
            oppgaveDTO.setTema(oppgave.fagomradeKode);
            oppgaveDTO.setBehandlingstema(oppgave.behandlingstema);
            oppgaveDTO.setBehandlingstype(oppgave.behandlingstype);
            oppgaveDTO.setPrioritet(oppgave.prioritetKode);
            oppgaveDTO.setBeskrivelse(oppgave.beskrivelse);
            oppgaveDTO.setAktivDato(oppgave.aktivFra.format(formatter));
            oppgaveDTO.setFristFerdigstillelse(oppgave.aktivTil.format(formatter));
            oppgaveDTO.setTildeltEnhetsnr(oppgave.ansvarligEnhetId);

        return oppgaveDTO;
    }

}
