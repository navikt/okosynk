package no.nav.okosynk.metrics;

import io.prometheus.client.Gauge;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlertMetrics extends AbstractMetrics {
    private static final Logger logger = LoggerFactory.getLogger(AlertMetrics.class);

    private static AlertMetrics singletonInstance;

    public static AlertMetrics getSingletonInstance(
            final OkosynkConfiguration okosynkConfiguration
    ) {
        if (singletonInstance == null) {
            singletonInstance = new AlertMetrics(okosynkConfiguration);
        }
        return AlertMetrics.singletonInstance;
    }

    private final Gauge batchAlertGauge;

    public AlertMetrics(final OkosynkConfiguration okosynkConfiguration) {
        super(okosynkConfiguration);
        this.batchAlertGauge = Gauge.build()
                .name(getBatchType().getAlertCollectorMetricName())
                .help("Relates to: Okosynk " + getBatchType() + ": It is as expected that the logs indicate that no further action must be taken. " +
                        "The potential problems can have been automatically solved by e.g. retries. However, the opposite may also be the case. " +
                        "This alert is based on a counter that counts up for each time an " + getBatchType() + " batch fails, so it may be anything above 0. 0 indicates, of course, no errors.")
                .register(getCollectorRegistry());

        pushAdd();
    }

    public void generateCheckTheLogAlertBasedOnBatchStatus(final BatchStatus batchStatus) {
        if (batchStatus.shouldAlert()) {
            generateCheckTheLogAlert();
        }
    }

    public void generateCheckTheLogAlert() {
        this.batchAlertGauge.inc();
        logger.warn("{} about to push alert metric(s) to {}...", getBatchName(), getPushGatewayEndpointNameAndPort());
        pushAdd();
    }
}
