package no.nav.okosynk.synkroniserer.consumer.oppgave;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import no.nav.okosynk.comm.AzureAdAuthenticationClient;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.model.Oppgave;
import no.nav.okosynk.synkroniserer.consumer.ConsumerStatistics;
import no.nav.okosynk.synkroniserer.consumer.oppgave.json.FinnOppgaverResponseJson;
import no.nav.okosynk.synkroniserer.consumer.oppgave.json.PostOppgaveRequestJson;
import no.nav.okosynk.synkroniserer.consumer.oppgave.json.PostOppgaveResponseJson;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static no.nav.okosynk.config.Constants.*;
import static no.nav.okosynk.synkroniserer.consumer.oppgave.OppgaveStatus.FERDIGSTILT;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class OppgaveRestClient {

    private static final Logger log = LoggerFactory.getLogger(OppgaveRestClient.class);

    private static final String FAGOMRADE_OKONOMI_KODE = "OKO";
    private final AzureAdAuthenticationClient azureAdAuthenticationClient;
    private final OkosynkConfiguration okosynkConfiguration;
    @Getter
    private final Constants.BATCH_TYPE batchType;
    private final CloseableHttpClient httpClient;
    private final UsernamePasswordCredentials credentials;
    private static final String MESSAGE = "Feilet ved kall mot Oppgave API";

    public OppgaveRestClient(
            final OkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType,
            final AzureAdAuthenticationClient azureAdAuthenticationClient) throws ConfigureOrInitializeOkosynkIoException {

        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType = batchType;
        this.azureAdAuthenticationClient = azureAdAuthenticationClient;

        final String bruker = okosynkConfiguration.getString(OPPGAVE_USERNAME);
        final String brukerPassword = okosynkConfiguration.getString(OPPGAVE_PASSWORD);
        try{
            this.credentials = new UsernamePasswordCredentials(bruker, brukerPassword);
        } catch (IllegalArgumentException e) {
            throw new ConfigureOrInitializeOkosynkIoException(e.getMessage());
        }
        this.httpClient = HttpClients.createDefault();
        log.info("OppgaveRestClient konfigurert for {}", batchType);
    }

    private static void addCorrelationIdToRequest(final AbstractHttpMessage request) {
        final String correlationIdString = UUID.randomUUID().toString();
        request.addHeader(X_CORRELATION_ID_HEADER_KEY, correlationIdString);
        log.debug("Added {} to request: {}", X_CORRELATION_ID_HEADER_KEY, correlationIdString);
    }

    private static HttpEntityEnclosingRequestBase createOppgaveRequestBase(
            final String oppgaveUrl,
            final Function<String, HttpEntityEnclosingRequestBase> requestCreatorFunction,
            final AzureAdAuthenticationClient azureAdAuthenticationClient
    ) throws IOException {
        final HttpEntityEnclosingRequestBase request = requestCreatorFunction.apply(oppgaveUrl);
        addCorrelationIdToRequest(request);
        request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        request.addHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
        OppgaveRestClient.addAzureAdAuthenticationHeader(request, azureAdAuthenticationClient);

        return request;
    }

    private static void addAzureAdAuthenticationHeader(
            final HttpRequestBase request, final AzureAdAuthenticationClient azureAdAuthenticationClient) throws IOException {
        final String azureAdAuthenticationToken = azureAdAuthenticationClient.getToken();
        request.addHeader(AUTHORIZATION, "Bearer " + azureAdAuthenticationToken);
    }

    AzureAdAuthenticationClient getAzureAdAuthenticationClient() {
        return this.azureAdAuthenticationClient;
    }

    public ConsumerStatistics opprettOppgaver(final Collection<Oppgave> oppgaver) throws IOException {

        String oppgaveUrl = getOkosynkConfiguration().getString(OPPGAVE_URL_KEY);
        final HttpEntityEnclosingRequestBase request =
                createOppgaveRequestBase(
                        oppgaveUrl,
                        HttpPost::new,
                        getAzureAdAuthenticationClient()
                );

        final ObjectMapper objectMapper =
                new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final List<PostOppgaveResponseJson> oppgaverSomErOpprettet = new ArrayList<>();
        final List<PostOppgaveRequestJson> oppgaverSomIkkeErOpprettet = new ArrayList<>();
        final List<PostOppgaveRequestJson> postOppgaveRequestJsons =
                oppgaver.stream()
                        .map(oppgave -> {
                                    try {
                                        return OppgaveMapper.mapFromFinnOppgaveResponseJsonToOppgave(oppgave);
                                    } catch (OppgaveMapperException_MoreThanOneActorType |
                                             OppgaveMapperException_AktivTilFraNull e) {
                                        throw new IllegalStateException("Feil i input date", e);
                                    }
                                }
                        )
                        .toList();

        postOppgaveRequestJsons.forEach(postOppgaveRequestJson -> {
            final String oppgaveJsonString;
            try {
                oppgaveJsonString = objectMapper.writeValueAsString(postOppgaveRequestJson);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(
                        "Klarte ikke serialisere oppgave i forkant av POST mot Oppgave", e);
            }
            request.setEntity(new StringEntity(oppgaveJsonString, "UTF-8"));

            try (final CloseableHttpResponse response = executeRequest(this.httpClient, request)) {
                final StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                    handleError(objectMapper, response, postOppgaveRequestJson);
                    oppgaverSomIkkeErOpprettet.add(postOppgaveRequestJson);
                } else {
                    oppgaverSomErOpprettet.add(
                            objectMapper.readValue(response.getEntity().getContent(), PostOppgaveResponseJson.class));
                }
            } catch (IOException e) {
                throw new IllegalStateException(MESSAGE, e);
            }
        });

        return ConsumerStatistics
                .builder(getBatchType())
                .antallOppgaverSomMedSikkerhetErOpprettet(oppgaverSomErOpprettet.size())
                .antallOppgaverSomMedSikkerhetIkkeErOpprettet(oppgaverSomIkkeErOpprettet.size())
                .build();
    }

    private void handleError(ObjectMapper objectMapper, CloseableHttpResponse response, PostOppgaveRequestJson postOppgaveRequestJson) throws IOException {
        ErrorResponse errorResponse = null;
        try {
            errorResponse = objectMapper.readValue(response.getEntity().getContent(), ErrorResponse.class);
        } catch (JsonParseException jpe) {
            parseRawErrorAndThrow(response);
        }
        log.error("Feil oppstod under oppretting av oppgave: {}, Error response: {}",
                postOppgaveRequestJson,
                errorResponse);
    }

    /**
     * Update a collection of oppgaver by
     * calling the Oppgave application's REST interface.
     *
     * @param oppgaverToBePatched The oppgaver to be updated.
     * @param ferdigstill         If <code>false</code>, the update changes
     *                            a few selected fields (typically beskrivelse),
     *                            but the oppgave status is kept as is. <BR/>
     *                            If <code>true</code>, the update behaves
     *                            as if it were <code>false</code>,
     *                            but the oppgave status is set to ferdigstilt.
     * @return The metrics of the update.
     */
    public ConsumerStatistics patchOppgaver(
            final Collection<Oppgave> oppgaverToBePatched,
            final boolean ferdigstill) {

        if (oppgaverToBePatched == null || oppgaverToBePatched.isEmpty()) {
            return ConsumerStatistics.zero(getBatchType());
        }

        if (ferdigstill) {
            log.info("Ferdigstiller {} oppgaver", oppgaverToBePatched.size());
        } else {
            log.info("Oppdaterer {} oppgaver", oppgaverToBePatched.size());
        }
        int suksess = 0;
        int feilet = 0;

        for (Oppgave oppgave : oppgaverToBePatched) {
            try {
                patchOppgave(oppgave, ferdigstill);
                suksess++;
            } catch (Exception e) {
                feilet++;
            }
        }
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

    public ConsumerStatistics finnOppgaver(final Set<Oppgave> oppgaverAccumulated) throws IOException {
        final int bulkSize = 50;
        final Collection<String> oppgaverOpprettetAvOkosynk =
                asList(getOkosynkConfiguration().getString(OPPGAVE_USERNAME), getOkosynkConfiguration().getNaisAppName());
        final AtomicInteger atomicInteger = new AtomicInteger(oppgaverAccumulated.size());
        final String oppgavetype = getBatchType().getOppgaveType();
        int offset = 0;
        log.info("Starter søk og evt. inkrementell henting av oppgaver av type: {}", oppgavetype);
        FinnOppgaverResponseJson finnOppgaverResponseJson =
                this.finnOppgaver(bulkSize, offset);
        log.info(
                "Fant {} oppgaver av oppgaver av type: {}",
                finnOppgaverResponseJson.getAntallTreffTotalt(), oppgavetype
        );
        while (!finnOppgaverResponseJson.getFinnOppgaveResponseJsons().isEmpty()) {
            log.debug("Akkumulerer {} oppgaver for behandling", finnOppgaverResponseJson.getFinnOppgaveResponseJsons().size());

            final List<Oppgave> oppgaver =
                    finnOppgaverResponseJson
                            .getFinnOppgaveResponseJsons()
                            .stream()
                            .filter(r -> {
                                boolean opprettetAvOkosynk = oppgaverOpprettetAvOkosynk.contains(r.getOpprettetAv());
                                if (!opprettetAvOkosynk) {
                                    log.warn("Filtrerer bort oppgave: {} fra resultatet, da denne ikke er opprettet av økosynk", r.getId());
                                }
                                return opprettetAvOkosynk;
                            })
                            .map(OppgaveMapper::mapFromFinnOppgaveResponseJsonToOppgave)
                            .toList();
            oppgaverAccumulated.addAll(oppgaver);
            if (finnOppgaverResponseJson.getFinnOppgaveResponseJsons().size() < bulkSize) {
                break;
            } else {
                offset += bulkSize;
                finnOppgaverResponseJson =
                        this.finnOppgaver(bulkSize, offset);
            }
        }
        int antallOppgaverSomSkalBehandles = oppgaverAccumulated.size() - atomicInteger.get();
        log.info("Hentet totalt {} unike oppgaver som skal behandles av Økosynk med oppgavetype : {}", antallOppgaverSomSkalBehandles, oppgavetype);
        if (antallOppgaverSomSkalBehandles != finnOppgaverResponseJson.getAntallTreffTotalt()) {
            log.warn("{} oppgaver har blitt filtrert bort fra resultatet. Disse er enten duplikater eller har blitt opprettet utenfor økosynk", finnOppgaverResponseJson.getAntallTreffTotalt() - antallOppgaverSomSkalBehandles);
        }
        return ConsumerStatistics
                .builder(getBatchType())
                .antallOppgaverSomErHentetFraDatabasen(oppgaverAccumulated.size())
                .build();
    }

    private void patchOppgave(Oppgave oppgave, boolean ferdigstill) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        final HttpEntityEnclosingRequestBase request =
                createOppgaveRequestBase(
                        getOkosynkConfiguration().getString(OPPGAVE_URL_KEY) + String.format("/%s", oppgave.oppgaveId),
                        HttpPatch::new,
                        getAzureAdAuthenticationClient()
                );
        try {
            final ObjectNode patchOppgaverObjectNode = objectMapper.createObjectNode();
            patchOppgaverObjectNode.put("id", oppgave.oppgaveId);
            patchOppgaverObjectNode.put("endretAvEnhetsnr", OppgaveMapper.ENHET_ID_FOR_ANDRE_EKSTERNE);
            patchOppgaverObjectNode.put("versjon", oppgave.versjon);
            patchOppgaverObjectNode.put("beskrivelse", oppgave.beskrivelse);
            if (ferdigstill) {
                patchOppgaverObjectNode.put("status", FERDIGSTILT.name());
            }
            final String jsonString = objectMapper.writeValueAsString(patchOppgaverObjectNode);
            request.setEntity(new StringEntity(jsonString, "UTF-8"));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Noe gikk galt under serialisering av patch request", e);
        }

        try (final CloseableHttpResponse response = executeRequest(this.httpClient, request)) {
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                handleError(objectMapper, response);
            }
        } catch (IOException e) {
            throw new IllegalStateException(MESSAGE, e);
        }
    }

    private void handleError(ObjectMapper objectMapper, CloseableHttpResponse response) throws IOException {
        try {
            final ErrorResponse errorResponse =
                    objectMapper.readValue(response.getEntity().getContent(), ErrorResponse.class);
            log.error("Feil oppstod under patching av oppgave: {}", errorResponse);
            throw illegalStateExceptionFrom(errorResponse);
        } catch (JsonParseException jpe) {
            parseRawErrorAndThrow(response);
        }
    }

    CloseableHttpResponse executeRequest(
            final CloseableHttpClient httpClient,
            final HttpUriRequest request) throws IOException {
        return httpClient.execute(request);
    }

    OkosynkConfiguration getOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    UsernamePasswordCredentials getUsernamePasswordCredentials() {
        return this.credentials;
    }

    private FinnOppgaverResponseJson finnOppgaver(final int bulkSize, final int offset) throws IOException {
        final URI uri;
        try {
            uri = new URIBuilder(getOkosynkConfiguration().getString(OPPGAVE_URL_KEY))
                    .addParameter("tema", FAGOMRADE_OKONOMI_KODE)
                    .addParameter("opprettetAv", (getBatchType() == BATCH_TYPE.OS ? "okosynkos" : "okosynkur"))
                    .addParameter("statuskategori", "AAPEN")
                    .addParameter("limit", String.valueOf(bulkSize))
                    .addParameter("offset", String.valueOf(offset))
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Klarte ikke bygge opp Oppgave URI", e);
        }

        final HttpGet request = new HttpGet(uri);
        addCorrelationIdToRequest(request);
        request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        OppgaveRestClient.addAzureAdAuthenticationHeader(request, getAzureAdAuthenticationClient());

        try (final CloseableHttpResponse response = executeRequest(this.httpClient, request)) {
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                handleError(response);
            }

            final String finnOppgaverResponseJsonEntityAsString =
                    new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8)
                    )
                            .lines()
                            .collect(Collectors.joining(System.lineSeparator()));
            final ObjectMapper objectMapper =
                    new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(finnOppgaverResponseJsonEntityAsString, FinnOppgaverResponseJson.class);
        } catch (IOException e) {
            throw new IllegalStateException(MESSAGE, e);
        }
    }

    private void handleError(CloseableHttpResponse response) throws IOException {
        try {
            final ErrorResponse errorResponse = new ObjectMapper()
                    .readValue(response.getEntity().getContent(), ErrorResponse.class);
            log.error("Feil oppsto under henting av oppgaver: {}", errorResponse);
            throw illegalStateExceptionFrom(errorResponse);
        } catch (JsonParseException jpe) {
            parseRawErrorAndThrow(response);
        }
    }

    private void parseRawErrorAndThrow(
            final CloseableHttpResponse response
    ) throws IOException {

        final String responseBody = new ObjectMapper()
                .readValue(response.getEntity().getContent(), String.class);
        log.error("Feilet under parsing av oppgave error response: {}", responseBody);
        throw new IllegalStateException("Feilet under parsing av oppgave error response");
    }

    private IllegalStateException illegalStateExceptionFrom(
            final ErrorResponse errorResponse
    ) {
        return new IllegalStateException(
                errorResponse.getFeilmelding() + " uuid: " + errorResponse.getUuid());
    }
}
