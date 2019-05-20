package no.nav.okosynk.io.os;

import java.util.function.BiFunction;
import java.util.function.Function;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.MeldingLinjeFtpReader;
import no.nav.okosynk.io.MeldingLinjeFtpReaderTest;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsMeldingLinjeFtpReaderTest
    extends MeldingLinjeFtpReaderTest {

    // =========================================================================
    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");
    // =========================================================================
    static {
        setFTP_HOST_URL_KEY(Constants.BATCH_TYPE.OS.getFtpHostUrlKey());
        setFTP_USER_KEY(Constants.BATCH_TYPE.OS.getFtpUserKey());
        setFTP_PASSWORD_KEY(Constants.BATCH_TYPE.OS.getFtpPasswordKey());
    }
    // =========================================================================
    @Override
    protected Function<String, IMeldingLinjeFileReader> getCreator() {
        return (
            fullyQualifiedInputFileNames) ->
            new MeldingLinjeFtpReader(getOkosynkConfiguration(), Constants.BATCH_TYPE.OS, fullyQualifiedInputFileNames);
    }

    @Override
    protected BiFunction<String, FTPClient, IMeldingLinjeFileReader> getBiCreator() {
        return (
            fullyQualifiedInputFileNames, ftpClient) ->
            new MeldingLinjeFtpReader(getOkosynkConfiguration(), Constants.BATCH_TYPE.OS, fullyQualifiedInputFileNames, ftpClient);
    }
    // =========================================================================
}
