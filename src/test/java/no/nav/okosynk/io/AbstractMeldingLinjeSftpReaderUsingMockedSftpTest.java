package no.nav.okosynk.io;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Common tests for OS/UR FTP tests.
 */
public abstract class AbstractMeldingLinjeSftpReaderUsingMockedSftpTest {

    private static final Logger logger =
            LoggerFactory.getLogger(AbstractMeldingLinjeSftpReaderUsingMockedSftpTest.class);

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");
    private static final String syntacticallyAcceptableFtpHostUri = "ftp://012.123.234.345:32000";
    private static final String syntacticallyAcceptableFtpUser = "somePlaceholderUser";
    private static final String syntacticallyAcceptableFtpPassword = "somePlaceholderPassword";
    private static final String syntacticallyAcceptableFullyQualifiedInputFileName = "/a/somePlaceholderFullyQualifiedInputFileName.txt";
    private static String FTP_HOST_URL_KEY;
    private static String FTP_USER_KEY;
    private static String FTP_PASSWORD_KEY;
    private final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

    protected static void setFtpHostUrlKey(final String ftpHostUrlKey) {
        FTP_HOST_URL_KEY = ftpHostUrlKey;
    }

    protected static void setFtpUserKey(final String ftpUserKey) {
        FTP_USER_KEY = ftpUserKey;
    }

    protected static void setFtpPasswordKey(final String ftpPasswordKey) {
        FTP_PASSWORD_KEY = ftpPasswordKey;
    }

    public IOkosynkConfiguration getOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    @BeforeEach
    void setNecessarySystemProperties() {

        this.okosynkConfiguration
                .setSystemProperty(FTP_HOST_URL_KEY, syntacticallyAcceptableFtpHostUri);
        this.okosynkConfiguration
                .setSystemProperty(FTP_USER_KEY, syntacticallyAcceptableFtpUser);
        this.okosynkConfiguration
                .setSystemProperty(FTP_PASSWORD_KEY, syntacticallyAcceptableFtpPassword);
        this.okosynkConfiguration
                .setSystemProperty(Constants.BATCH_TYPE.OS.getFtpCharsetNameKey(), "ISO8859_1");
        this.okosynkConfiguration
                .setSystemProperty(Constants.BATCH_TYPE.UR.getFtpCharsetNameKey(), "ISO8859_1");
    }

