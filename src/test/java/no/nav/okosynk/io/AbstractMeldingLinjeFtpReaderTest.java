package no.nav.okosynk.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import org.apache.commons.net.ftp.FTPClient;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common tests for OS/UR FTP tests.
 */
public abstract class AbstractMeldingLinjeFtpReaderTest
    extends AbstractMeldingLinjeFileReaderTest {

    // =========================================================================
    private static final Logger logger =
        LoggerFactory.getLogger(AbstractMeldingLinjeFtpReaderTest.class);
    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");
    // =========================================================================

    private static String FTP_HOST_URL_KEY;
    private static String FTP_USER_KEY;
    private static String FTP_PASSWORD_KEY;

    public static String getFtpHostUrlKey() {
        return FTP_HOST_URL_KEY;
    }

    protected static void setFtpHostUrlKey(String ftpHostUrlKey) {
        FTP_HOST_URL_KEY = ftpHostUrlKey;
    }

    public static String getFtpUserKey() {
        return FTP_USER_KEY;
    }

    protected static void setFtpUserKey(String ftpUserKey) {
        FTP_USER_KEY = ftpUserKey;
    }

    public static String getFtpPasswordKey() {
        return FTP_PASSWORD_KEY;
    }

    protected static void setFtpPasswordKey(String ftpPasswordKey) {
        FTP_PASSWORD_KEY = ftpPasswordKey;
    }

    public static String getFtpInputFilePath() {
        return FTP_INPUT_FILE_PATH;
    }

    public static void setFtpInputFilePath(String ftpInputFilePath) {
        FTP_INPUT_FILE_PATH = ftpInputFilePath;
    }

    private static  String FTP_INPUT_FILE_PATH;

    private IOkosynkConfiguration okosynkConfiguration;
    // =========================================================================
    private static final String syntacticallyAcceptableFtpHostUri                  = "ftp://012.123.234.345:32000";
    private static final String syntacticallyAcceptableFtpUser                     = "somePlaceholderUser";
    private static final String syntacticallyAcceptableFtpPassword                 = "somePlaceholderPassword";
    private static final String syntacticallyAcceptableFullyQualifiedInputFileName = "/a/somePlaceholderFullyQualifiedInputFileName.txt";
    // =========================================================================

    @BeforeEach
    void setNecessarySystemProperties() {

        this.okosynkConfiguration = new FakeOkosynkConfiguration();

        this.okosynkConfiguration.setSystemProperty(FTP_HOST_URL_KEY, syntacticallyAcceptableFtpHostUri );
        this.okosynkConfiguration.setSystemProperty(FTP_USER_KEY    , syntacticallyAcceptableFtpUser    );
        this.okosynkConfiguration.setSystemProperty(FTP_PASSWORD_KEY, syntacticallyAcceptableFtpPassword);
        this.okosynkConfiguration.setSystemProperty(Constants.BATCH_TYPE.OS.getFtpCharsetNameKey(), "ISO8859_1");
        this.okosynkConfiguration.setSystemProperty(Constants.BATCH_TYPE.UR.getFtpCharsetNameKey(), "ISO8859_1");
    }
    // =========================================================================

    /*
        1) Escalate to also cover UR:
        2) Something is probably wrong with the usage of constants by the business classes
    */
    @Test
    void testGetFtpProtocol() throws LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        // Just to get the AbstractMeldingLinjeFtpOrSftpReader not to throw:
        final String ftpHostUrlKey = FTP_HOST_URL_KEY;
        this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, "ftp://ser:733/con");
        final AbstractMeldingLinjeFtpOrSftpReader meldingLinjeFtpOrSftpReader =
            (AbstractMeldingLinjeFtpOrSftpReader)getCreator().apply("somethingDummy");

        final Collection<Triplet<String, Constants.FTP_PROTOCOL, Class<? extends Exception>>> testData =
            new ArrayList<Triplet<String, Constants.FTP_PROTOCOL, Class<? extends Exception>>>() {{
                add(new Triplet<>(null, null, LinjeUnreadableException.class));
                add(new Triplet<>("", null, LinjeUnreadableException.class));
                add(new Triplet<>(" ", null, LinjeUnreadableException.class));
                add(new Triplet<>("  ", null, LinjeUnreadableException.class));
                add(new Triplet<>("pro://ser:por/con", null, LinjeUnreadableException.class));
                add(new Triplet<>("pro://ser:por", null, LinjeUnreadableException.class));
                add(new Triplet<>("pro://ser", null, LinjeUnreadableException.class));
                add(new Triplet<>("ser:por", null, LinjeUnreadableException.class));
                add(new Triplet<>("pro://ser:377", null, LinjeUnreadableException.class));
                add(new Triplet<>("ser", Constants.FTP_PROTOCOL_DEFAULT_VALUE, null));
                add(new Triplet<>("ser:377", null, LinjeUnreadableException.class));
                add(new Triplet<>("http://ser:377", null, LinjeUnreadableException.class));
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
                add(new Triplet<>("sftp://876.765.654.543:377/a/b.txt", Constants.FTP_PROTOCOL.SFTP, null));
                add(new Triplet<>("sftp://a.543:377/a/b.txt", Constants.FTP_PROTOCOL.SFTP, null));
                add(new Triplet<>("sftp://543.b:377/a/b.txt", Constants.FTP_PROTOCOL.SFTP, null));
                add(new Triplet<>("localhost", Constants.FTP_PROTOCOL.SFTP, null));
                add(new Triplet<>("Focalhust", Constants.FTP_PROTOCOL.SFTP, null));
                add(new Triplet<>("ftp://155.55.1.78:21/P230.K230M131.UTPL(0)", Constants.FTP_PROTOCOL.FTP, null));
            }};

        for (final Triplet<String, Constants.FTP_PROTOCOL, Class<? extends Exception>> testDatum : testData) {

            final String ftpHostUrl = testDatum.getValue0();
            if (ftpHostUrl == null) {
                this.okosynkConfiguration.clearSystemProperty(ftpHostUrlKey);
            } else {
                this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, ftpHostUrl);
            }

            final Class<? extends Exception> expectedExceptionClass = testDatum.getValue2();

            final URI uri;
            try {
                logger.debug("ftpHostUrl: " + ftpHostUrl);
                uri = new URI(ftpHostUrl);
            } catch (Throwable e) {
                final String msg = "ftpHostUrl: " + ftpHostUrl;
                logger.debug(msg, e);
                Assertions.assertThrows(
                    expectedExceptionClass,
                    () -> meldingLinjeFtpOrSftpReader.getFtpProtocol(okosynkConfiguration),
                    msg
                );
                continue;
            }

            if (expectedExceptionClass == null) {
                final Constants.FTP_PROTOCOL actualFtpProtocol =
                    meldingLinjeFtpOrSftpReader.getFtpProtocol(okosynkConfiguration);

                final Constants.FTP_PROTOCOL expectedFtpProtocol1 = testDatum.getValue1();
                final Constants.FTP_PROTOCOL expectedFtpProtocol2 =
                    (uri.getScheme() == null)
                        ?
                        Constants.FTP_PROTOCOL_DEFAULT_VALUE
                        :
                        Constants.FTP_PROTOCOL.valueOf(uri.getScheme().toUpperCase())
                    ;

                Assertions.assertEquals(expectedFtpProtocol1, actualFtpProtocol);
                Assertions.assertEquals(expectedFtpProtocol2, actualFtpProtocol);
            } else {
                Assertions.assertThrows(
                    expectedExceptionClass,
                    () -> meldingLinjeFtpOrSftpReader.getFtpProtocol(okosynkConfiguration),
                    "ftpHostUrl: " + ftpHostUrl
                );
            }
        }
    }

    @Test
    void testGetFtpHostServerName() throws LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        // Just to get the AbstractMeldingLinjeFtpOrSftpReader not to throw:
        final String ftpHostUrlKey = FTP_HOST_URL_KEY;
        this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, "ftp://ser:733/con");

        final AbstractMeldingLinjeFtpOrSftpReader meldingLinjeFtpOrSftpReader =
            (AbstractMeldingLinjeFtpOrSftpReader)getCreator().apply("somethingDummy");

        final Collection<Triplet<String, String, Class<? extends Exception>>> testData =
            new ArrayList<Triplet<String, String, Class<? extends Exception>>>() {{
                add(new Triplet<>(null, null, LinjeUnreadableException.class));
                add(new Triplet<>("", null, LinjeUnreadableException.class));
                add(new Triplet<>(" ", null, LinjeUnreadableException.class));
                add(new Triplet<>("  ", null, LinjeUnreadableException.class));
                add(new Triplet<>("pro://ser:por/con", null, LinjeUnreadableException.class));
                add(new Triplet<>("pro://ser:por", null, LinjeUnreadableException.class));
                add(new Triplet<>("pro://ser", "ser", null));
                add(new Triplet<>("ser:por", null, LinjeUnreadableException.class));
                add(new Triplet<>("pro://ser:377", "ser", null));
                add(new Triplet<>("ser", "ser", null));
                add(new Triplet<>("ser:377", null, LinjeUnreadableException.class));
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
            if (ftpHostUrl == null) {
                this.okosynkConfiguration.clearSystemProperty(ftpHostUrlKey);
            } else {
                this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, ftpHostUrl);
            }

            final Class<? extends Exception> expectedExceptionClass = testDatum.getValue2();

            try {
                logger.debug("ftpHostUrl: " + ftpHostUrl);
                new URI(ftpHostUrl);
            } catch (Throwable e) {
                final String msg = "ftpHostUrl: " + ftpHostUrl;
                logger.debug(msg, e);
                Assertions.assertThrows(
                    expectedExceptionClass,
                    () -> meldingLinjeFtpOrSftpReader.getFtpHostServerName(okosynkConfiguration),
                    msg
                );
                continue;
            }

            if (expectedExceptionClass == null) {
                final String actualFtpHostServerName =
                    meldingLinjeFtpOrSftpReader.getFtpHostServerName(okosynkConfiguration);

                final String expectedFtpHostServerName1 = testDatum.getValue1();

                Assertions.assertEquals(expectedFtpHostServerName1, actualFtpHostServerName);
            } else {
                Assertions.assertThrows(
                    expectedExceptionClass,
                    () -> meldingLinjeFtpOrSftpReader.getFtpHostServerName(okosynkConfiguration),
                    "ftpHostUrl: " + ftpHostUrl
                );
            }
        }
    }

    @Test
    void testGetFtpHostPort() throws LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        // Just to get the AbstractMeldingLinjeFtpOrSftpReader not to throw:
        final String ftpHostUrlKey = FTP_HOST_URL_KEY;
        this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, "ftp://ser:733/con");

        final AbstractMeldingLinjeFtpOrSftpReader meldingLinjeFtpOrSftpReader =
            (AbstractMeldingLinjeFtpOrSftpReader)getCreator().apply("somethingDummy");

        final Collection<Triplet<String, Integer, Class<? extends Exception>>> testData =
            new ArrayList<Triplet<String, Integer, Class<? extends Exception>>>() {{
                add(new Triplet<>(null, null, LinjeUnreadableException.class));
                add(new Triplet<>("", null, LinjeUnreadableException.class));
                add(new Triplet<>(" ", null, LinjeUnreadableException.class));
                add(new Triplet<>("  ", null, LinjeUnreadableException.class));
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
            if (ftpHostUrl == null) {
                this.okosynkConfiguration.clearSystemProperty(ftpHostUrlKey);
            } else {
                this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, ftpHostUrl);
            }

            final Class<? extends Exception> expectedExceptionClass = testDatum.getValue2();

            try {
                logger.debug("ftpHostUrl: " + ftpHostUrl);
                new URI(ftpHostUrl);
            } catch (Throwable e) {
                final String msg = "ftpHostUrl: " + ftpHostUrl;
                logger.debug(msg, e);
                Assertions.assertThrows(
                    expectedExceptionClass,
                    () -> meldingLinjeFtpOrSftpReader.getFtpHostPort(okosynkConfiguration),
                    msg
                );
                continue;
            }

            if (expectedExceptionClass == null) {

                final int actualFtpHostPort =
                    meldingLinjeFtpOrSftpReader.getFtpHostPort(okosynkConfiguration);

                final int expectedFtpHostPort1 = testDatum.getValue1();

                Assertions.assertEquals(expectedFtpHostPort1, actualFtpHostPort);
            } else {
                Assertions.assertThrows(
                    expectedExceptionClass,
                    () -> meldingLinjeFtpOrSftpReader.getFtpHostPort(okosynkConfiguration),
                    "ftpHostUrl: " + ftpHostUrl
                );
            }
        }
    }

    @Test
    @DisplayName("Tests that all necessary system properties are set to successfully create an instance of OsMeldingLinjeFtpReader")
    void testSystemProperties() {

        enteringTestHeaderLogger.debug(null);

        final List<Quartet<String, String, String, Boolean>> testData =
            new ArrayList<Quartet<String, String, String, Boolean>>() {{

                add(new Quartet<>(                             null, syntacticallyAcceptableFtpUser, syntacticallyAcceptableFtpPassword, true ));
                add(new Quartet<>(                               "", syntacticallyAcceptableFtpUser, syntacticallyAcceptableFtpPassword, true ));
                add(new Quartet<>(                              " ", syntacticallyAcceptableFtpUser, syntacticallyAcceptableFtpPassword, true ));
                add(new Quartet<>(syntacticallyAcceptableFtpHostUri,                           null, syntacticallyAcceptableFtpPassword, true ));
                add(new Quartet<>(syntacticallyAcceptableFtpHostUri,                             "", syntacticallyAcceptableFtpPassword, true ));
                add(new Quartet<>(syntacticallyAcceptableFtpHostUri,                            " ", syntacticallyAcceptableFtpPassword, true ));
                add(new Quartet<>(syntacticallyAcceptableFtpHostUri, syntacticallyAcceptableFtpUser,                               null, true ));
                add(new Quartet<>(syntacticallyAcceptableFtpHostUri, syntacticallyAcceptableFtpUser,                               ""  , true ));
                add(new Quartet<>(syntacticallyAcceptableFtpHostUri, syntacticallyAcceptableFtpUser,                               " " , true ));
                add(new Quartet<>(syntacticallyAcceptableFtpHostUri, syntacticallyAcceptableFtpUser, syntacticallyAcceptableFtpPassword, false));
            }};

        for (final Quartet<String, String, String, Boolean> testDatum : testData) {

            final String sftpHostUri  = testDatum.getValue0();
            final String sftpUser     = testDatum.getValue1();
            final String sftpPassword = testDatum.getValue2();
            final Boolean shouldThrow = testDatum.getValue3();

            logger.debug(
                System.lineSeparator()
                    + "About to test"                  + System.lineSeparator()
                    + "============="                  + System.lineSeparator()
                    + "sftpHostUri  : " + sftpHostUri  + System.lineSeparator()
                    + "sftpUser     : " + sftpUser     + System.lineSeparator()
                    + "sftpPassword : " + sftpPassword + System.lineSeparator()
                    + "shouldThrow  : " + shouldThrow  + System.lineSeparator()
                    + System.lineSeparator()
            );

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
                Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> getCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName),
                    "Enough system properties not set. Creation of <OS/UR>MeldingLinjeFtpReader should have thrown."
                );
            } else {
                try {
                    getCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName);
                    // OK: Should not throw
                } catch (Throwable e) {
                    Assertions.fail(("Unexpected exception received: " +  e.getMessage()));
                }
            }
        }
    }

    @Test
    @DisplayName("Tests that a correct exception is thrown when trying to connect")
    void testExceptionUponConnect() throws IOException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.doThrow(IOException.class).when(mockedFTPClient).connect(Matchers.anyString(), Matchers.anyInt());

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        Assertions.assertThrows(LinjeUnreadableException.class, uspesifikkMeldingLinjeFtpReader::read);
    }

    @Test
    @DisplayName("Tests that a correct exception is thrown when connect implies a bad return code")
    void testBadReplyUponConnect() {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(-1);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        Assertions.assertThrows(LinjeUnreadableException.class, uspesifikkMeldingLinjeFtpReader::read);
    }

    @Test
    @DisplayName("Tests that a correct exception is thrown when login throws an exception")
    void testExceptionUponLogin() throws IOException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        Mockito.when(mockedFTPClient.login(Matchers.anyString(), Matchers.anyString())).thenThrow(IOException.class);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        Assertions.assertThrows(LinjeUnreadableException.class, uspesifikkMeldingLinjeFtpReader::read);
    }

    @Test
    @DisplayName("Tests that a correct exception is thrown when login returns unsuccessfully")
    void testUnsuccessfulLogin() throws IOException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        Mockito.when(mockedFTPClient.login(Matchers.anyString(), Matchers.anyString())).thenReturn(false);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        Assertions.assertThrows(LinjeUnreadableException.class, uspesifikkMeldingLinjeFtpReader::read);
    }

    @Test
    @DisplayName("Tests that a correct exception is thrown when the returned InputStream is null")
    void testInputStreamIsNull_FileDoesNotExist() throws IOException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        Mockito.when(mockedFTPClient.login(Matchers.anyString(), Matchers.anyString())).thenReturn(true);
        Mockito.when(mockedFTPClient.retrieveFileStream(Matchers.anyString())).thenReturn(null);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        Assertions.assertThrows(LinjeUnreadableException.class, uspesifikkMeldingLinjeFtpReader::read);
    }

    @Test
    @DisplayName("Tests that a correct exception is thrown when acquiring an InputStream implies an exception")
    void testExceptionWhenAcquiringTheInputStream() throws IOException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        Mockito.when(mockedFTPClient.login(Matchers.anyString(), Matchers.anyString())).thenReturn(true);
        Mockito.when(mockedFTPClient.retrieveFileStream(Matchers.anyString())).thenThrow(IOException.class);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        Assertions.assertThrows(LinjeUnreadableException.class, uspesifikkMeldingLinjeFtpReader::read);
    }

    @Test
    @DisplayName("Tests that that an exception is NOT thrown when completePendingCommand() throws upon cleanup.")
    void testNoExceptionWhenCompletePendingCommandThrows() throws IOException, LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        Mockito.when(mockedFTPClient.login(Matchers.anyString(), Matchers.anyString())).thenReturn(true);

        final InputStream mockedInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockedInputStream.read()).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(-1);

        Mockito.when(mockedFTPClient.retrieveFileStream(Matchers.anyString())).thenReturn(mockedInputStream);
        Mockito.when(mockedFTPClient.completePendingCommand()).thenThrow(IOException.class);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        uspesifikkMeldingLinjeFtpReader.read();

        // Should not throw an exception
    }

    @Test
    @DisplayName("Tests that an exception is NOT thrown when completePendingCommand() returns an error.")
    void testNoExceptionWhenCompletePendingReturnsAnError() throws IOException, LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        Mockito.when(mockedFTPClient.login(Matchers.anyString(), Matchers.anyString())).thenReturn(true);

        final InputStream mockedInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockedInputStream.read()).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(-1);

        Mockito.when(mockedFTPClient.retrieveFileStream(Matchers.anyString())).thenReturn(mockedInputStream);
        Mockito.when(mockedFTPClient.completePendingCommand()).thenReturn(false);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        uspesifikkMeldingLinjeFtpReader.read();

        // Should not throw an exception
    }

    @Test
    @DisplayName("Tests that an exception is NOT thrown when closing the input stream throws.")
    void testNoExceptionWhenClosingTheInputStreamThrows() throws IOException, LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        Mockito.when(mockedFTPClient.login(Matchers.anyString(), Matchers.anyString())).thenReturn(true);

        final InputStream mockedInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockedInputStream.read()).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(-1);
        Mockito.doThrow(IOException.class).when(mockedInputStream).close();

        Mockito.when(mockedFTPClient.retrieveFileStream(Matchers.anyString())).thenReturn(mockedInputStream);
        Mockito.when(mockedFTPClient.completePendingCommand()).thenReturn(true);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        uspesifikkMeldingLinjeFtpReader.read();

        // Should not throw an exception
    }

    @Test
    @DisplayName("Tests that an exception is NOT thrown when logging out from the ftp client throws.")
    void testNoExceptionWhenLoggingOutFromFtpClientThrows() throws IOException, LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        Mockito.when(mockedFTPClient.login(Matchers.anyString(), Matchers.anyString())).thenReturn(true);

        final InputStream mockedInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockedInputStream.read()).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(-1);

        Mockito.when(mockedFTPClient.retrieveFileStream(Matchers.anyString())).thenReturn(mockedInputStream);
        Mockito.when(mockedFTPClient.completePendingCommand()).thenReturn(true);
        Mockito.when(mockedFTPClient.logout()).thenThrow(IOException.class);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        uspesifikkMeldingLinjeFtpReader.read();

        // Should not throw an exception
    }

    @Test
    @DisplayName("Tests that an exception is NOT thrown when logging out from the ftp client is unsuccessful.")
    void testNoExceptionWhenLoggingOutFromFtpClientReturnsNoSuccess() throws IOException, LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        Mockito.when(mockedFTPClient.login(Matchers.anyString(), Matchers.anyString())).thenReturn(true);

        final InputStream mockedInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockedInputStream.read()).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(-1);

        Mockito.when(mockedFTPClient.retrieveFileStream(Matchers.anyString())).thenReturn(mockedInputStream);
        Mockito.when(mockedFTPClient.completePendingCommand()).thenReturn(true);
        Mockito.when(mockedFTPClient.logout()).thenReturn(false);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        uspesifikkMeldingLinjeFtpReader.read();

        // Should not throw an exception
    }

    @Test
    @DisplayName("Tests that an exception is NOT thrown when disconnecting the ftp client throws.")
    void testNoExceptionWhenDisconnectingFtpClientThrows() throws IOException, LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        Mockito.when(mockedFTPClient.login(Matchers.anyString(), Matchers.anyString())).thenReturn(true);

        final InputStream mockedInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockedInputStream.read()).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(-1);

        Mockito.when(mockedFTPClient.retrieveFileStream(Matchers.anyString())).thenReturn(mockedInputStream);
        Mockito.when(mockedFTPClient.completePendingCommand()).thenReturn(true);
        Mockito.when(mockedFTPClient.logout()).thenReturn(true);
        Mockito.when(mockedFTPClient.isConnected()).thenReturn(true);
        Mockito.doThrow(IOException.class).when(mockedFTPClient).disconnect();

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        uspesifikkMeldingLinjeFtpReader.read();

        // Should not throw an exception
    }

    @Test
    @DisplayName("Tests that an exception is NOT thrown when disconnecting an unconnected ftp client.")
    void testNoExceptionWhenDisconnectingUnconnectedFtpClient() throws IOException, LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = Mockito.mock(FTPClient.class);

        Mockito.when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        Mockito.when(mockedFTPClient.login(Matchers.anyString(), Matchers.anyString())).thenReturn(true);

        final InputStream mockedInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockedInputStream.read()).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any())).thenReturn(-1);
        Mockito.when(mockedInputStream.read(Matchers.any(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(-1);

        Mockito.when(mockedFTPClient.retrieveFileStream(Matchers.anyString())).thenReturn(mockedInputStream);
        Mockito.when(mockedFTPClient.completePendingCommand()).thenReturn(true);
        Mockito.when(mockedFTPClient.logout()).thenReturn(true);
        Mockito.when(mockedFTPClient.isConnected()).thenReturn(false);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        uspesifikkMeldingLinjeFtpReader.read();

        // Should not throw an exception
    }

    /*
    TODO: Mock the constructor of InputStreamReader. NB: Coverage will NOT increase.
    @Test
    @DisplayName("Tests that a correct exception is thrown when it is impossible to create an InputStreamReader from the InputStream.")
    public void testExceptionWhenImpossibleToCreateInputStreamReaderFromInputStream() throws IOException {

        enteringTestHeaderLogger.debug(null);

        final FTPClient mockedFTPClient = mock(FTPClient.class);

        when(mockedFTPClient.getReplyCode()).thenReturn(200); // OK-code
        when(mockedFTPClient.login(anyString(), anyString())).thenReturn(true);

        final InputStream mockedInputStream = mock(InputStream.class);
        when(mockedFTPClient.retrieveFileStream(anyString())).thenReturn(mockedInputStream);

        final IMeldingLinjeFileReader uspesifikkMeldingLinjeFtpReader =
            getBiCreator().apply(syntacticallyAcceptableFullyQualifiedInputFileName, mockedFTPClient);

        <when InputStreamReader.new then throw>

        assertThrows(LinjeUnreadableException.class, () -> uspesifikkMeldingLinjeFtpReader.read());
    }
    */

    protected abstract BiFunction<String, FTPClient, IMeldingLinjeFileReader> getBiCreator();
}
