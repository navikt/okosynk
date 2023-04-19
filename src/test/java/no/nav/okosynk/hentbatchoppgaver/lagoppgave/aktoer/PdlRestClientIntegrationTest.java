package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import jakarta.ws.rs.core.MediaType;
import no.nav.okosynk.config.AbstractOkosynkConfiguration;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.synkroniserer.consumer.security.OidcStsClientTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static no.nav.okosynk.config.Constants.AUTHORIZATION;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_NAV_CALL_ID_KEY;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_NAV_CONSUMER_TOKEN_KEY;
import static no.nav.okosynk.config.Constants.X_CORRELATION_ID_HEADER_KEY;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdlRestClientIntegrationTest {

    private static final int PDL_TEST_URL_PORT = 9012;
    private static final String PDL_TEST_URL_CONTEXT = "/graphql";
    private final static WireMockServer wireMockServer =
            new WireMockServer(new WireMockConfiguration()
                    .port(PDL_TEST_URL_PORT)
                    .extensions(new ResponseTemplateTransformer(true)));
    private static final String TEST_TOKEN = OidcStsClientTest.TEST_BASE64_ENCODED_AND_TAGGED_NON_EXPIRED_JSON_TOKEN;// "---RUBBISH-TOKEN-FOR-TEST---";
    private static final PdlRestClient nonAlwaysThrowingPdlRestClient =
            new PdlRestClient(new FakeOkosynkConfiguration() {}, Constants.BATCH_TYPE.UR);
    private static String PDL_TEST_URL_PROTOCOL = "http";
    private static String PDL_TEST_URL_SERVER = "localhost";
    private static final String PDL_TEST_URL_PROTOCOL_SERVER_AND_PORT = PDL_TEST_URL_PROTOCOL + "://" + PDL_TEST_URL_SERVER + ":" + PDL_TEST_URL_PORT;
    private static final String PDL_TEST_URL = PDL_TEST_URL_PROTOCOL_SERVER_AND_PORT + PDL_TEST_URL_CONTEXT;
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
        PdlRestClientIntegrationTest.wireMockServer.resetAll();
        PdlRestClientIntegrationTest.wireMockServer.start();
        PdlRestClientIntegrationTest.configureOkSts(PdlRestClientIntegrationTest.wireMockServer);
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
    void when_instantiated_not_to_prefer_pdl_then_it_should_throw() {

        final PdlRestClient defaultPdlRestClient = new PdlRestClient(new FakeOkosynkConfiguration(), Constants.BATCH_TYPE.UR);

        assertThrows(IllegalStateException.class, () -> defaultPdlRestClient.hentGjeldendeAktoerId("dummy"));
    }

    @Test
    void when_buildHentIdenterEntityAsString_then_it_should_contain_a_string_as_expected() {

        final String ident = "12345678911";
        final String hentIdenterEntityAsString = PdlRestClient.buildHentIdenterEntityAsString(ident);
        assertNotNull(hentIdenterEntityAsString);
        assertFalse(hentIdenterEntityAsString.isEmpty());

        final String stringPart1 = "hentIdenter(ident: $ident, historikk: false)";
        assertTrue(hentIdenterEntityAsString.contains(stringPart1));

        final String stringPart2 = "\"ident\": \"" + ident + "\"";
        assertTrue(hentIdenterEntityAsString.contains(stringPart2));
    }

    @Test
    void when_hentGjeldendeAktoerId_and_not_instantiated_to_always_throw_then_an_exception_should_not_be_thrown() {

        final String expectedFolkeregisterIdent = "78945678911";

        wireMockServer.stubFor(
                createPostRequestMappingBuilder()
                        .willReturn(
                                aResponse()
                                        .withHeader(HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY, "application/json")
                                        .withBodyFile("pdl_ok_PostHentIdenterResponse_001.json")
                                        .withStatus(200)
                        )
        );

        assertDoesNotThrow(() -> nonAlwaysThrowingPdlRestClient.hentGjeldendeAktoerId(expectedFolkeregisterIdent));
    }

    @Test
    void when_hentGjeldendeAktoerId_response_is_parsable_as_ok_response_without_data_but_invalid_as_error_then_an_exception_should_be_thrown() {

        final String npidOrFolkeregisterIdent = "78945678911";

        wireMockServer.stubFor(
                createPostRequestMappingBuilder()
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("pdl_error_error_001.json")
                                        .withStatus(200)
                        )
        );

        final IllegalStateException actualThrowable =
                assertThrows(
                        IllegalStateException.class,
                        () -> PdlRestClientIntegrationTest
                                .nonAlwaysThrowingPdlRestClient
                                .hentGjeldendeAktoerId(npidOrFolkeregisterIdent)
                );
        final String expectedMsg = "Error when calling PDL. Response could not be parsed as an expected error message: {\n    \"data\": {\n        \"hentIdenter\": null\n    }\n}\n".replaceAll("\n", "").replaceAll(" ", "");
        final String actualMsg = actualThrowable.getMessage().replaceAll("\n", "").replaceAll("\r", "").replaceAll(" ", "");
        assertEquals(expectedMsg, actualMsg);
    }

    @Test
    void when_hentGjeldendeAktoerId_response_non_parsable_then_an_exception_should_be_thrown() {

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

    @Test
    void when_hentGjeldendeAktoerId_status_differs_from_200_then_an_exception_should_be_thrown() {

        final String npidOrFolkeregisterIdent = "78945678911";

        wireMockServer.stubFor(
                createPostRequestMappingBuilder()
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("Something went wrong")
                                        .withStatus(HTTP_INTERNAL_ERROR)
                        )
        );
        final IllegalStateException actualThrowable =
                assertThrows(
                        IllegalStateException.class,
                        () -> PdlRestClientIntegrationTest
                                .nonAlwaysThrowingPdlRestClient
                                .hentGjeldendeAktoerId(npidOrFolkeregisterIdent)
                );
        assertEquals("Feil ved kall mot PDL", actualThrowable.getMessage());
    }

    @Test
    void when_hentGjeldendeAktoerId_succeeds_then_correct_value_should_be_retrieved() {

        final String npidOrFolkeregisterIdent = "78945678911";
        final String expectedAktoerId = "3210987654321";

        wireMockServer.stubFor(
                createPostRequestMappingBuilder()
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("pdl_ok_PostHentIdenterResponse_001.json")
                                        .withStatus(200)
                        )
        );
        final AktoerRespons actualAktoerRespons =
                PdlRestClientIntegrationTest.nonAlwaysThrowingPdlRestClient.hentGjeldendeAktoerId(npidOrFolkeregisterIdent);
        assertNotNull(actualAktoerRespons);
        assertNull(actualAktoerRespons.getFeilmelding());
        assertEquals(expectedAktoerId, actualAktoerRespons.getAktoerId());
    }

    @Test
    void when_hentGjeldendeAktoerId_fails_then_an_exception_should_be_thrown() {

        final String npidOrFolkeregisterIdent = "78945678911";

        wireMockServer.stubFor(
                createPostRequestMappingBuilder()
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("pdl_error_PdlErrorResponseJson_001.json")
                                        .withStatus(200)
                        )
        );

        final IllegalStateException actualThrowable =
                assertThrows(
                        IllegalStateException.class,
                        () -> PdlRestClientIntegrationTest
                                .nonAlwaysThrowingPdlRestClient
                                .hentGjeldendeAktoerId(npidOrFolkeregisterIdent)
                );
        assertEquals("Error when calling PDL. pdlErrorResponseJson: PdlErrorResponseJson(errors=[PdlErrorJson(message=Ikke autentisert, locations=[PdlErrorLocationJson(line=1, column=21)], path=[hentIdenter], extensions=PdlErrorExtensionsJson(code=unauthenticated, classification=ExecutionAborted))])", actualThrowable.getMessage());
    }

    @Test
    void when_hentGjeldendeAktoerId_for_non_active_criterion_then_no_exception_should_be_thrown() {

        final String nonActiveIdent = "NON-ACTIVE";

        wireMockServer.stubFor(
                createPostRequestMappingBuilder()
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("pdl_ok_PostHentIdenterResponse_003.json")
                                        .withStatus(200)
                        )
        );
        assertDoesNotThrow(() -> nonAlwaysThrowingPdlRestClient.hentGjeldendeAktoerId(nonActiveIdent));
    }

    @Test
    void when_hentGjeldendeAktoerId_for_non_active_criterion_then_expected_value_should_be_found() {

        final String nonActiveIdent = "NON-ACTIVE";
        final String expectedAktoerId = "1234567890123";

        wireMockServer.stubFor(
                createPostRequestMappingBuilder()
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("pdl_ok_PostHentIdenterResponse_003.json")
                                        .withStatus(200)
                        )
        );

        final AktoerRespons actualAktoerRespons =
                PdlRestClientIntegrationTest.nonAlwaysThrowingPdlRestClient.hentGjeldendeAktoerId(nonActiveIdent);

        assertNotNull(actualAktoerRespons);
        assertNull(actualAktoerRespons.getFeilmelding());
        assertEquals(expectedAktoerId, actualAktoerRespons.getAktoerId());
    }

    private MappingBuilder createPostRequestMappingBuilder() {
        return post(urlPathMatching(PdlRestClientIntegrationTest.PDL_TEST_URL_CONTEXT))
                .withHeader(AUTHORIZATION, matching("Bearer " + PdlRestClientIntegrationTest.TEST_TOKEN))
                .withHeader(HTTP_HEADER_NAV_CONSUMER_TOKEN_KEY, matching("Bearer " + PdlRestClientIntegrationTest.TEST_TOKEN))
                .withHeader(HTTP_HEADER_NAV_CALL_ID_KEY, matching(".*"))
                .withHeader(X_CORRELATION_ID_HEADER_KEY, matching(".*"))
                .withHeader(CONTENT_TYPE, matching(MediaType.APPLICATION_JSON))
                ;
    }
}
