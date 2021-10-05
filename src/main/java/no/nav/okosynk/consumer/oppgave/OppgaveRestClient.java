package no.nav.okosynk.consumer.oppgave;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.consumer.oppgave.json.FinnOppgaverResponseJson;
import no.nav.okosynk.consumer.oppgave.json.PatchOppgaverResponseJson;
import no.nav.okosynk.consumer.oppgave.json.PostOppgaveRequestJson;
import no.nav.okosynk.consumer.oppgave.json.PostOppgaveResponseJson;
import no.nav.okosynk.consumer.security.AzureAdAuthenticationClient;
import no.nav.okosynk.domain.Oppgave;
import no.nav.okosynk.domain.util.AktoerUt;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static no.nav.okosynk.config.Constants.OPPGAVE_URL_KEY;
import static no.nav.okosynk.config.Constants.X_CORRELATION_ID_HEADER_KEY;
import static no.nav.okosynk.consumer.oppgave.OppgaveStatus.FERDIGSTILT;
import static no.nav.okosynk.consumer.util.ListeOppdeler.delOppListe;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class OppgaveRestClient {

    private static final Logger log = LoggerFactory.getLogger(OppgaveRestClient.class);
    private static final String FAGOMRADE_OKONOMI_KODE = "OKO";
    final AzureAdAuthenticationClient azureAdAuthenticationClient;
    private final IOkosynkConfiguration okosynkConfiguration;
    private final Constants.BATCH_TYPE batchType;
    private final CloseableHttpClient httpClient;
    private final UsernamePasswordCredentials credentials;

    public OppgaveRestClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType,
            final AzureAdAuthenticationClient azureAdAuthenticationClient) {

        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType = batchType;
        this.azureAdAuthenticationClient = azureAdAuthenticationClient;
        final String bruker = okosynkConfiguration.getBatchBruker(batchType);
        final String brukerPassword = okosynkConfiguration.getBatchBrukerPassword(batchType);
        this.credentials = new UsernamePasswordCredentials(bruker, brukerPassword);
        this.httpClient = HttpClients.createDefault();
        log.info("OppgaveRestClient konfigurert for {}", batchType);
    }

    private static void addCorrelationIdToRequest(final AbstractHttpMessage request) {
        final String correlationIdString = UUID.randomUUID().toString();
        request.addHeader(X_CORRELATION_ID_HEADER_KEY, correlationIdString);
        log.debug("Added " + X_CORRELATION_ID_HEADER_KEY + " to request: " + correlationIdString);
    }

    private static HttpEntityEnclosingRequestBase createOppgaveRequestBase(
            final IOkosynkConfiguration okosynkConfiguration,
            final Function<String, HttpEntityEnclosingRequestBase> requestCreatorFunction,
            final UsernamePasswordCredentials usernamePasswordCredentials,
            final AzureAdAuthenticationClient azureAdAuthenticationClient
    ) {
        final String oppgaveUrl = okosynkConfiguration.getRequiredString(OPPGAVE_URL_KEY);
        final HttpEntityEnclosingRequestBase request = requestCreatorFunction.apply(oppgaveUrl);
        addCorrelationIdToRequest(request);
        request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        request.addHeader(CONTENT_TYPE, "application/json; charset=UTF-8");
        try {
            OppgaveRestClient.addAuthenticationHeader(
                    request,
                    usernamePasswordCredentials,
                    okosynkConfiguration,
                    azureAdAuthenticationClient);
        } catch (AuthenticationException e) {
            throw new IllegalStateException(e);
        }

        return request;
    }

    private static int summerAntallFraResponse(
            final List<PatchOppgaverResponseJson> responses,
            final ToIntFunction<PatchOppgaverResponseJson> function) {
        return responses.stream()
                .mapToInt(function)
                .reduce(Integer::sum)
                .orElse(0);
    }

    private static void addAuthenticationHeader(
            final HttpRequestBase request,
            final UsernamePasswordCredentials usernamePasswordCredentials,
            final IOkosynkConfiguration okosynkConfiguration,
            final AzureAdAuthenticationClient azureAdAuthenticationClient
    ) throws AuthenticationException {

        boolean shouldAuthenticateUsingBasicAgainstOppgave = true;
        if (okosynkConfiguration.shouldAuthenticateUsingAzureADAgainstOppgave()) {
            try {
                addAzureAdAuthenticationHeader(request, azureAdAuthenticationClient);
                shouldAuthenticateUsingBasicAgainstOppgave = false;
            } catch (Throwable e) {
                log.error("Exception received when trying Azure AD authentication", e);
                log.warn("Falling back on basic authentication");
            }
        }
        if (shouldAuthenticateUsingBasicAgainstOppgave) {
            addBasicAuthenticationHeader(request, usernamePasswordCredentials);
        }
    }

    private static void addAzureAdAuthenticationHeader(
            final HttpRequestBase request, final AzureAdAuthenticationClient azureAdAuthenticationClient) {
        final String azureAdAuthenticationToken = azureAdAuthenticationClient.getToken();
        request.addHeader("Authorization", "Bearer " + azureAdAuthenticationToken);
    }

    private static void addBasicAuthenticationHeader(
            final HttpRequestBase request,
            final UsernamePasswordCredentials usernamePasswordCredentials) throws AuthenticationException {
        request.addHeader(new BasicScheme(UTF_8).authenticate(usernamePasswordCredentials, request, null));
    }

    public ConsumerStatistics opprettOppgaver(final Collection<Oppgave> oppgaver) {

        final HttpEntityEnclosingRequestBase request =
                createOppgaveRequestBase(
                        getOkosynkConfiguration(),
                        HttpPost::new,
                        getUsernamePasswordCredentials(),
                        this.azureAdAuthenticationClient
                );

        final ObjectMapper objectMapper =
                new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ;
        final List<PostOppgaveResponseJson> oppgaverSomErOpprettet = new ArrayList<>();
        final List<PostOppgaveRequestJson> oppgaverSomIkkeErOpprettet = new ArrayList<>();
        final List<PostOppgaveRequestJson> postOppgaveRequestJsons =
                oppgaver.stream()
                        .map(
                                (oppgave) ->
                                {
                                    try {
                                        return OppgaveMapper.mapFromFinnOppgaveResponseJsonToOppgave(oppgave);
                                    } catch (OppgaveMapperException_MoreThanOneActorType | OppgaveMapperException_AktivTilFraNull e) {
                                        throw new IllegalStateException("Feil i input date", e);
                                    }
                                }
                        )
                        .collect(Collectors.toList());

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
                    ErrorResponse errorResponse = null;
                    try {
                        errorResponse =
                                objectMapper
                                        .readValue(response.getEntity().getContent(), ErrorResponse.class);
                    } catch (JsonParseException jpe) {
                        parseRawErrorAndThrow(response);
                    }
                    log.error("Feil oppsto under oppretting av oppgave: {}, Error response: {}. {}",
                            postOppgaveRequestJson,
                            errorResponse,
                            AktoerUt.isDnr(postOppgaveRequestJson.getNpidOrFolkeregisterIdent()) ? "Hint: folkeregisterIdent er et dnr." : "");
                    oppgaverSomIkkeErOpprettet.add(postOppgaveRequestJson);
                } else {
                    oppgaverSomErOpprettet.add(
                            objectMapper.readValue(response.getEntity().getContent(), PostOppgaveResponseJson.class));
                }
            } catch (IOException e) {
                throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
            }
        });

        return ConsumerStatistics
                .builder(getBatchType())
                .antallOppgaverSomMedSikkerhetErOpprettet(oppgaverSomErOpprettet.size())
                .antallOppgaverSomMedSikkerhetIkkeErOpprettet(oppgaverSomIkkeErOpprettet.size())
                .build();
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

        final HttpEntityEnclosingRequestBase request =
                createOppgaveRequestBase(
                        getOkosynkConfiguration(),
                        HttpPatch::new,
                        getUsernamePasswordCredentials(),
                        this.azureAdAuthenticationClient);

        final List<List<Oppgave>> listOfListsOfOppgaverToBePatched =
                delOppListe(new ArrayList<>(oppgaverToBePatched), 500);

        log.info(
                "Starter patching av oppgaver, sublistestørrelse: "
                        + "{}, antall sublister {}, antall oppgaver totalt: {}",
                500, listOfListsOfOppgaverToBePatched.size(), oppgaverToBePatched.size());
        final List<PatchOppgaverResponseJson> responses =
                listOfListsOfOppgaverToBePatched.stream()
                        .map(listOfOppgaverToBePatched -> patchOppgaver(listOfOppgaverToBePatched, ferdigstill, request))
                        .collect(Collectors.toList());

        final int suksess =
                summerAntallFraResponse(responses, PatchOppgaverResponseJson::getSuksess);
        final int feilet =
                summerAntallFraResponse(responses, PatchOppgaverResponseJson::getFeilet);

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
        return this.batchType;
    }

    public ConsumerStatistics finnOppgaver(final Set<Oppgave> oppgaverAccumulated) {
        final int bulkSize = 50;
        final Collection<String> oppprettetAvValuesForFinn = getOkosynkConfiguration().getOpprettetAvValuesForFinn(getBatchType());
        final AtomicInteger atomicInteger = new AtomicInteger(oppgaverAccumulated.size());
        oppprettetAvValuesForFinn
                .stream()
                .forEach(oppprettetAvValueForFinn ->
                        {
                            int offset = 0;
                            log.info("Starter søk i og evt. inkrementell henting av oppgaver med opprettetAv = \"" + oppprettetAvValueForFinn + "\" fra oppgave-servicen...");
                            FinnOppgaverResponseJson finnOppgaverResponseJson =
                                    this.finnOppgaver(oppprettetAvValueForFinn, bulkSize, offset);
                            log.info(
                                    "Estimat: Vi kommer totalt til å hente {} oppgaver med opprettetAv = \"" + oppprettetAvValueForFinn + "\"",
                                    finnOppgaverResponseJson.getAntallTreffTotalt()
                            );
                            while (!finnOppgaverResponseJson.getFinnOppgaveResponseJsons().isEmpty()) {
                                log.debug("Akkumulerer {} oppgaver for behandling", finnOppgaverResponseJson.getFinnOppgaveResponseJsons().size());

                                final List<Oppgave> oppgaverReadFromTheDatabase =
                                        finnOppgaverResponseJson
                                                .getFinnOppgaveResponseJsons()
                                                .stream()
                                                .map(OppgaveMapper::mapFromFinnOppgaveResponseJsonToOppgave)
                                                .collect(Collectors.toList());

                                final int sizeOfOppgaverReadBeforeAccumulation = oppgaverReadFromTheDatabase.size();
                                final int sizeOfOppgaverAccumulatedBeforeAccumulation = oppgaverAccumulated.size();
                                oppgaverAccumulated.addAll(oppgaverReadFromTheDatabase);
                                final int sizeOfOppgaverAccumulatedAfterAccumulation = oppgaverAccumulated.size();
                                final int discrepancyBetweenReadAndAccumulated =
                                        sizeOfOppgaverAccumulatedAfterAccumulation -
                                                (sizeOfOppgaverReadBeforeAccumulation + sizeOfOppgaverAccumulatedBeforeAccumulation);
                                if (discrepancyBetweenReadAndAccumulated != 0) {
                                    log.warn(
                                            "Noen oppgaver lest fra databasen har blitt ansett som duplikater. " +
                                                    "discrepancyBetweenReadAndAccumulated: {}" +
                                                    ", sizeOfOppgaverReadBeforeAccumulation: {}" +
                                                    ", sizeOfOppgaverAccumulatedBeforeAccumulation: {}" +
                                                    ", sizeOfOppgaverAccumulatedAfterAccumulation: {}",
                                            discrepancyBetweenReadAndAccumulated,
                                            sizeOfOppgaverReadBeforeAccumulation,
                                            sizeOfOppgaverAccumulatedBeforeAccumulation,
                                            sizeOfOppgaverAccumulatedAfterAccumulation
                                    );
                                }

                                if (finnOppgaverResponseJson.getFinnOppgaveResponseJsons().size() < bulkSize) {
                                    break;
                                } else {
                                    offset += bulkSize;
                                    finnOppgaverResponseJson =
                                            this.finnOppgaver(oppprettetAvValueForFinn, bulkSize, offset);
                                }
                            }
                            log.info("Hentet totalt {} unike oppgaver fra Oppgave  med opprettetAv = \"" + oppprettetAvValueForFinn + "\"", oppgaverAccumulated.size() - atomicInteger.get());
                            atomicInteger.addAndGet(oppgaverAccumulated.size());
                        }
                );
        try {
            final Oppgave aRandomFoundOppgave = oppgaverAccumulated.stream().filter(oppgave -> oppgave.folkeregisterIdent != null || oppgave.aktoerId != null).findAny().get();
            log.debug("A random found oppgave: " + aRandomFoundOppgave);
        } catch (Exception e) {
            log.warn("Exception when logging a random found oppgave", e);
        }

        log.info("Hentet fra databasen totalt {} unike oppgaver fra Oppgave  med alle verdier av opprettetAv", oppgaverAccumulated.size());
        return ConsumerStatistics
                .builder(getBatchType())
                .antallOppgaverSomErHentetFraDatabasen(oppgaverAccumulated.size())
                .build();
    }

    CloseableHttpResponse executeRequest(
            final CloseableHttpClient httpClient,
            final HttpUriRequest request) throws IOException {
        final CloseableHttpResponse response = httpClient.execute(request);
        return response;
    }

    IOkosynkConfiguration getOkosynkConfiguration() {
        return this.okosynkConfiguration;
    }

    UsernamePasswordCredentials getUsernamePasswordCredentials() {
        return this.credentials;
    }

    private FinnOppgaverResponseJson finnOppgaver(
            final String opprettetAv,
            final int bulkSize,
            final int offset) {
        final URI uri;
        try {
            uri = new URIBuilder(getOkosynkConfiguration().getRequiredString(OPPGAVE_URL_KEY))
                    .addParameter("opprettetAv", opprettetAv)
                    .addParameter("tema", FAGOMRADE_OKONOMI_KODE)
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
        try {
            OppgaveRestClient.addAuthenticationHeader(
                    request,
                    getUsernamePasswordCredentials(),
                    getOkosynkConfiguration(),
                    this.azureAdAuthenticationClient);
        } catch (AuthenticationException e) {
            throw new IllegalStateException(e);
        }

        try (final CloseableHttpResponse response = executeRequest(this.httpClient, request)) {
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                try {
                    final ErrorResponse errorResponse = new ObjectMapper()
                            .readValue(response.getEntity().getContent(), ErrorResponse.class);
                    log.error("Feil oppsto under henting av oppgaver: {}", errorResponse);
                    throw illegalStateExceptionFrom(errorResponse);
                } catch (JsonParseException jpe) {
                    parseRawErrorAndThrow(response);
                }
            }

            final ObjectMapper objectMapper =
                    new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final HttpEntity httpEntity = response.getEntity();
            final Scanner scanner = new Scanner(httpEntity.getContent()).useDelimiter(System.lineSeparator());
            final String finnOppgaverResponseJsonEntityAsString = scanner.hasNext() ? scanner.next() : "";
            final FinnOppgaverResponseJson finnOppgaverResponseJson =
                    objectMapper.readValue(finnOppgaverResponseJsonEntityAsString, FinnOppgaverResponseJson.class);
            try {
                // Do some random logging of the response entity as a string:
                if (offset < 100) {
                    // Do some "random" logging to see some random response entities:
                    log.debug("finnOppgaverResponseJsonEntityAsString fra oppgave: {}", finnOppgaverResponseJsonEntityAsString);
                }
                log.debug("A random FinnOppgaveResponseJson: " + finnOppgaverResponseJson.getFinnOppgaveResponseJsons().stream().findAny().get());
            } catch (Exception e) {
                log.debug("Exception when logging random oppgave info", e);
            }
            return finnOppgaverResponseJson;
        } catch (IOException e) {
            throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
        }
    }

    private PatchOppgaverResponseJson patchOppgaver(
            final List<Oppgave> oppgaveListe,
            final boolean ferdigstill,
            final HttpEntityEnclosingRequestBase request) {
        try {
            final ObjectNode patchOppgaverObjectNode = createPatchOppgaverObjectNode(oppgaveListe, ferdigstill);
            final String jsonString = new ObjectMapper().writeValueAsString(patchOppgaverObjectNode);
            request.setEntity(new StringEntity(jsonString, "UTF-8"));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Noe gikk galt under serialisering av patch request", e);
        }

        try (final CloseableHttpResponse response = executeRequest(this.httpClient, request)) {
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                try {
                    final ErrorResponse errorResponse =
                            new ObjectMapper().readValue(response.getEntity().getContent(), ErrorResponse.class);
                    log.error("Feil oppsto under patching av oppgaver: {}", errorResponse);
                    throw illegalStateExceptionFrom(errorResponse);
                } catch (JsonParseException jpe) {
                    parseRawErrorAndThrow(response);
                }
            }

            return new ObjectMapper()
                    .readValue(response.getEntity().getContent(), PatchOppgaverResponseJson.class);
        } catch (IOException e) {
            throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
        }
    }

    private PatchOppgaverResponseJson parseRawErrorAndThrow(
            final CloseableHttpResponse response
    ) throws IOException {

        final String responseBody = new ObjectMapper()
                .readValue(response.getEntity().getContent(), String.class);
        log.error("Feilet under parsing av oppgave error response: {}", responseBody);
        throw new IllegalStateException("Feilet under parsing av oppgave error response");
    }

    private ObjectNode createPatchOppgaverObjectNode(
            final List<Oppgave> oppgaverToBePatched,
            final boolean ferdigstill
    ) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectNode patchOppgaverObjectNode = objectMapper.createObjectNode();
        patchOppgaverObjectNode.put("endretAvEnhetsnr", OppgaveMapper.ENHET_ID_FOR_ANDRE_EKSTERNE);
        if (ferdigstill) {
            patchOppgaverObjectNode.put("status", FERDIGSTILT.name());
        }
        final ArrayNode patchOppgaverArrayNode = patchOppgaverObjectNode.putArray("oppgaver");

        oppgaverToBePatched.forEach(oppgaveToBePatched -> {
            final ObjectNode oppgaveToBePatchedObjectNnode = objectMapper.createObjectNode();
            oppgaveToBePatchedObjectNnode.put("id", oppgaveToBePatched.oppgaveId);
            oppgaveToBePatchedObjectNnode.put("versjon", oppgaveToBePatched.versjon);
            oppgaveToBePatchedObjectNnode.put("beskrivelse", oppgaveToBePatched.beskrivelse);
            patchOppgaverArrayNode.add(oppgaveToBePatchedObjectNnode);
        });

        return patchOppgaverObjectNode;
    }

    private IllegalStateException illegalStateExceptionFrom(
            final ErrorResponse errorResponse
    ) {
        return new IllegalStateException(
                errorResponse.getFeilmelding() + " uuid: " + errorResponse.getUuid());
    }
}