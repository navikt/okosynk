package no.nav.okosynk.io;

import java.net.URL;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeldingLinjeReaderWrapper
    implements IMeldingLinjeFileReader {

    private static final Logger logger = LoggerFactory.getLogger(MeldingLinjeReaderWrapper.class);

    @Getter(AccessLevel.PACKAGE)
    private final IMeldingLinjeFileReader wrappedMeldingLinjeFileReader;

    @Getter(AccessLevel.PROTECTED)
    final Constants.BATCH_TYPE batchType;

    // =========================================================================

    private boolean shouldUseSftp(final IOkosynkConfiguration okosynkConfiguration) {
        return AbstractMeldingLinjeFtpOrSftpReader.shouldUseSftp(
                okosynkConfiguration.getString(getBatchType().getFtpHostUrlKey())
            );
    }

    private String getFtpInputFilePath(final IOkosynkConfiguration okosynkConfiguration) throws LinjeUnreadableException {
        return AbstractMeldingLinjeFtpOrSftpReader.getFtpInputFilePath(
            okosynkConfiguration.getString(getBatchType().getFtpHostUrlKey())
        );
    }

    // =========================================================================
    public MeldingLinjeReaderWrapper(
          final IOkosynkConfiguration okosynkConfiguration
        , final Constants.BATCH_TYPE  batchType) {

        this.batchType = batchType;

        final IMeldingLinjeFileReader wrappedMeldingLinjeFileReader;
        try {
            wrappedMeldingLinjeFileReader = createMeldingLinjeReader(okosynkConfiguration);
        } catch (LinjeUnreadableException e) {
            throw new RuntimeException("Could not create wrappedMeldingLinjeFileReader", e);
        }

        this.wrappedMeldingLinjeFileReader = wrappedMeldingLinjeFileReader;
    }
    // =========================================================================

    @Override
    public List<String> read() throws LinjeUnreadableException {
        return getWrappedMeldingLinjeFileReader().read();
    }

    @Override
    public Status getStatus() {
        return getWrappedMeldingLinjeFileReader().getStatus();
    }

    // =========================================================================

    private IMeldingLinjeFileReader createMeldingLinjeReader(
        final IOkosynkConfiguration okosynkConfiguration) throws LinjeUnreadableException {

        final IMeldingLinjeFileReader meldingLinjeFileReader;

        final String fullyQualifiedInputFileName = getFtpInputFilePath(okosynkConfiguration);

        if (shouldUseSftp(okosynkConfiguration)) {
            logger.info("Using SFTP for " + this.getClass().getSimpleName() + ", reading fullyQualifiedInputFileName: \"" + fullyQualifiedInputFileName + "\"");
            meldingLinjeFileReader = createMeldingLinjeSftpReader(okosynkConfiguration, fullyQualifiedInputFileName);
        } else {
            logger.info("Using FTP for " + this.getClass().getSimpleName() + ", reading fullyQualifiedInputFileName: \"" + fullyQualifiedInputFileName + "\"");
            meldingLinjeFileReader = createMeldingLinjeFtpReader(okosynkConfiguration, fullyQualifiedInputFileName);
        }

        return meldingLinjeFileReader;
    }

    private IMeldingLinjeFileReader createMeldingLinjeSftpReader(
        final IOkosynkConfiguration okosynkConfiguration,
        final String                fullyQualifiedInputFileName) {

        return new MeldingLinjeSftpReader(okosynkConfiguration, getBatchType(), fullyQualifiedInputFileName);
    }

    private IMeldingLinjeFileReader createMeldingLinjeFtpReader (
        final IOkosynkConfiguration okosynkConfiguration,
        final String                fullyQualifiedInputFileName) {

        return new MeldingLinjeFtpReader(okosynkConfiguration, getBatchType(), fullyQualifiedInputFileName);
    }

    private String  getFtpHostUrl(final IOkosynkConfiguration okosynkConfiguration) {

        final String ftpHostUrl = okosynkConfiguration.getString(getBatchType().getFtpHostUrlKey());

        return ftpHostUrl;
    }
}
