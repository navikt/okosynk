package no.nav.okosynk.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

abstract class AbstractMetrics {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMetrics.class);

    private final Constants.BATCH_TYPE batchType;
    private final String pushGatewayEndpointNameAndPort;
    private final CollectorRegistry collectorRegistry;
    private final PushGateway pushGateway;

    protected AbstractMetrics(
            final IOkosynkConfiguration okosynkConfiguration,
            final BATCH_TYPE batchType) {

        final String pushGatewayEndpointNameAndPort =
                okosynkConfiguration
                        .getPrometheusAddress("nais-prometheus-prometheus-pushgateway.nais:9091");

        final CollectorRegistry collectorRegistry = new CollectorRegistry();
        collectorRegistry.clear();
        this.batchType = batchType;
        this.pushGatewayEndpointNameAndPort = pushGatewayEndpointNameAndPort;
        this.collectorRegistry = collectorRegistry;
        this.pushGateway = new PushGateway(this.pushGatewayEndpointNameAndPort);
    }

    public Constants.BATCH_TYPE getBatchType() {
        return this.batchType;
    }

    protected String getBatchName() {
        return this.batchType.getName();
    }

    protected String getPushGatewayEndpointNameAndPort() {
        return this.pushGatewayEndpointNameAndPort;
    }

    protected CollectorRegistry getCollectorRegistry() {
        return this.collectorRegistry;
    }

    protected void pushAdd() {

        logger.info("Pusher {}-metrikk(er) til {}", getBatchType(), getPushGatewayEndpointNameAndPort());
        try {
            this.pushGateway
                    .pushAdd(
                            getCollectorRegistry(),
                            "kubernetes-pods",
                            Collections.singletonMap("cronjob", getBatchName())
                    );
        } catch (Throwable e) {
            logger.error(getBatchName() + " failed pushing metric(s) ", e);
        }
    }
}