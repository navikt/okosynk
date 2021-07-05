package no.nav.okosynk.consumer.security;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import java.util.Base64;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OidcStsClientTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private final static String TEST_URL_PROTOCOL = "http";
    private final static int TEST_URL_PORT = 9097;
    private final static String TEST_URL_SERVER = "localhost";
    private final static String TEST_URL_CONTEXT = "/rest/v1/sts/token";

    private final static String TEST_URL_PARM_KEY_1 = "grant_type";
    private final static String TEST_URL_PARM_VALUE_1 = "client_credentials";
    private final static String TEST_URL_PARM_KEY_2 = "scope";
    private final static String TEST_URL_PARM_VALUE_2 = "openid";

    private final static String TEST_URL_PARMS =
            "?"
                    + TEST_URL_PARM_KEY_1 + "=" + TEST_URL_PARM_VALUE_1
                    + "&"
                    + TEST_URL_PARM_KEY_2 + "=" + TEST_URL_PARM_VALUE_2
            ;

    private final static String TEST_CONTEXT_FOR_MATCHING =
            OidcStsClientTest.TEST_URL_CONTEXT
                    + OidcStsClientTest.TEST_URL_PARMS;

    private final static String TEST_NON_EXISTING_CONTEXT_FOR_MATCHING = TEST_URL_CONTEXT + "/NOT/" + TEST_URL_PARMS;
    private final static String TEST_EXPIRED_JSON_TOKEN =
            "{\n  \"sub\"       : \"alice\",\n  \"iss\"       : \"https://openid.c2id.com\",\n  \"aud\"       : \"client-12345\",\n  \"nonce\"     : \"n-0S6_WzA2Mj\",\n  \"auth_time\" : 1311280969,\n  \"acr\"       : \"c2id.loa.hisec\",\n  \"iat\"       : 1311280970,\n  \"exp\"       : 78\n}";
    private final static String TEST_BASE64_ENCODED_AND_TAGGED_EXPIRED_JSON_TOKEN =
            "." + Base64.getEncoder().encodeToString(TEST_EXPIRED_JSON_TOKEN.getBytes()) + ".";
    private final static String TEST_NON_EXPIRED_JSON_TOKEN =
            "{\n  \"sub\"       : \"alice\",\n  \"iss\"       : \"https://openid.c2id.com\",\n  \"aud\"       : \"client-12345\",\n  \"nonce\"     : \"n-0S6_WzA2Mj\",\n  \"auth_time\" : 1311280969,\n  \"acr\"       : \"c2id.loa.hisec\",\n  \"iat\"       : 1311280970,\n  \"exp\"       : " + Integer.MAX_VALUE + "\n}";
    private final static String TEST_BASE64_ENCODED_AND_TAGGED_NON_EXPIRED_JSON_TOKEN =
            "." + Base64.getEncoder().encodeToString(TEST_NON_EXPIRED_JSON_TOKEN.getBytes()) + ".";

    private WireMockServer wireMockServer = null;

    public static void setupStubWithOKResponseEntityAndWithAnInterpretableToken(final WireMockServer wireMockServer) throws JsonProcessingException {

        final StsOidcResponse stsOidcResponse = new StsOidcResponse();

        stsOidcResponse.setAccessToken(OidcStsClientTest.TEST_BASE64_ENCODED_AND_TAGGED_NON_EXPIRED_JSON_TOKEN);
        stsOidcResponse.setExpiresIn(Integer.MAX_VALUE);
        stsOidcResponse.setTokenType("SOME RUBBISH TOKEN TYPE FOR TESTING PURPOSES");
        final String presumableJsonOfSTSOidcResponse = new ObjectMapper().writeValueAsString(stsOidcResponse);

        OidcStsClientTest.setupStub(wireMockServer, OidcStsClientTest.TEST_CONTEXT_FOR_MATCHING, presumableJsonOfSTSOidcResponse);
    }

    public static void configureResourceUrlWithoutParms(
            final String protocol,
            final String server,
            final int    port) {
        final String testUrl = protocol + "://" + server + ":" + port + OidcStsClientTest.TEST_URL_CONTEXT;
        System.setProperty(Constants.REST_STS_URL_KEY, testUrl);
    }

    private static void configureResourceUrlWithoutParms() {
        OidcStsClientTest.configureResourceUrlWithoutParms(
                OidcStsClientTest.TEST_URL_PROTOCOL,
                OidcStsClientTest.TEST_URL_SERVER,
                OidcStsClientTest.TEST_URL_PORT
        );
    }

    private static void setupStub(
            final WireMockServer wireMockServer,
            final String         urlIncludingParms,
            final String         responseBody) {

        wireMockServer
                .stubFor(
                        get(urlEqualTo(urlIncludingParms))
                                .withHeader("Accept", matching("application/json"))
                                .withHeader("Authorization", matching("Basic .*=="))
                                .withQueryParam(OidcStsClientTest.TEST_URL_PARM_KEY_1, new EqualToPattern(OidcStsClientTest.TEST_URL_PARM_VALUE_1))
                                .withQueryParam(OidcStsClientTest.TEST_URL_PARM_KEY_2, new EqualToPattern(OidcStsClientTest.TEST_URL_PARM_VALUE_2))
                                .willReturn(
                                        aResponse()
                                                .withHeader("Content-Type", "text/plain")
                                                .withStatus(200)
                                                .withBody(responseBody)
                                )
                );
    }

    private static void setupStubScenarioForExpiredAndThenNotExpiredAccessToken(
            final WireMockServer wireMockServer,
            final String         urlIncludingParms,
            final String         firstResponseBody,
            final String         secondResponseBody) {

        final String scenarioName = "ExpierdAndThenNotExpiredToken";
        wireMockServer
                .stubFor(
                        get(urlEqualTo(urlIncludingParms))
                                .inScenario(scenarioName)
                                .whenScenarioStateIs(STARTED)
                                .willSetStateTo("2")
                                .withHeader("Accept", matching("application/json"))
                                .withHeader("Authorization", matching("Basic .*=="))
                                .withQueryParam(OidcStsClientTest.TEST_URL_PARM_KEY_1, new EqualToPattern(OidcStsClientTest.TEST_URL_PARM_VALUE_1))
                                .withQueryParam(OidcStsClientTest.TEST_URL_PARM_KEY_2, new EqualToPattern(OidcStsClientTest.TEST_URL_PARM_VALUE_2))
                                .willReturn(
                                        aResponse()
                                                .withHeader("Content-Type", "text/plain")
                                                .withStatus(200)
                                                .withBody(firstResponseBody)
                                )
                );
        wireMockServer
                .stubFor(
                        get(urlEqualTo(urlIncludingParms))
                                .inScenario(scenarioName)
                                .whenScenarioStateIs("2")
                                .withHeader("Accept", matching("application/json"))
                                .withHeader("Authorization", matching("Basic .*=="))
                                .withQueryParam(OidcStsClientTest.TEST_URL_PARM_KEY_1, new EqualToPattern(OidcStsClientTest.TEST_URL_PARM_VALUE_1))
                                .withQueryParam(OidcStsClientTest.TEST_URL_PARM_KEY_2, new EqualToPattern(OidcStsClientTest.TEST_URL_PARM_VALUE_2))
                                .willReturn(
                                        aResponse()
                                                .withHeader("Content-Type", "text/plain")
                                                .withStatus(200)
                                                .withBody(secondResponseBody)
                                )
                );
    }

    @BeforeEach
    void beforeEach() {
        this.wireMockServer = new WireMockServer(OidcStsClientTest.TEST_URL_PORT);
        this.wireMockServer.start();

        OidcStsClientTest.configureResourceUrlWithoutParms();
    }

    @AfterEach
    void afterEach () {
        this.wireMockServer.resetAll();
        this.wireMockServer.stop();
        this.wireMockServer = null;
    }

    @Test
    void when_instantiating_OidcStsClient_and_the_id_provider_returns_some_well_formed_STSOidcResponse_then_the_json_parser_should_not_fail()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithOKResponseEntityAndWithAnInterpretableToken();

        final IOkosynkConfiguration okosynkConfiguration  = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE  batchType             = Constants.BATCH_TYPE.UR;

        assertDoesNotThrow(() -> new OidcStsClient(okosynkConfiguration, batchType));
    }

    @Test
    void when_successfully_instantiating_OidcStsClient_and_the_id_provider_returns_an_ok_response_entity_but_an_uninterpretable_token_then_the_B64_decoder_should_fail_with_npe()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithOKResponseEntityButWithAnUninterpretableToken();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE  batchType            = Constants.BATCH_TYPE.UR;
        final OidcStsClient oidcStsClient                =
                assertDoesNotThrow(() -> new OidcStsClient(okosynkConfiguration, batchType));
        final NullPointerException nullPointerException =
                assertThrows(
                        NullPointerException.class,
                        () -> oidcStsClient.getOidcToken()
                );

        assertNull(nullPointerException.getMessage());
    }

    @Test
    void when_instantiating_OidcStsClient_and_the_id_provider_returns_a_rubbish_response_entity_then_the_json_parser_should_fail() {

        enteringTestHeaderLogger.debug(null);

        setupStubWithRubbishResponseEntity();

        final IOkosynkConfiguration okosynkConfiguration  = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE  batchType             = Constants.BATCH_TYPE.UR;
        final IllegalStateException illegalStateException =
                assertThrows(
                        IllegalStateException.class,
                        () -> new OidcStsClient(okosynkConfiguration, batchType)
                );

        assertNotNull(illegalStateException.getCause());
        assertEquals(IllegalStateException.class, illegalStateException.getCause().getClass());
        assertNotNull(illegalStateException.getCause().getCause());
        assertEquals(JsonParseException.class, illegalStateException.getCause().getCause().getClass());
    }

    @Test
    void when_instantiating_OidcStsClient_and_the_id_provider_returns_an_ok_response_entity_with_a_base64_interpretable_token_then_getOidcToken_should_not_fail()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithOKResponseEntityAndWithAnInterpretableToken();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE  batchType            = Constants.BATCH_TYPE.UR;
        final OidcStsClient oidcStsClient =
                assertDoesNotThrow(() -> new OidcStsClient(okosynkConfiguration, batchType));
        final String actualOidcToken =
                assertDoesNotThrow(() -> oidcStsClient.getOidcToken());

        assertEquals(TEST_BASE64_ENCODED_AND_TAGGED_NON_EXPIRED_JSON_TOKEN, actualOidcToken);
    }

    @Test
    void when_instantiating_an_OidcStsClient_and_the_id_provider_has_an_non_existing_url_then_a_recognizable_exception_should_be_thrown()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithOKResponseEntityAndWithAnInterpretableTokenButWithAnErroneousUrl();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE  batchType            = Constants.BATCH_TYPE.UR;

        final IllegalStateException illegalStateException =
                assertThrows(
                        IllegalStateException.class,
                        () -> new OidcStsClient(okosynkConfiguration, batchType)
                );

        assertNotNull(illegalStateException.getCause());
        assertEquals(IllegalStateException.class, illegalStateException.getCause().getClass());
    }

    @Test
    void when_the_token_is_expired_then_a_new_call_to_the_id_provider_should_be_done()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithOKResponseEntityAndWithAnInterpretableTokenThatIsFirstExpiredAndThenFresh();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE  batchType            = Constants.BATCH_TYPE.UR;

        final OidcStsClient oidcStsClient =
                assertDoesNotThrow(() -> new OidcStsClient(okosynkConfiguration, batchType));

        final OidcStsClient spiedOidcStsClient = Mockito.spy(oidcStsClient);

        // Should bring off a new call to the id provider,
        // as the token first supplied is expired
        final String oidcToken1 = assertDoesNotThrow(() -> spiedOidcStsClient.getOidcToken());
        assertNotNull(oidcToken1);
        final String oidcToken2 = assertDoesNotThrow(() -> spiedOidcStsClient.getOidcToken());
        assertNotNull(oidcToken2);
        assertSame(oidcToken1, oidcToken2);

        verify(spiedOidcStsClient, times(2)).getOidcToken();
        this.wireMockServer.verify(2, getRequestedFor(urlEqualTo(OidcStsClientTest.TEST_CONTEXT_FOR_MATCHING)));
    }

    private void setupStubWithOKResponseEntityAndWithAnInterpretableTokenThatIsFirstExpiredAndThenFresh()
            throws JsonProcessingException {

        final StsOidcResponse stsOidcResponse1 = new StsOidcResponse();
        stsOidcResponse1.setAccessToken(TEST_BASE64_ENCODED_AND_TAGGED_EXPIRED_JSON_TOKEN);
        stsOidcResponse1.setExpiresIn(78);
        stsOidcResponse1.setTokenType("SOME RUBBISH TOKEN TYPE FOR TESTING PURPOSES");
        final String presumableJsonOfFirstSTSOidcResponse = new ObjectMapper().writeValueAsString(stsOidcResponse1);

        final StsOidcResponse stsOidcResponse2 = new StsOidcResponse();
        stsOidcResponse2.setAccessToken(TEST_BASE64_ENCODED_AND_TAGGED_NON_EXPIRED_JSON_TOKEN);
        stsOidcResponse2.setExpiresIn(78);
        stsOidcResponse2.setTokenType("SOME RUBBISH TOKEN TYPE FOR TESTING PURPOSES");
        final String presumableJsonOfSecondSTSOidcResponse = new ObjectMapper().writeValueAsString(stsOidcResponse2);

        setupStubScenarioForExpiredAndThenNotExpiredAccessToken(
                this.wireMockServer,
                TEST_CONTEXT_FOR_MATCHING,
                presumableJsonOfFirstSTSOidcResponse,
                presumableJsonOfSecondSTSOidcResponse);
    }

    private void setupStubWithOKResponseEntityAndWithAnInterpretableTokenButWithAnErroneousUrl() throws JsonProcessingException {

        final StsOidcResponse stsOidcResponse = new StsOidcResponse();

        stsOidcResponse.setAccessToken(TEST_BASE64_ENCODED_AND_TAGGED_NON_EXPIRED_JSON_TOKEN);
        stsOidcResponse.setExpiresIn(78);
        stsOidcResponse.setTokenType("SOME RUBBISH TOKEN TYPE FOR TESTING PURPOSES");
        final String presumableJsonOfSTSOidcResponse = new ObjectMapper().writeValueAsString(stsOidcResponse);

        setupStub(this.wireMockServer, TEST_NON_EXISTING_CONTEXT_FOR_MATCHING, presumableJsonOfSTSOidcResponse);
    }

    private void setupStubWithOKResponseEntityAndWithAnInterpretableToken() throws JsonProcessingException {
        OidcStsClientTest.setupStubWithOKResponseEntityAndWithAnInterpretableToken(this.wireMockServer);
    }

    private void setupStubWithOKResponseEntityButWithAnUninterpretableToken() throws JsonProcessingException {

        final StsOidcResponse stsOidcResponse = new StsOidcResponse();
        stsOidcResponse.setAccessToken("kjnjdfskjnskjnckjsnc");
        stsOidcResponse.setExpiresIn(78);
        stsOidcResponse.setTokenType("SOME RUBBISH TOKEN TYPE FOR TESTING PURPOSES");
        final String presumableJsonOfSTSOidcResponse = new ObjectMapper().writeValueAsString(stsOidcResponse);

        OidcStsClientTest.setupStub(this.wireMockServer, TEST_CONTEXT_FOR_MATCHING, presumableJsonOfSTSOidcResponse);
    }

    private void setupStubWithRubbishResponseEntity() {
        final String presumableJsonOfSTSOidcResponse = "RUBBISH BODY FOR TESTING PURPOSES";
        OidcStsClientTest.setupStub(this.wireMockServer, TEST_CONTEXT_FOR_MATCHING, presumableJsonOfSTSOidcResponse);
    }
}