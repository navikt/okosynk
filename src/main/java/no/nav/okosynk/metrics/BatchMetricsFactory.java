package no.nav.okosynk.metrics;

import no.nav.okosynk.metrics.os.OsBatchMetrics;
import no.nav.okosynk.metrics.ur.UrBatchMetrics;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class BatchMetricsFactory {

  private BatchMetricsFactory() {
  }

  public static AbstractBatchMetrics get(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE  batchType) {

    final AbstractBatchMetrics abstractBatchMetrics;
    if (Constants.BATCH_TYPE.UR.equals(batchType)) {
      abstractBatchMetrics = UrBatchMetrics.getSingletonInstance(okosynkConfiguration);
    } else {
      abstractBatchMetrics = OsBatchMetrics.getSingletonInstance(okosynkConfiguration);
    }
    return abstractBatchMetrics;
  }
}