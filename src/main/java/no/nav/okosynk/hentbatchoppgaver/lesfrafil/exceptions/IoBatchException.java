package no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions;

import no.nav.okosynk.exceptions.AbstractBatchException;

public class IoBatchException extends AbstractBatchException {
  public IoBatchException(final Exception cause) {
    super(cause);
  }
}