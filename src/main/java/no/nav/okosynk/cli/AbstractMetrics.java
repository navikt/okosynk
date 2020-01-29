package no.nav.okosynk.cli;

import io.prometheus.client.CollectorRegistry;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.IOkosynkConfiguration;

abstract class AbstractMetrics {

  private final Constants.BATCH_TYPE batchType;
  private final String               pushGatewayEndpointNameAndPort;
  private final CollectorRegistry    collectorRegistry;

  protected AbstractMetrics(
      final IOkosynkConfiguration okosynkConfiguration,
      final BATCH_TYPE batchType) {

    final String pushGatewayEndpointNameAndPort =
        okosynkConfiguration.getString(
            Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY,
            "nais-prometheus-prometheus-pushgateway.nais:9091"
        );
    final CollectorRegistry collectorRegistry = new CollectorRegistry();
    collectorRegistry.clear();
    this.batchType = batchType;
    this.pushGatewayEndpointNameAndPort = pushGatewayEndpointNameAndPort;
    this.collectorRegistry = collectorRegistry;
  }

  protected Constants.BATCH_TYPE getBatchType() {
    return this.batchType;
  }

  protected String getPushGatewayEndpointNameAndPort() {
    return this.pushGatewayEndpointNameAndPort;
  }

  protected CollectorRegistry getCollectorRegistry() {
    return this.collectorRegistry;
  }

  protected String getBatchName() {
    return this.batchType.getName();
  }
}