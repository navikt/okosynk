package no.nav.okosynk.consumer.security;

import no.nav.okosynk.config.IOkosynkConfiguration;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

        String azureAdAccessTokenForCurrentServiceUser = null;
        try {
            final String secureHttpProxyUrlString = getSecureHttpProxyUrl(this.okosynkConfiguration);
            final CloseableHttpClient closeableHttpClient;
            if (secureHttpProxyUrlString == null) {
                closeableHttpClient = HttpClients.createDefault();
            } else {
                final URL proxyUrl = new URL(secureHttpProxyUrlString);
                final HttpHost proxyHttpHost =
                        new HttpHost(proxyUrl.getHost(), proxyUrl.getPort(), proxyUrl.getProtocol());
                logger.info("proxyHttpHost: {}", proxyHttpHost);
                final DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHttpHost);
                closeableHttpClient = HttpClients.custom()
                        .setRoutePlanner(routePlanner)
                        .build();
            }
            final String urlString = AzureAdAuthenticationClient.getAzureAppWellKnownUrl(this.okosynkConfiguration); // Preconfigured by NAIS to include the tenant in GUID format
            final HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = new HttpPost(urlString);






            if (true) {
                final List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("client_id", AzureAdAuthenticationClient.getAzureAppClientId(this.okosynkConfiguration)));
                params.add(new BasicNameValuePair("client_secret", AzureAdAuthenticationClient.getAzureAppClientSecret(this.okosynkConfiguration)));
                params.add(new BasicNameValuePair("scope", AzureAdAuthenticationClient.getAzureAppScopes(okosynkConfiguration)));
                params.add(new BasicNameValuePair("grant_type", AzureAdAuthenticationClient.getGrantType()));
                httpEntityEnclosingRequestBase.setEntity(new UrlEncodedFormEntity(params));
            } else {
                final String parmsBody =
                        Stream.of(
                                ImmutablePair.of("client_id", AzureAdAuthenticationClient.getAzureAppClientId(this.okosynkConfiguration)),
                                ImmutablePair.of("client_secret", AzureAdAuthenticationClient.getAzureAppClientSecret(this.okosynkConfiguration)),
                                ImmutablePair.of("scope", AzureAdAuthenticationClient.getAzureAppScopes(okosynkConfiguration)),
                                ImmutablePair.of("grant_type", AzureAdAuthenticationClient.getGrantType())
                        )
                                .map(pair ->
                                        {
                                            final String key = pair.left;
                                            final String value = pair.right;
                                            final String urlEncodedValue;
                                            try {
                                                urlEncodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
                                            } catch (UnsupportedEncodingException e) {
                                                throw new RuntimeException("Exception when trying to URL encode the parameters for Azure AD authentication", e);
                                            }
                                            return ImmutablePair.of(key, urlEncodedValue);
                                        }
                                )
                                .map(pair -> pair.left + "=" + pair.right)
                                .collect(Collectors.joining("&"));

                final BasicHttpEntity httpEntity = new BasicHttpEntity();
                httpEntity.setContent(new ByteArrayInputStream(parmsBody.getBytes()));
                httpEntityEnclosingRequestBase.setEntity(httpEntity);
            }





            httpEntityEnclosingRequestBase.addHeader("Content-Type", "application/x-www-form-urlencoded");

            final CloseableHttpResponse closeableHttpResponse;
            final StatusLine statusLine;

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
        } catch (Throwable e) {
            logger.error("Exception received when doing HTTP against Azure Ad provider", e);
        }

        return azureAdAccessTokenForCurrentServiceUser;
    }
}