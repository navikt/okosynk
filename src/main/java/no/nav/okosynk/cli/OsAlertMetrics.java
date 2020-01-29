package no.nav.okosynk.cli;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class OsAlertMetrics extends AbstractAlertMetrics {

  private static OsAlertMetrics singletonInstance;

  protected static OsAlertMetrics getSingletonInstance(
      final IOkosynkConfiguration okosynkConfiguration
  ) {
    if (singletonInstance == null) {
      singletonInstance = new OsAlertMetrics(okosynkConfiguration);
    }
    return OsAlertMetrics.singletonInstance;
  }

  private OsAlertMetrics(final IOkosynkConfiguration okosynkConfiguration) {
    super(okosynkConfiguration, Constants.BATCH_TYPE.OS);
  }
}