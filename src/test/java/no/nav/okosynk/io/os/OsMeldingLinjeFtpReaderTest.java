package no.nav.okosynk.io.os;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.function.BiFunction;
import java.util.function.Function;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.io.AbstractMeldingLinjeFtpOrSftpReader;
import no.nav.okosynk.io.MeldingLinjeFtpReader;
import no.nav.okosynk.io.AbstractMeldingLinjeFtpReaderTest;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.OkosynkIoException;
import no.nav.okosynk.io.OkosynkIoException.ErrorCode;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsMeldingLinjeFtpReaderTest
    extends AbstractMeldingLinjeFtpReaderTest {

    // =========================================================================
    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");
    // =========================================================================
    static {
        setFtpHostUrlKey(Constants.BATCH_TYPE.OS.getFtpHostUrlKey());
        setFtpUserKey(Constants.BATCH_TYPE.OS.getFtpUserKey());
        setFtpPasswordKey(Constants.BATCH_TYPE.OS.getFtpPasswordKey());
    }
    // =========================================================================
    @Override
    protected Function<String, IMeldingLinjeFileReader> getMeldingLinjeFileReaderCreator() {
        return
            (fullyQualifiedInputFileNames) ->
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
