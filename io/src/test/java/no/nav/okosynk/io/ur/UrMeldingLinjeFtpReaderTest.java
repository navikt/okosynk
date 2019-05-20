package no.nav.okosynk.io.ur;

import java.util.function.BiFunction;
import java.util.function.Function;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.MeldingLinjeFtpReader;
import no.nav.okosynk.io.MeldingLinjeFtpReaderTest;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrMeldingLinjeFtpReaderTest
    extends MeldingLinjeFtpReaderTest {

    // =========================================================================
    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");
    // =========================================================================
    static {
        setFTP_HOST_URL_KEY(Constants.BATCH_TYPE.UR.getFtpHostUrlKey());
        setFTP_USER_KEY(Constants.BATCH_TYPE.UR.getFtpUserKey());
        setFTP_PASSWORD_KEY(Constants.BATCH_TYPE.UR.getFtpPasswordKey());
    }
    // =========================================================================
    @Override
    protected Function<String, IMeldingLinjeFileReader> getCreator() {
        return (
            fullyQualifiedInputFileNames) ->
            new MeldingLinjeFtpReader(getOkosynkConfiguration(), Constants.BATCH_TYPE.UR, fullyQualifiedInputFileNames);
    }

    @Override
    protected BiFunction<String, FTPClient, IMeldingLinjeFileReader> getBiCreator() {
        return (
            fullyQualifiedInputFileNames, ftpClient) ->
            new MeldingLinjeFtpReader(getOkosynkConfiguration(), Constants.BATCH_TYPE.UR, fullyQualifiedInputFileNames, ftpClient);
    }
    // =========================================================================
}
