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
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.io.OkosynkIoException.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeldingLinjeSftpReader
    extends AbstractMeldingLinjeFtpOrSftpReader {

  private static class SftpResourceContainer
      extends AbstractMeldingLinjeFtpOrSftpReader.AbstractFtpOrSftpResourceContainer {

    private final JSch jSch;
    private Session sftpSession;
    private ChannelSftp sftpChannel;

    JSch getjSch() {
      return jSch;
    }

    Session getSftpSession() {
      return sftpSession;
    }

    void setSftpSession(Session sftpSession) {
      this.sftpSession = sftpSession;
    }

    ChannelSftp getSftpChannel() {
      return sftpChannel;
    }

    void setSftpChannel(ChannelSftp sftpChannel) {
      this.sftpChannel = sftpChannel;
    }

    private SftpResourceContainer(final JSch jSch) {
      this.jSch = jSch;
    }

    @Override
    public void free() {

      super.free();

      if (this.sftpSession != null) {
        this.sftpSession.disconnect();
      }

      if (this.sftpChannel != null) {
        this.sftpChannel.disconnect();
      }
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(MeldingLinjeSftpReader.class);
  private static final String JSCH_CHANNEL_TYPE_SFTP = "sftp";
  private final JSch jSch;

  private JSch getjSch() {
    return jSch;
  }

  public MeldingLinjeSftpReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType,
      final String fullyQualifiedInputFileName
  ) {
    this(okosynkConfiguration, batchType, fullyQualifiedInputFileName, new JSch());
  }

  private MeldingLinjeSftpReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType,
      final String fullyQualifiedInputFileName,
      final JSch jSch) {

    super(okosynkConfiguration, batchType, fullyQualifiedInputFileName.replace('\\', '/'));

    this.jSch = jSch;
  }

  @Override
  protected BufferedReader lagBufferedReader(
      final IOkosynkConfiguration okosynkConfiguration,
      final AbstractMeldingLinjeFileReader.IResourceContainer resourceContainer)
      throws OkosynkIoException {

    establishSftpResources(
        okosynkConfiguration,
        (MeldingLinjeSftpReader.SftpResourceContainer) resourceContainer);
    final BufferedReader bufferedReader =
        createBufferedReader(okosynkConfiguration,
            (MeldingLinjeSftpReader.SftpResourceContainer) resourceContainer);

    return bufferedReader;
  }

  @Override
  protected AbstractMeldingLinjeFileReader.IResourceContainer createResourceContainer() {
    return new MeldingLinjeSftpReader.SftpResourceContainer(getjSch());
  }

  private void establishSftpResources(
      final IOkosynkConfiguration okosynkConfiguration,
      final MeldingLinjeSftpReader.SftpResourceContainer sftpResourceContainer)
      throws OkosynkIoException {
    try {
      final String sftpUser = this.getFtpUser(okosynkConfiguration);
      final String sftpHostServerName = this.getFtpHostServerName(okosynkConfiguration);
      final int sftpPort = this.getFtpHostPort(okosynkConfiguration);
      final Session sftpSession =
          sftpResourceContainer.getjSch().getSession(sftpUser, sftpHostServerName, sftpPort);
      sftpResourceContainer.setSftpSession(sftpSession);
    } catch (JSchException e) {
      final String msg =
            "Could not establish an sftp session. " + System.lineSeparator()
          + this.toString();
      setStatus(IMeldingLinjeFileReader.Status.ERROR);
      throw new OkosynkIoException(ErrorCode.CONFIGURE_OR_INITIALIZE, msg, e);
    }

    // TODO: What's this?
    sftpResourceContainer.getSftpSession().setConfig("StrictHostKeyChecking", "no");
    final String sftpPassword = this.getFtpPassword(okosynkConfiguration);
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
      final MeldingLinjeSftpReader.SftpResourceContainer sftpResourceContainer)
      throws OkosynkIoException {
    try {
      logger.debug("About to acquire an InputStream from the batch input file...");
      final String fullyQualifiedInputFileName = this.getFullyQualifiedInputFileName();
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
    final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

    return bufferedReader;
  }
}