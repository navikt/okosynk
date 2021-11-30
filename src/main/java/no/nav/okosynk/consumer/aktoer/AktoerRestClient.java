package no.nav.okosynk.consumer.aktoer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.security.OidcStsClient;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static no.nav.okosynk.config.Constants.HTTP_HEADER_NAV_CALL_ID_KEY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class AktoerRestClient implements IAktoerClient {

    public static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    public static final String NAV_PERSONIDENTER = "Nav-Personidenter";
    private static final Logger log = LoggerFactory.getLogger(AktoerRestClient.class);
    private final IOkosynkConfiguration okosynkConfiguration;
    private final Constants.BATCH_TYPE batchType;
    private final String consumerId;
    private CloseableHttpClient httpClient;
    private OidcStsClient oidcStsClient;

    public AktoerRestClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {

        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType = batchType;
        this.consumerId = okosynkConfiguration.getBatchBruker(batchType);

        log.info("Aktoerregister REST client bygd opp for {}", this.consumerId);
    }

    private static CloseableHttpClient createCloseableHttpClient() {

        final CloseableHttpClient httpClient = HttpClients.createDefault();

        return httpClient;
    }

    private static OidcStsClient createOidcStsClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType
    ) {
        return new OidcStsClient(okosynkConfiguration, batchType);
    }

    @Override
    public AktoerRespons hentGjeldendeAktoerId(final String folkeregisterIdent) {
        final URI uri;
        try {
            final String restAktoerRegisterUrl =
                    this.okosynkConfiguration.getRequiredString(Constants.REST_AKTOER_REGISTER_URL_KEY);
            uri = new URIBuilder(restAktoerRegisterUrl)
                    .addParameter("identgruppe", "AktoerId")
                    .addParameter("gjeldende", "true")
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed while building aktoerregister endpoint URI", e);
        }

        final HttpUriRequest request = new HttpGet(uri);
        final String oidcToken = getOidcToken(okosynkConfiguration, batchType);
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + oidcToken);
        request.addHeader(HTTP_HEADER_NAV_CALL_ID_KEY, String.valueOf(UUID.randomUUID()));
        request.addHeader(AktoerRestClient.NAV_PERSONIDENTER, folkeregisterIdent);
        request.addHeader(AktoerRestClient.NAV_CONSUMER_ID, this.consumerId);
        request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        final AktoerRespons aktoerRespons;
        try (final CloseableHttpResponse response = getCloseableHttpClient().execute(request)) {
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
                final ObjectMapper objectMapper = new ObjectMapper();
                final JsonNode jsonNode = objectMapper.readTree(response.getEntity().getContent());
                final JsonNode aktoerResponseJsonNode = jsonNode.get(folkeregisterIdent);
                final AktoerIdent aktoerIdent =
                        objectMapper.treeToValue(aktoerResponseJsonNode, AktoerIdent.class);

                if (isNotBlank(aktoerIdent.getFeilmelding())) {
                    aktoerRespons = AktoerRespons.feil(String
                            .format("Mottok feilmelding fra aktoerregisteret: %s", aktoerIdent.getFeilmelding()));
                } else {
                    final List<AktoerIdentEntry> identer = aktoerIdent.getIdenter();
                    if (identer != null && !identer.isEmpty()) {
                        if (identer.size() == 1) {
                            final AktoerIdentEntry entry = identer.get(0);
                            if (!entry.isGjeldende()) {
                                aktoerRespons = AktoerRespons.feil(
                                        "Spurte etter kun gjeldene identer "
                                                + "men mottok gyldig respons hvor "
                                                + "ident ikke er satt til gyldig");
                            } else {
                                aktoerRespons = AktoerRespons.ok(entry.getIdent());
                            }
                        } else {
                            aktoerRespons = AktoerRespons.feil(String
                                    .format("Forventet kun en aktoerId i responsen men fikk: %s", identer.size()));
                        }
                    } else {
                        aktoerRespons = AktoerRespons.feil(
                                "Fant ingen identer i responsen fra aktoerregisteret som, "
                                        + "responsen innholdt ingen feilmeldinger.");
                    }
                }
            } else {
                final String entityStringRepresentation =
                        HttpResponseUtil.createStringRepresentationOfResponseEntity(response);
                final String msg;
                if (statusLine.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    msg = "Fant ikke forespurt FNR i aktoerregisteret." + entityStringRepresentation;
                } else {
                    msg =
                            String.format(
                                    "Feil ved kall mot Aktoerregister - %s %s." + entityStringRepresentation,
                                    statusLine.getStatusCode(),
                                    statusLine.getReasonPhrase()
                            );
                }
                aktoerRespons = AktoerRespons.feil(msg);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Feil ved kall mot Aktoerregister API", e);
        }

        return aktoerRespons;
    }

    private String getOidcToken(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType
    ) {

        final OidcStsClient oidcStsClient = getOidcStsClient(okosynkConfiguration, batchType);
        final String oidcToken = oidcStsClient.getOidcToken();

        return oidcToken;
    }

    private void setOidcStsClient(final OidcStsClient oidcStsClient) {
        this.oidcStsClient = oidcStsClient;
    }

    private OidcStsClient getOidcStsClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {

        if (this.oidcStsClient == null) {
            final OidcStsClient oidcStsClient =
                    AktoerRestClient.createOidcStsClient(okosynkConfiguration, batchType);
            setOidcStsClient(oidcStsClient);
        }
        return this.oidcStsClient;
    }

    private CloseableHttpClient getCloseableHttpClient() {

        if (this.httpClient == null) {
            final CloseableHttpClient httpClient =
                    AktoerRestClient.createCloseableHttpClient();
            setCloseableHttpClient(httpClient);
        }
        return this.httpClient;
    }

    private void setCloseableHttpClient(final CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }
}