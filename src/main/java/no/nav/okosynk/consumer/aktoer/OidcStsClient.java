package no.nav.okosynk.consumer.aktoer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.StsOidcResponse;
import org.apache.commons.io.IOUtils;
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

class OidcStsClient {

    private static final Logger log = LoggerFactory.getLogger(OidcStsClient.class);

    private final URI endpointUri;
    private final String batchBruker;
    private final UsernamePasswordCredentials credentials;
    private final CloseableHttpClient httpClient;
    private String oidcToken;

    OidcStsClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {

        this.batchBruker = okosynkConfiguration
                .getString(batchType.getBatchBrukerKey(), batchType.getBatchBrukerDefaultValue());
        this.credentials = new UsernamePasswordCredentials(this.batchBruker,
                okosynkConfiguration.getString(batchType.getBatchBrukerPasswordKey()));

        try {
            this.endpointUri =
                    new URIBuilder(okosynkConfiguration.getRequiredString(Constants.REST_STS_URL_KEY))
                            .addParameter("grant_type", "client_credentials")
                            .addParameter("scope", "openid")
                            .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Klarte ikke bygge opp URI for STS kall", e);
        }

        this.httpClient = HttpClients.createDefault();
        this.oidcToken = getTokenFromSts();
    }

    String getOidcToken() {

        if (isExpired(this.oidcToken)) {
            log.info("OIDC Token for {} expired, getting a new one from the STS", this.batchBruker);
            this.oidcToken = getTokenFromSts();
        }

        return this.oidcToken;
    }

    private boolean isExpired(final String oidcToken) {

        final boolean isExpired;
        final ObjectMapper mapper = new ObjectMapper();
        final String oidcTokenBetweenTags = substringBetween(oidcToken, ".");
        final String tokenBody = new String(Base64.getDecoder().decode(oidcTokenBetweenTags));
        final JsonNode node;
        try {
            node = mapper.readTree(tokenBody);
        } catch (IOException e) {
            throw new IllegalStateException("Klarte ikke parse oidc token fra STS", e);
        }
        final Instant now = Instant.now();
        final Instant expiry = Instant.ofEpochSecond(node.get("exp").longValue())
                .minusSeconds(300);//5 min timeskew
        if (now.isAfter(expiry)) {
            log.info("OIDC token expired {} is after {}", now, expiry);
            isExpired = true;
        } else {
            isExpired = false;
        }

        return isExpired;
    }

    private String getTokenFromSts() {

        log.info("henter OIDC Token for {}.", this.batchBruker);
        final HttpGet request = new HttpGet(this.endpointUri);
        request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        try {
            request.addHeader(new BasicScheme(UTF_8).authenticate(this.credentials, request, null));
        } catch (AuthenticationException e) {
            // As far as I have found out,
            // the declared exception is NEVER thrown.
            throw new IllegalStateException(e);
        }

        try (final CloseableHttpResponse response = this.httpClient.execute(request)) {
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
                String stsOidcResponseAsString = null;
                final StsOidcResponse stsOidcResponse;
                try {
                    stsOidcResponseAsString =
                            IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name());
                    stsOidcResponse =
                            new ObjectMapper().readValue(stsOidcResponseAsString, StsOidcResponse.class);
                } catch (IOException e) {
                    throw new IllegalStateException(
                            "Klarte ikke deserialisere respons fra STS. stsOidcResponseAsString: "
                                    + System.lineSeparator()
                                    + stsOidcResponseAsString,
                            e
                    );
                }
                return stsOidcResponse.getAccessToken();
            } else {
                final String msg =
                        "Feil oppsto under henting av token fra STS - %s"
                                + HttpResponseUtil.createStringRepresentationOfResponseEntity(response);
                throw new IllegalStateException(String.format(msg, statusLine.getReasonPhrase()));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
