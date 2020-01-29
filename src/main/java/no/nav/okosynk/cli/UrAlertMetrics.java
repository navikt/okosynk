package no.nav.okosynk.cli;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class UrAlertMetrics extends AbstractAlertMetrics {

  private static UrAlertMetrics singletonInstance;

  protected static UrAlertMetrics getSingletonInstance(
      final IOkosynkConfiguration okosynkConfiguration
  ) {
    if (singletonInstance == null) {
      singletonInstance = new UrAlertMetrics(okosynkConfiguration);
    }
    return UrAlertMetrics.singletonInstance;
  }

  private UrAlertMetrics(final IOkosynkConfiguration okosynkConfiguration) {
    super(okosynkConfiguration, Constants.BATCH_TYPE.UR);
  }
}