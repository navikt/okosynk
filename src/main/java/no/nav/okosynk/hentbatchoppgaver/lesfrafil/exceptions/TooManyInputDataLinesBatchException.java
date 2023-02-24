package no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions;

import no.nav.okosynk.exceptions.AbstractBatchException;

public class TooManyInputDataLinesBatchException extends AbstractBatchException {

  public TooManyInputDataLinesBatchException(
      final int actualnumberOfOppgaverRetrievedFromBatchInput,
      final int upperLimitOfOppgaverRetrievedFromBatchInput) {
    super(
        String.format(
            "Batch data input contains %d lines. "
          + "The greatest number of lines that okosynk can handle is %d.",
          actualnumberOfOppgaverRetrievedFromBatchInput,
          upperLimitOfOppgaverRetrievedFromBatchInput
    ));
  }
}
