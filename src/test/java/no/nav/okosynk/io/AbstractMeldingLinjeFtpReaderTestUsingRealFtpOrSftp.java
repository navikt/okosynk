package no.nav.okosynk.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.io.OkosynkIoException.ErrorCode;
import no.nav.okosynk.testutil.AbstractTestFtpServer;
import no.nav.okosynk.testutil.TestFtpServer;
import no.nav.okosynk.testutil.TestSftpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp {

    private static final Logger logger =
        LoggerFactory.getLogger(AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp.class);
    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    static final String FILNAVN_EXISTING_UR_OR_OS_INPUT_FIL = "testfilForMeldingsleser.txt";
    // =========================================================================

    public IOkosynkConfiguration getOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    private IOkosynkConfiguration okosynkConfiguration;

    // =========================================================================

    protected static void setFtpHostUriKey(String ftpHostUriKey) {
        FTP_HOST_URI_KEY = ftpHostUriKey;
    }

    // Set by subclass:
    private static String FTP_HOST_URI_KEY;

    public static void setFtpHostPortKey(String ftpHostPortKey) {
        FTP_HOST_PORT_KEY = ftpHostPortKey;
    }

    private static String FTP_HOST_PORT_KEY;

    protected static void setFtpUserKey(String ftpUserKey) {
        FTP_USER_KEY = ftpUserKey;
    }

    private static String FTP_USER_KEY;

    protected static void setFtpPasswordKey(String ftpPasswordKey) {
        FTP_PASSWORD_KEY = ftpPasswordKey;
    }

    private static String FTP_PASSWORD_KEY;

    private static String getFtpInputFilePath() {
        return FTP_INPUT_FILE_PATH;
    }

    private static void setFtpInputFilePath(String ftpInputFilePath) {
        FTP_INPUT_FILE_PATH = ftpInputFilePath;
    }

    // ---------------------------------------------------------------------
    private static String FTP_INPUT_FILE_PATH;

    private static String getFtpInputFileName() {
        return FTP_INPUT_FILE_NAME;
    }

    private static final String FTP_INPUT_FILE_NAME = AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp.FILNAVN_EXISTING_UR_OR_OS_INPUT_FIL;

    public static void setFtpTestServerFtpProtocol(Constants.FTP_PROTOCOL ftpTestServerFtpProtocol) {
        FTP_TEST_SERVER_FTP_PROTOCOL = ftpTestServerFtpProtocol;
    }

    // =========================================================================
    private static Constants.FTP_PROTOCOL FTP_TEST_SERVER_FTP_PROTOCOL;

    private static AbstractTestFtpServer getFtpTestServer() {
        return ftpTestServer;
    }

    private static void setFtpTestServer(AbstractTestFtpServer ftpTestServer) {
        AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp.ftpTestServer = ftpTestServer;
    }

    // =========================================================================
    private static AbstractTestFtpServer ftpTestServer;
    // =========================================================================
    @AfterAll
    protected static void freeFTPServerResources() {
        final AbstractTestFtpServer ftpTestServer = getFtpTestServer();
        if (ftpTestServer != null) {
            ftpTestServer.stop();
        }
    }

    protected static void establishAndStartFTPServer() {

        logger.info(
            System.lineSeparator()                                                      + System.lineSeparator()
                + "About to create a test FTP server"                                   + System.lineSeparator()
                + "================================================================="   + System.lineSeparator()
                + "ftpTestServerFtpProtocol          : " + FTP_TEST_SERVER_FTP_PROTOCOL + System.lineSeparator()
        );

        if (Constants.FTP_PROTOCOL.FTP.equals(FTP_TEST_SERVER_FTP_PROTOCOL)) {
            setFtpTestServer(new TestFtpServer());
        } else if (Constants.FTP_PROTOCOL.SFTP.equals(FTP_TEST_SERVER_FTP_PROTOCOL)) {
            setFtpTestServer(new TestSftpServer());
        } else {
            throw new IllegalStateException("FTP_TEST_SERVER_FTP_PROTOCOL illegal value: " + FTP_TEST_SERVER_FTP_PROTOCOL);
        }
        getFtpTestServer().start();
    }

    @BeforeEach
    public void setup() throws IOException {

        final IOkosynkConfiguration okosynkConfiguration =
            new FakeOkosynkConfiguration();

        setWorkingSystemProperties(okosynkConfiguration);

        okosynkConfiguration.setSystemProperty(
            Constants.FILE_READER_MAX_NUMBER_OF_READ_TRIES_KEY,
            "5");
        okosynkConfiguration.setSystemProperty(
            Constants.FILE_READER_RETRY_WAIT_TIME_IN_MILLISECONDS_KEY,
            "1234");

        this.okosynkConfiguration = okosynkConfiguration;

        createAWorkingMeldingLinjeFtpOrSftpFileReader();
    }

    private void setWorkingSystemProperties(final IOkosynkConfiguration okosynkConfiguration) {

        // =====================================================================
        // === Used by the creator of OsMeldingLinjeFtpReader and UrMeldingLinjeFtpReader:
        // ===
        {
            final String homeDir = AbstractTestFtpServer.FTP_TEST_SERVER_HOME_DIRECTORY;
            final URL resource =
                AbstractTestFtpServer
                    .class
                    .getClassLoader()
                    .getResource(FILNAVN_EXISTING_UR_OR_OS_INPUT_FIL);
            final String fullyQualifiedInputOperatingSystemFileName;
            try {
                fullyQualifiedInputOperatingSystemFileName = Paths.get(resource.toURI()).toString();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            final String ftpFilePath =
                new File(fullyQualifiedInputOperatingSystemFileName).getParent().substring(homeDir.length());
            setFtpInputFilePath(ftpFilePath);
        }
        // ===
        // =====================================================================
        // === Used by OsMeldingLinjeFtpReader and UrMeldingLinjeFtpReader:
        // ===
        final String ftpHostUriKey  = FTP_HOST_URI_KEY;
        final String ftpHostUri     =
              FTP_TEST_SERVER_FTP_PROTOCOL
            + "://"
            + AbstractTestFtpServer.FTP_TEST_SERVER_HOST_URI
            + ":"
            + AbstractTestFtpServer.FTP_TEST_SERVER_PORT
            + "/"
            + new File(
                  AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp.getFtpInputFilePath(),
                  AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp.getFtpInputFileName()
              ).getPath().replace('\\', '/')
            ;
        final String ftpUserKey     = FTP_USER_KEY;
        final String ftpUser        = AbstractTestFtpServer.FTP_TEST_SERVER_USER;
        final String ftpPasswordKey = FTP_PASSWORD_KEY;
        final String ftpPassword    = AbstractTestFtpServer.FTP_TEST_SERVER_PASSWORD;
        // ---------------------------------------------------------------------
        okosynkConfiguration.setSystemProperty(ftpHostUriKey , ftpHostUri );
        okosynkConfiguration.setSystemProperty(ftpUserKey    , ftpUser    );
        okosynkConfiguration.setSystemProperty(ftpPasswordKey, ftpPassword);
        // ---------------------------------------------------------------------
        okosynkConfiguration.setSystemProperty(Constants.BATCH_TYPE.OS.getFtpCharsetNameKey(), "ISO8859_1");
        okosynkConfiguration.setSystemProperty(Constants.BATCH_TYPE.UR.getFtpCharsetNameKey(), "ISO8859_1");
        // ===
        // =====================================================================
    }

    private IMeldingLinjeFileReader uspesifikkMeldingLinjeFileReader;

    @Test
    @DisplayName("Tets that reading an existing file using FTP is successful.")
    void when_connecting_with_ok_parameters_and_reading_an_existing_file_then_no_error_should_result() throws OkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader();

        logTestProperties();

        final List<String> actualLines = uspesifikkMeldingLinjeReader.read();

        assertEquals(3, actualLines.size());
    }

    @Test
    void when_connecting_ftp_with_an_invalid_host_then_an_configuration_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader();

        // Invalidate a parameter so that
        // OsMeldingLinjeFtpReader and UrMeldingLinjeFtpReader don't not work any more:
        final String key   = FTP_HOST_URI_KEY;
        final String value = AbstractTestFtpServer.FTP_TEST_SERVER_HOST_URI + "__NOT";
        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        okosynkConfiguration.setSystemProperty(key, value);

        logTestProperties();

        final OkosynkIoException okosynkIoException =
            Assertions.assertThrows(OkosynkIoException.class, uspesifikkMeldingLinjeReader::read);
        assertEquals(ErrorCode.CONFIGURE_OR_INITIALIZE, okosynkIoException.getErrorCode());
    }

    @Test
    void when_connecting_ftp_with_an_invalid_userid_then_an_authentication_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader();

        // Invalidate a parameter so that
        // OsMeldingLinjeFtpReader and UrMeldingLinjeFtpReader don't not work any more:
        final String key   = FTP_USER_KEY;
        final String value = AbstractTestFtpServer.FTP_TEST_SERVER_USER + "__NOT";
        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        okosynkConfiguration.setSystemProperty(key, value);

        logTestProperties();

        final OkosynkIoException okosynkIoException =
            Assertions.assertThrows(OkosynkIoException.class, uspesifikkMeldingLinjeReader::read);
        assertEquals(ErrorCode.AUTHENTICATION, okosynkIoException.getErrorCode());
    }

    @Test
    void when_connecting_ftp_with_an_invalid_password_then_an_authentication_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader();

        // Invalidate a parameter so that
        // OsMeldingLinjeFtpReader and UrMeldingLinjeFtpReader don't not work any more:
        final String key   = FTP_PASSWORD_KEY;
        final String value = AbstractTestFtpServer.FTP_TEST_SERVER_PASSWORD + "__NOT";
        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        okosynkConfiguration.setSystemProperty(key, value);

        logTestProperties();

        final OkosynkIoException okosynkIoException =
            Assertions.assertThrows(OkosynkIoException.class, uspesifikkMeldingLinjeReader::read);
        assertEquals(ErrorCode.AUTHENTICATION, okosynkIoException.getErrorCode());
    }

    @Test
    void when_the_input_file_does_not_exist_then_an_adequate_exception_should_be_thrown_after_a_specified_number_of_retries() {

        enteringTestHeaderLogger.debug(null);

        setFtpInputFilePath(FTP_INPUT_FILE_PATH + "_NOT");

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader();

        logTestProperties();

        final OkosynkIoException okosynkIoException =
            Assertions.assertThrows(OkosynkIoException.class, uspesifikkMeldingLinjeReader::read);
        assertEquals(ErrorCode.NUMBER_OF_RETRIES_EXCEEDED, okosynkIoException.getErrorCode());
        assertEquals(OkosynkIoException.class, okosynkIoException.getCause().getClass());
        assertEquals(ErrorCode.NOT_FOUND, ((OkosynkIoException)okosynkIoException.getCause()).getErrorCode());
    }

    private void createAWorkingMeldingLinjeFtpOrSftpFileReader() throws IOException {

        final File path = new File(FTP_INPUT_FILE_PATH, FILNAVN_EXISTING_UR_OR_OS_INPUT_FIL);

        this.uspesifikkMeldingLinjeFileReader = getCreator().apply(path.getCanonicalPath());
    }

    private IMeldingLinjeFileReader createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader() {

        // Create a working FTP server and an instance of OsMeldingLinjeFtpReader:
        final String ftpFilePath = FTP_INPUT_FILE_PATH;
        final String ftpFileName = FTP_INPUT_FILE_NAME;
        final String fullyQualifiedInputFileName =
            new File(ftpFilePath, ftpFileName).getPath().replace('\\', '/');

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            getCreator().apply(fullyQualifiedInputFileName);

        return uspesifikkMeldingLinjeReader;
    }

    private void logTestProperties() {

        final String ftpHostUriKey  = FTP_HOST_URI_KEY;
        final String ftpUserKey     = FTP_USER_KEY;
        final String ftpPasswordKey = FTP_PASSWORD_KEY;
        final String ftpFilePath    = FTP_INPUT_FILE_PATH;
        final String ftpFileName    = FTP_INPUT_FILE_NAME;

        logger.debug(
              System.lineSeparator()                                  + System.lineSeparator()
            + "Starting test. Active keys and properties:"            + System.lineSeparator()
            + "=====================================================" + System.lineSeparator()
            + "ftpHostUriKey : " + ftpHostUriKey                      + System.lineSeparator()
            + "ftpHostUri    : " + System.getProperty(ftpHostUriKey)  + System.lineSeparator()
            + "ftpUserKey    : " + ftpUserKey                         + System.lineSeparator()
            + "ftpUser       : " + System.getProperty(ftpUserKey)     + System.lineSeparator()
            + "ftpPasswordKey: " + ftpPasswordKey                     + System.lineSeparator()
            + "ftpPassword   : " + System.getProperty(ftpPasswordKey) + System.lineSeparator()
            + "ftpFilePath   : " + ftpFilePath                        + System.lineSeparator()
            + "ftpFileName   : " + ftpFileName                        + System.lineSeparator()
            + "=====================================================" + System.lineSeparator()
            + System.lineSeparator()
        );
    }

    protected abstract Function<String, IMeldingLinjeFileReader> getCreator();
}
