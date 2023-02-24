package no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions;

import com.jcraft.jsch.SftpException;

public class NotFoundOkosynkIoException extends AbstractOkosynkIoException {

  public NotFoundOkosynkIoException(final String msg, final SftpException e) {
    super(msg, e);
  }
}