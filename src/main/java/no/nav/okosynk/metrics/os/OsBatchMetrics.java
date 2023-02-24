package no.nav.okosynk.metrics.os;

import no.nav.okosynk.metrics.AbstractBatchMetrics;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class OsBatchMetrics extends AbstractBatchMetrics {

  private static OsBatchMetrics singletonInstance;

  private OsBatchMetrics(final IOkosynkConfiguration okosynkConfiguration) {
    super(okosynkConfiguration, Constants.BATCH_TYPE.OS);
  }

  public static OsBatchMetrics getSingletonInstance(
      final IOkosynkConfiguration okosynkConfiguration
  ) {
    if (singletonInstance == null) {
      singletonInstance = new OsBatchMetrics(okosynkConfiguration);
    }
    return OsBatchMetrics.singletonInstance;
  }
}
