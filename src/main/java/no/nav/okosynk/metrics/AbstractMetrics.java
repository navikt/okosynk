package no.nav.okosynk.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import lombok.Getter;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.OkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

abstract class AbstractMetrics {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMetrics.class);

    @Getter
    private final Constants.BATCH_TYPE batchType;
    private final String pushGatewayEndpointNameAndPort;
    private final CollectorRegistry collectorRegistry;
    private final PushGateway pushGateway;

    protected AbstractMetrics(
            final OkosynkConfiguration okosynkConfiguration,
            final BATCH_TYPE batchType) {

        final String localpushGatewayEndpointNameAndPort =
                okosynkConfiguration
                        .getPrometheusAddress("prometheus-pushgateway.nais-system:9091");

        final CollectorRegistry localcollectorRegistry = new CollectorRegistry();
        localcollectorRegistry.clear();
        this.batchType = batchType;
        this.pushGatewayEndpointNameAndPort = localpushGatewayEndpointNameAndPort;
        this.collectorRegistry = localcollectorRegistry;
        this.pushGateway = new PushGateway(this.pushGatewayEndpointNameAndPort);
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
        } catch (Exception e) {
            logger.error(getBatchName() + " failed pushing metric(s) ", e);
        }
    }
}
