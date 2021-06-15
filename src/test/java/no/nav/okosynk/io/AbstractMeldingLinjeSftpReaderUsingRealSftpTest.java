package no.nav.okosynk.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.cli.AbstractTestFtpServer;
import no.nav.okosynk.cli.TestFtpServer;
import no.nav.okosynk.cli.TestSftpServer;
import org.javatuples.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMeldingLinjeSftpReaderUsingRealSftpTest {

    static final String FILNAVN_EXISTING_UR_OR_OS_INPUT_FIL = "testfilForMeldingsleser.txt";
    private static final Logger logger =
        LoggerFactory.getLogger(AbstractMeldingLinjeSftpReaderUsingRealSftpTest.class);
    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");
    private static final String INPUT_TEST_DATA_FILE_NAME_BACKUP_SUFFIX = ".test.backup";
    private IOkosynkConfiguration okosynkConfiguration;
    private static String FTP_INPUT_FILE_PATH;
    private static final String FTP_INPUT_FILE_NAME =
        AbstractMeldingLinjeSftpReaderUsingRealSftpTest.FILNAVN_EXISTING_UR_OR_OS_INPUT_FIL;
    private static Constants.FTP_PROTOCOL FTP_TEST_SERVER_FTP_PROTOCOL;
    private static AbstractTestFtpServer ftpTestServer;

    private IMeldingLinjeFileReader uspesifikkMeldingLinjeFileReader;

    public IOkosynkConfiguration getOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    public static void setFtpTestServerFtpProtocol(Constants.FTP_PROTOCOL ftpTestServerFtpProtocol) {
        FTP_TEST_SERVER_FTP_PROTOCOL = ftpTestServerFtpProtocol;
    }

    @AfterAll
    static void freeFTPServerResources() {
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

    private static void setFtpTestServer(AbstractTestFtpServer ftpTestServer) {
        AbstractMeldingLinjeSftpReaderUsingRealSftpTest.ftpTestServer = ftpTestServer;
    }

    private static AbstractTestFtpServer getFtpTestServer() {
        return ftpTestServer;
    }

    private static String getFtpInputFilePath() {
        return FTP_INPUT_FILE_PATH;
    }

    private static void setFtpInputFilePath(String ftpInputFilePath) {
        FTP_INPUT_FILE_PATH = ftpInputFilePath;
    }

    @BeforeEach
    void setup() throws IOException, URISyntaxException {

        final IOkosynkConfiguration okosynkConfiguration =
            new FakeOkosynkConfiguration();

        try {
            setWorkingSystemProperties(okosynkConfiguration);
        } catch (Throwable e) {
            logger.error(
                  "Call to setWorkingSystemProperties(okosynkConfiguration) threw. "
                + "There may be many(?) causes to this, "
                + "one is that there is trouble with the test data input file "
                + "which implicitly means that the backup/restore "
                + "methods in before/after each test has failed.",
                e
            );
        }

        this.okosynkConfiguration = okosynkConfiguration;

        createAWorkingMeldingLinjeFtpOrSftpFileReader();

        backupInputFileBeforeEach();
    }

    @AfterEach
    void restoreInputFileAfterEach() {
        // Since the input file will be
        // renamed by many of the tests,
        // a backup is made as a source
        // for restore before each test:
        try {
            final Pair<String, String> fullyQualifiedOperatingSystemInputTestDataFileNames =
                getFullyQualifiedOperatingSystemInputTestDataFileNames();
            final File from = new File(fullyQualifiedOperatingSystemInputTestDataFileNames.getValue1());
            final File to   = new File(fullyQualifiedOperatingSystemInputTestDataFileNames.getValue0());
            System.out.println("from: " + from + ", to: " + to);
            if (!to.exists()) {
                Files.copy(from.toPath(), to.toPath());
            }
        } catch (Throwable e) {
            logger.error(
                  "Could not restore backed up input file after a test. "
                + "Some tests will or will not fail after this;-)",
                e
            );
        }
    }

    @Test
    @DisplayName("Tests that reading an existing file using FTP is successful.")
    void when_connecting_with_ok_parameters_and_reading_an_existing_file_then_no_error_should_result()
        throws AbstractOkosynkIoException, URISyntaxException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader();

        logTestProperties();

        final List<String> actualLines = uspesifikkMeldingLinjeReader.read();

        assertEquals(3, actualLines.size());

        final Pair<String, String> fullyQualifiedOperatingSystemInputTestDataFileNames =
            getFullyQualifiedOperatingSystemInputTestDataFileNames();
        final File inputTestDataFile = new File(fullyQualifiedOperatingSystemInputTestDataFileNames.getValue0());

        final boolean inputFileExistsAfterSuccessfulRead  = inputTestDataFile.exists();
        final boolean shouldRenameFileAfterSuccessfulRead = shouldRenameFileAfterSuccessfulRead();

        assertNotEquals(inputFileExistsAfterSuccessfulRead, shouldRenameFileAfterSuccessfulRead);
    }

    @Test
    void when_connecting_ftp_with_an_invalid_host_then_an_configuration_exception_should_be_thrown()
        throws URISyntaxException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader();

        // Invalidate a parameter so that
        // OsMeldingLinjeFtpReader and UrMeldingLinjeFtpReader don't not work any more:
        final String key   = getFtpHostUriKey();
        final String value = AbstractTestFtpServer.FTP_TEST_SERVER_HOST_URI + "__NOT";
        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        okosynkConfiguration.setSystemProperty(key, value);

        logTestProperties();

        final ConfigureOrInitializeOkosynkIoException okosynkIoException =
            Assertions.assertThrows(ConfigureOrInitializeOkosynkIoException.class, uspesifikkMeldingLinjeReader::read);

        final Pair<String, String> fullyQualifiedOperatingSystemInputTestDataFileNames =
            getFullyQualifiedOperatingSystemInputTestDataFileNames();
        final File inputTestDataFile = new File(fullyQualifiedOperatingSystemInputTestDataFileNames.getValue0());

        assertTrue(inputTestDataFile.exists());
    }

    @Test
    void when_connecting_ftp_with_an_invalid_userid_then_an_authentication_exception_should_be_thrown()
        throws URISyntaxException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader();

        // Invalidate a parameter so that
        // OsMeldingLinjeFtpReader and UrMeldingLinjeFtpReader don't not work any more:
        final String key   = getFtpUserKey();
        final String value = AbstractTestFtpServer.FTP_TEST_SERVER_USER + "__NOT";
        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        okosynkConfiguration.setSystemProperty(key, value);

        logTestProperties();

        final AuthenticationOkosynkIoException okosynkIoException =
            Assertions.assertThrows(AuthenticationOkosynkIoException.class, uspesifikkMeldingLinjeReader::read);

        final Pair<String, String> fullyQualifiedOperatingSystemInputTestDataFileNames =
            getFullyQualifiedOperatingSystemInputTestDataFileNames();
        final File inputTestDataFile = new File(fullyQualifiedOperatingSystemInputTestDataFileNames.getValue0());

        assertTrue(inputTestDataFile.exists());
    }

    @Test
    void when_connecting_ftp_with_an_invalid_password_then_an_authentication_exception_should_be_thrown()
        throws URISyntaxException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader();

        // Invalidate a parameter so that
        // OsMeldingLinjeFtpReader and UrMeldingLinjeFtpReader don't not work any more:
        final String key   = getFtpPasswordKey();
        final String value = AbstractTestFtpServer.FTP_TEST_SERVER_PASSWORD + "__NOT";
        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        okosynkConfiguration.setSystemProperty(key, value);

        logTestProperties();

        final AuthenticationOkosynkIoException okosynkIoException =
            Assertions.assertThrows(AuthenticationOkosynkIoException.class, uspesifikkMeldingLinjeReader::read);

        final Pair<String, String> fullyQualifiedOperatingSystemInputTestDataFileNames =
            getFullyQualifiedOperatingSystemInputTestDataFileNames();
        final File inputTestDataFile = new File(fullyQualifiedOperatingSystemInputTestDataFileNames.getValue0());

        assertTrue(inputTestDataFile.exists());
    }

    @Test
    void when_the_input_file_does_not_exist_then_an_adequate_exception_should_be_thrown_after_a_specified_number_of_retries() {

        enteringTestHeaderLogger.debug(null);

        setFtpInputFilePath(FTP_INPUT_FILE_PATH + "_NOT");

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader();

        logTestProperties();

        final NotFoundOkosynkIoException okosynkIoException =
            Assertions.assertThrows(NotFoundOkosynkIoException.class, uspesifikkMeldingLinjeReader::read);

        final Pair<String, String> fullyQualifiedOperatingSystemInputTestDataFileNames =
            getFullyQualifiedOperatingSystemInputTestDataFileNames();
        final File inputTestDataFile = new File(fullyQualifiedOperatingSystemInputTestDataFileNames.getValue0());

        assertTrue(inputTestDataFile.exists());
    }

    @Test
    void when_removeInputData_is_called_then_the_input_file_should_no_more_be_available()
        throws AbstractOkosynkIoException, URISyntaxException {

        enteringTestHeaderLogger.debug(null);

        // -----------------------------------------------------------------------------------------

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader();

        logTestProperties();

        uspesifikkMeldingLinjeReader.read();

        final Pair<String, String> fullyQualifiedOperatingSystemInputTestDataFileNames =
            getFullyQualifiedOperatingSystemInputTestDataFileNames();
        final File inputTestDataFile =
            new File(fullyQualifiedOperatingSystemInputTestDataFileNames.getValue0());
        final boolean inputFileExistsAfterSuccessfulRead  = inputTestDataFile.exists();
        final boolean shouldRenameFileAfterSuccessfulRead = shouldRenameFileAfterSuccessfulRead();
        assertNotEquals(inputFileExistsAfterSuccessfulRead, shouldRenameFileAfterSuccessfulRead);

        // -----------------------------------------------------------------------------------------

        boolean removeInputDataHasSucceeded = uspesifikkMeldingLinjeReader.removeInputData();
        assertTrue(removeInputDataHasSucceeded);
        assertFalse(inputTestDataFile.exists());

        removeInputDataHasSucceeded = uspesifikkMeldingLinjeReader.removeInputData();
        assertFalse(removeInputDataHasSucceeded);
        assertFalse(inputTestDataFile.exists());

        // -----------------------------------------------------------------------------------------
    }

    protected abstract String getFtpHostUriKey();
    protected abstract String getFtpUserKey();
    protected abstract String getFtpPasswordKey();
    protected abstract boolean shouldRenameFileAfterSuccessfulRead();
    protected abstract Function<String, IMeldingLinjeFileReader> getMeldingLinjeFileReaderCreator();

    private Pair<String, String> getFullyQualifiedOperatingSystemInputTestDataFileNames() {

        final String inputTestDataFileName       = FILNAVN_EXISTING_UR_OR_OS_INPUT_FIL;
        final String suffix                      = INPUT_TEST_DATA_FILE_NAME_BACKUP_SUFFIX;
        final String inputTestDataBackupFileName = FILNAVN_EXISTING_UR_OR_OS_INPUT_FIL + suffix;
        final String[] fullyQualifiedOperatingSystemFileNames = new String[2];
        int counter = 0;
        for (final String fileName : new String[]{inputTestDataFileName, inputTestDataBackupFileName}) {
            final URL resource =
                AbstractTestFtpServer
                    .class
                    .getClassLoader()
                    .getResource(fileName);
            try {
                final String fullyQualifiedOperatingSystemFileName =
                    Paths.get(resource.toURI()).toString();
                fullyQualifiedOperatingSystemFileNames[counter] = fullyQualifiedOperatingSystemFileName;
            } catch (Throwable e) {
                fullyQualifiedOperatingSystemFileNames[counter] = null;
            }
            counter++;
        } // END for

        if (fullyQualifiedOperatingSystemFileNames[0] == null) {
            fullyQualifiedOperatingSystemFileNames[0] = fullyQualifiedOperatingSystemFileNames[1].replace(suffix, "");
        } else {
            fullyQualifiedOperatingSystemFileNames[1] = fullyQualifiedOperatingSystemFileNames[0] + suffix;
        }

        return Pair.fromArray(fullyQualifiedOperatingSystemFileNames);
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
                logger.error(
                    "Input file most probably not found, "
                        + "which implicitly means that the backup/restore "
                        + "methods in before/after each test has failed.",
                    e
                );
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
        final String ftpHostUriKey  = getFtpHostUriKey();

        final String ftpHostUri     =
            FTP_TEST_SERVER_FTP_PROTOCOL
                + "://"
                + AbstractTestFtpServer.FTP_TEST_SERVER_HOST_URI
                + ":"
                + AbstractTestFtpServer.FTP_TEST_SERVER_PORT
                + "/"
                + new File(
                AbstractMeldingLinjeSftpReaderUsingRealSftpTest.getFtpInputFilePath(),
                AbstractMeldingLinjeSftpReaderUsingRealSftpTest.getFtpInputFileName()
            ).getPath().replace('\\', '/')
            ;
        final String ftpUserKey     = getFtpUserKey();
        final String ftpUser        = AbstractTestFtpServer.FTP_TEST_SERVER_USER;
        final String ftpPasswordKey = getFtpPasswordKey();
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

    private void createAWorkingMeldingLinjeFtpOrSftpFileReader() throws IOException {

        final File path = new File(FTP_INPUT_FILE_PATH, FILNAVN_EXISTING_UR_OR_OS_INPUT_FIL);

        this.uspesifikkMeldingLinjeFileReader =
            getMeldingLinjeFileReaderCreator().apply(path.getCanonicalPath());
    }

    private static String getFtpInputFileName() {
        return FTP_INPUT_FILE_NAME;
    }

    private IMeldingLinjeFileReader createAWorkingFtpServerAndAnInstanceOfMeldingLinjeFtpOrSftpFileReader() {

        // Create a working FTP server and an instance of OsMeldingLinjeFtpReader:
        final String ftpFilePath = FTP_INPUT_FILE_PATH;
        final String ftpFileName = FTP_INPUT_FILE_NAME;
        final String fullyQualifiedInputFileName =
            new File(ftpFilePath, ftpFileName).getPath().replace('\\', '/');

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader =
            getMeldingLinjeFileReaderCreator().apply(fullyQualifiedInputFileName);

        return uspesifikkMeldingLinjeReader;
    }

    private void logTestProperties() {

        final String ftpHostUriKey  = getFtpHostUriKey();
        final String ftpUserKey     = getFtpUserKey();
        final String ftpPasswordKey = getFtpPasswordKey();
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

    /**
     * Since the input file will be
     * renamed by many of the tests,
     * a backup is made as a source
     * for restore before each test
     *
     * @throws URISyntaxException
     */
    private void backupInputFileBeforeEach() throws URISyntaxException {

        final Pair<String, String> fullyQualifiedOperatingSystemInputTestDataFileNames =
            getFullyQualifiedOperatingSystemInputTestDataFileNames();

        if (!new File(fullyQualifiedOperatingSystemInputTestDataFileNames.getValue0()).exists()) {
            restoreInputFileAfterEach();
        } else {
            try {
                final File from = new File(fullyQualifiedOperatingSystemInputTestDataFileNames.getValue0());
                final File to   = new File(fullyQualifiedOperatingSystemInputTestDataFileNames.getValue1());
                System.out.println("from: " + from + ", to: " + to);
                Files.copy(from.toPath(), to.toPath());
            } catch (FileAlreadyExistsException e) {
                // Probably OK, as the test did not succeed,
                // and hence the input file was not renamed.
            } catch (Throwable e) {
                logger.error(
                    "Could not back up input file before a test. "
                        + "Some tests will or will not fail after this;-)",
                    e
                );
            }
        }
    }
}
