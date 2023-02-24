package no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions;

import no.nav.okosynk.exceptions.AbstractBatchException;

public class InputDataNotFoundBatchException extends AbstractBatchException {
  public InputDataNotFoundBatchException(final Exception cause) {
    super(cause);
  }
}
