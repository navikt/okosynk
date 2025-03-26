package no.nav.okosynk.comm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.okosynk.config.OkosynkConfiguration;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.*;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY;

public class AzureAdAuthenticationClient {

    private static final Logger logger = LoggerFactory.getLogger(AzureAdAuthenticationClient.class);

    private static final String GRANT_TYPE = "client_credentials";

    final OkosynkConfiguration okosynkConfiguration;

    AzureAdTokenSuccessResponseJson lastAzureAdTokenSuccessResponseJson = null;
    long lastAzureAdTokenSuccessResponseTimestampInMs = Long.MIN_VALUE;

    public AzureAdAuthenticationClient(final OkosynkConfiguration okosynkConfiguration) {
        this.okosynkConfiguration = okosynkConfiguration;
    }

    private static DefaultProxyRoutePlanner proxyRoutePlanner(URL httpPostProxyUrl) {
        final HttpHost proxyHttpHost =
                new HttpHost(httpPostProxyUrl.getHost(), httpPostProxyUrl.getPort(), httpPostProxyUrl.getProtocol());
        return new DefaultProxyRoutePlanner(proxyHttpHost);
    }

    private static Map.Entry<Integer, String> httpPost(
            final URI httpPostProviderUri,
            final URL httpPostProxyUrl,
            final List<Map.Entry<String, String>> httpPostParameters,
            final List<Map.Entry<String, String>> httpPostHeaders
    ) throws IOException {
        final Map.Entry<Integer, String> postResult;
        // ---------------------------------------------------------------------------------------------------------
        final HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = new HttpPost(httpPostProviderUri);
        // ---------------------------------------------------------------------------------------------------------

        try (
                final CloseableHttpClient closeableHttpClient = (httpPostProxyUrl == null) ? HttpClients.createDefault()
                        : HttpClients.custom().setRoutePlanner(proxyRoutePlanner(httpPostProxyUrl)).build()
        ) {
            final List<? extends NameValuePair> convertedHttpPostParameters =
                    httpPostParameters
                            .stream()
                            .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                            .toList();
            httpEntityEnclosingRequestBase.setEntity(new UrlEncodedFormEntity(convertedHttpPostParameters));
            httpPostHeaders
                    .forEach(
                            httpPostHeader ->
                                    httpEntityEnclosingRequestBase.addHeader(httpPostHeader.getKey(), httpPostHeader.getValue())
                    );
            final CloseableHttpResponse closeableHttpResponse;
            final StatusLine statusLine;
            closeableHttpResponse = closeableHttpClient.execute(httpEntityEnclosingRequestBase);
            statusLine = closeableHttpResponse.getStatusLine();
            final HttpEntity responseHttpEntity = closeableHttpResponse.getEntity();
            final String postResponseEntityAsString = new BufferedReader(
                    new InputStreamReader(responseHttpEntity.getContent(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(joining("\n"));
            postResult = ImmutablePair.of(statusLine.getStatusCode(), postResponseEntityAsString);
        }

        return postResult;
    }

    public String getToken() throws IOException {

        final String token;
        if (isLastAzureAdTokenExpired()) {
            // ---------------------------------------------------------------------------------------------------------
            final String httpPostProviderUriString = this.okosynkConfiguration.getAzureAppTokenUrl();
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
            final List<Map.Entry<String, String>> httpPostHeaders = List.of(
                    ImmutablePair.of(HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY, "application/x-www-form-urlencoded"));
            // ---------------------------------------------------------------------------------------------------------
            logger.info("About to acquire an Azure AD access token...");
            final Map.Entry<Integer, String> postResult =
                    AzureAdAuthenticationClient.httpPost(httpPostProviderUri, httpPostProxyUrl, httpPostParameters, httpPostHeaders);
            // ---------------------------------------------------------------------------------------------------------
            final int httpStatusCode = postResult.getKey();
            final String postResponseEntityAsString = postResult.getValue();
            final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            if (HttpStatus.SC_OK == httpStatusCode) {
                final AzureAdTokenSuccessResponseJson azureAdTokenSuccessResponseJson;
                try {
                    azureAdTokenSuccessResponseJson =
                            objectMapper.readValue(postResponseEntityAsString, AzureAdTokenSuccessResponseJson.class);
                    setLastAzureAdTokenSuccessResponseJson(azureAdTokenSuccessResponseJson);
                    token = azureAdTokenSuccessResponseJson.getAccessToken();
                    logger.info("An Azure AD access token successfully acquired");
                } catch (Throwable e) {
                    logger.error("An error occurred while parsing Azure AD token response: {}", e.getMessage());
                    throw new IllegalStateException("Could not parse token", e);
                }
            } else {
                final AzureAdTokenErrorResponseJson azureAdTokenErrorResponseJson;
                try {
                    azureAdTokenErrorResponseJson =
                            objectMapper.readValue(postResponseEntityAsString, AzureAdTokenErrorResponseJson.class);
                } catch (Throwable e) {
                    logger.error("An error occurred while parsing error: {}", e.getMessage());
                    throw new IllegalStateException("Something strange happened when trying to parse the token request error. postResponseEntityAsString: " + postResponseEntityAsString, e);
                }
                logger.error("A {} error occurred while fetching token from azure ad:", httpStatusCode);
                throw new IllegalStateException("The Azure AD token provider returned an error" + azureAdTokenErrorResponseJson);
            }
        } else {
            token = this.lastAzureAdTokenSuccessResponseJson.getAccessToken();
        }
        return token;
    }

    private void setLastAzureAdTokenSuccessResponseJson(final AzureAdTokenSuccessResponseJson azureAdTokenSuccessResponseJson) {
        this.lastAzureAdTokenSuccessResponseJson = azureAdTokenSuccessResponseJson;
        this.lastAzureAdTokenSuccessResponseTimestampInMs = System.currentTimeMillis();
    }

    private boolean isLastAzureAdTokenExpired() {
        final long marginInMs = 30000L;
        return (this.lastAzureAdTokenSuccessResponseJson == null) ||
                ((System.currentTimeMillis() - this.lastAzureAdTokenSuccessResponseTimestampInMs + marginInMs) / 1000) >
                        this.lastAzureAdTokenSuccessResponseJson.getExtExpiresIn();
    }
}