    @Test
    void when_fully_qualified_input_file_name_is_null_then_an_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        Assertions.assertThrows(
                NullPointerException.class,
                () -> getMeldingLinjeFileReaderCreator().apply(null)
        );
    }

    @Test
    void when_fully_qualified_input_file_name_is_empty_then_an_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final String[] fullyQualifiedInputFileNames = new String[]{"", " ", "  "};

        for (final String fullyQualifiedInputFileName : fullyQualifiedInputFileNames) {

            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> getMeldingLinjeFileReaderCreator().apply(fullyQualifiedInputFileName)
            );
        }
    }

    @Test
    void when_ftp_url_is_or_is_not_valid_then_it_should_or_should_not_be_possible_to_parse_out_the_correct_protocol()
            throws AbstractOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        // Just to get the AbstractMeldingLinjeFtpOrSftpReader not to throw:
        final String ftpHostUrlKey = FTP_HOST_URL_KEY;
        this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, "ftp://ser:733/con");
        final MeldingLinjeSftpReader meldingLinjeFtpOrSftpReader =
                (MeldingLinjeSftpReader) getMeldingLinjeFileReaderCreator()
                        .apply("somethingDummy");

        final Collection<Triplet<String, Constants.FTP_PROTOCOL, Class<? extends Exception>>> testData =
                new ArrayList<Triplet<String, Constants.FTP_PROTOCOL, Class<? extends Exception>>>() {{
                    add(new Triplet<>(null, null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>(" ", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("  ", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("pro://ser:por/con", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("pro://ser:por", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("pro://ser", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("ser:por", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("pro://ser:377", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("ser", Constants.FTP_PROTOCOL_DEFAULT_VALUE, null));
                    add(new Triplet<>("ser:377", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("http://ser:377", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("ftp://ser:377", Constants.FTP_PROTOCOL.FTP, null));
                    add(new Triplet<>("ftp://ser:377/", Constants.FTP_PROTOCOL.FTP, null));
                    add(new Triplet<>("ftp://ser:377/a", Constants.FTP_PROTOCOL.FTP, null));
                    add(new Triplet<>("ftp://ser:377/a/b", Constants.FTP_PROTOCOL.FTP, null));
                    add(new Triplet<>("ftp://ser:377/a/b.txt", Constants.FTP_PROTOCOL.FTP, null));
                    add(new Triplet<>("sftp://ser:377", Constants.FTP_PROTOCOL.SFTP, null));
                    add(new Triplet<>("sftp://ser:377/", Constants.FTP_PROTOCOL.SFTP, null));
                    add(new Triplet<>("sftp://ser:377/a", Constants.FTP_PROTOCOL.SFTP, null));
                    add(new Triplet<>("sftp://ser:377/a/b", Constants.FTP_PROTOCOL.SFTP, null));
                    add(new Triplet<>("sftp://ser:377/a/b.txt", Constants.FTP_PROTOCOL.SFTP, null));
                    add(new Triplet<>("sftp://876.765.654.543:377/a/b.txt", Constants.FTP_PROTOCOL.SFTP,
                            null));
                    add(new Triplet<>("sftp://a.543:377/a/b.txt", Constants.FTP_PROTOCOL.SFTP, null));
                    add(new Triplet<>("sftp://543.b:377/a/b.txt", Constants.FTP_PROTOCOL.SFTP, null));
                    add(new Triplet<>("localhost", Constants.FTP_PROTOCOL.SFTP, null));
                    add(new Triplet<>("Focalhust", Constants.FTP_PROTOCOL.SFTP, null));
                    add(new Triplet<>("ftp://155.55.1.78:21/P230.K230M131.UTPL(0)",
                            Constants.FTP_PROTOCOL.FTP, null));
                }};

        for (final Triplet<String, Constants.FTP_PROTOCOL, Class<? extends Exception>> testDatum : testData) {

            final String ftpHostUrl = testDatum.getValue0();
            final Constants.FTP_PROTOCOL expectedFtpProtocol1 = testDatum.getValue1();
            final Class<? extends Exception> expectedExceptionClass = testDatum.getValue2();

            if (ftpHostUrl == null) {
                this.okosynkConfiguration.clearSystemProperty(ftpHostUrlKey);
            } else {
                this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, ftpHostUrl);
            }

            final URI uri;
            try {
                logger.debug("ftpHostUrl: " + ftpHostUrl);
                uri = new URI(ftpHostUrl);
            } catch (Throwable e) {
                final String msg = "ftpHostUrl: " + ftpHostUrl;
                logger.debug(msg, e);
                assertThrows(
                        expectedExceptionClass,
                        () -> meldingLinjeFtpOrSftpReader.getFtpProtocol(okosynkConfiguration),
                        msg
                );
                continue;
            }

            if (expectedExceptionClass == null) {
                final Constants.FTP_PROTOCOL actualFtpProtocol =
                        meldingLinjeFtpOrSftpReader.getFtpProtocol(okosynkConfiguration);

                final Constants.FTP_PROTOCOL expectedFtpProtocol2 =
                        (uri.getScheme() == null)
                                ?
                                Constants.FTP_PROTOCOL_DEFAULT_VALUE
                                :
                                Constants.FTP_PROTOCOL.valueOf(uri.getScheme().toUpperCase());

                assertEquals(expectedFtpProtocol1, actualFtpProtocol);
                assertEquals(expectedFtpProtocol2, actualFtpProtocol);
            } else {
                assertThrows(
                        expectedExceptionClass,
                        () -> meldingLinjeFtpOrSftpReader.getFtpProtocol(okosynkConfiguration),
                        "ftpHostUrl: " + ftpHostUrl
                );
            }
        }
    }

    @Test
    void when_ftp_url_is_or_is_not_valid_then_it_should_or_should_not_be_possible_to_parse_out_the_correct_host_name()
            throws AbstractOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        // Just to get the AbstractMeldingLinjeFtpOrSftpReader not to throw:
        final String ftpHostUrlKey = FTP_HOST_URL_KEY;
        this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, "ftp://ser:733/con");

        final MeldingLinjeSftpReader meldingLinjeFtpOrSftpReader =
                (MeldingLinjeSftpReader) getMeldingLinjeFileReaderCreator()
                        .apply("somethingDummy");

        final Collection<Triplet<String, String, Class<? extends Exception>>> testData =
                new ArrayList<Triplet<String, String, Class<? extends Exception>>>() {{
                    add(new Triplet<>(null, null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>(" ", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("  ", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("pro://ser:por/con", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("pro://ser:por", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("pro://ser", "ser", null));
                    add(new Triplet<>("ser:por", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("pro://ser:377", "ser", null));
                    add(new Triplet<>("ser", "ser", null));
                    add(new Triplet<>("ser:377", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("http://ser:377", "ser", null));
                    add(new Triplet<>("ftp://ser:377", "ser", null));
                    add(new Triplet<>("ftp://ser:377/", "ser", null));
                    add(new Triplet<>("ftp://ser:377/a", "ser", null));
                    add(new Triplet<>("ftp://ser:377/a/b", "ser", null));
                    add(new Triplet<>("ftp://ser:377/a/b.txt", "ser", null));
                    add(new Triplet<>("sftp://ser:377", "ser", null));
                    add(new Triplet<>("sftp://ser:377/", "ser", null));
                    add(new Triplet<>("sftp://ser:377/a", "ser", null));
                    add(new Triplet<>("sftp://ser:377/a/b", "ser", null));
                    add(new Triplet<>("sftp://ser:377/a/b.txt", "ser", null));
                    add(new Triplet<>("sftp://876.765.654.543:377/a/b.txt", "876.765.654.543", null));
                    add(new Triplet<>("sftp://a.543:377/a/b.txt", "a.543", null));
                    add(new Triplet<>("sftp://543.b:377/a/b.txt", "543.b", null));
                    add(new Triplet<>("localhost", "localhost", null));
                    add(new Triplet<>("Focalhust", "Focalhust", null));
                    add(new Triplet<>("ftp://155.55.1.78:21/P230.K230M131.UTPL(0)", "155.55.1.78", null));
                }};

        for (final Triplet<String, String, Class<? extends Exception>> testDatum : testData) {

            final String ftpHostUrl = testDatum.getValue0();
            final String expectedFtpHostServerName1 = testDatum.getValue1();
            final Class<? extends Exception> expectedExceptionClass = testDatum.getValue2();

            if (ftpHostUrl == null) {
                this.okosynkConfiguration.clearSystemProperty(ftpHostUrlKey);
            } else {
                this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, ftpHostUrl);
            }

            try {
                logger.debug("ftpHostUrl: " + ftpHostUrl);
                new URI(ftpHostUrl);
            } catch (Throwable e) {
                final String msg = "ftpHostUrl: " + ftpHostUrl;
                logger.debug(msg, e);
                assertThrows(
                        expectedExceptionClass,
                        () -> meldingLinjeFtpOrSftpReader.getFtpHostServerName(okosynkConfiguration),
                        msg
                );
                continue;
            }

            if (expectedExceptionClass == null) {

                final String actualFtpHostServerName =
                        meldingLinjeFtpOrSftpReader.getFtpHostServerName(okosynkConfiguration);

                assertEquals(expectedFtpHostServerName1, actualFtpHostServerName);
            } else {
                assertThrows(
                        expectedExceptionClass,
                        () -> meldingLinjeFtpOrSftpReader.getFtpHostServerName(okosynkConfiguration),
                        "ftpHostUrl: " + ftpHostUrl
                );
            }
        }
    }

    @Test
    void when_ftp_url_is_or_is_not_valid_then_it_should_or_should_not_be_possible_to_parse_out_the_correct_port_number()
            throws AbstractOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        // Just to get the AbstractMeldingLinjeFtpOrSftpReader not to throw:
        final String ftpHostUrlKey = FTP_HOST_URL_KEY;
        this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, "ftp://ser:733/con");

        final MeldingLinjeSftpReader meldingLinjeFtpOrSftpReader =
                (MeldingLinjeSftpReader) getMeldingLinjeFileReaderCreator()
                        .apply("somethingDummy");

        final Collection<Triplet<String, Integer, Class<? extends Exception>>> testData =
                new ArrayList<Triplet<String, Integer, Class<? extends Exception>>>() {{
                    add(new Triplet<>(null, null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>(" ", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("  ", null, AbstractOkosynkIoException.class));
                    add(new Triplet<>("pro://ser:por/con", Constants.FTP_HOST_PORT_DEFAULT_VALUE, null));
                    add(new Triplet<>("pro://ser:por", Constants.FTP_HOST_PORT_DEFAULT_VALUE, null));
                    add(new Triplet<>("pro://ser", Constants.FTP_HOST_PORT_DEFAULT_VALUE, null));
                    add(new Triplet<>("ser:por", Constants.FTP_HOST_PORT_DEFAULT_VALUE, null));
                    add(new Triplet<>("pro://ser:377", 377, null));
                    add(new Triplet<>("ser", Constants.FTP_HOST_PORT_DEFAULT_VALUE, null));
                    add(new Triplet<>("ser:377", Constants.FTP_HOST_PORT_DEFAULT_VALUE, null));
                    add(new Triplet<>("http://ser:377", 377, null));
                    add(new Triplet<>("ftp://ser:377", 377, null));
                    add(new Triplet<>("ftp://ser:377/", 377, null));
                    add(new Triplet<>("ftp://ser:377/a", 377, null));
                    add(new Triplet<>("ftp://ser:377/a/b", 377, null));
                    add(new Triplet<>("ftp://ser:377/a/b.txt", 377, null));
                    add(new Triplet<>("sftp://ser:377", 377, null));
                    add(new Triplet<>("sftp://ser:377/", 377, null));
                    add(new Triplet<>("sftp://ser:377/a", 377, null));
                    add(new Triplet<>("sftp://ser:377/a/b", 377, null));
                    add(new Triplet<>("sftp://ser:377/a/b.txt", 377, null));
                    add(new Triplet<>("sftp://123.234.345.456:21013/a/b.txt", 21013, null));
                    add(new Triplet<>("sftp://a.456:21013/a/b.txt", 21013, null));
                    add(new Triplet<>("sftp://456.b:21013/a/b.txt", 21013, null));
                    add(new Triplet<>("localhost", Constants.FTP_HOST_PORT_DEFAULT_VALUE, null));
                    add(new Triplet<>("Focalhust", Constants.FTP_HOST_PORT_DEFAULT_VALUE, null));
                    add(new Triplet<>("ftp://155.55.1.78:21/P230.K230M131.UTPL(0)", 21, null));
                }};

        for (final Triplet<String, Integer, Class<? extends Exception>> testDatum : testData) {

            final String ftpHostUrl = testDatum.getValue0();
            final Integer expectedFtpHostPort1 = testDatum.getValue1();
            final Class<? extends Exception> expectedExceptionClass = testDatum.getValue2();

            if (ftpHostUrl == null) {
                this.okosynkConfiguration.clearSystemProperty(ftpHostUrlKey);
            } else {
                this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, ftpHostUrl);
            }

            try {
                logger.debug("ftpHostUrl: " + ftpHostUrl);
                new URI(ftpHostUrl);
            } catch (Throwable e) {
                final String msg = "ftpHostUrl: " + ftpHostUrl;
                logger.debug(msg, e);
                assertThrows(
                        expectedExceptionClass,
                        () -> meldingLinjeFtpOrSftpReader.getFtpHostPort(okosynkConfiguration),
                        msg
                );
                continue;
            }

            if (expectedExceptionClass == null) {

                final int actualFtpHostPort =
                        meldingLinjeFtpOrSftpReader.getFtpHostPort(okosynkConfiguration);

                assertEquals(expectedFtpHostPort1, actualFtpHostPort);
            } else {
                assertThrows(
                        expectedExceptionClass,
                        () -> meldingLinjeFtpOrSftpReader.getFtpHostPort(okosynkConfiguration),
                        "ftpHostUrl: " + ftpHostUrl
                );
            }
        }
    }

    @Test
    void when_enough_or_not_enough_system_properties_are_set_then_it_should_or_should_not_be_possible_to_create_a_xxMeldingLinjeFtpReader() {

        enteringTestHeaderLogger.debug(null);

        final List<Quartet<String, String, String, Boolean>> testData =
                new ArrayList<Quartet<String, String, String, Boolean>>() {{

                    add(new Quartet<>(null, syntacticallyAcceptableFtpUser,
                            syntacticallyAcceptableFtpPassword, true));
                    add(new Quartet<>("", syntacticallyAcceptableFtpUser, syntacticallyAcceptableFtpPassword,
                            true));
                    add(new Quartet<>(" ", syntacticallyAcceptableFtpUser, syntacticallyAcceptableFtpPassword,
                            true));
                    add(new Quartet<>(syntacticallyAcceptableFtpHostUri, null,
                            syntacticallyAcceptableFtpPassword, true));
                    add(new Quartet<>(syntacticallyAcceptableFtpHostUri, "",
                            syntacticallyAcceptableFtpPassword, true));
                    add(new Quartet<>(syntacticallyAcceptableFtpHostUri, " ",
                            syntacticallyAcceptableFtpPassword, true));
                    add(new Quartet<>(syntacticallyAcceptableFtpHostUri, syntacticallyAcceptableFtpUser, null,
                            true));
                    add(new Quartet<>(syntacticallyAcceptableFtpHostUri, syntacticallyAcceptableFtpUser, "",
                            true));
                    add(new Quartet<>(syntacticallyAcceptableFtpHostUri, syntacticallyAcceptableFtpUser, " ",
                            true));
                    add(new Quartet<>(syntacticallyAcceptableFtpHostUri, syntacticallyAcceptableFtpUser,
                            syntacticallyAcceptableFtpPassword, false));
                }};

        for (final Quartet<String, String, String, Boolean> testDatum : testData) {

            final String sftpHostUri = testDatum.getValue0();
            final String sftpUser = testDatum.getValue1();
            final String sftpPassword = testDatum.getValue2();
            final Boolean shouldThrow = testDatum.getValue3();

            final String testDetails =
                    System.lineSeparator()
                            + "Test details" + System.lineSeparator()
                            + "==============" + System.lineSeparator()
                            + "sftpHostUri  : " + sftpHostUri + System.lineSeparator()
                            + "sftpUser     : " + sftpUser + System.lineSeparator()
                            + "sftpPassword : " + sftpPassword + System.lineSeparator()
                            + "shouldThrow  : " + shouldThrow + System.lineSeparator()
                            + System.lineSeparator();

            if (sftpHostUri == null) {
                this.okosynkConfiguration.clearSystemProperty(FTP_HOST_URL_KEY);
            } else {
                this.okosynkConfiguration.setSystemProperty(FTP_HOST_URL_KEY, sftpHostUri);
            }
            if (sftpUser == null) {
                this.okosynkConfiguration.clearSystemProperty(FTP_USER_KEY);
            } else {
                this.okosynkConfiguration.setSystemProperty(FTP_USER_KEY, sftpUser);
            }
            if (sftpPassword == null) {
                this.okosynkConfiguration.clearSystemProperty(FTP_PASSWORD_KEY);
            } else {
                this.okosynkConfiguration.setSystemProperty(FTP_PASSWORD_KEY, sftpPassword);
            }

            if (shouldThrow) {
                assertThrows(
                        IllegalArgumentException.class,
                        () -> getMeldingLinjeFileReaderCreator()
                                .apply(syntacticallyAcceptableFullyQualifiedInputFileName),
                        "Enough system properties not set. Creation of <OS/UR>MeldingLinjeFtpReader should have thrown."
                                + System.lineSeparator() + testDetails
                );
            } else {
                try {
                    getMeldingLinjeFileReaderCreator()
                            .apply(syntacticallyAcceptableFullyQualifiedInputFileName);
                    // OK: Should not throw
                } catch (Throwable e) {
                    Assertions.fail(("Unexpected exception received: " + e.getMessage()));
                }
            }
        }
    }

    @Test
    void when_connect_fails_then_a_correct_OkosynkIoException_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        assertThrows(ConfigureOrInitializeOkosynkIoException.class, uspesifikkMeldingLinjeFtpReader::read);
    }

    @Test
    void when_trying_to_create_meldingLinjeFileReader_with_null_filename_then_an_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final Function<String, IMeldingLinjeFileReader> meldingLinjeFileReaderCreator =
                getMeldingLinjeFileReaderCreator();

        assertThrows(NullPointerException.class, () -> meldingLinjeFileReaderCreator.apply(null));
    }

    @Disabled
    @Test
    void when_error_connecting_ftp_then_retries_should_result_in_giving_up_with_an_io_exception() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        final IoOkosynkIoException okosynkIoException =
                Assertions.assertThrows(IoOkosynkIoException.class, uspesifikkMeldingLinjeFtpReader::read);

        assertEquals(AbstractOkosynkIoException.class, okosynkIoException.getCause().getClass());
    }

    @Disabled
    @Test
    void when_logging_in_throws_an_exception_then_an_OkosynkIoException_authentication_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        final AuthenticationOkosynkIoException okosynkIoException =
                Assertions.assertThrows(AuthenticationOkosynkIoException.class, uspesifikkMeldingLinjeFtpReader::read);
    }

    @Disabled
    @Test
    void when_logging_in_returns_failure_then_an_OkosynkIoException_authentication_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        final AuthenticationOkosynkIoException okosynkIoException =
                Assertions.assertThrows(AuthenticationOkosynkIoException.class, uspesifikkMeldingLinjeFtpReader::read);
    }

    @Disabled
    @Test
    void when_retrieving_a_null_input_stream_from_the_ftp_client_then_an_OkosynkIoException_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        final IoOkosynkIoException okosynkIoException =
                Assertions.assertThrows(IoOkosynkIoException.class, uspesifikkMeldingLinjeFtpReader::read);
    }

    @Disabled
    @Test
    void when_closing_the_ftp_client_input_stream_throws_an_exception_then_read_should_not_throw_an_exception() throws IOException {

        enteringTestHeaderLogger.debug(null);

        final InputStream mockedInputStream = mock(InputStream.class);
        when(mockedInputStream.read()).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(-1);
        Mockito.doThrow(IOException.class).when(mockedInputStream).close();

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        assertDoesNotThrow(() -> uspesifikkMeldingLinjeFtpReader.read());
    }

    @Disabled
    @Test
    void when_retrieving_an_input_stream_from_the_ftp_client_throws_an_exception_then_an_OkosynkIoException_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        final IoOkosynkIoException okosynkIoException =
                Assertions.assertThrows(IoOkosynkIoException.class, uspesifikkMeldingLinjeFtpReader::read);
    }

    @Disabled
    @Test
    void when_ftp_client_complete_pending_command_throws_an_exception_then_read_should_not_throw_an_exception() throws IOException {

        enteringTestHeaderLogger.debug(null);

        final InputStream mockedInputStream = mock(InputStream.class);
        when(mockedInputStream.read()).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(-1);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        assertDoesNotThrow(() -> uspesifikkMeldingLinjeFtpReader.read());
    }

    @Disabled
    @Test
    void when_ftp_client_complete_pending_command_returns_an_error_then_read_should_not_throw_an_exception() throws IOException {

        enteringTestHeaderLogger.debug(null);

        final InputStream mockedInputStream = mock(InputStream.class);
        when(mockedInputStream.read()).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(-1);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        assertDoesNotThrow(() -> uspesifikkMeldingLinjeFtpReader.read());
    }

    @Disabled
    @Test
    void when_logging_out_from_ftp_returns_failure_then_read_should_not_throw_an_exception()
            throws IOException {

        enteringTestHeaderLogger.debug(null);

        final InputStream mockedInputStream = mock(InputStream.class);
        when(mockedInputStream.read()).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(-1);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        assertDoesNotThrow(() -> uspesifikkMeldingLinjeFtpReader.read());
    }

    @Disabled
    @Test
    void when_logging_out_from_ftp_throws_an_exception_then_read_should_not_throw_an_exception()
            throws IOException {

        enteringTestHeaderLogger.debug(null);

        final InputStream mockedInputStream = mock(InputStream.class);
        when(mockedInputStream.read()).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(-1);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        assertDoesNotThrow(() -> uspesifikkMeldingLinjeFtpReader.read());
    }

    @Disabled
    @Test
    void when_disconnecting_from_an_unconnected_ftp_throws_an_exception_then_read_should_not_throw_an_exception()
            throws IOException {

        enteringTestHeaderLogger.debug(null);

        final InputStream mockedInputStream = mock(InputStream.class);
        when(mockedInputStream.read()).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(-1);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        assertDoesNotThrow(() -> uspesifikkMeldingLinjeFtpReader.read());
    }

    @Disabled
    @Test
    void when_disconnecting_from_ftp_throws_an_exception_then_read_should_not_throw_an_exception() throws IOException {

        enteringTestHeaderLogger.debug(null);

        final InputStream mockedInputStream = mock(InputStream.class);
        when(mockedInputStream.read()).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(-1);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
                getMeldingLinjeFileReaderCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);

        assertDoesNotThrow(() -> uspesifikkMeldingLinjeFtpReader.read());
    }

    @Test
    void when_trying_to_create_an_meldingLinjeSftpReader_with_any_of_the_parameters_null_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final List<Quartet<IOkosynkConfiguration, BATCH_TYPE, String, Boolean>> testData =
                new ArrayList<Quartet<IOkosynkConfiguration, BATCH_TYPE, String, Boolean>>() {{
                    add(new Quartet<>(null, null, null, true));
                    add(new Quartet<>(new FakeOkosynkConfiguration(), null, null, true));
                    add(new Quartet<>(null, getBatchType(), null, true));
                    add(new Quartet<>(null, null, "x", true));
                    add(new Quartet<>(null, null, null, true));
                    add(new Quartet<>(new FakeOkosynkConfiguration(), getBatchType(), null, true));
                    add(new Quartet<>(new FakeOkosynkConfiguration(), null, "x", true));
                    add(new Quartet<>(new FakeOkosynkConfiguration(), null, null, true));
                    add(new Quartet<>(null, getBatchType(), "x", true));
                    add(new Quartet<>(null, getBatchType(), null, true));
                    add(new Quartet<>(null, null, "x", true));
                    add(new Quartet<>(new FakeOkosynkConfiguration(), getBatchType(), null, true));
                    add(new Quartet<>(null, getBatchType(), "x", true));
                    add(new Quartet<>(new FakeOkosynkConfiguration(), getBatchType(), "x", false));
                }};

        for (final Quartet<IOkosynkConfiguration, Constants.BATCH_TYPE, String, Boolean> testdatum : testData) {

            final IOkosynkConfiguration okosynkConfiguration = testdatum.getValue0();
            final Constants.BATCH_TYPE batchType = testdatum.getValue1();
            final String fullyQualifiedInputFileName = testdatum.getValue2();
            final boolean shouldThrow = testdatum.getValue3();

            final String msg =
                    System.lineSeparator()
                            + "okosynkConfiguration       : " + okosynkConfiguration + System.lineSeparator()
                            + "batchType                  : " + batchType + System.lineSeparator()
                            + "fullyQualifiedInputFileName: " + fullyQualifiedInputFileName + System.lineSeparator();
            if (shouldThrow) {
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new MeldingLinjeSftpReader(
                                        okosynkConfiguration,
                                        batchType,
                                        fullyQualifiedInputFileName
                                ),
                        msg
                );
            } else {
                assertDoesNotThrow(
                        () ->
                                new MeldingLinjeSftpReader(
                                        okosynkConfiguration,
                                        batchType,
                                        fullyQualifiedInputFileName
                                ),
                        msg
                );
            }
        }
    }

    @Test
    void when_trying_to_parse_a_file_path_from_an_illeagal_uri_then_an_exception_shouild_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final AbstractOkosynkIoException okosynkIoException =
                assertThrows(
                        AbstractOkosynkIoException.class,
                        ()
                                ->
                                MeldingLinjeSftpReader.getFtpInputFilePath("\\:-978y&&65fuyuv_tullepath")
                );
        assertTrue(okosynkIoException instanceof ConfigureOrInitializeOkosynkIoException);
    }

    protected abstract Constants.BATCH_TYPE getBatchType();

    private Function<String, IMeldingLinjeFileReader> getMeldingLinjeFileReaderCreator() {
        return
                (fullyQualifiedInputFileNames) ->
                        new MeldingLinjeSftpReader(getOkosynkConfiguration(), getBatchType(), fullyQualifiedInputFileNames);
    }
}