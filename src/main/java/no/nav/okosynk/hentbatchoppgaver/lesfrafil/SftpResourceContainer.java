package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SftpResourceContainer {

  private static final Logger logger = LoggerFactory.getLogger(SftpResourceContainer.class);
  private final JSch javaSecureChannel;
  private Session sftpSession;
  private ChannelSftp sftpChannel;
  private InputStream inputStream;

  SftpResourceContainer(final JSch javaSecureChannel) {
    this.javaSecureChannel = javaSecureChannel;
  }

  JSch getJavaSecureChannel() {
    return this.javaSecureChannel;
  }

  Session getSftpSession() {
    return this.sftpSession;
  }

  void setSftpSession(Session sftpSession) {
    this.sftpSession = sftpSession;
  }

  ChannelSftp getSftpChannel() {
    return this.sftpChannel;
  }

  void setSftpChannel(ChannelSftp sftpChannel) {
    this.sftpChannel = sftpChannel;
  }

  InputStream getInputStream() {
    return this.inputStream;
  }

  void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  void free() {

    if (getInputStream() != null) {
      try {
        getInputStream().close();
      } catch (IOException e) {
        logger.warn("Exception when closing the input stream.", e);
      }
    }
    if (this.sftpSession != null) {
      this.sftpSession.disconnect();
    }
    this.sftpSession = null;

    if (this.sftpChannel != null) {
      this.sftpChannel.disconnect();
    }
    this.sftpChannel = null;
  }
}
