package no.nav.okosynk.metrics;

import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class FakeAlertMetrics extends AbstractAlertMetrics {

  public FakeAlertMetrics(
      final IOkosynkConfiguration okosynkConfiguration,
      final BATCH_TYPE            batchType) {
    super(okosynkConfiguration, batchType);
  }

  @Override
  protected void pushAdd() {
    // Intentionally NOP
  }
}