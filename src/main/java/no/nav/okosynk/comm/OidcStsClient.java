package no.nav.okosynk.comm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.HttpResponseUtil;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static no.nav.okosynk.config.Constants.OPPGAVE_PASSWORD;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class OidcStsClient {

    private static final Logger log = LoggerFactory.getLogger(OidcStsClient.class);

    private final URI endpointUri;
    private final String batchBruker;
    private final UsernamePasswordCredentials credentials;
    private final CloseableHttpClient httpClient;
    private String oidcToken;

    public OidcStsClient(final OkosynkConfiguration okosynkConfiguration) {

        this.batchBruker = okosynkConfiguration.getString(Constants.OPPGAVE_USERNAME);
        final String brukerPassword = okosynkConfiguration.getString(OPPGAVE_PASSWORD);
        this.credentials = new UsernamePasswordCredentials(this.batchBruker, brukerPassword);

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

    public String getOidcToken() {

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
        log.info("Henter OIDC Token for {}.", this.batchBruker);
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
                return getStsOidcResponse(response).getAccessToken();
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

    private StsOidcResponse getStsOidcResponse(CloseableHttpResponse response) {
        String stsOidcResponseAsString = null;
        try {
            stsOidcResponseAsString =
                    IOUtils.toString(response.getEntity().getContent(), UTF_8);
            return new ObjectMapper().readValue(stsOidcResponseAsString, StsOidcResponse.class);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Klarte ikke deserialisere respons fra STS. stsOidcResponseAsString: "
                            + System.lineSeparator()
                            + stsOidcResponseAsString,
                    e
            );
        }
    }

}
