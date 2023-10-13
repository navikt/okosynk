package no.nav.okosynk.metrics;

import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.metrics.os.OsAlertMetrics;
import no.nav.okosynk.metrics.ur.UrAlertMetrics;
import no.nav.okosynk.config.Constants;

public class AlertMetricsFactory {

  private AlertMetricsFactory() {
  }

  public static AbstractAlertMetrics get(
      final OkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE  batchType) {

    final AbstractAlertMetrics abstractAlertMetrics;
    if (Constants.BATCH_TYPE.UR == batchType) {
      abstractAlertMetrics = UrAlertMetrics.getSingletonInstance(okosynkConfiguration);
    } else {
      abstractAlertMetrics = OsAlertMetrics.getSingletonInstance(okosynkConfiguration);
    }
    return abstractAlertMetrics;
  }
}
