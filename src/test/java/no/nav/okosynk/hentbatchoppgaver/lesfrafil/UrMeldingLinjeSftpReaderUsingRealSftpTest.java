package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import no.nav.okosynk.config.Constants;
import org.junit.jupiter.api.BeforeAll;

import java.util.function.Function;

/**
 * Refs.:
 * <a href="https://superuser.com/questions/677966/ftps-versus-sftp-versus-scp">FTPS versus SFTP versus SCP</a>
 * <a href="https://www.codeguru.com/csharp/.net/net_general/internet/article.php/c14329/FTPS-vs-SFTP-What-to-Choose.htm">FTPS vs. SFTP: What to Choose</a>
 * <a href="https://stackoverflow.com/questions/12803942/secure-ftp-with-org-apache-commons-net-ftp-ftpclient">Secure FTP with org.apache.commons.net.ftp.FTPClient</a>
 */
public class UrMeldingLinjeSftpReaderUsingRealSftpTest
    extends AbstractMeldingLinjeSftpReaderUsingRealSftpTest {

    static {
        AbstractMeldingLinjeSftpReaderUsingRealSftpTest
            .setFtpTestServerFtpProtocol(Constants.FTP_PROTOCOL.SFTP);
    }

    @BeforeAll
    static void beforeAll() {
        establishAndStartFTPServer();
    }

    @Override
    protected boolean shouldRenameFileAfterSuccessfulRead() {
        return false;
    }

    @Override
    protected String getFtpHostUriKey() {
        return Constants.BATCH_TYPE.UR.getFtpHostUrlKey();
    };

    @Override
    protected String getFtpUserKey() {
        return Constants.BATCH_TYPE.UR.getFtpUserKey();
    };

    @Override
    protected String getFtpPasswordKey() {
        return Constants.BATCH_TYPE.UR.getFtpPasswordKey();
    };

    @Override
    protected Function<String, IMeldingLinjeFileReader> getMeldingLinjeFileReaderCreator() {

        return (
            fullyQualifiedInputFileName) ->
            new MeldingLinjeSftpReader(getOkosynkConfiguration(), Constants.BATCH_TYPE.UR, fullyQualifiedInputFileName);
    }
}