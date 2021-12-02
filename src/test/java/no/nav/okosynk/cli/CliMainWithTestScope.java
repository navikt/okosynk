package no.nav.okosynk.cli;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_NAV_CALL_ID_KEY;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_NAV_CONSUMER_TOKEN_KEY;
import static no.nav.okosynk.config.Constants.OPPGAVE_URL_KEY;
import static no.nav.okosynk.config.Constants.X_CORRELATION_ID_HEADER_KEY;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public class CliMainWithTestScope extends CliMain {

    private static final Logger logger = LoggerFactory.getLogger(CliMainWithTestScope.class);

    private IStartableAndStoppable testFtpServerStarter = null;
    private Collection<WireMockServer> mockedProviderServers;

    private CliMainWithTestScope(final String applicationPropertiesFileName) {
        super(applicationPropertiesFileName);
    }

    public static void main(final String[] args) throws Exception {
        CliMain.runMain(args, CliMainWithTestScope::new);
    }

    private static Collection<WireMockServer> startMockedProviderServers(final IOkosynkConfiguration okosynkConfiguration) throws MalformedURLException {
        final Collection<WireMockServer> mockedProviderServers = new ArrayList<>();
        mockedProviderServers.add(CliMainWithTestScope.mockPrometheusProviderAndStartIt(okosynkConfiguration));

        mockedProviderServers.add(CliMainWithTestScope.mockAktoerIdProviderAndStartIt(okosynkConfiguration));

        mockedProviderServers.add(CliMainWithTestScope.mockStsProviderAndStartIt(okosynkConfiguration));
        mockedProviderServers.add(CliMainWithTestScope.mockAzureAdProviderAndStartIt(okosynkConfiguration));
        mockedProviderServers.add(CliMainWithTestScope.mockOppgaveProviderAndStartIt(okosynkConfiguration));

        return mockedProviderServers;
    }


// TPS -> PDL
/*
    - name: AKTOERREGISTER_URL
      value: https://app.adeo.no/aktoerregister/api/v1/identer
 */

    // TPS -> PDL
    public static final String REST_AKTOERID_PROVIDER_URL_KEY = "PDL_URL";

    // TPS -> PDL
    private static WireMockServer mockAktoerIdProviderAndStartIt(final IOkosynkConfiguration okosynkConfiguration) throws MalformedURLException {
        final String aktoerIdProviderUrl =
                okosynkConfiguration.getRequiredString(REST_AKTOERID_PROVIDER_URL_KEY);
        final URL url = new URL(aktoerIdProviderUrl);
        final WireMockConfiguration wireMockConfiguration = WireMockConfiguration.wireMockConfig();
        wireMockConfiguration.port(url.getPort());
        wireMockConfiguration.extensions(new ResponseTemplateTransformer(true));
        final WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration);
        wireMockServer.addMockServiceRequestListener(CliMainWithTestScope::logAktoerIdProviderRequest);
        wireMockServer.start();
        CliMainWithTestScope.mockAktoerIdProviderAndStartIt(wireMockServer, okosynkConfiguration);

        return wireMockServer;
    }

    // TPS -> PDL
    private static void logAktoerIdProviderRequest(
            final com.github.tomakehurst.wiremock.http.Request request,
            final com.github.tomakehurst.wiremock.http.Response response) {
        logger.info("*** Mocked *** AktoerIdProvider request absoluteUrl: {}", request.getAbsoluteUrl());
        logger.info("*** Mocked *** AktoerIdProvider request headers: \n{}", request.getHeaders());
        logger.info("*** Mocked *** AktoerIdProvider response body: {}", new String(response.getBody()));
    }

    // TPS -> PDL
    public static final String NAV_PERSONIDENTER = "Nav-Personidenter";

    // TPS -> PDL
    public static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";

    // TPS -> PDL
    private static void mockAktoerIdProviderAndStartIt(final WireMockServer wireMockServer, final IOkosynkConfiguration okosynkConfiguration) {

        // testset_fileName_aktoerRegisterResponseFnrToAktoerId -> testset_fileName_aktoerIdProviderResponseFnrToAktoerId
        final String responseFilename = okosynkConfiguration.getRequiredString("testset_fileName_aktoerIdProviderResponseFnrToAktoerId");
        /*

        return  OK: ->    post(urlPathMatching(PdlRestClientIntegrationTest.PDL_TEST_URL_CONTEXT))
                OK: ->    .withHeader(AUTHORIZATION, matching("Bearer " + PdlRestClientIntegrationTest.TEST_TOKEN))
                OK: ->    .withHeader(HTTP_HEADER_NAV_CONSUMER_TOKEN_KEY, matching("Bearer " + PdlRestClientIntegrationTest.TEST_TOKEN))
                OK: ->    .withHeader(HTTP_HEADER_NAV_CALL_ID_KEY, matching(".*"))
                OK: ->    .withHeader(X_CORRELATION_ID_HEADER_KEY, matching(".*"))
                OK: ->    .withHeader(CONTENT_TYPE, matching(MediaType.APPLICATION_JSON))
                ;
         */
        wireMockServer
                .stubFor(
                        WireMock
/* OK */.post(WireMock.urlEqualTo("/graphql"))
/* OK */.withHeader(HttpHeaders.AUTHORIZATION, containing("Bearer "))
/* OK */.withHeader(HTTP_HEADER_NAV_CONSUMER_TOKEN_KEY, containing("Bearer "))


/* OK */.withHeader(HTTP_HEADER_NAV_CALL_ID_KEY, matching(".*"))
/* OK */.withHeader(X_CORRELATION_ID_HEADER_KEY, matching(".*"))
/* OK */.withHeader(CONTENT_TYPE, matching(MediaType.APPLICATION_JSON))


/* OK */ //                     .withHeader(HTTP_HEADER_NAV_CALL_ID_KEY, matching(".*"))
/* OK */ //                     .withHeader(NAV_PERSONIDENTER, matching(".*"))
/* OK */ //                     .withHeader(NAV_CONSUMER_ID, matching(".*"))
/* OK */ //                     .withHeader(HttpHeaders.ACCEPT, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                                .willReturn(
                                        aResponse()
                                                .withBodyFile(responseFilename)
                                                .withStatus(200)
                                )
                )
        ;
    }


    private static WireMockServer mockPrometheusProviderAndStartIt(final IOkosynkConfiguration okosynkConfiguration) throws MalformedURLException {

        final String pushGatewayEndpointNameAndPort =
                okosynkConfiguration.getPrometheusAddress("http://localhost:5678");

        final String urlAsString =
                (
                        pushGatewayEndpointNameAndPort.startsWith("http://") ||
                                pushGatewayEndpointNameAndPort.startsWith("https://")
                ) ? pushGatewayEndpointNameAndPort : "http://" + pushGatewayEndpointNameAndPort;
        final URL url = new URL(urlAsString);
        final WireMockConfiguration wireMockConfiguration = WireMockConfiguration.wireMockConfig();
        wireMockConfiguration.port(url.getPort());
        final WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration);
        wireMockServer.addMockServiceRequestListener(CliMainWithTestScope::logPrometheusRequest);
        wireMockServer.start();
        CliMainWithTestScope.mockPrometheusProviderAndStartIt(wireMockServer, "/metrics/job/kubernetes-pods/cronjob/bokosynk001");
        CliMainWithTestScope.mockPrometheusProviderAndStartIt(wireMockServer, "/metrics/job/kubernetes-pods/cronjob/bokosynk002");

        return wireMockServer;
    }

    private static void mockPrometheusProviderAndStartIt(final WireMockServer wireMockServer, final String urlContext) {
        wireMockServer
                .stubFor(
                        WireMock
                                .post(WireMock.urlEqualTo(urlContext))
                                .willReturn(
                                        aResponse()
                                                .withStatus(200)
                                )
                )
        ;
    }

    private static void logPrometheusRequest(
            final com.github.tomakehurst.wiremock.http.Request request,
            final com.github.tomakehurst.wiremock.http.Response response) {
        logger.info("*** Mocked *** Prometheus request body: \n{}", request.getBodyAsString());
    }

    private static WireMockServer mockStsProviderAndStartIt(final IOkosynkConfiguration okosynkConfiguration) throws MalformedURLException {
        final String stsUrl = okosynkConfiguration.getRequiredString(Constants.REST_STS_URL_KEY);
        final URL url = new URL(stsUrl);
        final WireMockConfiguration wireMockConfiguration = WireMockConfiguration.wireMockConfig();
        wireMockConfiguration.port(url.getPort());
        final WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration);
        wireMockServer.addMockServiceRequestListener(CliMainWithTestScope::logStsRequest);
        wireMockServer.start();
        CliMainWithTestScope.mockStsProviderAndStartIt(wireMockServer, "/rest/v1/sts/token?grant_type=client_credentials&scope=openid", okosynkConfiguration);

        return wireMockServer;
    }

    private static void mockStsProviderAndStartIt(final WireMockServer wireMockServer, final String urlContext, final IOkosynkConfiguration okosynkConfiguration) {

        final String responseFilename = okosynkConfiguration.getRequiredString("testset_fileName_stsResponse");
        wireMockServer
                .stubFor(
                        WireMock
                                .get(WireMock.urlEqualTo(urlContext))
                                .withQueryParam("grant_type", equalTo("client_credentials"))
                                .withQueryParam("scope", equalTo("openid"))
                                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic "))
                                .withHeader(HttpHeaders.ACCEPT, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                                .willReturn(
                                        aResponse()
                                                .withBodyFile(responseFilename)
                                                .withStatus(200)
                                )
                )
        ;
    }

    private static void logStsRequest(
            final com.github.tomakehurst.wiremock.http.Request request,
            final com.github.tomakehurst.wiremock.http.Response response) {
        logger.info("*** Mocked *** Sts parameter: {}", request.queryParameter("grant_type"));
        logger.info("*** Mocked *** Sts parameter: {}", request.queryParameter("scope"));
        logger.info("*** Mocked *** Sts response body: {}", new String(response.getBody()));
    }

    private static WireMockServer mockAzureAdProviderAndStartIt(final IOkosynkConfiguration okosynkConfiguration) throws MalformedURLException {
         final String azureAdUrl = okosynkConfiguration.getAzureAppTokenUrl();
        final URL url = new URL(azureAdUrl);
        final WireMockConfiguration wireMockConfiguration = WireMockConfiguration.wireMockConfig();
        wireMockConfiguration.port(url.getPort());
        final WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration);
        wireMockServer.addMockServiceRequestListener(CliMainWithTestScope::logAzureAdRequest);
        wireMockServer.start();
        CliMainWithTestScope.mockAzureAdProviderAndStartIt(wireMockServer, "/" + okosynkConfiguration.getRequiredString("AZURE_APP_TENANT_ID") + "/oauth2/v2.0/token", okosynkConfiguration);

        return wireMockServer;
    }

    private static void mockAzureAdProviderAndStartIt(final WireMockServer wireMockServer, final String urlContext, final IOkosynkConfiguration okosynkConfiguration) {

        final String responseFilename = okosynkConfiguration.getRequiredString("testset_fileName_azureAdResponse");
        wireMockServer
                .stubFor(
                        WireMock
                                .post(WireMock.urlEqualTo(urlContext))
                                .withHeader(HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY, equalTo("application/x-www-form-urlencoded"))
                                .willReturn(
                                        aResponse()
                                                .withBodyFile(responseFilename)
                                                .withStatus(200)
                                )
                )
        ;
    }

    private static void logAzureAdRequest(
            final com.github.tomakehurst.wiremock.http.Request request,
            final com.github.tomakehurst.wiremock.http.Response response) {
        logger.info("*** Mocked *** AzureAd body: {}", new String(request.getBody()));
        logger.info("*** Mocked *** AzureAd response body: {}", new String(response.getBody()));
    }

    private static WireMockServer mockOppgaveProviderAndStartIt(final IOkosynkConfiguration okosynkConfiguration) throws MalformedURLException {
        final String oppgaveUrl = okosynkConfiguration.getRequiredString(OPPGAVE_URL_KEY);
        final URL url = new URL(oppgaveUrl);
        final WireMockConfiguration wireMockConfiguration = WireMockConfiguration.wireMockConfig();
        wireMockConfiguration.port(url.getPort());
        wireMockConfiguration.extensions(new ResponseTemplateTransformer(true));
        final WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration);
        wireMockServer.addMockServiceRequestListener(CliMainWithTestScope::logOppgaveRequest);
        wireMockServer.start();
        CliMainWithTestScope.mockOppgaveProviderAndStartIt(wireMockServer, "/api/v1/oppgaver", okosynkConfiguration);

        return wireMockServer;
    }

    private static void mockOppgaveProviderAndStartIt(final WireMockServer wireMockServer, final String urlContext, final IOkosynkConfiguration okosynkConfiguration) {

        final String responseFilename_oppgaveResponseFinnOppgaver = okosynkConfiguration.getRequiredString("testset_fileName_oppgaveResponseFinnOppgaver");
        final String responseFilename_oppgaveResponseOpprettOppgaver = okosynkConfiguration.getRequiredString("testset_fileName_oppgaveResponseOpprettOppgaver");
        final String responseFilename_oppgaveResponsePatchOppgaver = okosynkConfiguration.getRequiredString("testset_fileName_oppgaveResponsePatchOppgaver");

        wireMockServer
                .stubFor(
                        WireMock
                                .get(WireMock.urlMatching(urlContext + "\\?opprettetAv=.*&tema=.*&statuskategori=.*&limit=.*&offset=.*"))
                                .withQueryParam("opprettetAv", matching("srvbokosynk00[12]|okosynk|okosynkos|okosynkur"))
                                .withQueryParam("tema", matching("OKO"))
                                .withQueryParam("statuskategori", matching("AAPEN"))
                                .withQueryParam("limit", matching(".*"))
                                .withQueryParam("offset", matching(".*"))
                                .withHeader(HttpHeaders.ACCEPT, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                                .withHeader(X_CORRELATION_ID_HEADER_KEY, matching(".*"))
                                .withHeader(HttpHeaders.AUTHORIZATION, matching("Basic .*|Bearer .*"))
                                .willReturn(
                                        aResponse()
                                                .withBodyFile(responseFilename_oppgaveResponseFinnOppgaver)
                                                .withStatus(200)
                                )
                )
        ;

        wireMockServer
                .stubFor(
                        WireMock
                                .post(WireMock.urlEqualTo(urlContext))
                                .withHeader(HttpHeaders.ACCEPT, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                                .withHeader(X_CORRELATION_ID_HEADER_KEY, matching(".*"))
                                .withHeader(HttpHeaders.AUTHORIZATION, matching("Basic .*|Bearer .*"))
                                .willReturn(
                                        aResponse()
                                                .withBodyFile(responseFilename_oppgaveResponseOpprettOppgaver)
                                                .withStatus(200)
                                )
                )
        ;

        wireMockServer
                .stubFor(
                        WireMock
                                .patch(WireMock.urlEqualTo(urlContext))
                                .withHeader(HttpHeaders.ACCEPT, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                                .withHeader(X_CORRELATION_ID_HEADER_KEY, matching(".*"))
                                .withHeader(HttpHeaders.AUTHORIZATION, matching("Basic .*|Bearer .*"))
                                .withHeader(CONTENT_TYPE, equalTo("application/json; charset=UTF-8"))
                                .willReturn(
                                        aResponse()
                                                .withBodyFile(responseFilename_oppgaveResponsePatchOppgaver)
                                                .withStatus(200)
                                )
                )
        ;
    }

    private static void logOppgaveRequest(
            final com.github.tomakehurst.wiremock.http.Request request,
            final com.github.tomakehurst.wiremock.http.Response response) {
        logger.info("*** Mocked *** Oppgave request absoluteUrl: {}", request.getAbsoluteUrl());
        logger.info("*** Mocked *** Oppgave request headers: \n{}", request.getHeaders());
        logger.info("*** Mocked *** Oppgave request parameter: {}", request.queryParameter("opprettetAv"));
        logger.info("*** Mocked *** Oppgave request parameter: {}", request.queryParameter("tema"));
        logger.info("*** Mocked *** Oppgave request parameter: {}", request.queryParameter("statuskategori"));
        logger.info("*** Mocked *** Oppgave request parameter: {}", request.queryParameter("limit"));
        logger.info("*** Mocked *** Oppgave request parameter: {}", request.queryParameter("offset"));
        logger.info("*** Mocked *** Oppgave request body: {}", response.getBody() == null ? null : new String(request.getBody()));
        logger.info("*** Mocked *** Oppgave response body: {}", response.getBody() == null ? null : new String(response.getBody()));
    }

    private static void stopMockedProviderServers(final Collection<WireMockServer> mockedProviderServers) {
        mockedProviderServers
                .stream()
                .forEach((mockedProviderServer) -> mockedProviderServer.stop());
    }

    private static IStartableAndStoppable startTestFtpServer(final IOkosynkConfiguration okosynkConfiguration) {

        logger.info("A test FTP server will be started");
        final IStartableAndStoppable testFtpServerStarter =
                new TestFtpServerStarter(okosynkConfiguration);
        testFtpServerStarter.start();

        return testFtpServerStarter;
    }

    private static void stopTestFtpServer(final IStartableAndStoppable testFtpServerStarter) {
        if (testFtpServerStarter != null) {
            testFtpServerStarter.stop();
        }
    }

    @Override
    protected void preRunAllBatches() {
        super.preRunAllBatches();
        try {
            this.mockedProviderServers = CliMainWithTestScope.startMockedProviderServers(createOkosynkConfiguration());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        setTestFtpServerStarter(CliMainWithTestScope.startTestFtpServer(createOkosynkConfiguration()));
    }

    @Override
    protected void postRunAllBatches() {
        CliMainWithTestScope.stopTestFtpServer(this.testFtpServerStarter);
        CliMainWithTestScope.stopMockedProviderServers(this.mockedProviderServers);
        super.postRunAllBatches();
    }

    private void setTestFtpServerStarter(final IStartableAndStoppable testFtpServerStarter) {
        this.testFtpServerStarter = testFtpServerStarter;
    }
}