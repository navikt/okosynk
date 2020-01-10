package no.nav.okosynk.io;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.io.OkosynkIoException.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeldingLinjeSftpReader
    implements IMeldingLinjeFileReader {

  private static final Logger logger = LoggerFactory.getLogger(MeldingLinjeSftpReader.class);

  private static final String JSCH_CHANNEL_TYPE_SFTP = "sftp";
  private final JSch jSch;
  private final IOkosynkConfiguration okosynkConfiguration;
  private final Constants.BATCH_TYPE batchType;
  private final int retryWaitTimeInMilliseconds;
  private final int maxNumberOfReadTries;
  private Status status = Status.UNSET;
  private final String fullyQualifiedInputFileName;

  public MeldingLinjeSftpReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType,
      final String fullyQualifiedInputFileName
  ) {

    setStatus(IMeldingLinjeFileReader.Status.ERROR);

    if (okosynkConfiguration == null) {
      throw new NullPointerException("okosynkConfiguration er null");
    }

    if (batchType == null) {
      throw new NullPointerException("batchType er null");
    }

    if (fullyQualifiedInputFileName == null) {
      throw new NullPointerException("fullyQualifiedInputFileName er null");
    }

    if (fullyQualifiedInputFileName.trim().isEmpty()) {
      throw new IllegalArgumentException("fullyQualifiedInputFileName er tom eller blank");
    }

    final String normalizedFullyQualifiedInputFileName =
        fullyQualifiedInputFileName.replace('\\', '/');

    setStatus(IMeldingLinjeFileReader.Status.OK);

    this.retryWaitTimeInMilliseconds =
        MeldingLinjeSftpReader.getRetryWaitTimeInMilliseconds(okosynkConfiguration);
    this.maxNumberOfReadTries =
        MeldingLinjeSftpReader.getMaxNumberOfReadTries(okosynkConfiguration);
    this.fullyQualifiedInputFileName = normalizedFullyQualifiedInputFileName;
    this.okosynkConfiguration = okosynkConfiguration;
    this.batchType = batchType;
    this.jSch = new JSch();

    String msg = "";
    try {
      getFtpProtocol(okosynkConfiguration);
    } catch (Throwable e) {
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
      msg +=
          System.lineSeparator()
              + "ftp protocol is invalid. It must be either "
              + Constants.FTP_PROTOCOL.FTP
              + " or "
              + Constants.FTP_PROTOCOL.SFTP
              + "If left empty, the default value "
              + Constants.FTP_PROTOCOL_DEFAULT_VALUE
              + " will be used.";
    }
    final String ftpHostUrl =
        MeldingLinjeSftpReader.getFtpHostUrl(okosynkConfiguration, getBatchType());
    if (StringUtils.isBlank(ftpHostUrl)) {
      msg = "ftpHostUrl er null eller tom: " + String.valueOf(ftpHostUrl);
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
    }
    try {
      // TODO: Bad programming? Just see that no exception is thrown:
      MeldingLinjeSftpReader.getFtpHostPort(ftpHostUrl);
    } catch (Throwable e) {
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
      msg += System.lineSeparator() + "Cannot deduce port number from URL: " + ftpHostUrl;
    }
    final String ftpUser = MeldingLinjeSftpReader.getFtpUser(okosynkConfiguration, getBatchType());
    if (StringUtils.isBlank(ftpUser)) {
      msg += System.lineSeparator() + "ftpUser er null eller tom: + " + String.valueOf(ftpUser);
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
    }
    final String ftpPassword =
        MeldingLinjeSftpReader.getFtpPassword(okosynkConfiguration, getBatchType());
    if (StringUtils.isBlank(ftpPassword)) {
      msg += System.lineSeparator() + "ftpPassword er null eller tom";
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
    }

    if (IMeldingLinjeFileReader.Status.ERROR.equals(getStatus())) {
      throw new IllegalArgumentException(msg);
    }
  }

  static String getFtpInputFilePath(final String ftpHostUrl)
      throws OkosynkIoException {
    try {
      final URI uri = MeldingLinjeSftpReader.checkFtpHostUrlAndProduceUri(ftpHostUrl);
      return uri.getPath();
    } catch (Throwable e) {
      throw new OkosynkIoException(
          ErrorCode.CONFIGURE_OR_INITIALIZE,
          "Invalid ftpHostUrl found "
              + "when trying to parse the file path: " + ftpHostUrl, e);
    }
  }

  private static Constants.FTP_PROTOCOL getFtpProtocol(final String ftpHostUrl)
      throws OkosynkIoException {

    final Constants.FTP_PROTOCOL ftpHostProtocol;
    try {
      final URI uri = MeldingLinjeSftpReader.checkFtpHostUrlAndProduceUri(ftpHostUrl);
      if (uri.getScheme() == null) {
        logger.warn(
            "ftpHostUrl has a missing protocol, so the default value "
                + Constants.FTP_PROTOCOL_DEFAULT_VALUE
                + " will be used"
        );
        ftpHostProtocol = Constants.FTP_PROTOCOL_DEFAULT_VALUE;
      } else {
        ftpHostProtocol = Constants.FTP_PROTOCOL.valueOf(uri.getScheme().toUpperCase());
      }
    } catch (Throwable e) {
      throw new OkosynkIoException(
          ErrorCode.CONFIGURE_OR_INITIALIZE,
          "Invalid ftpHostUrl found when trying to parse the protocol: " + ftpHostUrl, e);
    }

    return ftpHostProtocol;
  }

  private static String getFtpHostServerName(final String ftpHostUrl)
      throws OkosynkIoException {

    String ftpHostServerName;
    try {
      final URI uri = MeldingLinjeSftpReader.checkFtpHostUrlAndProduceUri(ftpHostUrl);
      ftpHostServerName = uri.getHost();
      if (ftpHostServerName == null) {
        final String authority = uri.getAuthority();
        if (authority == null) {
          ftpHostServerName = ftpHostUrl; // ... which we know is not null at this point
        } else {
          ftpHostServerName = authority;
        }
      }
      final int ftpHostPort = MeldingLinjeSftpReader.getFtpHostPort(ftpHostUrl);
      final String ftpHostPortSuffix = ":" + ftpHostPort;
      if (ftpHostServerName.endsWith(ftpHostPortSuffix)) {
        ftpHostServerName =
            ftpHostServerName.substring(0, ftpHostServerName.lastIndexOf(ftpHostPortSuffix));
      }

      if (ftpHostServerName.contains(":")) {
        throw new OkosynkIoException(
            ErrorCode.CONFIGURE_OR_INITIALIZE,
            "Invalid ftpHostUrl: "
                + ftpHostUrl
                + ", parsed to give an invalid host containing a colon, "
                + "indicating an erroneous port."
        );
      }

    } catch (Throwable e) {
      throw new OkosynkIoException(
          ErrorCode.CONFIGURE_OR_INITIALIZE,
          "Invalid ftpHostUrl found when "
              + "trying to parse the host name: "
              + ftpHostUrl, e);
    }

    return ftpHostServerName;
  }

  private static int getFtpHostPort(final String ftpHostUrl) throws OkosynkIoException {

    final int ftpHostPort;
    try {
      final URI uri = MeldingLinjeSftpReader.checkFtpHostUrlAndProduceUri(ftpHostUrl);
      int tempFtpHostPort = uri.getPort();
      if (tempFtpHostPort == -1) {
        final String authority = uri.getAuthority();
        if (authority != null) {
          final String[] authorityParts = authority.split(":");
          if (authorityParts.length > 1) {
            try {
              tempFtpHostPort =
                  Integer.parseInt(authorityParts[authorityParts.length - 1]);
            } catch (NumberFormatException e) {
              logger.warn(
                  "ftpHostUrl has an invalid or missing port, so the default value "
                      + Constants.FTP_HOST_PORT_DEFAULT_VALUE
                      + " will be used"
              );
              tempFtpHostPort = Constants.FTP_HOST_PORT_DEFAULT_VALUE;
            }
            ftpHostPort = tempFtpHostPort;
          } else {
            logger.warn(
                "ftpHostUrl has an invalid or missing port, so the default value "
                    + Constants.FTP_HOST_PORT_DEFAULT_VALUE
                    + " will be used"
            );
            ftpHostPort = Constants.FTP_HOST_PORT_DEFAULT_VALUE;
          }
        } else {
          logger.warn(
              "ftpHostUrl has an invalid or missing port, so the default value "
                  + Constants.FTP_HOST_PORT_DEFAULT_VALUE
                  + " will be used"
          );
          ftpHostPort = Constants.FTP_HOST_PORT_DEFAULT_VALUE;
        }
      } else {
        ftpHostPort = tempFtpHostPort;
      }
    } catch (Throwable e) {
      throw new OkosynkIoException(
          ErrorCode.CONFIGURE_OR_INITIALIZE,
          "Invalid ftpHostUrl found when "
              + "trying to parse the port: " + ftpHostUrl, e);
    }

    return ftpHostPort;
  }

  private static URI checkFtpHostUrlAndProduceUri(final String ftpHostUrl)
      throws OkosynkIoException {

    final URI uri;
    try {
      if (ftpHostUrl == null) {
        throw new NullPointerException("Host url null");
      } else if (ftpHostUrl.trim().isEmpty()) {
        throw new IllegalArgumentException("Host url blank");
      } else {
        uri = new URI(ftpHostUrl);
      }
    } catch (Throwable e) {
      throw new OkosynkIoException(
          ErrorCode.CONFIGURE_OR_INITIALIZE,
          "Invalid ftpHostUrl found when trying"
              + " to parse the host name: "
              + ftpHostUrl, e);
    }

    return uri;
  }

  private static int getRetryWaitTimeInMilliseconds(
      final IOkosynkConfiguration okosynkConfiguration) {
    return okosynkConfiguration
        .getRequiredInt(Constants.FILE_READER_RETRY_WAIT_TIME_IN_MILLISECONDS_KEY);
  }

  private static int getMaxNumberOfReadTries(final IOkosynkConfiguration okosynkConfiguration) {
    return okosynkConfiguration.getRequiredInt(Constants.FILE_READER_MAX_NUMBER_OF_READ_TRIES_KEY);
  }

  private static String getFtpInputFilePath(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType)
      throws OkosynkIoException {

    return MeldingLinjeSftpReader
        .getFtpInputFilePath(getFtpHostUrl(okosynkConfiguration, batchType));
  }

  private static String getFtpHostUrl(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType) {

    return okosynkConfiguration.getString(batchType.getFtpHostUrlKey());
  }

  private static String getCharsetName(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType) {
    return okosynkConfiguration.getString(batchType.getFtpCharsetNameKey(), "ISO8859_1");
  }

  private static String getFtpUser(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType) {
    return okosynkConfiguration.getString(batchType.getFtpUserKey());
  }

  private static String getFtpPassword(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType) {
    return okosynkConfiguration.getString(batchType.getFtpPasswordKey());
  }

  public SftpResourceContainer createResourceContainer() {
    return new SftpResourceContainer(this.jSch);
  }

  public IOkosynkConfiguration getOkosynkConfiguration() {
    return this.okosynkConfiguration;
  }

  public Constants.BATCH_TYPE getBatchType() {
    return this.batchType;
  }

  @Override
  public Status getStatus() {
    return this.status;
  }

  @Override
  final public List<String> read() throws OkosynkIoException {

    List<String> meldinger = null;
    SftpResourceContainer resourceContainer = null;
    try {
      final int retryWaitTimeInMilliseconds = this.retryWaitTimeInMilliseconds;
      int numberOfTriesDone = 0;
      boolean shouldTryReadingTheInputFile = true;
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
            if (numberOfTriesDone < this.maxNumberOfReadTries) {
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
                  this.maxNumberOfReadTries,
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
              final String msg = "maxNumberOfTries: " + this.maxNumberOfReadTries
                  + ", retryWaitTimeInMilliseconds: " + retryWaitTimeInMilliseconds;
              throw new OkosynkIoException(OkosynkIoException.ErrorCode.NUMBER_OF_RETRIES_EXCEEDED,
                  msg, okosynkIoException);
            }
          } else {
            throw okosynkIoException;
          }
        }
      }
      // At this point in code the read process has been successful,
      //      // and the input file may be renamed:
      renameInputFile(okosynkConfiguration, resourceContainer);
    } finally {
      resourceContainer.free();
    }

    return meldinger;
  }

  @Override
  public String toString() {

    final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();

    return
        "Linjereader properties:" + System.lineSeparator()
            + "=======================" + System.lineSeparator()
            + "ftpHostUrl                 : "
            + (
            MeldingLinjeSftpReader.getFtpHostUrl(okosynkConfiguration, getBatchType()) == null
                ?
                "null"
                :
                    MeldingLinjeSftpReader.getFtpHostUrl(okosynkConfiguration, getBatchType())
        )
            + System.lineSeparator()
            + "user                       : "
            + (
            MeldingLinjeSftpReader.getFtpUser(okosynkConfiguration, getBatchType()) == null
                ?
                "null"
                :
                    MeldingLinjeSftpReader.getFtpUser(okosynkConfiguration, getBatchType())
        )
            + System.lineSeparator()
            + "fully qualified file name  : "
            + (
            this.fullyQualifiedInputFileName == null
                ?
                "null"
                :
                    this.fullyQualifiedInputFileName
        ) + System.lineSeparator()
            + "retryWaitTimeInMilliseconds: "
            + this.retryWaitTimeInMilliseconds + System.lineSeparator()
            + "maxNumberOfReadTries       : "
            + this.maxNumberOfReadTries + System.lineSeparator()
        ;
  }

  List<String> lesMeldingerFraFil(final SftpResourceContainer resourceContainer)
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

  Constants.FTP_PROTOCOL getFtpProtocol(final IOkosynkConfiguration okosynkConfiguration)
      throws OkosynkIoException {

    return MeldingLinjeSftpReader
        .getFtpProtocol(
            MeldingLinjeSftpReader.getFtpHostUrl(okosynkConfiguration,
                getBatchType()
            )
        );
  }

  String getFtpHostServerName(
      final IOkosynkConfiguration okosynkConfiguration) throws OkosynkIoException {

    return MeldingLinjeSftpReader
        .getFtpHostServerName(
            MeldingLinjeSftpReader.getFtpHostUrl(okosynkConfiguration, getBatchType()));
  }

  int getFtpHostPort(
      final IOkosynkConfiguration okosynkConfiguration) throws OkosynkIoException {

    return MeldingLinjeSftpReader
        .getFtpHostPort(
            MeldingLinjeSftpReader.getFtpHostUrl(okosynkConfiguration, getBatchType())
        );
  }

  private BufferedReader lagBufferedReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final SftpResourceContainer resourceContainer)
      throws OkosynkIoException {

    establishSftpResources(okosynkConfiguration, resourceContainer);

    final BufferedReader bufferedReader =
        createBufferedReader(
            okosynkConfiguration,
            resourceContainer);

    return bufferedReader;
  }

  private void renameInputFile(
      final IOkosynkConfiguration okosynkConfiguration,
      final SftpResourceContainer resourceContainer
  ) {
    try {
      final ChannelSftp channelSftp = resourceContainer.getSftpChannel();
      final String home = channelSftp.getHome();
      final String inputFilePath =
          MeldingLinjeSftpReader.getFtpInputFilePath(okosynkConfiguration, getBatchType());
      final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd'T'HH.mm.ss");
      final LocalDateTime now = LocalDateTime.now();
      final String formatDateTime = now.format(formatter);
      final String toFileName = inputFilePath + "." + formatDateTime;
      channelSftp.cd(home);
      channelSftp.rename(inputFilePath, toFileName);
    } catch (Throwable e) {
      logger.warn(
          "Exception when trying to rename the (s)ftp input file. "
              + "Rename will not be done, "
              + "but the program will not be exited. This implies that "
              + "the input file will be re-read the next time the batch is run, "
              + "unless it has been overwritten by a new one.", e);
    }
  }

  private void establishSftpResources(
      final IOkosynkConfiguration okosynkConfiguration,
      final SftpResourceContainer sftpResourceContainer)
      throws OkosynkIoException {

    final Session sftpSession;
    try {
      final String sftpUser =
          MeldingLinjeSftpReader.getFtpUser(okosynkConfiguration, getBatchType());
      final String sftpHostServerName = this.getFtpHostServerName(okosynkConfiguration);
      final int sftpPort = this.getFtpHostPort(okosynkConfiguration);
      sftpSession =
          sftpResourceContainer.getjSch().getSession(sftpUser, sftpHostServerName, sftpPort);

    } catch (JSchException e) {
      final String msg =
          "Could not establish an sftp session. " + System.lineSeparator()
              + this.toString();
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
      throw new OkosynkIoException(ErrorCode.CONFIGURE_OR_INITIALIZE, msg, e);
    }
    sftpResourceContainer.setSftpSession(sftpSession);

    // TODO: What's this?
    sftpResourceContainer.getSftpSession().setConfig("StrictHostKeyChecking", "no");
    final String sftpPassword =
        MeldingLinjeSftpReader.getFtpPassword(okosynkConfiguration, getBatchType());
    sftpResourceContainer.getSftpSession().setPassword(sftpPassword);
    try {
      sftpResourceContainer.getSftpSession().connect();
    } catch (JSchException e) {
      final String msg =
          "Could not connect SFTP session. " + System.lineSeparator()
              + this.toString();
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
      if ("Auth fail".equals(e.getMessage())) {
        throw new OkosynkIoException(ErrorCode.AUTHENTICATION, msg, e);
      } else {
        throw new OkosynkIoException(ErrorCode.CONFIGURE_OR_INITIALIZE, msg, e);
      }
    }

    try {
      final Channel channel =
          sftpResourceContainer.getSftpSession().openChannel(JSCH_CHANNEL_TYPE_SFTP);
      sftpResourceContainer.setSftpChannel((ChannelSftp) channel);
    } catch (JSchException e) {
      final String msg =
          "Could not run openChannel on SFTP session. " + System.lineSeparator()
              + this.toString();
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
      throw new OkosynkIoException(ErrorCode.IO, msg, e);
    }

    try {
      sftpResourceContainer.getSftpChannel().connect();
    } catch (JSchException e) {
      final String msg =
          "Could not connect to channel. " + System.lineSeparator()
              + this.toString();
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
      throw new OkosynkIoException(ErrorCode.IO, msg, e);
    }
  }

  private BufferedReader createBufferedReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final SftpResourceContainer sftpResourceContainer)
      throws OkosynkIoException {
    try {
      logger.debug("About to acquire an InputStream from the batch input file...");
      final String fullyQualifiedInputFileName = this.fullyQualifiedInputFileName;
      final InputStream inputStream =
          sftpResourceContainer
              .getSftpChannel()
              .get(fullyQualifiedInputFileName);
      if (inputStream == null) {
        final String msg =
            "InputStream instance acquired from ChannelSftp is null, "
                + "which most probably is caused by the file not existing.";
        throw new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, msg);
      }
      sftpResourceContainer.setInputStream(inputStream);
    } catch (SftpException e) {
      final ErrorCode errorCode;
      final String msg;
      if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
        errorCode = ErrorCode.NOT_FOUND;
        msg = "Input file does not exist";
      } else {
        errorCode = ErrorCode.IO;
        msg = "Could not acquire an input stream from the sftp channel.";
      }
      throw new OkosynkIoException(errorCode, msg + System.lineSeparator() + this.toString(), e);
    }
    final InputStreamReader inputStreamReader;
    try {
      inputStreamReader =
          createInputStreamReader(okosynkConfiguration, sftpResourceContainer);
    } catch (UnsupportedEncodingException e) {
      final String msg =
          "Could not acquire an InputStreamReader for the input stream. "
              + System.lineSeparator()
              + this.toString();
      throw new OkosynkIoException(ErrorCode.ENCODING, msg, e);
    }
    assert
        (inputStreamReader != null)
        :
        "Programming error: (inputStreamReader == null) after leaving the while loop.";

    return new BufferedReader(inputStreamReader);
  }

  private InputStreamReader createInputStreamReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final SftpResourceContainer resourceContainer)
      throws UnsupportedEncodingException {

    return new InputStreamReader(
        resourceContainer.getInputStream(),
        getCharsetName(okosynkConfiguration, getBatchType())
    );
  }

  private void setStatus(final Status status) {
    this.status = status;
  }
}