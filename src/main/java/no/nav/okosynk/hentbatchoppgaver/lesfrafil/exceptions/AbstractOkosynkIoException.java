package no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions;

public abstract class AbstractOkosynkIoException extends Exception {

  protected AbstractOkosynkIoException(final String message) {
    this(message, null);
  }

  protected AbstractOkosynkIoException(
      final String message,
      final Throwable cause) {
    super(message, cause);
  }
}
