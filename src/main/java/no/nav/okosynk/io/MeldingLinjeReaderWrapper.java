package no.nav.okosynk.io;

import java.util.List;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeldingLinjeReaderWrapper
    implements IMeldingLinjeFileReader {

  private static final Logger logger = LoggerFactory.getLogger(MeldingLinjeReaderWrapper.class);

  IMeldingLinjeFileReader getWrappedMeldingLinjeFileReader() {
    return wrappedMeldingLinjeFileReader;
  }

  public Constants.BATCH_TYPE getBatchType() {
    return batchType;
  }

  private final IMeldingLinjeFileReader wrappedMeldingLinjeFileReader;
  private final Constants.BATCH_TYPE batchType;

  private String getFtpInputFilePath(final IOkosynkConfiguration okosynkConfiguration)
      throws OkosynkIoException {
    return MeldingLinjeSftpReader.getFtpInputFilePath(
        okosynkConfiguration.getString(getBatchType().getFtpHostUrlKey())
    );
  }

  // =========================================================================
  public MeldingLinjeReaderWrapper(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType) {

    this.batchType = batchType;

    final IMeldingLinjeFileReader wrappedMeldingLinjeFileReader;
    try {
      wrappedMeldingLinjeFileReader = createMeldingLinjeReader(okosynkConfiguration);
    } catch (OkosynkIoException e) {
      throw new RuntimeException("Could not create wrappedMeldingLinjeFileReader", e);
    }

    this.wrappedMeldingLinjeFileReader = wrappedMeldingLinjeFileReader;
  }
  // =========================================================================

  @Override
  public List<String> read() throws OkosynkIoException {
    return getWrappedMeldingLinjeFileReader().read();
  }

  @Override
  public Status getStatus() {
    return getWrappedMeldingLinjeFileReader().getStatus();
  }

  // =========================================================================

  private IMeldingLinjeFileReader createMeldingLinjeReader(
      final IOkosynkConfiguration okosynkConfiguration) throws OkosynkIoException {

    final IMeldingLinjeFileReader meldingLinjeFileReader;

    final String fullyQualifiedInputFileName = getFtpInputFilePath(okosynkConfiguration);

    logger.info("Using SFTP for " + this.getClass().getSimpleName()
        + ", reading fullyQualifiedInputFileName: \"" + fullyQualifiedInputFileName + "\"");
    meldingLinjeFileReader = createMeldingLinjeSftpReader(okosynkConfiguration,
        fullyQualifiedInputFileName);

    return meldingLinjeFileReader;
  }

  private IMeldingLinjeFileReader createMeldingLinjeSftpReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final String fullyQualifiedInputFileName) {

    return new MeldingLinjeSftpReader(okosynkConfiguration, getBatchType(),
        fullyQualifiedInputFileName);
  }
}
