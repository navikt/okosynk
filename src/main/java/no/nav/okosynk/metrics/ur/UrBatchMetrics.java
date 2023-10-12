package no.nav.okosynk.metrics.ur;

import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.metrics.AbstractBatchMetrics;
import no.nav.okosynk.config.Constants;

public class UrBatchMetrics extends AbstractBatchMetrics {

  private static UrBatchMetrics singletonInstance;

  private UrBatchMetrics(final OkosynkConfiguration okosynkConfiguration) {
    super(okosynkConfiguration, Constants.BATCH_TYPE.UR);
  }

  public static UrBatchMetrics getSingletonInstance(
      final OkosynkConfiguration okosynkConfiguration
  ) {
    if (singletonInstance == null) {
      singletonInstance = new UrBatchMetrics(okosynkConfiguration);
    }
    return UrBatchMetrics.singletonInstance;
  }
}
