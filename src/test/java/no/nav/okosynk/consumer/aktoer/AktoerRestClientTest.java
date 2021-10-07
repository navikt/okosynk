package no.nav.okosynk.consumer.aktoer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static no.nav.okosynk.config.Constants.AUTHORIZATION;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_ACCEPT_APPLICATION_JSON_VALUE;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_CONTENT_TYPE_TEXT_PLAIN_VALUE;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_NAV_CALL_ID_KEY;
import static no.nav.okosynk.consumer.aktoer.AktoerRestClient.NAV_CONSUMER_ID;
import static no.nav.okosynk.consumer.aktoer.AktoerRestClient.NAV_PERSONIDENTER;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.security.OidcStsClientTest;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AktoerRestClientTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private final static String TEST_URL_PROTOCOL = "http";
    private final static int TEST_URL_PORT = 9098;
    private final static String TEST_URL_SERVER = "localhost";
    private final static String TEST_URL_CONTEXT = "/x/y/z";

    private final static String TEST_URL_PARM_KEY_1 = "identgruppe";
    private final static String TEST_URL_PARM_VALUE_1 = "AktoerId";
    private final static String TEST_URL_PARM_KEY_2 = "gjeldende";
    private final static String TEST_URL_PARM_VALUE_2 = "true";

    private final static String TEST_URL_PARMS =
            "?"
                    + AktoerRestClientTest.TEST_URL_PARM_KEY_1 + "="
                    + AktoerRestClientTest.TEST_URL_PARM_VALUE_1
                    + "&"
                    + AktoerRestClientTest.TEST_URL_PARM_KEY_2 + "="
                    + AktoerRestClientTest.TEST_URL_PARM_VALUE_2;

    private final static String TEST_CONTEXT_FOR_MATCHING =
            AktoerRestClientTest.TEST_URL_CONTEXT
                    + AktoerRestClientTest.TEST_URL_PARMS;

    private static final String TEST_FNR = "12345678901";
    private static final String TEST_AKTOER_NR = "001199228377446655";
    private static final String TEST_FEILMELDING_FROM_AKTOER_REGISTERET = "Her skjedde det noe drit";

    private WireMockServer wireMockServer = null;

    private static void configureResourceUrlWithoutParms(
            final String protocol,
            final String server,
            final int port) {
        final String testUrl =
                protocol + "://" + server + ":" + port + AktoerRestClientTest.TEST_URL_CONTEXT;
        System.setProperty(Constants.REST_AKTOER_REGISTER_URL_KEY, testUrl);
    }

    private static void configureResourceUrlWithoutParms() {
        AktoerRestClientTest.configureResourceUrlWithoutParms(
                AktoerRestClientTest.TEST_URL_PROTOCOL,
                AktoerRestClientTest.TEST_URL_SERVER,
                AktoerRestClientTest.TEST_URL_PORT
        );
    }

    private static void configureOkSts(final WireMockServer wireMockServer)
            throws JsonProcessingException {
        OidcStsClientTest.configureResourceUrlWithoutParms(
                AktoerRestClientTest.TEST_URL_PROTOCOL,
                AktoerRestClientTest.TEST_URL_SERVER,
                AktoerRestClientTest.TEST_URL_PORT);
        OidcStsClientTest.setupStubWithOKResponseEntityAndWithAnInterpretableToken(wireMockServer);
    }

    private static void setupStub(
            final WireMockServer wireMockServer,
            final String         urlIncludingParms,
            final String         responseBody,
            final int            httpCode
    ) {

        wireMockServer
                .stubFor(
                        get(urlEqualTo(urlIncludingParms))
                                .withHeader(ACCEPT, matching(HTTP_HEADER_ACCEPT_APPLICATION_JSON_VALUE))
                                .withHeader(AUTHORIZATION, matching("Bearer[\\s\\n]*\\.[\\n0-9a-zA-Z]*\\."))
                                .withHeader(HTTP_HEADER_NAV_CALL_ID_KEY, matching(
                                        "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))
                                .withHeader(NAV_PERSONIDENTER, matching("[0-9]{11}"))
                                .withHeader(NAV_CONSUMER_ID, matching("[a-z0-9A-Z]*"))
                                .withQueryParam(AktoerRestClientTest.TEST_URL_PARM_KEY_1,
                                        new EqualToPattern(AktoerRestClientTest.TEST_URL_PARM_VALUE_1))
                                .withQueryParam(AktoerRestClientTest.TEST_URL_PARM_KEY_2,
                                        new EqualToPattern(AktoerRestClientTest.TEST_URL_PARM_VALUE_2))
                                .willReturn(
                                        aResponse()
                                                .withHeader(HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY, HTTP_HEADER_CONTENT_TYPE_TEXT_PLAIN_VALUE)
                                                .withStatus(httpCode)
                                                .withBody(responseBody)
                                )
                );
    }

    @BeforeEach
    void beforeEach() throws JsonProcessingException {
        this.wireMockServer = new WireMockServer(AktoerRestClientTest.TEST_URL_PORT);
        this.wireMockServer.start();
        AktoerRestClientTest.configureOkSts(this.wireMockServer);
        AktoerRestClientTest.configureResourceUrlWithoutParms();
    }

    @AfterEach
    void afterEach() {
        this.wireMockServer.resetAll();
        this.wireMockServer.stop();
        this.wireMockServer = null;
    }

    @Test
    void when_asking_for_an_existing_aktoerid_on_fnr_then_a_correct_aktoerid_should_be_returned()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithOkResponseEntity();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;

        final IAktoerClient aktoerRestClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));
        final AktoerRespons actualAktoerRespons =
                assertDoesNotThrow(() -> aktoerRestClient.hentGjeldendeAktoerId(TEST_FNR));
        assertNull(actualAktoerRespons.getFeilmelding());
        assertNotNull(actualAktoerRespons.getAktoerId());
        assertEquals(TEST_AKTOER_NR, actualAktoerRespons.getAktoerId());
    }

    @Test
    void when_id_provider_is_configured_properly_then_instantiating_an_AktoerRestClient_should_not_fail()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithRubbishResponseEntity();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;
        assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));
    }

    @Test
    void when_aktoer_register_returns_an_unparseable_entity_then_a_relevant_exception_should_be_thrown()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithRubbishResponseEntity();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;
        final IAktoerClient aktoerClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));
        final IllegalStateException illegalStateException =
                assertThrows(IllegalStateException.class,
                        () -> aktoerClient.hentGjeldendeAktoerId("98765412345"));
        assertNotNull(illegalStateException.getCause());
        assertEquals(JsonParseException.class, illegalStateException.getCause().getClass());
    }

    @Test
    void when_asking_for_an_existing_aktoerid_when_url_is_not_configured_then_an_exception_should_be_thrown()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithOkResponseEntity();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;

        // Will provoke an error:
        System.clearProperty(Constants.REST_AKTOER_REGISTER_URL_KEY);

        final IAktoerClient aktoerClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));

        assertThrows(IllegalStateException.class,
                () -> aktoerClient.hentGjeldendeAktoerId(TEST_FNR));
    }

    @Test
    void when_asking_for_an_existing_aktoerid_when_an_erroneous_url_is_configured_then_an_exception_should_be_thrown()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithOkResponseEntity();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;

        // Will provoke an error:
        System.setProperty(Constants.REST_AKTOER_REGISTER_URL_KEY,
                "977yf83hg38yh6/7/-+09763423454vjhbkjn");

        final IAktoerClient aktoerClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));

        final IllegalStateException illegalStateException =
                assertThrows(IllegalStateException.class,
                        () -> aktoerClient.hentGjeldendeAktoerId(TEST_FNR));

        assertNotNull(illegalStateException.getCause());
        assertEquals(ClientProtocolException.class, illegalStateException.getCause().getClass());
        assertNotNull(illegalStateException.getCause().getCause());
        assertEquals(ProtocolException.class, illegalStateException.getCause().getCause().getClass());
    }

    @Test
    void when_asking_for_an_existing_aktoerid_when_an_misformed_url_is_configured_then_an_exception_should_be_thrown()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithOkResponseEntity();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;

        // Will provoke an error:
        System.setProperty(Constants.REST_AKTOER_REGISTER_URL_KEY,
                "99::::\\ht\\tpssss::::/fiiiileeeee::::///:89898999898989898:///////hei//på/deg/?s=7");

        final IAktoerClient aktoerClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));

        final IllegalStateException illegalStateException =
                assertThrows(IllegalStateException.class,
                        () -> aktoerClient.hentGjeldendeAktoerId(TEST_FNR));

        assertNotNull(illegalStateException.getCause());
        assertEquals(URISyntaxException.class, illegalStateException.getCause().getClass());
    }

    @Test
    void when_id_provider_returns_feilmelding_then_the_return_code_of_hentGjeldendeAktoerId_should_reflect_that()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithResponseEntityWithFeilmelding();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;

        final IAktoerClient aktoerClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));
        final AktoerRespons aktoerRespons =
                assertDoesNotThrow(() -> aktoerClient.hentGjeldendeAktoerId(TEST_FNR));

        assertNull(aktoerRespons.getAktoerId());
        assertNotNull(aktoerRespons.getFeilmelding());
        assertTrue(aktoerRespons.getFeilmelding().contains(TEST_FEILMELDING_FROM_AKTOER_REGISTERET));
    }

    @Test
    void when_id_provider_returns_no_identer_by_null_then_the_return_code_of_hentGjeldendeAktoerId_should_reflect_that()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithResponseEntityWithNoIdenter();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;

        final IAktoerClient aktoerClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));
        final AktoerRespons aktoerRespons =
                assertDoesNotThrow(() -> aktoerClient.hentGjeldendeAktoerId(TEST_FNR));

        assertNull(aktoerRespons.getAktoerId());
        assertNotNull(aktoerRespons.getFeilmelding());
    }

    @Test
    void when_id_provider_returns_no_identer_by_empty_then_the_return_code_of_hentGjeldendeAktoerId_should_reflect_that()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithResponseEntityWithEmptyIdenter();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;

        final IAktoerClient aktoerClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));
        final AktoerRespons aktoerRespons =
                assertDoesNotThrow(() -> aktoerClient.hentGjeldendeAktoerId(TEST_FNR));

        assertNull(aktoerRespons.getAktoerId());
        assertNotNull(aktoerRespons.getFeilmelding());
    }

    @Test
    void when_id_provider_returns_one_ident_that_is_not_gjeldende_then_the_return_code_of_hentGjeldendeAktoerId_should_reflect_that()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithResponseEntityWithOneIdentThatIsNotGjeldende();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;

        final IAktoerClient aktoerClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));
        final AktoerRespons aktoerRespons =
                assertDoesNotThrow(() -> aktoerClient.hentGjeldendeAktoerId(TEST_FNR));

        assertNull(aktoerRespons.getAktoerId());
        assertNotNull(aktoerRespons.getFeilmelding());
    }

    @Test
    void when_id_provider_returns_more_than_one_ident_then_the_return_code_of_hentGjeldendeAktoerId_should_reflect_that()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithResponseEntityWithMoreThanOneIdent();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;

        final IAktoerClient aktoerClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));
        final AktoerRespons actualAktoerRespons =
                assertDoesNotThrow(() -> aktoerClient.hentGjeldendeAktoerId(TEST_FNR));
        assertNotNull(actualAktoerRespons.getFeilmelding());
        assertNull(actualAktoerRespons.getAktoerId());
    }

    @Test
    void when_url_is_not_found_or_no_fnr_exists_then_the_return_code_of_hentGjeldendeAktoerId_should_reflect_that()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithResponseEntityWhichReturnsHttpNotFound();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;

        final IAktoerClient aktoerClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));
        final AktoerRespons actualAktoerRespons =
                assertDoesNotThrow(() -> aktoerClient.hentGjeldendeAktoerId(TEST_FNR));
        assertNotNull(actualAktoerRespons.getFeilmelding());
        assertNull(actualAktoerRespons.getAktoerId());
    }

    @Test
    void when_url_internal_error_then_the_return_code_of_hentGjeldendeAktoerId_should_reflect_that()
            throws JsonProcessingException {

        enteringTestHeaderLogger.debug(null);

        setupStubWithResponseEntityWhichReturnsHttpInternal();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final Constants.BATCH_TYPE batchType = Constants.BATCH_TYPE.OS;

        final IAktoerClient aktoerClient =
                assertDoesNotThrow(() -> AktoerRestClientTest.createAktoerClient(okosynkConfiguration, batchType));
        final AktoerRespons actualAktoerRespons =
                assertDoesNotThrow(() -> aktoerClient.hentGjeldendeAktoerId(TEST_FNR));
        assertNotNull(actualAktoerRespons.getFeilmelding());
        assertNull(actualAktoerRespons.getAktoerId());
    }

    private void setupStubWithResponseEntityWhichReturnsHttpInternal() throws JsonProcessingException {

        final String presumableJsonOfAktoerRegisterResponse = "RUBBISH BODY FOR TESTING PURPOSES";
        setupStub(
                this.wireMockServer,
                AktoerRestClientTest.TEST_CONTEXT_FOR_MATCHING,
                presumableJsonOfAktoerRegisterResponse,
                HttpURLConnection.HTTP_INTERNAL_ERROR
        );
    }

    private void setupStubWithResponseEntityWhichReturnsHttpNotFound() throws JsonProcessingException {

        final String presumableJsonOfAktoerRegisterResponse = "RUBBISH BODY FOR TESTING PURPOSES";
        setupStub(
                this.wireMockServer,
                AktoerRestClientTest.TEST_CONTEXT_FOR_MATCHING,
                presumableJsonOfAktoerRegisterResponse,
                HttpURLConnection.HTTP_NOT_FOUND
        );
    }

    private void setupStubWithRubbishResponseEntity() throws JsonProcessingException {
        final String presumableJsonOfAktoerRegisterResponse = "RUBBISH BODY FOR TESTING PURPOSES";
        setupStub(
                this.wireMockServer,
                AktoerRestClientTest.TEST_CONTEXT_FOR_MATCHING,
                presumableJsonOfAktoerRegisterResponse,
                HttpURLConnection.HTTP_OK
        );
    }

    private void setupStubWithResponseEntityWhichReturnsHTTP_NOT_FOUND()
            throws JsonProcessingException {
    }

    private void setupStubWithResponseEntityWithOneIdentThatIsNotGjeldende()
            throws JsonProcessingException {

        final List<AktoerIdentEntry> identer = new ArrayList<>();
        final AktoerIdentEntry aktoerIdentEntry = new AktoerIdentEntry();
        aktoerIdentEntry.setGjeldende(false);
        aktoerIdentEntry.setIdentgruppe("What is an identgruppe??");
        aktoerIdentEntry.setIdent(TEST_AKTOER_NR);
        identer.add(aktoerIdentEntry);
        final AktoerIdent aktoerIdent = new AktoerIdent();
        aktoerIdent.setFeilmelding(null);
        aktoerIdent.setIdenter(identer);

        final ObjectMapper objectMapper = new ObjectMapper();

        final String aktoerIdentAsJson = objectMapper.writeValueAsString(aktoerIdent);

        final String presumableJsonOfAktoerRegisterResponse =
                "{\"" + TEST_FNR + "\": " + aktoerIdentAsJson + "}";

        setupStub(
                this.wireMockServer,
                AktoerRestClientTest.TEST_CONTEXT_FOR_MATCHING,
                presumableJsonOfAktoerRegisterResponse,
                HttpURLConnection.HTTP_OK
        );
    }

    private void setupStubWithResponseEntityWithMoreThanOneIdent()
            throws JsonProcessingException {

        final List<AktoerIdentEntry> identer = new ArrayList<>();

        final AktoerIdentEntry aktoerIdentEntry1 = new AktoerIdentEntry();
        aktoerIdentEntry1.setGjeldende(true);
        aktoerIdentEntry1.setIdentgruppe("What is an identgruppe??");
        aktoerIdentEntry1.setIdent("33");

        identer.add(aktoerIdentEntry1);

        final AktoerIdentEntry aktoerIdentEntry2 = new AktoerIdentEntry();
        aktoerIdentEntry2.setGjeldende(true);
        aktoerIdentEntry2.setIdentgruppe("What is an identgruppe??");
        aktoerIdentEntry1.setIdent("33");

        identer.add(aktoerIdentEntry2);

        final AktoerIdent aktoerIdent = new AktoerIdent();
        aktoerIdent.setFeilmelding(null);
        aktoerIdent.setIdenter(identer);

        final ObjectMapper objectMapper = new ObjectMapper();

        final String aktoerIdentAsJson = objectMapper.writeValueAsString(aktoerIdent);

        final String presumableJsonOfAktoerRegisterResponse =
                "{\"" + TEST_FNR + "\": " + aktoerIdentAsJson + "}";

        setupStub(
                this.wireMockServer,
                AktoerRestClientTest.TEST_CONTEXT_FOR_MATCHING,
                presumableJsonOfAktoerRegisterResponse,
                HttpURLConnection.HTTP_OK
        );
    }

    private void setupStubWithResponseEntityWithEmptyIdenter() throws JsonProcessingException {
        final List<AktoerIdentEntry> identer = new ArrayList<>();
        final AktoerIdent aktoerIdent = new AktoerIdent();
        aktoerIdent.setFeilmelding(null);
        aktoerIdent.setIdenter(identer);

        final ObjectMapper objectMapper = new ObjectMapper();

        final String aktoerIdentAsJson = objectMapper.writeValueAsString(aktoerIdent);

        final String presumableJsonOfAktoerRegisterResponse =
                "{\"" + TEST_FNR + "\": " + aktoerIdentAsJson + "}";

        setupStub(
                this.wireMockServer,
                AktoerRestClientTest.TEST_CONTEXT_FOR_MATCHING,
                presumableJsonOfAktoerRegisterResponse,
                HttpURLConnection.HTTP_OK
        );
    }

    private void setupStubWithResponseEntityWithNoIdenter() throws JsonProcessingException {
        final List<AktoerIdentEntry> identer = null;
        final AktoerIdent aktoerIdent = new AktoerIdent();
        aktoerIdent.setFeilmelding(null);
        aktoerIdent.setIdenter(identer);

        final ObjectMapper objectMapper = new ObjectMapper();

        final String aktoerIdentAsJson = objectMapper.writeValueAsString(aktoerIdent);

        final String presumableJsonOfAktoerRegisterResponse =
                "{\"" + TEST_FNR + "\": " + aktoerIdentAsJson + "}";

        setupStub(
                this.wireMockServer,
                AktoerRestClientTest.TEST_CONTEXT_FOR_MATCHING,
                presumableJsonOfAktoerRegisterResponse,
                HttpURLConnection.HTTP_OK
        );
    }

    private void setupStubWithResponseEntityWithFeilmelding() throws JsonProcessingException {

        final List<AktoerIdentEntry> identer = null;
        final AktoerIdent aktoerIdent = new AktoerIdent();
        aktoerIdent.setFeilmelding(TEST_FEILMELDING_FROM_AKTOER_REGISTERET);
        aktoerIdent.setIdenter(identer);

        final ObjectMapper objectMapper = new ObjectMapper();

        final String aktoerIdentAsJson = objectMapper.writeValueAsString(aktoerIdent);

        final String presumableJsonOfAktoerRegisterResponse =
                "{\"" + TEST_FNR + "\": " + aktoerIdentAsJson + "}";

        setupStub(
                this.wireMockServer,
                AktoerRestClientTest.TEST_CONTEXT_FOR_MATCHING,
                presumableJsonOfAktoerRegisterResponse,
                HttpURLConnection.HTTP_OK
        );
    }

    private void setupStubWithOkResponseEntity() throws JsonProcessingException {
        final List<AktoerIdentEntry> identer = new ArrayList<>();
        final AktoerIdentEntry aktoerIdentEntry = new AktoerIdentEntry();
        aktoerIdentEntry.setGjeldende(true);
        aktoerIdentEntry.setIdentgruppe("What is an identgruppe??");
        aktoerIdentEntry.setIdent(TEST_AKTOER_NR);
        identer.add(aktoerIdentEntry);
        final AktoerIdent aktoerIdent = new AktoerIdent();
        aktoerIdent.setFeilmelding(null);
        aktoerIdent.setIdenter(identer);

        final ObjectMapper objectMapper = new ObjectMapper();

        final String aktoerIdentAsJson = objectMapper.writeValueAsString(aktoerIdent);

        final String presumableJsonOfAktoerRegisterResponse =
                "{\"" + TEST_FNR + "\": " + aktoerIdentAsJson + "}";

        setupStub(
                this.wireMockServer,
                AktoerRestClientTest.TEST_CONTEXT_FOR_MATCHING,
                presumableJsonOfAktoerRegisterResponse,
                HttpURLConnection.HTTP_OK
        );
    }

    private static IAktoerClient createAktoerClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType
    ) {
        return new AktoerRestClient(okosynkConfiguration, batchType);
    }
}