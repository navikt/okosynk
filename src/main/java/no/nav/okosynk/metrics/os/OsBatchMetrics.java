package no.nav.okosynk.metrics.os;

import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.metrics.AbstractBatchMetrics;
import no.nav.okosynk.config.Constants;

public class OsBatchMetrics extends AbstractBatchMetrics {

  private static OsBatchMetrics singletonInstance;

  private OsBatchMetrics(final OkosynkConfiguration okosynkConfiguration) {
    super(okosynkConfiguration, Constants.BATCH_TYPE.OS);
  }

  public static OsBatchMetrics getSingletonInstance(
      final OkosynkConfiguration okosynkConfiguration
  ) {
    if (singletonInstance == null) {
      singletonInstance = new OsBatchMetrics(okosynkConfiguration);
    }
    return OsBatchMetrics.singletonInstance;
  }
}
