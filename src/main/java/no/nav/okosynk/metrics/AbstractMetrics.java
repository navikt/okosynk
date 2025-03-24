package no.nav.okosynk.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import no.nav.okosynk.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

abstract class AbstractMetrics {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMetrics.class);

    private final Constants.BATCH_TYPE batchType;
    private final String pushGatewayEndpointNameAndPort;
    private final CollectorRegistry collectorRegistry;
    private final PushGateway pushGateway;

    Constants.BATCH_TYPE getBatchType() {
        return batchType;
    }

    protected AbstractMetrics(String prometheusaddress, Constants.BATCH_TYPE batchType) {

        final CollectorRegistry localcollectorRegistry = new CollectorRegistry();
        localcollectorRegistry.clear();
        this.batchType = batchType;
        this.pushGatewayEndpointNameAndPort = prometheusaddress;
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
