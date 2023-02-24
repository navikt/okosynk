package no.nav.okosynk.metrics;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.UserAuthFactory;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.auth.password.UserAuthPasswordFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.SubsystemFactory;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Refs.:
 * <a href="https://superuser.com/questions/677966/ftps-versus-sftp-versus-scp">FTPS versus SFTP versus SCP</a>
 * <a href="https://www.codeguru.com/csharp/.net/net_general/internet/article.php/c14329/FTPS-vs-SFTP-What-to-Choose.htm">FTPS vs. SFTP: What to Choose</a>
 * <a href="https://stackoverflow.com/questions/12803942/secure-ftp-with-org-apache-commons-net-ftp-ftpclient">Secure FTP with org.apache.commons.net.ftp.FTPClient</a>
 * <a href="https://www.goanywhere.com/blog/2011/10/20/sftp-ftps-secure-ftp-transfers">SFTP vs. FTPS: What's the Best Protocol for Secure FTP?</a>
 * <a href="https://stackoverflow.com/questions/40695899/embedded-sftp-server-in-java-code">Embedded SFTP server in java code</a>
 * <a href="http://mina.apache.org/sshd-project/index.html">Apache MINA Overview</a>
 * <a href="https://github.com/stefanbirkner/fake-sftp-server-rule">Fake SFTP Server Rule</a>
 * <a href="https://stackoverflow.com/questions/11837948/using-apache-mina-as-a-mock-in-memory-sftp-server-for-unit-testing">Using Apache Mina as a Mock/In Memory SFTP Server for Unit Testing</a>
 * <a href="https://dzone.com/articles/spring-integration-mock-0">Spring Integration Mock SftpServer Example</a>
 * <a href="https://stackoverflow.com/questions/3076443/java-sftp-server-library">Java SFTP server library?</a>
 */
public class TestSftpServer
        extends AbstractTestFtpServer {

    private static final Logger logger =
            LoggerFactory.getLogger(TestSftpServer.class);

    private final SshServer sshServer;

    public TestSftpServer() {

        super();

        logger.info(
                System.lineSeparator() + System.lineSeparator()
                        + "=================================================================" + System.lineSeparator()
                        + System.lineSeparator()
        );

        try {
            final SshServer sshServer = SshServer.setUpDefaultServer();
            sshServer.setPort(FTP_TEST_SERVER_PORT);
            sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

            final List<UserAuthFactory> userAuthFactories = new ArrayList<>();

            userAuthFactories.add(new UserAuthPasswordFactory());
            sshServer.setUserAuthFactories(userAuthFactories);
            sshServer.setPasswordAuthenticator((username, password, serverSession) -> (
                    FTP_TEST_SERVER_USER.equals(username)
                            &&
                            FTP_TEST_SERVER_PASSWORD.equals(password)
            ));

            sshServer.setCommandFactory(new ScpCommandFactory());
            final List<SubsystemFactory> namedFactoryList = new ArrayList<>();
            namedFactoryList.add(new SftpSubsystemFactory());
            sshServer.setSubsystemFactories(namedFactoryList);

            final VirtualFileSystemFactory virtualFileSystemFactory = new VirtualFileSystemFactory();
            final Path path = Paths.get(FTP_TEST_SERVER_HOME_DIRECTORY);
            virtualFileSystemFactory.setUserHomeDir(FTP_TEST_SERVER_USER, path);
            sshServer.setFileSystemFactory(virtualFileSystemFactory);

            this.sshServer = sshServer;

            logger.info(
                    System.lineSeparator()
                            + "Test SFTP server successfully created" + System.lineSeparator()
                            + "==================================================================" + System.lineSeparator()
                            + this.sshServer + System.lineSeparator()
                            + System.lineSeparator()
            );

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        logger.info("Test SFTP server about to be started...");
        try {
            this.sshServer.start();
            logger.info("Test SFTP server successfully started.");
        } catch (Exception e) {
            logger.error("Exception received when trying to start " + this.sshServer.getClass().getName());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            logger.info("Test SFTP server about to be stopped...");
            this.sshServer.stop();
            logger.info("Test SFTP server successfully stopped.");
        } catch (Exception e) {
            logger.error("Exception received when trying to stop " + this.sshServer.getClass().getName());
            throw new RuntimeException(e);
        }
    }
}
