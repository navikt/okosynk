package no.nav.okosynk.consumer.oppgave;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.*;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.domain.Oppgave;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static no.nav.okosynk.consumer.oppgave.OppgaveStatus.OPPRETTET;
import static no.nav.okosynk.domain.AbstractMelding.ORGANISASJON;
import static no.nav.okosynk.domain.AbstractMelding.PERSON;
import static no.nav.okosynk.domain.AbstractMelding.SAMHANDLER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class OppgaveRestClient {
    private static final Logger log = LoggerFactory.getLogger(OppgaveRestClient.class);
    private static final String FAGOMRADE_OKONOMI_KODE = "OKO";

    private final IOkosynkConfiguration okosynkConfiguration;
    private final CloseableHttpClient httpClient;
    private final UsernamePasswordCredentials credentials;

    public OppgaveRestClient(IOkosynkConfiguration okosynkConfiguration, Constants.BATCH_TYPE batchType) {
        this.okosynkConfiguration = okosynkConfiguration;

        this.credentials = new UsernamePasswordCredentials(
                okosynkConfiguration.getString(batchType.getBatchBrukerKey(), batchType.getBatchBrukerDefaultValue()),
                okosynkConfiguration.getString(batchType.getBatchBrukerPasswordKey())
        );

        this.httpClient = HttpClients.createDefault();
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
            throw new IllegalStateException("Klarte ikke bygge opp URI for STS kall", e);
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
                ErrorResponse errorResponse = new ObjectMapper().readValue(response.getEntity().getContent(), ErrorResponse.class);
                log.error("Feil oppsto under henting av oppgaver: {}", errorResponse);
                throw illegalArgumentFrom(errorResponse);
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
        Collection<OppgaveDTO> oppgaveDTOs = oversettOppgaver(oppgaver);

        return null;
    }

    public ConsumerStatistics patchOppgaver(Set<Oppgave> oppgaver, boolean ferdigstill) {
        return ConsumerStatistics.zero();
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
                .withAktivFra(oppgaveDTO.getAktivDato())
                .withAktivTil(oppgaveDTO.getFristFerdigstillelse())
                .withAnsvarligEnhetId(oppgaveDTO.getTildeltEnhetsnr())
                .withLest(oppgaveDTO.getStatus() != OPPRETTET)
                .withVersjon(oppgaveDTO.getVersjon())
                .withSistEndret(oppgaveDTO.getEndretTidspunkt())
                .withMappeId(oppgaveDTO.getMappeId())
                .withAnsvarligSaksbehandlerIdent(oppgaveDTO.getTilordnetRessurs())
                .build();
    }

    private Set<OppgaveDTO> oversettOppgaver(Collection<Oppgave> oppgaver) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Set<OppgaveDTO> oppgaveDTOs = new HashSet<>();

        oppgaver.forEach(oppgave -> {
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

            oppgaveDTOs.add(oppgaveDTO);
        });

        return oppgaveDTOs;
    }

}
