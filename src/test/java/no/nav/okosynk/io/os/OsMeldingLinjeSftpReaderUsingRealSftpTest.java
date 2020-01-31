package no.nav.okosynk.io.os;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Function;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.AbstractMeldingLinjeSftpReaderUsingRealSftpTest;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.MeldingLinjeSftpReader;
import no.nav.okosynk.io.OkosynkIoException;
import no.nav.okosynk.io.OkosynkIoException.ErrorCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Refs.:
 * <a href="https://superuser.com/questions/677966/ftps-versus-sftp-versus-scp">FTPS versus SFTP versus SCP</a>
 * <a href="https://www.codeguru.com/csharp/.net/net_general/internet/article.php/c14329/FTPS-vs-SFTP-What-to-Choose.htm">FTPS vs. SFTP: What to Choose</a>
 * <a href="https://stackoverflow.com/questions/12803942/secure-ftp-with-org-apache-commons-net-ftp-ftpclient">Secure FTP with org.apache.commons.net.ftp.FTPClient</a>
 */
public class OsMeldingLinjeSftpReaderUsingRealSftpTest
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
        return Constants.BATCH_TYPE.OS.getFtpHostUrlKey();
    }

    @Override
    protected String getFtpUserKey() {
        return Constants.BATCH_TYPE.OS.getFtpUserKey();
    }

    @Override
    protected String getFtpPasswordKey() {
        return Constants.BATCH_TYPE.OS.getFtpPasswordKey();
    }

    @Override
    protected Function<String, IMeldingLinjeFileReader> getMeldingLinjeFileReaderCreator() {
        return (
            fullyQualifiedInputFileName)
            ->
            new MeldingLinjeSftpReader(
                getOkosynkConfiguration(), Constants.BATCH_TYPE.OS, fullyQualifiedInputFileName
            );
    }
}
