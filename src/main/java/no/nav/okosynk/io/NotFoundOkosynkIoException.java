package no.nav.okosynk.io;

import com.jcraft.jsch.SftpException;

public class NotFoundOkosynkIoException extends AbstractOkosynkIoException {

  public NotFoundOkosynkIoException(final String msg, final SftpException e) {
    super(msg, e);
  }
}