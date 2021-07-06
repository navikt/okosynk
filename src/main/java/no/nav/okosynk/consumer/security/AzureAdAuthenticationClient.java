package no.nav.okosynk.consumer.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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

    private final static String GRANT_TYPE = "client_credentials";

    final IOkosynkConfiguration okosynkConfiguration;

    public AzureAdAuthenticationClient(final IOkosynkConfiguration okosynkConfiguration) {
        this.okosynkConfiguration = okosynkConfiguration;
        logDevelopmentInfo();
    }

    private static String post(
            final URI httpPostProviderUri,
            final URL httpPostProxyUrl,
            final List<Map.Entry<String, String>> httpPostParameters,
            final List<Map.Entry<String, String>> httpPostHeaders
    ) {
        logger.info("Entering post()...");

        String postResponseEntityAsString = null;
        try {
            // ---------------------------------------------------------------------------------------------------------
            final HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = new HttpPost(httpPostProviderUri);
            // ---------------------------------------------------------------------------------------------------------
            final CloseableHttpClient closeableHttpClient;
            if (httpPostProxyUrl == null) {
                closeableHttpClient = HttpClients.createDefault();
            } else {
                final HttpHost proxyHttpHost =
                        new HttpHost(httpPostProxyUrl.getHost(), httpPostProxyUrl.getPort(), httpPostProxyUrl.getProtocol());
                final DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHttpHost);
                closeableHttpClient = HttpClients.custom().setRoutePlanner(routePlanner).build();
            }
            // ---------------------------------------------------------------------------------------------------------
            final List<NameValuePair> convertedHttpPostParameters =
                    httpPostParameters
                            .stream()
                            .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList());
            httpEntityEnclosingRequestBase.setEntity(new UrlEncodedFormEntity(convertedHttpPostParameters));
            httpPostHeaders
                    .stream()
                    .forEach(
                            httpPostHeader ->
                                    httpEntityEnclosingRequestBase.addHeader(httpPostHeader.getKey(), httpPostHeader.getValue())
                    );
            // ---------------------------------------------------------------------------------------------------------
            final CloseableHttpResponse closeableHttpResponse;
            final StatusLine statusLine;
            logger.info("About to POST provider...");
            closeableHttpResponse = closeableHttpClient.execute(httpEntityEnclosingRequestBase);
            statusLine = closeableHttpResponse.getStatusLine();
            logger.info("statusLine.getStatusCode(): {}", statusLine.getStatusCode());
            if (statusLine.getStatusCode() == 200) {
                final HttpEntity responseHttpEntity = closeableHttpResponse.getEntity();
                postResponseEntityAsString = new BufferedReader(
                        new InputStreamReader(responseHttpEntity.getContent(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                logger.info("postResponseEntityAsString {}", postResponseEntityAsString == null ? null : "***<Something>***");
            }
            // ---------------------------------------------------------------------------------------------------------
        } catch (Throwable e) {
            logger.error("Exception received when doing HTTP post", e);
        } finally {
            logger.info("Leaving post()");
        }

        return postResponseEntityAsString;
    }

    private void logDevelopmentInfo() {
        // TODO: AZURE: Remove when finished developement
        logger.info("***** BEGIN Azure AD Development info (to be removed when in prod: *****");
        logger.info("getSecureHttpProxyUrl: {}", this.okosynkConfiguration.getSecureHttpProxyUrl());
        logger.info("getAzureAppClientId: {}", this.okosynkConfiguration.getAzureAppClientId());
        logger.info("getAzureAppScopes: {}", this.okosynkConfiguration.getAzureAppScopes());
        logger.info("getAzureAppClientSecret: {}", this.okosynkConfiguration.getAzureAppClientSecret() == null ? null : "***<Something>***");
        logger.info("getAzureAppWellKnownUrl: {}", okosynkConfiguration.getAzureAppWellKnownUrl());
        logger.info("getGrantType: {}", AzureAdAuthenticationClient.GRANT_TYPE);
        logger.info("getToken(): {}", getToken() == null ? null : "***<Something>***");
        logger.info("getNaisAppName(): {}", okosynkConfiguration.getNaisAppName() == null ? null : okosynkConfiguration.getNaisAppName());
        logger.info("***** END Azure AD Development info (to be removed when in prod *****");
    }

    public String getToken() {
        logger.info("Entering getToken()...");
        // ---------------------------------------------------------------------------------------------------------

        // AZURE_OPENID_CONFIG_TOKEN_ENDPOINT:  https://login.microsoftonline.com/${AZURE_APP_TENANT_ID}/oauth2/v2.0/token

        final String httpPostProviderUriString =
                this.okosynkConfiguration.getAzureAppWellKnownUrl(); // Preconfigured by NAIS to include the tenant in GUID format

        logger.info("httpPostProviderUriString = {}", httpPostProviderUriString);

        final URI httpPostProviderUri = URI.create(httpPostProviderUriString);
        // ---------------------------------------------------------------------------------------------------------
        final String httpPostProxyUrlString = this.okosynkConfiguration.getSecureHttpProxyUrl();
        final URL httpPostProxyUrl;
        try {
            httpPostProxyUrl = (httpPostProxyUrlString == null ? null : new URL(httpPostProxyUrlString));
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Could not convert proxy URL " + httpPostProxyUrlString + " to URL", e);
        }
        // ---------------------------------------------------------------------------------------------------------
        final List<Map.Entry<String, String>> httpPostParameters = new ArrayList<>();
        httpPostParameters.add(ImmutablePair.of("client_id", okosynkConfiguration.getAzureAppClientId()));
        httpPostParameters.add(ImmutablePair.of("client_secret", okosynkConfiguration.getAzureAppClientSecret()));
        httpPostParameters.add(ImmutablePair.of("scope", okosynkConfiguration.getAzureAppScopes()));
        httpPostParameters.add(ImmutablePair.of("grant_type", AzureAdAuthenticationClient.GRANT_TYPE));
        // ---------------------------------------------------------------------------------------------------------
        final List<Map.Entry<String, String>> httpPostHeaders = new ArrayList<Map.Entry<String, String>>() {{
            add(ImmutablePair.of("Content-Type", "application/x-www-form-urlencoded"));
        }};
        // ---------------------------------------------------------------------------------------------------------
        final String postResponseEntityAsString =
                AzureAdAuthenticationClient.post(httpPostProviderUri, httpPostProxyUrl, httpPostParameters, httpPostHeaders);
        logger.info("postResponseEntityAsString = {}", postResponseEntityAsString);




        final Random random = new Random(10293847);
        final int l = postResponseEntityAsString.length();
        final int start = random.nextInt(l);
        final int end = random.nextInt(l - start) + start;

        final String postResponseEntityAsStringPart = postResponseEntityAsString.substring(start, end);
        logger.info("postResponseEntityAsStringPart = {}", postResponseEntityAsStringPart);
        logger.info("first = {}, l = {}, start = {}, end = {}, the expected string  is present: {}",
                postResponseEntityAsString.substring(0, 10),
                l,
                start,
                end,
                postResponseEntityAsString.contains("access_token"));





        final ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        String token = null;
        try {
            jsonNode = objectMapper.readTree(postResponseEntityAsString);
            token = jsonNode.get("access_token").asText();
            logger.info("theRealToken: {}", token == null ? null : "***<Something wanted, I guess>***");
        } catch (JsonProcessingException e) {
            logger.error("Could not parse token", e);
        } catch (Throwable e) {
            logger.error("Something strange happened when trying to parse the token", e);
        } finally {

        }



        logger.info("Leaving getToken()");
        return token;
    }
}