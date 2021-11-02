package no.nav.okosynk.consumer.aktoer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import no.nav.okosynk.config.AbstractOkosynkConfiguration;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.consumer.security.OidcStsClientTest;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static no.nav.okosynk.config.Constants.AUTHORIZATION;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_ACCEPT_APPLICATION_JSON_VALUE;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_CONTENT_TYPE_TEXT_PLAIN_VALUE;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_NAV_CALL_ID_KEY;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_NAV_CONSUMER_TOKEN_KEY;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_X_CORRELATION_ID_KEY;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class PdlRestClientIntegrationTest {

    private static String PDL_TEST_URL_PROTOCOL = "http";
    private static String PDL_TEST_URL_SERVER = "localhost";
    private static final int PDL_TEST_URL_PORT = 9012;
    private static final String PDL_TEST_URL_PROTOCOL_SERVER_AND_PORT = PDL_TEST_URL_PROTOCOL + "://" + PDL_TEST_URL_SERVER + ":" + PDL_TEST_URL_PORT;
    private static final String PDL_TEST_URL_CONTEXT = "/graphql";
    private static final String PDL_TEST_URL = PDL_TEST_URL_PROTOCOL_SERVER_AND_PORT + PDL_TEST_URL_CONTEXT;
    private final static WireMockServer wireMockServer =
            new WireMockServer(new WireMockConfiguration()
                    .port(PDL_TEST_URL_PORT)
                    .extensions(new ResponseTemplateTransformer(true)));

    private static final String TEST_TOKEN = OidcStsClientTest.TEST_BASE64_ENCODED_AND_TAGGED_NON_EXPIRED_JSON_TOKEN;// "---RUBBISH-TOKEN-FOR-TEST---";

    private static final PdlRestClient alwaysThrowingPdlRestClient =
            new PdlRestClient(new FakeOkosynkConfiguration(), Constants.BATCH_TYPE.UR, true);

    private static final PdlRestClient nonAlwaysThrowingPdlRestClient =
            new PdlRestClient(
                    new FakeOkosynkConfiguration() {{

                    }}

                    , Constants.BATCH_TYPE.UR, false);

    private static final PdlRestClient defaultPdlRestClient =
            new PdlRestClient(new FakeOkosynkConfiguration(), Constants.BATCH_TYPE.UR);

    private static String savedPdlUrl;

    private static void configureOkSts(final WireMockServer wireMockServer) throws JsonProcessingException {
        OidcStsClientTest.configureResourceUrlWithoutParms(
                PdlRestClientIntegrationTest.PDL_TEST_URL_PROTOCOL,
                PdlRestClientIntegrationTest.PDL_TEST_URL_SERVER,
                PdlRestClientIntegrationTest.PDL_TEST_URL_PORT);
        OidcStsClientTest.setupStubWithOKResponseEntityAndWithAnInterpretableToken(wireMockServer);
    }

    @BeforeAll
    static void beforeAll() {
        PdlRestClientIntegrationTest.savedPdlUrl = System.getProperty(AbstractOkosynkConfiguration.PDL_URL_KEY);
        System.setProperty(AbstractOkosynkConfiguration.PDL_URL_KEY, PDL_TEST_URL);
        PdlRestClientIntegrationTest.wireMockServer.start();
    }

    @AfterAll
    static void afterAll() {
        PdlRestClientIntegrationTest.wireMockServer.stop();
        if (PdlRestClientIntegrationTest.savedPdlUrl == null) {
            System.clearProperty(AbstractOkosynkConfiguration.PDL_URL_KEY);
        } else {
            System.setProperty(AbstractOkosynkConfiguration.PDL_URL_KEY, PdlRestClientIntegrationTest.savedPdlUrl);
        }
    }

    @BeforeEach
    void beforeEach() throws JsonProcessingException {
        //reset(/*mockedOppgaveConfiguration, mockedOidcStsClient, mockedPdlRedisCache*/);
        PdlRestClientIntegrationTest.wireMockServer.resetAll();
        PdlRestClientIntegrationTest.wireMockServer.start();
        PdlRestClientIntegrationTest.configureOkSts(PdlRestClientIntegrationTest.wireMockServer);
    }

    @AfterEach
    void afterEach() {
    }

    @Test
    void when_instantiated_to_always_throw_then_it_should_throw() {
        assertThrows(NotImplementedException.class, () -> PdlRestClientIntegrationTest.alwaysThrowingPdlRestClient.hentGjeldendeAktoerId("dummy"));
    }

    @Test
    void when_instantiated_not_to_always_throw_then_it_should_not_throw() {

        PdlRestClientIntegrationTest.wireMockServer.stubFor(
                createPostRequestMappingBuilder()
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("pdl_ok_PostHentIdenterResponse_001.json")
                                        .withStatus(200)
                        )
        );

        assertDoesNotThrow(() -> PdlRestClientIntegrationTest.nonAlwaysThrowingPdlRestClient.hentGjeldendeAktoerId("dummy"));
    }

    @Test
    void when_instantiated_as_default_then_it_should_throw() {
        assertThrows(NotImplementedException.class, () -> PdlRestClientIntegrationTest.defaultPdlRestClient.hentGjeldendeAktoerId("dummy"));
    }

    @Test
    void when_hentAktoerId_response_non_parsable_then_an_exception_should_be_thrown() {

        final String expectedFolkeregisterIdent = "78945678911";

        wireMockServer.stubFor(
                createPostRequestMappingBuilder()
                        .willReturn(
                                aResponse()
                                        .withHeader(HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY, "application/json")
                                        .withBody("NON-JSON-RUBBISH")
                                        .withStatus(200)
                        )
        );
        final IllegalStateException actualThrowable =
                assertThrows(
                        IllegalStateException.class,
                        () -> PdlRestClientIntegrationTest
                                .nonAlwaysThrowingPdlRestClient
                                .hentGjeldendeAktoerId(expectedFolkeregisterIdent)
                );

        assertInstanceOf(IllegalStateException.class, actualThrowable);
        assertEquals("Exception received when trying to parse the response", actualThrowable.getMessage());
        assertNotNull(actualThrowable.getCause());
        assertInstanceOf(JsonProcessingException.class, actualThrowable.getCause());
        assertInstanceOf(JsonParseException.class, actualThrowable.getCause());
    }

    private MappingBuilder createPostRequestMappingBuilder() {
        return post(urlPathMatching(PdlRestClientIntegrationTest.PDL_TEST_URL_CONTEXT))
                .withHeader(AUTHORIZATION, matching("Bearer " + PdlRestClientIntegrationTest.TEST_TOKEN))
                .withHeader(HTTP_HEADER_NAV_CONSUMER_TOKEN_KEY, matching("Bearer " + PdlRestClientIntegrationTest.TEST_TOKEN))
                .withHeader(HTTP_HEADER_NAV_CALL_ID_KEY, matching(".*"))
                .withHeader(HTTP_HEADER_X_CORRELATION_ID_KEY, matching(".*"))
                .withHeader(ACCEPT, matching(HTTP_HEADER_ACCEPT_APPLICATION_JSON_VALUE))
                .withHeader(CONTENT_TYPE, matching(HTTP_HEADER_CONTENT_TYPE_TEXT_PLAIN_VALUE))
                ;
    }
}