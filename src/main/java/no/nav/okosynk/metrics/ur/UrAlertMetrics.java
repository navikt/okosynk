package no.nav.okosynk.metrics.ur;

import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.metrics.AbstractAlertMetrics;
import no.nav.okosynk.config.Constants;

public class UrAlertMetrics extends AbstractAlertMetrics {

  private static UrAlertMetrics singletonInstance;

  private UrAlertMetrics(final OkosynkConfiguration okosynkConfiguration) {
    super(okosynkConfiguration, Constants.BATCH_TYPE.UR);
  }

  public static synchronized UrAlertMetrics getSingletonInstance(
      final OkosynkConfiguration okosynkConfiguration
  ) {
    if (singletonInstance == null) {
      singletonInstance = new UrAlertMetrics(okosynkConfiguration);
    }
    return UrAlertMetrics.singletonInstance;
  }
}
