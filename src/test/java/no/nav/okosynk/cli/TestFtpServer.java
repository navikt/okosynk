package no.nav.okosynk.cli;

import com.jcraft.jsch.JSch;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestFtpServer
        extends AbstractTestFtpServer {

    private static final Logger logger =
            LoggerFactory.getLogger(TestFtpServer.class);

    private static String FTP_TEST_SERVER_LISTENER_NAME = "default";

    private final FtpServer ftpTestServer;

    public TestFtpServer() {

        super();

        logger.info(
                System.lineSeparator() + System.lineSeparator()
                        + "FTP_TEST_SERVER_LISTENER_NAME : " + FTP_TEST_SERVER_LISTENER_NAME + System.lineSeparator()
                        + "=================================================================" + System.lineSeparator()
                        + System.lineSeparator()
        );

        try {
            final PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
            final UserManager userManager = userManagerFactory.createUserManager();
            final BaseUser testUser = new BaseUser();
            testUser.setName(FTP_TEST_SERVER_USER);
            testUser.setPassword(FTP_TEST_SERVER_PASSWORD);
            testUser.setHomeDirectory(FTP_TEST_SERVER_HOME_DIRECTORY);
            userManager.save(testUser);

            final ListenerFactory listenerFactory = new ListenerFactory();

            listenerFactory.setServerAddress(FTP_TEST_SERVER_HOST_URI);
            listenerFactory.setPort(FTP_TEST_SERVER_PORT);

            final FtpServerFactory factory = new FtpServerFactory();

            factory.setUserManager(userManager);
            factory.addListener(FTP_TEST_SERVER_LISTENER_NAME, listenerFactory.createListener());

            this.ftpTestServer = factory.createServer();

            logger.info(
                    System.lineSeparator()
                            + "Test FTP server successfully created" + System.lineSeparator()
                            + "=================================================================" + System.lineSeparator()
                            + this.ftpTestServer.toString() + System.lineSeparator()
                            + System.lineSeparator()
            );
        } catch (FtpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        logger.info("Test FTP server about to be started...");
        try {
            JSch.setLogger(new OkosynkJschLogger());
            this.ftpTestServer.start();
            logger.info("Test FTP server successfully started.");
        } catch (FtpException e) {
            logger.error("Exception received when trying to start " + this.ftpTestServer.getClass().getName());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        if (this.ftpTestServer != null) {
            try {
                logger.info("Test FTP server about to be stopped...");
                this.ftpTestServer.stop();
                logger.info("Test FTP server successfully stopped.");
            } catch (Throwable e) {
                logger.error("Exception received when trying to stop " + this.ftpTestServer.getClass().getName());
                throw new RuntimeException(e);
            }
        }
    }
}