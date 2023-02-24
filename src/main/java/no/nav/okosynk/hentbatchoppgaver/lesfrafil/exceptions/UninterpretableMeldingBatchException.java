package no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions;

import no.nav.okosynk.exceptions.AbstractBatchException;

public class UninterpretableMeldingBatchException extends AbstractBatchException {
  public UninterpretableMeldingBatchException(final Exception cause) {
    super(cause);
  }
}