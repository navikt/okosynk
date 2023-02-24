package no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions;

public class ConfigureOrInitializeOkosynkIoException extends AbstractOkosynkIoException {

  public ConfigureOrInitializeOkosynkIoException(final String msg) {
    super(msg);
  }

  public ConfigureOrInitializeOkosynkIoException(final String msg, final Throwable e) {
    super(msg, e);
  }
}