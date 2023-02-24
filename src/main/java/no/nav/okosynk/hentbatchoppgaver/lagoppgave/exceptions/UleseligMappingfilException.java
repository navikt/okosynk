package no.nav.okosynk.hentbatchoppgaver.lagoppgave.exceptions;

public class UleseligMappingfilException extends RuntimeException {

  public UleseligMappingfilException(final Exception cause) {
    super(cause);
  }

  @Override
  public String toString() {
    return super.getCause().toString();
  }
}
