package no.nav.okosynk.consumer.oppgave;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.consumer.security.AzureAdAuthenticationClient;
import no.nav.okosynk.domain.Oppgave;
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
        log.info("Added " + X_CORRELATION_ID_HEADER_KEY + " to request: " + correlationIdString);
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
            final List<PatchOppgaverResponse> responses,
            final ToIntFunction<PatchOppgaverResponse> function) {
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

        final ObjectMapper objectMapper = new ObjectMapper();
        final List<OppgaveDto> oppgaverSomErOpprettet = new ArrayList<>();
        final List<OppgaveDto> oppgaverSomIkkeErOpprettet = new ArrayList<>();
        final List<OppgaveDto> oppgaveDtos =
                oppgaver.stream()
                        .map(
                                (oppgave) ->
                                {
                                    try {
                                        return OppgaveMapper.map(oppgave);
                                    } catch (OppgaveMapperException_MoreThanOneActorType | OppgaveMapperException_AktivTilFraNull e) {
                                        throw new IllegalStateException("Feil i input date", e);
                                    }
                                }
                        )
                        .collect(Collectors.toList());

        oppgaveDtos.forEach(dto -> {
            final String dtoAsJsonString;
            try {
                dtoAsJsonString = objectMapper.writeValueAsString(dto);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(
                        "Klarte ikke serialisere oppgave i forkant av POST mot Oppgave", e);
            }
            request.setEntity(new StringEntity(dtoAsJsonString, "UTF-8"));

            try (final CloseableHttpResponse response = executeRequest(this.httpClient, request)) {
                final StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                    ErrorResponse errorResponse = null;
                    try {
                        errorResponse =
                                objectMapper
                                        .readValue(response.getEntity().getContent(), ErrorResponse.class);
                    } catch (JsonParseException jpe) {
                        parseRawError(response);
                    }
                    log.error("Feil oppsto under oppretting av oppgave: {}, Error response: {}", dto,
                            errorResponse);
                    oppgaverSomIkkeErOpprettet.add(dto);
                } else {
                    oppgaverSomErOpprettet.add(
                            objectMapper.readValue(response.getEntity().getContent(), OppgaveDto.class));
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
     * @param oppgaver    The oppgaver to be updated.
     * @param ferdigstill If <code>false</code>, the update changes
     *                    a few selected fields (typically beskrivelse),
     *                    but the oppgave status is kept as is. <BR/>
     *                    If <code>true</code>, the update behaves
     *                    as if it were <code>false</code>,
     *                    but the oppgave status is set to ferdigstilt.
     * @return The metrics of the update.
     */
    public ConsumerStatistics patchOppgaver(
            final Collection<Oppgave> oppgaver,
            final boolean ferdigstill) {

        if (oppgaver == null || oppgaver.isEmpty()) {
            return ConsumerStatistics.zero(getBatchType());
        }

        final HttpEntityEnclosingRequestBase request =
                createOppgaveRequestBase(
                        getOkosynkConfiguration(),
                        HttpPatch::new,
                        getUsernamePasswordCredentials(),
                        this.azureAdAuthenticationClient);

        final List<List<Oppgave>> oppgaverLister =
                delOppListe(new ArrayList<>(oppgaver), 500);

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
        return this.batchType;
    }

    public ConsumerStatistics finnOppgaver(final Set<Oppgave> oppgaver) {
        final int bulkSize = 50;
        final Collection<String> oppprettetAvValuesForFinn = getOkosynkConfiguration().getOpprettetAvValuesForFinn(getBatchType());
        final AtomicInteger atomicInteger = new AtomicInteger(oppgaver.size());
        oppprettetAvValuesForFinn
                .stream()
                .forEach(oppprettetAvValueForFinn ->
                        {
                            int offset = 0;
                            log.info("Starter søk i og evt. inkrementell henting av oppgaver med opprettetAv = \"" + oppprettetAvValueForFinn + "\" fra oppgave-servicen...");
                            FinnOppgaveResponse finnOppgaveResponse = this.finnOppgaver(oppprettetAvValueForFinn, bulkSize, offset);
                            log.info(
                                    "Estimat: Vi kommer totalt til å hente {} oppgaver med opprettetAv = \"" + oppprettetAvValueForFinn + "\"",
                                    finnOppgaveResponse.getAntallTreffTotalt()
                            );
                            while (!finnOppgaveResponse.getOppgaver().isEmpty()) {
                                log.debug("Akkumulerer {} oppgaver for behandling", finnOppgaveResponse.getOppgaver().size());
                                oppgaver.addAll(finnOppgaveResponse.getOppgaver()
                                        .stream()
                                        .map(OppgaveMapper::map)
                                        .collect(Collectors.toList()));
                                if (finnOppgaveResponse.getOppgaver().size() < bulkSize) {
                                    break;
                                } else {
                                    offset += bulkSize;
                                    finnOppgaveResponse = this.finnOppgaver(oppprettetAvValueForFinn, bulkSize, offset);
                                }
                            }
                            log.info("Hentet totalt {} unike oppgaver fra Oppgave  med opprettetAv = \"" + oppprettetAvValueForFinn + "\"", oppgaver.size() - atomicInteger.get());
                            atomicInteger.addAndGet(oppgaver.size());
                        }
                );
        log.info("Hentet totalt {} unike oppgaver fra Oppgave  med alle verdier av opprettetAv", oppgaver.size());
        return ConsumerStatistics
                .builder(getBatchType())
                .antallOppgaverSomErHentetFraDatabasen(oppgaver.size())
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

    private FinnOppgaveResponse finnOppgaver(
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
                    parseRawError(response);
                }
            }

            final ObjectMapper objectMapper = new ObjectMapper();
            final HttpEntity httpEntity = response.getEntity();

            final FinnOppgaveResponse finnOppgaveResponse;
            if (true) {
                final Scanner scanner = new Scanner(httpEntity.getContent()).useDelimiter(System.lineSeparator());
                final String entityAsString = scanner.hasNext() ? scanner.next() : "";
                // Do some random logging of the response entity as a string:
                finnOppgaveResponse = objectMapper.readValue(entityAsString, FinnOppgaveResponse.class);
                if (offset < 100) {
                    // Do some "random" logging to see the response entity:
                    log.info("finn oppgaver response entityAsString fra oppgave: {}", entityAsString);
                }
            } else {
                finnOppgaveResponse = objectMapper.readValue(httpEntity.getContent(), FinnOppgaveResponse.class);
            }
            return finnOppgaveResponse;
        } catch (IOException e) {
            throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
        }
    }

    private PatchOppgaverResponse patchOppgaver(
            final List<Oppgave> oppgaver,
            final boolean ferdigstill,
            final HttpEntityEnclosingRequestBase request) {
        try {
            final ObjectNode patchJson = createPatchJson(oppgaver, ferdigstill);
            final String jsonString = new ObjectMapper().writeValueAsString(patchJson);
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
                    parseRawError(response);
                }
            }

            return new ObjectMapper()
                    .readValue(response.getEntity().getContent(), PatchOppgaverResponse.class);
        } catch (IOException e) {
            throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
        }
    }

    private PatchOppgaverResponse parseRawError(
            final CloseableHttpResponse response
    ) throws IOException {

        final String responseBody = new ObjectMapper()
                .readValue(response.getEntity().getContent(), String.class);
        log.error("Feilet under parsing av oppgave error response: {}", responseBody);
        throw new IllegalStateException("Feilet under parsing av oppgave error response");
    }

    private ObjectNode createPatchJson(
            final List<Oppgave> oppgaver,
            final boolean ferdigstill
    ) {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode patchJson = mapper.createObjectNode();
        patchJson.put("endretAvEnhetsnr", OppgaveMapper.ENHET_ID_FOR_ANDRE_EKSTERNE);
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

    private IllegalStateException illegalStateExceptionFrom(
            final ErrorResponse errorResponse
    ) {
        return new IllegalStateException(
                errorResponse.getFeilmelding() + " uuid: " + errorResponse.getUuid());
    }
}