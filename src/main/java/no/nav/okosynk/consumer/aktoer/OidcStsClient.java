package no.nav.okosynk.consumer.aktoer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.STSOidcResponse;
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
import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class OidcStsClient {
    private static final Logger log = LoggerFactory.getLogger(OidcStsClient.class);
    private final CloseableHttpClient httpClient;
    private final URI endpointURI;
    private final UsernamePasswordCredentials credentials;
    private final String batchBruker;
    private String oidcToken;

    public OidcStsClient(IOkosynkConfiguration okosynkConfiguration, Constants.BATCH_TYPE batchType) {
        this.batchBruker = okosynkConfiguration.getString(batchType.getBatchBrukerKey(), batchType.getBatchBrukerDefaultValue());
        this.credentials = new UsernamePasswordCredentials(this.batchBruker, okosynkConfiguration.getString(batchType.getBatchBrukerPasswordKey()));

        try {
            this.endpointURI = new URIBuilder(okosynkConfiguration.getRequiredString("REST_STS_URL"))
                    .addParameter("grant_type", "client_credentials")
                    .addParameter("scope", "openid")
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Klarte ikke bygge opp URI for STS kall", e);
        }

        this.httpClient = HttpClients.createDefault();
        this.oidcToken = getTokenFromSTS();
    }

    public String getOidcToken() {
        if (isExpired(oidcToken)) {
            log.info("OIDC Token for {} expired, getting a new one from the STS", this.batchBruker);
            oidcToken = getTokenFromSTS();
        }

        return oidcToken;
    }

    private boolean isExpired(String oidcToken) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String tokenBody = new String(java.util.Base64.getDecoder().decode(substringBetween(oidcToken, ".")));
            JsonNode node = mapper.readTree(tokenBody);

            Instant now = Instant.now();
            Instant expiry = Instant.ofEpochSecond(node.get("exp").longValue()).minusSeconds(300);//5 min timeskew

            if (now.isAfter(expiry)) {
                log.info("OIDC token expired {} is after {}", now, expiry);
                return true;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Klarte ikke parse oidc token fra STS", e);
        }

        return false;
    }

    private String getTokenFromSTS() {
        log.info("henter OIDC Token for {}.", this.batchBruker);
        HttpGet request = new HttpGet(this.endpointURI);
        request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());

        try {
            request.addHeader(new BasicScheme(UTF_8).authenticate(credentials, request, null));
        } catch (AuthenticationException e) {
            throw new IllegalStateException(e);
        }

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() != 200) {
                throw new IllegalStateException(String.format("Feil oppsto under henting av token fra STS - %s", statusLine.getReasonPhrase()));
            }

            STSOidcResponse stsOidcResponse;
            try {
                stsOidcResponse = new ObjectMapper().readValue(response.getEntity().getContent(), STSOidcResponse.class);
            } catch (IOException e) {
                throw new IllegalStateException("Klarte ikke deserialisere respons fra STS", e);
            }

            return stsOidcResponse.getAccess_token();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
