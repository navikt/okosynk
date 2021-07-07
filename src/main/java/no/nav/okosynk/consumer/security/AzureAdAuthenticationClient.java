package no.nav.okosynk.consumer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
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
    }

    private static Map.Entry<Integer, String> httpPost(
            final URI httpPostProviderUri,
            final URL httpPostProxyUrl,
            final List<Map.Entry<String, String>> httpPostParameters,
            final List<Map.Entry<String, String>> httpPostHeaders
    ) {
        final Map.Entry<Integer, String> postResult;
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
            closeableHttpResponse = closeableHttpClient.execute(httpEntityEnclosingRequestBase);
            statusLine = closeableHttpResponse.getStatusLine();
            final HttpEntity responseHttpEntity = closeableHttpResponse.getEntity();
            final String postResponseEntityAsString = new BufferedReader(
                    new InputStreamReader(responseHttpEntity.getContent(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            // ---------------------------------------------------------------------------------------------------------
            postResult = ImmutablePair.of(statusLine.getStatusCode(), postResponseEntityAsString);
        } catch (Throwable e) {
            throw new IllegalStateException("Exception received when doing HTTP post", e);
        }
        return postResult;
    }

    public String getToken() {
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
        final List<Map.Entry<String, String>> httpPostHeaders = new ArrayList<Map.Entry<String, String>>() {{
            add(ImmutablePair.of("Content-Type", "application/x-www-form-urlencoded"));
        }};
        // ---------------------------------------------------------------------------------------------------------
        final Map.Entry<Integer, String> postResult =
                AzureAdAuthenticationClient.httpPost(httpPostProviderUri, httpPostProxyUrl, httpPostParameters, httpPostHeaders);
        // ---------------------------------------------------------------------------------------------------------
        final String token;
        final int httpStatusCode = postResult.getKey();
        final String postResponseEntityAsString = postResult.getValue();
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        if (HttpStatus.SC_OK == httpStatusCode) {
            final AzureAdTokenSuccessResponseJson azureAdTokenSuccessResponseJson;
            try {
                azureAdTokenSuccessResponseJson =
                        objectMapper.readValue(postResponseEntityAsString, AzureAdTokenSuccessResponseJson.class);
                token = azureAdTokenSuccessResponseJson.getAccessToken();
            } catch (Throwable e) {
                throw new IllegalStateException("Could not parse token", e);
            }
        } else {
            final AzureAdTokenErrorResponseJson azureAdTokenErrorResponseJson;
            try {
                azureAdTokenErrorResponseJson =
                        objectMapper.readValue(postResponseEntityAsString, AzureAdTokenErrorResponseJson.class);
            } catch (Throwable e) {
                throw new IllegalStateException("Something strange happened when trying to parse the token request error. postResponseEntityAsString: " + postResponseEntityAsString, e);
            }
            throw new IllegalStateException("The Azure AD token provider returned an error" + azureAdTokenErrorResponseJson);
        }
        return token;
    }
}