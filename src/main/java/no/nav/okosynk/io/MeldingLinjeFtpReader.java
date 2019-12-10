package no.nav.okosynk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.io.OkosynkIoException.ErrorCode;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeldingLinjeFtpReader
    extends AbstractMeldingLinjeFtpOrSftpReader {

  private static class FtpResourceContainer
      extends AbstractMeldingLinjeFtpOrSftpReader.AbstractFtpOrSftpResourceContainer {

    FTPClient getFtpClient() {
      return ftpClient;
    }

    private final FTPClient ftpClient;

    private FtpResourceContainer(final FTPClient ftpClient) {
      this.ftpClient = ftpClient;
    }

    @Override
    public void free() {

      super.free();

      if (getFtpClient() != null) {

        String status = null;
        try {
          status = getFtpClient().getStatus();
        } catch (Throwable e) {
          logger.warn("Exception when getting status from the FTP client.", e);
        }

        if (
            (status != null)
                &&
                (getInputStream() != null)
        ) {
          // TODO: Not confirmed that the following works: Testing on status and input stream is the only way trying to prevent completePendingCommand from hanging.
          try {
            final boolean success = getFtpClient().completePendingCommand();
            if (!success) {
              logger.warn(
                    "Completing the pending command "
                  + "from the FTP client gave failure"
                  + " without an exception");
            }
          } catch (Throwable e) {
            logger.warn("Exception when completing the pending command from the FTP client.", e);
          }
        }

        try {
          getFtpClient().logout();
        } catch (IOException e) {
          logger.warn("Exception when logging out the FTP client.", e);
        }

        if (getFtpClient().isConnected()) {
          try {
            getFtpClient().disconnect();
          } catch (IOException ioe) {
            logger.warn("Exception when disconnecting the FTP client.", ioe);
          }
        }
      }
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(MeldingLinjeFtpReader.class);

  private FTPClient getFtpClient() {
    return ftpClient;
  }

  private final FTPClient ftpClient;

  public MeldingLinjeFtpReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType,
      final String fullyQualifiedInputFileName
  ) {
    this(okosynkConfiguration, batchType, fullyQualifiedInputFileName, new FTPClient());
  }

  public MeldingLinjeFtpReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType,
      final String fullyQualifiedInputFileName,
      final FTPClient ftpClient) {

    super(okosynkConfiguration, batchType, fullyQualifiedInputFileName.replace('\\', '/'));

    this.ftpClient = ftpClient;
  }

  @Override
  protected BufferedReader lagBufferedReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final AbstractMeldingLinjeFileReader.IResourceContainer resourceContainer)
      throws OkosynkIoException {

    establishFtpResources(
        okosynkConfiguration,
        (MeldingLinjeFtpReader.FtpResourceContainer) resourceContainer);
    final BufferedReader bufferedReader =
        createBufferedReader(okosynkConfiguration,
            (MeldingLinjeFtpReader.FtpResourceContainer) resourceContainer);

    return bufferedReader;
  }

  @Override
  protected AbstractMeldingLinjeFileReader.IResourceContainer createResourceContainer() {
    return new MeldingLinjeFtpReader.FtpResourceContainer(getFtpClient());
  }

  private void establishFtpResources(
      final IOkosynkConfiguration okosynkConfiguration,
      final MeldingLinjeFtpReader.FtpResourceContainer ftpResourceContainer)
      throws OkosynkIoException {

    try {
      final String ftpHostServerName = this.getFtpHostServerName(okosynkConfiguration);
      final int ftpHostPort = this.getFtpHostPort(okosynkConfiguration);
      ftpResourceContainer
          .getFtpClient()
          .setConnectTimeout(this.getFtpConnectionTimeoutInMs(okosynkConfiguration));
      ftpResourceContainer.getFtpClient().connect(ftpHostServerName, ftpHostPort);
    } catch (IOException e) {
      final String msg =
          "Could not connect an ftp client. " + System.lineSeparator()
              + this.toString();

      setStatus(IMeldingLinjeFileReader.Status.ERROR);
      throw new OkosynkIoException(ErrorCode.CONFIGURE_OR_INITIALIZE, msg, e);
    }

    final int reply = ftpResourceContainer.getFtpClient().getReplyCode();
    if (!FTPReply.isPositiveCompletion(reply)) {

      final String[] replyStrings = getFtpClient().getReplyStrings();
      final StringBuffer msgStringBuffer =
          new StringBuffer("The connected FTP client has a reply code indicating faliure: ");

      for (final String replyString : replyStrings) {
        msgStringBuffer.append(System.lineSeparator()).append(replyString);
      }
      final String msg = msgStringBuffer.toString();
      logger.error(msg);
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
      throw new OkosynkIoException(ErrorCode.IO, msg);
    }

    final boolean loginOk;
    try {
      loginOk =
          ftpResourceContainer.getFtpClient().login(this.getFtpUser(okosynkConfiguration),
              this.getFtpPassword(okosynkConfiguration));
    } catch (IOException e) {
      final String msg =
          "Could not log in to the connected FTP server. " + System.lineSeparator()
              + this.toString();

      setStatus(IMeldingLinjeFileReader.Status.ERROR);
      throw new OkosynkIoException(ErrorCode.AUTHENTICATION, msg, e);
    }
    if (!loginOk) {
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
      throw new OkosynkIoException(ErrorCode.AUTHENTICATION, "Login returned an erroneous return code");
    }
  }

  private BufferedReader createBufferedReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final MeldingLinjeFtpReader.FtpResourceContainer ftpResourceContainer)
      throws OkosynkIoException {

    final String fullyQualifiedInputFileName = this.getFullyQualifiedInputFileName();
    final InputStream inputStream;
    try {
      inputStream =
          ftpResourceContainer
              .getFtpClient()
              .retrieveFileStream(fullyQualifiedInputFileName);
    } catch (IOException e) {
      final String msg =
          "Could not acquire an input stream. " + System.lineSeparator()
              + this.toString();
      throw new OkosynkIoException(ErrorCode.IO, msg, e);
    }
    if (inputStream == null) {
      final String msg =
            "InputStream instance acquired from FtpClient is null, "
          + "which most probably is caused by the file not existing."
          + System.lineSeparator()
          + this.toString();
      throw new OkosynkIoException(ErrorCode.NOT_FOUND, msg);
    }
    ftpResourceContainer.setInputStream(inputStream);

    final InputStreamReader inputStreamReader;
    try {
      inputStreamReader = createInputStreamReader(okosynkConfiguration, ftpResourceContainer);
    } catch (UnsupportedEncodingException e) {
      final String msg =
          "Could not acquire an InputStreamReaderfor the input stream. " + System.lineSeparator()
              + this.toString();
      throw new OkosynkIoException(ErrorCode.ENCODING, msg, e);
    }

    final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

    return bufferedReader;
  }
}
