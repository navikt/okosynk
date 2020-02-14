package no.nav.okosynk.cli;

import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class FakeBatchMetrics extends AbstractBatchMetrics {

  public FakeBatchMetrics(
      final IOkosynkConfiguration okosynkConfiguration,
      final BATCH_TYPE            batchType) {
    super(okosynkConfiguration, batchType);
  }

  @Override
  protected void pushAdd() {
    // Intentionally NOP
  }
}