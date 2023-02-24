package no.nav.okosynk.metrics.os;

import no.nav.okosynk.metrics.AbstractAlertMetrics;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class OsAlertMetrics extends AbstractAlertMetrics {

  private static OsAlertMetrics singletonInstance;

  private OsAlertMetrics(final IOkosynkConfiguration okosynkConfiguration) {
    super(okosynkConfiguration, Constants.BATCH_TYPE.OS);
  }

  public static OsAlertMetrics getSingletonInstance(
      final IOkosynkConfiguration okosynkConfiguration
  ) {
    if (singletonInstance == null) {
      singletonInstance = new OsAlertMetrics(okosynkConfiguration);
    }
    return OsAlertMetrics.singletonInstance;
  }
}