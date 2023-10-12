package no.nav.okosynk.metrics.os;

import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.metrics.AbstractAlertMetrics;
import no.nav.okosynk.config.Constants;

public class OsAlertMetrics extends AbstractAlertMetrics {

  private static OsAlertMetrics singletonInstance;

  private OsAlertMetrics(final OkosynkConfiguration okosynkConfiguration) {
    super(okosynkConfiguration, Constants.BATCH_TYPE.OS);
  }

  public static OsAlertMetrics getSingletonInstance(
      final OkosynkConfiguration okosynkConfiguration
  ) {
    if (singletonInstance == null) {
      singletonInstance = new OsAlertMetrics(okosynkConfiguration);
    }
    return OsAlertMetrics.singletonInstance;
  }
}
