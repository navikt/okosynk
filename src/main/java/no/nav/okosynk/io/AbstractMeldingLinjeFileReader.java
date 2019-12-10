package no.nav.okosynk.io;

import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.io.OkosynkIoException.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMeldingLinjeFileReader
    implements IMeldingLinjeFileReader {

  protected interface IResourceContainer {

    void free();
  }

  private static final Logger logger =
      LoggerFactory.getLogger(AbstractMeldingLinjeFileReader.class);

  private final IOkosynkConfiguration okosynkConfiguration;
  private final Constants.BATCH_TYPE batchType;
  private final int retryWaitTimeInMilliseconds;
  private final int maxNumberOfTries;
  private Status status = Status.UNSET;
  private final String fullyQualifiedInputFileName;

  AbstractMeldingLinjeFileReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType,
      final String fullyQualifiedInputFileName) {

    setStatus(Status.ERROR);

    if (fullyQualifiedInputFileName == null) {
      throw new NullPointerException("fullyQualifiedInputFileName er null");
    }

    if (fullyQualifiedInputFileName.trim().isEmpty()) {
      throw new IllegalArgumentException("fullyQualifiedInputFileName er tom eller blank");
    }
    this.retryWaitTimeInMilliseconds =
        AbstractMeldingLinjeFileReader.getRetryWaitTimeInMilliseconds(okosynkConfiguration);
    this.maxNumberOfTries =
        AbstractMeldingLinjeFileReader.getMaxNumberOfReadTries(okosynkConfiguration);
    this.fullyQualifiedInputFileName = fullyQualifiedInputFileName;
    this.okosynkConfiguration = okosynkConfiguration;
    this.batchType = batchType;

    setStatus(Status.OK);
  }

  private static int getRetryWaitTimeInMilliseconds(
      final IOkosynkConfiguration okosynkConfiguration) {
    return okosynkConfiguration
        .getRequiredInt(Constants.FILE_READER_RETRY_WAIT_TIME_IN_MILLISECONDS_KEY);
  }

  private static int getMaxNumberOfReadTries(final IOkosynkConfiguration okosynkConfiguration) {
    return okosynkConfiguration.getRequiredInt(Constants.FILE_READER_MAX_NUMBER_OF_READ_TRIES_KEY);
  }

  // =========================================================================

  public IOkosynkConfiguration getOkosynkConfiguration() {
    return okosynkConfiguration;
  }

  public Constants.BATCH_TYPE getBatchType() {
    return batchType;
  }

  @Override
  public Status getStatus() {
    return status;
  }

  @Override
  final public List<String> read() throws OkosynkIoException {

    List<String> meldinger = null;
    IResourceContainer resourceContainer = null;
    try {
      final int retryWaitTimeInMilliseconds  = getRetryWaitTimeInMilliseconds();
      final int maxNumberOfTries             = getMaxNumberOfReadTries();
      int       numberOfTriesDone            = 0;
      boolean   shouldTryReadingTheInputFile = true;
      while (shouldTryReadingTheInputFile) {
        try {
          logger.debug(
                "About to call lesMeldingerFraFil "
              + "from the batch input file for the {}. time ...",
              numberOfTriesDone + 1
          );
          if (resourceContainer != null) {
            resourceContainer.free();
          }
          resourceContainer = createResourceContainer();
          meldinger = lesMeldingerFraFil(resourceContainer);
          numberOfTriesDone++;
          shouldTryReadingTheInputFile = false;
        } catch (OkosynkIoException okosynkIoException) {
          numberOfTriesDone++;
          if (
            ErrorCode.NOT_FOUND.equals(okosynkIoException.getErrorCode())
            ||
            ErrorCode.IO.equals(okosynkIoException.getErrorCode())
          ) {
            if (numberOfTriesDone < maxNumberOfTries) {
              final String msg =
                  System.lineSeparator()
                + "I have tried reading the input file {}"
                + " times, and I will not give up until I have tried {}"
                + " times."
                + System.lineSeparator()
                + "I will try again in {}"
                + " ms. Until then, I will take a nap."
                + System.lineSeparator();
              logger.warn(
                  msg,
                  numberOfTriesDone,
                  maxNumberOfTries,
                  retryWaitTimeInMilliseconds);
              try {
                logger.debug("Going to sleep, good night!");
                Thread.sleep(retryWaitTimeInMilliseconds);
                logger.debug("Good morning, I just woke up again!");
              } catch (InterruptedException ex) {
                logger.warn(
                    "Ooooops, of unknown reasons, I was waked up before {} "
                  + "ms had passed.",
                  retryWaitTimeInMilliseconds);
              }
              logger.info("I will try re-reading...");
            } else {
              final String msg = "maxNumberOfTries: " + maxNumberOfTries + ", retryWaitTimeInMilliseconds: " + retryWaitTimeInMilliseconds;
              throw new OkosynkIoException(OkosynkIoException.ErrorCode.NUMBER_OF_RETRIES_EXCEEDED, msg, okosynkIoException);
            }
          } else {
            throw okosynkIoException;
          }
        }
      }
    } finally {
      resourceContainer.free();
    }

    return meldinger;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getFullyQualifiedInputFileName() {
    return fullyQualifiedInputFileName;
  }

  protected abstract BufferedReader lagBufferedReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final IResourceContainer resources) throws OkosynkIoException;

  protected abstract IResourceContainer createResourceContainer();

  String getCharsetName(final IOkosynkConfiguration okosynkConfiguration) {
    return okosynkConfiguration.getString(getBatchType().getFtpCharsetNameKey(), "ISO8859_1");
  }

  int getMaxNumberOfReadTries() {
    return this.maxNumberOfTries;
  }

  int getRetryWaitTimeInMilliseconds() {
    return this.retryWaitTimeInMilliseconds;
  }

  private List<String> lesMeldingerFraFil(final IResourceContainer resourceContainer)
      throws OkosynkIoException {

    final BufferedReader bufferedReader =
        lagBufferedReader(this.getOkosynkConfiguration(), resourceContainer);
    final List<String> lines;
    try {
      lines = bufferedReader.lines().collect(Collectors.toList());
    } catch (Throwable e) {
      final String msg =
          "Could not read lines from buffered reader. " + System.lineSeparator()
              + this.toString();
      throw new OkosynkIoException(ErrorCode.READ, msg, e);
    }

    return lines;
  }
}