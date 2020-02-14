package no.nav.okosynk.batch;

public class IoBatchException extends AbstractBatchException {
  public IoBatchException(final Exception cause) {
    super(cause);
  }
}