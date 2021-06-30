package no.nav.okosynk.consumer.security;

import no.nav.okosynk.config.IOkosynkConfiguration;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * https://doc.nais.io/appendix/zero-trust/index.html
 * https://doc.nais.io/nais-application/application/
 * https://doc.nais.io/security/auth/
 * https://doc.nais.io/security/auth/azure-ad
 * https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow
 * https://github.com/navikt/security-blueprints
 * https://github.com/navikt/security-blueprints/tree/master/examples/service-to-service/daemon-clientcredentials-tokensupport/src/main/java/no/nav/security/examples/tokensupport/clientcredentials
 * https://security.labs.nais.io/
 * https://security.labs.nais.io/pages/flows/oauth2/client_credentials.html
 * https://security.labs.nais.io/pages/guide/api-kall/maskin_til_maskin_uten_bruker.html
 * https://security.labs.nais.io/pages/idp/azure-ad.html#registrere-din-applikasjon-i-azure-ad
 */
public class AzureAdClient {

    private static final Logger logger = LoggerFactory.getLogger(AzureAdClient.class);

    final IOkosynkConfiguration okosynkConfiguration;

    public AzureAdClient(final IOkosynkConfiguration okosynkConfiguration) {
        this.okosynkConfiguration = okosynkConfiguration;

        // TODO: AZURE: Remove when finished developement
        logger.info("***** BEGIN Development info (to be removed when in prod: *****");
        Stream.of("AZURE_APP_CLIENT_ID", "AZURE_APP_WELL_KNOWN_URL")
                .forEach(envVar -> logger.info("{}: {}", envVar, okosynkConfiguration.getString(envVar)));

        final String token = getToken();
        logger.info("Token: {}", token == null ? null : "***<Something>***");

        logger.info("***** END Development info (to be removed when in prod *****");
    }

    private static String getAzureAppClientId(final IOkosynkConfiguration okosynkConfiguration) {
        return okosynkConfiguration.getAzureAppClientId();
    }

    private static String getAzureAppScopes(final IOkosynkConfiguration okosynkConfiguration) {
        return okosynkConfiguration.getAzureAppScopes();
    }

    private static String getAzureAppClientSecret(final IOkosynkConfiguration okosynkConfiguration) {
        return okosynkConfiguration.getAzureAppClientSecret();
    }

    private static String getAzureAppWellKnownUrl(final IOkosynkConfiguration okosynkConfiguration) {
        return okosynkConfiguration.getAzureAppWellKnownUrl();
    }

    private static String getGrantType() {
        return "client_credentials";
    }

    public String getToken() {
        return getTokenUsingClientSecret();
    }

    private String getTokenUsingClientSecret() {
        final String urlString = AzureAdClient.getAzureAppWellKnownUrl(this.okosynkConfiguration); // Preconfigured by NAIS to include the tenant in GUID format
        final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        final HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = new HttpPost(urlString);
        final String parmsBody =
                Stream.of(
                        ImmutablePair.of("client_id", AzureAdClient.getAzureAppClientId(this.okosynkConfiguration)),
                        ImmutablePair.of("client_secret", AzureAdClient.getAzureAppClientSecret(this.okosynkConfiguration)),
                        ImmutablePair.of("scope", AzureAdClient.getAzureAppScopes(okosynkConfiguration)),
                        ImmutablePair.of("grant_type", AzureAdClient.getGrantType())
                )
                        .map(pair -> pair.left + "=" + pair.right)
                        .collect(Collectors.joining("\n&"));

        final BasicHttpEntity httpEntity = new BasicHttpEntity();
        httpEntity.setContent(new ByteArrayInputStream(parmsBody.getBytes()));
        httpEntityEnclosingRequestBase.setEntity(httpEntity);
        httpEntityEnclosingRequestBase.addHeader("Content-Type", "application/x-www-form-urlencoded");

        final CloseableHttpResponse closeableHttpResponse;
        final StatusLine statusLine;
        String azureAdAccessTokenForCurrentServiceUser = null;
        try {
            closeableHttpResponse = closeableHttpClient.execute(httpEntityEnclosingRequestBase);
            statusLine = closeableHttpResponse.getStatusLine();
            logger.error("statusLine.getStatusCode(): {}", statusLine.getStatusCode());
            if (statusLine.getStatusCode() == 200) {
                final HttpEntity responseHttpEntity = closeableHttpResponse.getEntity();
                azureAdAccessTokenForCurrentServiceUser = new BufferedReader(
                        new InputStreamReader(responseHttpEntity.getContent(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                logger.error("azureAdAccessTokenForCurrentServiceUser {}", azureAdAccessTokenForCurrentServiceUser == null ? null : "***<Something>***");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return azureAdAccessTokenForCurrentServiceUser;
    }
}