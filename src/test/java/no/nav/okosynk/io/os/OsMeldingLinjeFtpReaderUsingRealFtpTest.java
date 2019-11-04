package no.nav.okosynk.io.os;

import java.util.function.Function;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.MeldingLinjeFtpReader;
import no.nav.okosynk.io.AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import org.junit.jupiter.api.BeforeAll;

/**
 * Refs.:
 * <a href="https://superuser.com/questions/677966/ftps-versus-sftp-versus-scp">FTPS versus SFTP versus SCP</a>
 * <a href="https://www.codeguru.com/csharp/.net/net_general/internet/article.php/c14329/FTPS-vs-SFTP-What-to-Choose.htm">FTPS vs. SFTP: What to Choose</a>
 * <a href="https://stackoverflow.com/questions/12803942/secure-ftp-with-org-apache-commons-net-ftp-ftpclient">Secure FTP with org.apache.commons.net.ftp.FTPClient</a>
 */
public class OsMeldingLinjeFtpReaderUsingRealFtpTest
    extends AbstractOsMeldingLinjeFtpReaderTestUsingRealFtpOrSftp {

    static {
        AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp
            .setFtpTestServerFtpProtocol(Constants.FTP_PROTOCOL.FTP);
    }

    @BeforeAll
    public static void beforeAll() {
        establishAndStartFTPServer();
    }

    @Override
    protected Function<String, IMeldingLinjeFileReader> getCreator() {

        return (
            fullyQualifiedInputFileName) ->
            new MeldingLinjeFtpReader(getOkosynkConfiguration(), Constants.BATCH_TYPE.OS, fullyQualifiedInputFileName);
    }
}
