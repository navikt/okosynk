package no.nav.okosynk.cli.ur;

import no.nav.okosynk.cli.AbstractBatchMetrics;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class UrBatchMetrics extends AbstractBatchMetrics {

  private static UrBatchMetrics singletonInstance;

  private UrBatchMetrics(final IOkosynkConfiguration okosynkConfiguration) {
    super(okosynkConfiguration, Constants.BATCH_TYPE.UR);
  }

  public static UrBatchMetrics getSingletonInstance(
      final IOkosynkConfiguration okosynkConfiguration
  ) {
    if (singletonInstance == null) {
      singletonInstance = new UrBatchMetrics(okosynkConfiguration);
    }
    return UrBatchMetrics.singletonInstance;
  }
}