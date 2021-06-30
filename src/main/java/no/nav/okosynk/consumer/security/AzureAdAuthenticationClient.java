package no.nav.okosynk.consumer.security;

import no.nav.okosynk.config.IOkosynkConfiguration;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
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
public class AzureAdAuthenticationClient {

    private static final Logger logger = LoggerFactory.getLogger(AzureAdAuthenticationClient.class);

    final IOkosynkConfiguration okosynkConfiguration;

    public AzureAdAuthenticationClient(final IOkosynkConfiguration okosynkConfiguration) {
        this.okosynkConfiguration = okosynkConfiguration;
        logDevelopmentInfo();
    }

    private static String getSecureHttpProxyUrl(final IOkosynkConfiguration okosynkConfiguration) {
        return okosynkConfiguration.getSecureHttpProxyUrl();
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

    private void logDevelopmentInfo() {
        // TODO: AZURE: Remove when finished developement
        logger.info("***** BEGIN Azure AD Development info (to be removed when in prod: *****");
        logger.info("getSecureHttpProxyUrl: {}", getSecureHttpProxyUrl(this.okosynkConfiguration));
        logger.info("getAzureAppClientId: {}", getAzureAppClientId(this.okosynkConfiguration));
        logger.info("getAzureAppScopes: {}", getAzureAppScopes(this.okosynkConfiguration));
        logger.info("getAzureAppClientSecret: {}", getAzureAppClientSecret(this.okosynkConfiguration) == null ? null : "***<Something>***");
        logger.info("getAzureAppWellKnownUrl: {}", getAzureAppWellKnownUrl(this.okosynkConfiguration));
        logger.info("getGrantType: {}", getGrantType());
        logger.info("getToken(): {}", getToken() == null ? null : "***<Something>***");
        logger.info("***** END Azure AD Development info (to be removed when in prod *****");
    }

    public String getToken() {
        return getTokenUsingClientSecret();
    }

    private String getTokenUsingClientSecret() {

        logger.info("Entering getTokenUsingClientSecret()...");

        final String secureHttpProxyUrl = getSecureHttpProxyUrl(this.okosynkConfiguration);
        final CloseableHttpClient closeableHttpClient;
        if (secureHttpProxyUrl == null) {
            closeableHttpClient = HttpClients.createDefault();
        } else {
            final HttpHost proxy = new HttpHost(secureHttpProxyUrl);
            final DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            closeableHttpClient = HttpClients.custom()
                    .setRoutePlanner(routePlanner)
                    .build();
        }
        final String urlString = AzureAdAuthenticationClient.getAzureAppWellKnownUrl(this.okosynkConfiguration); // Preconfigured by NAIS to include the tenant in GUID format
        final HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = new HttpPost(urlString);
        final String parmsBody =
                Stream.of(
                        ImmutablePair.of("client_id", AzureAdAuthenticationClient.getAzureAppClientId(this.okosynkConfiguration)),
                        ImmutablePair.of("client_secret", AzureAdAuthenticationClient.getAzureAppClientSecret(this.okosynkConfiguration)),
                        ImmutablePair.of("scope", AzureAdAuthenticationClient.getAzureAppScopes(okosynkConfiguration)),
                        ImmutablePair.of("grant_type", AzureAdAuthenticationClient.getGrantType())
                )
                        .map(pair -> pair.left + "=" + pair.right)
                        .collect(Collectors.joining("&"));

        final BasicHttpEntity httpEntity = new BasicHttpEntity();
        httpEntity.setContent(new ByteArrayInputStream(parmsBody.getBytes()));
        httpEntityEnclosingRequestBase.setEntity(httpEntity);
        httpEntityEnclosingRequestBase.addHeader("Content-Type", "application/x-www-form-urlencoded");

        final CloseableHttpResponse closeableHttpResponse;
        final StatusLine statusLine;
        String azureAdAccessTokenForCurrentServiceUser = null;
        try {
            logger.info("About to call Azure Ad provider...");
            closeableHttpResponse = closeableHttpClient.execute(httpEntityEnclosingRequestBase);
            statusLine = closeableHttpResponse.getStatusLine();
            logger.info("statusLine.getStatusCode(): {}", statusLine.getStatusCode());
            if (statusLine.getStatusCode() == 200) {
                final HttpEntity responseHttpEntity = closeableHttpResponse.getEntity();
                azureAdAccessTokenForCurrentServiceUser = new BufferedReader(
                        new InputStreamReader(responseHttpEntity.getContent(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                logger.error("azureAdAccessTokenForCurrentServiceUser {}", azureAdAccessTokenForCurrentServiceUser == null ? null : "***<Something>***");
            }
        } catch (IOException e) {
            logger.error("Exception received when doing HTTP against Azure Ad provider", e);
        }

        return azureAdAccessTokenForCurrentServiceUser;
    }
}