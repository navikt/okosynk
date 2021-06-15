package no.nav.okosynk.cli;

import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestFtpServerStarter
        implements IStartableAndStoppable {

    // =========================================================================
    private static final Logger logger = LoggerFactory.getLogger(TestFtpServerStarter.class);
    // =========================================================================

    private AbstractTestFtpServer testFtpServer;
    // =========================================================================

    public TestFtpServerStarter(final IOkosynkConfiguration okosynkConfiguration) {
        this.testFtpServer = new TestSftpServer();
    }

    @Override
    public void start() {
        this.testFtpServer.start();
    }

    @Override
    public void stop() {
        if (this.testFtpServer != null) {
            logger.info("About to shutdown the test FTP server...");
            this.testFtpServer.stop();
            this.testFtpServer = null;
        }
    }
}