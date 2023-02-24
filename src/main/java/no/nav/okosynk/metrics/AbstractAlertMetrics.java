package no.nav.okosynk.metrics;

import io.prometheus.client.Gauge;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAlertMetrics extends AbstractMetrics {

  private static final Logger logger = LoggerFactory.getLogger(AbstractAlertMetrics.class);

  private final Gauge batchAlertGauge;

  protected AbstractAlertMetrics(
      final IOkosynkConfiguration okosynkConfiguration,
      final BATCH_TYPE batchType) {

    super(okosynkConfiguration, batchType);

    final Gauge batchAlertGauge =
        Gauge
            .build()
            .name(batchType.getAlertCollectorMetricName())
            .help("Relates to: Okosynk " + batchType + ": It is as expected that the logs indicate that no further action must be taken. The potential problems can have been automatically solved by e.g. retries. However, the opposite may also be the case. This alert is based on a counter that counts up for each time an " + batchType + " batch fails, so it may be anything above 0. 0 indicates, of course, no errors.")
            .register(getCollectorRegistry());
    this.batchAlertGauge = batchAlertGauge;

    batchAlertGauge.set(0);
    pushAdd();
  }

  public void generateCheckTheLogAlertBasedOnBatchStatus(final BatchStatus batchStatus) {
    if (batchStatus.shouldAlert()) {
      generateCheckTheLogAlert();
    }
  }

  /**
   * Never throws any exception
   */
  public void generateCheckTheLogAlert() {

    this.batchAlertGauge.inc();
    logger.warn(getBatchName() + " about to push alert metric(s) to {}...", getPushGatewayEndpointNameAndPort());
    pushAdd();
  }
}