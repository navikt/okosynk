package no.nav.okosynk.testutil;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTestFtpServer {

    private static final Logger logger =
        LoggerFactory.getLogger(AbstractTestFtpServer.class);

    public static final String FTP_TEST_SERVER_HOST_URI       = "localhost";
    public static final int    FTP_TEST_SERVER_PORT           = 2223;
    public static final String FTP_TEST_SERVER_USER           = "okosynkTestuser";
    public static final String FTP_TEST_SERVER_PASSWORD       = "X";
    public static final String FTP_TEST_SERVER_HOME_DIRECTORY = getRootOfWorkingDirectory();

    private static String getRootOfWorkingDirectory() {

        // IOkosynkConfiguration not needed here, as "user.dir" is part of the OS/Java ecosystem.
        final String workingDirectory = System.getProperty("user.dir");

        String possibleParent = workingDirectory;
        File parent = new File(possibleParent);
        do {
            possibleParent = parent.getParent();
            if (possibleParent == null) {
                break;
            } else {
                parent = new File(possibleParent);
            }
        } while (true);

        return parent.getPath();
    }
    // =========================================================================

    protected AbstractTestFtpServer() {

        logger.info(
              System.lineSeparator()                                              + System.lineSeparator()
            + "Test (S)FTP server about to be created"                            + System.lineSeparator()
            + "=================================================================" + System.lineSeparator()
            + "FTP_TEST_SERVER_USER          : {}" + System.lineSeparator()
            + "FTP_TEST_SERVER_PASSWORD      : {}" + System.lineSeparator()
            + "FTP_TEST_SERVER_HOME_DIRECTORY: {}" + System.lineSeparator()
            + "FTP_TEST_SERVER_HOST_URI      : {}" + System.lineSeparator(),
            FTP_TEST_SERVER_USER          , FTP_TEST_SERVER_PASSWORD,
            FTP_TEST_SERVER_HOME_DIRECTORY, FTP_TEST_SERVER_HOST_URI
        );
    }

    public abstract void start();
    public abstract void stop();
}
