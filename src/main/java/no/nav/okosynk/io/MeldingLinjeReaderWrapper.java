package no.nav.okosynk.io;

import java.util.List;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: This indirecting class is no longer needed.
 */
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

  private String getFtpInputFilePath_2(final IOkosynkConfiguration okosynkConfiguration)
      throws ConfigureOrInitializeOkosynkIoException {
    return MeldingLinjeSftpReader.getFtpInputFilePath(
        okosynkConfiguration.getString(getBatchType().getFtpHostUrlKey())
    );
  }

  // =========================================================================
  public MeldingLinjeReaderWrapper(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType) throws ConfigureOrInitializeOkosynkIoException {

    this.batchType = batchType;
    this.wrappedMeldingLinjeFileReader = createMeldingLinjeReader_2(okosynkConfiguration);
  }
  // =========================================================================

  @Override
  public List<String> read()
      throws ConfigureOrInitializeOkosynkIoException,
      AuthenticationOkosynkIoException,
      IoOkosynkIoException,
      NotFoundOkosynkIoException,
      EncodingOkosynkIoException {
    return getWrappedMeldingLinjeFileReader().read();
  }

  @Override
  public boolean removeInputData() {
    return getWrappedMeldingLinjeFileReader().removeInputData();
  }

  @Override
  public Status getStatus() {
    return getWrappedMeldingLinjeFileReader().getStatus();
  }

  // =========================================================================

  private IMeldingLinjeFileReader createMeldingLinjeReader_2(
      final IOkosynkConfiguration okosynkConfiguration)
      throws ConfigureOrInitializeOkosynkIoException {

    final IMeldingLinjeFileReader meldingLinjeFileReader;

    final String fullyQualifiedInputFileName = getFtpInputFilePath_2(okosynkConfiguration);

    logger.info("Using SFTP for " + this.getClass().getSimpleName()
        + ", reading fullyQualifiedInputFileName: \"" + fullyQualifiedInputFileName + "\"");
    meldingLinjeFileReader = createMeldingLinjeSftpReader_2(okosynkConfiguration,
        fullyQualifiedInputFileName);

    return meldingLinjeFileReader;
  }

  private IMeldingLinjeFileReader createMeldingLinjeSftpReader_2(
      final IOkosynkConfiguration okosynkConfiguration,
      final String fullyQualifiedInputFileName) {

    return new MeldingLinjeSftpReader(okosynkConfiguration, getBatchType(),
        fullyQualifiedInputFileName);
  }
}
