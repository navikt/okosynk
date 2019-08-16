package no.nav.okosynk.cli;

import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.testutil.TestFtpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpServerTestStarter
    implements IStartableAndStoppable {

    // =========================================================================
    private static final Logger logger = LoggerFactory.getLogger(FtpServerTestStarter.class);
    // =========================================================================
    private TestFtpServer testFtpServer;
    // =========================================================================

    public FtpServerTestStarter(final IOkosynkConfiguration okosynkConfiguration) {
        this.testFtpServer = new TestFtpServer();
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
