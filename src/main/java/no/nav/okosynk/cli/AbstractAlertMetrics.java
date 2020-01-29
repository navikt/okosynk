package no.nav.okosynk.cli;

import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import java.util.Collections;
import no.nav.okosynk.batch.BatchStatus;
import no.nav.okosynk.cli.os.OsAlertMetrics;
import no.nav.okosynk.cli.ur.UrAlertMetrics;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAlertMetrics extends AbstractMetrics {

  private static final Logger logger = LoggerFactory.getLogger(AbstractAlertMetrics.class);

  private final Gauge                batchAlert;

  protected AbstractAlertMetrics(
      final IOkosynkConfiguration okosynkConfiguration,
      final BATCH_TYPE batchType) {

    super(okosynkConfiguration, batchType);

    final Gauge batchAlert =
        Gauge
            .build()
            .name(batchType.getAlertCollectorMetricName())
            .help("Relates to: Okosynk " + batchType + ": It is as expected that the logs indicate that no further action must be taken. The potential problems can have been automatically solved by e.g. retries. However, the opposite may also be the case. This alert is based on a counter that counts up for each time an " + batchType + " batch fails, so it may be anything above 0. 0 indicates, of course, no errors.")
            .register(getCollectorRegistry());
    batchAlert.set(0);
    this.batchAlert = batchAlert;
  }

  public static AbstractAlertMetrics getSingletonInstance(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType
  ) {
    if (Constants.BATCH_TYPE.UR.equals(batchType)) {
      return UrAlertMetrics.getSingletonInstance(okosynkConfiguration);
    } else {
      return OsAlertMetrics.getSingletonInstance(okosynkConfiguration);
    }
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

    this.batchAlert.inc();

    logger.warn("About to push alert metric(s) to {}...", getPushGatewayEndpointNameAndPort());
    try {
      new PushGateway(getPushGatewayEndpointNameAndPort())
          .pushAdd(
              getCollectorRegistry(),
              "kubernetes-pods",
              Collections.singletonMap("cronjob", getBatchName())
          );
    } catch (Throwable e) {
      logger.error("Pushing alert metric(s) failed", e);
    }
  }
}