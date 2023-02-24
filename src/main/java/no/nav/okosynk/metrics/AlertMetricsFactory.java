package no.nav.okosynk.metrics;

import no.nav.okosynk.metrics.os.OsAlertMetrics;
import no.nav.okosynk.metrics.ur.UrAlertMetrics;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class AlertMetricsFactory {

  private AlertMetricsFactory() {
  }

  public static AbstractAlertMetrics get(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE  batchType) {

    final AbstractAlertMetrics abstractAlertMetrics;
    if (Constants.BATCH_TYPE.UR.equals(batchType)) {
      abstractAlertMetrics = UrAlertMetrics.getSingletonInstance(okosynkConfiguration);
    } else {
      abstractAlertMetrics = OsAlertMetrics.getSingletonInstance(okosynkConfiguration);
    }
    return abstractAlertMetrics;
  }
}