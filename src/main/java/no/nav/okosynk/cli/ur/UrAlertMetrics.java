package no.nav.okosynk.cli.ur;

import no.nav.okosynk.cli.AbstractAlertMetrics;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class UrAlertMetrics extends AbstractAlertMetrics {

  private static UrAlertMetrics singletonInstance;

  private UrAlertMetrics(final IOkosynkConfiguration okosynkConfiguration) {
    super(okosynkConfiguration, Constants.BATCH_TYPE.UR);
  }

  public static UrAlertMetrics getSingletonInstance(
      final IOkosynkConfiguration okosynkConfiguration
  ) {
    if (singletonInstance == null) {
      singletonInstance = new UrAlertMetrics(okosynkConfiguration);
    }
    return UrAlertMetrics.singletonInstance;
  }
}