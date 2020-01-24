package no.nav.okosynk.batch;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import java.io.IOException;
import java.util.Collections;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchMetrics {

  private static final Logger logger = LoggerFactory.getLogger(BatchMetrics.class);

  private final Constants.BATCH_TYPE  batchType;
  private final String                pushGatewayEndpointNameAndPort;
  private final CollectorRegistry     collectorRegistry;
  private final Gauge                 oppgaverOpprettetGauge;
  private final Gauge                 oppgaverOppdatertGauge;
  private final Gauge                 oppgaverFerdigstiltGauge;
  private final Gauge                 batchAlert;
  private final Gauge.Timer           durationGaugeTimer;
  private       ConsumerStatistics    consumerStatistics = null;

  /**
   * Ref. setting 0: https://prometheus.io/docs/practices/instrumentation/#avoid-missing-metrics
   * @param batchType
   */
  BatchMetrics(final IOkosynkConfiguration okosynkConfiguration, final Constants.BATCH_TYPE batchType) {

    this.batchType = batchType;
    this.pushGatewayEndpointNameAndPort =
        okosynkConfiguration.getString(
            Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY,
            "nais-prometheus-prometheus-pushgateway.nais:9091"
    );
    this.collectorRegistry = new CollectorRegistry();
    this.collectorRegistry.clear();
    this.oppgaverOpprettetGauge =
      Gauge
        .build()
        .name("okosynk_job_oppgaver_opprettet")
        .help("Antall oppgaver opprettet")
        .register(this.collectorRegistry);
    this.oppgaverOpprettetGauge.set(0);
    this.oppgaverOppdatertGauge =
        Gauge
            .build()
            .name("okosynk_job_oppgaver_oppdatert")
            .help("Antall oppgaver oppdatert")
            .register(this.collectorRegistry);
    this.oppgaverOppdatertGauge.set(0);
    this.oppgaverFerdigstiltGauge =
        Gauge
            .build()
            .name("okosynk_job_oppgaver_ferdigstilt")
            .help("Antall oppgaver ferdigstilt")
            .register(this.collectorRegistry);
    this.oppgaverFerdigstiltGauge.set(0);
    this.batchAlert =
        Gauge
          .build()
          .name(getBatchType().getAlertCollectorMetricName())
          .help("Relates to: Okosynk OS: It is as expected that the logs indicate that no further action must be taken. The potential problems can have been automatically solved by e.g. retries. However, the opposite may also be the case. This alert is based on a counter that counts up for each time an OS batch fails, so it may be anything above 0. 0 indicates, of course, no errors.")
          .register(this.collectorRegistry);
    this.batchAlert.set(0);
    final Gauge durationGauge =
        Gauge
            .build()
            .name("okosynk_job_duration_seconds")
            .help("Duration of okosynk batch job in seconds.")
            .register(this.collectorRegistry);
    durationGauge.set(0);
    // Zero out:
    try {
      new PushGateway(pushGatewayEndpointNameAndPort)
          .pushAdd(
              this.collectorRegistry,
              "kubernetes-pods",
              Collections.singletonMap("cronjob", getBatchName())
          );
    } catch (IOException e) {
      // Intentionally NOP
    }

    this.durationGaugeTimer = durationGauge.startTimer();
  }

  void setSuccessfulMetrics(final ConsumerStatistics consumerStatistics) {

    setMetrics(consumerStatistics);
    final Gauge lastSuccess =
        Gauge
            .build()
            .name("okosynk_batch_job_last_success_unixtime")
            .help("Last time okosynk batch job succeeded, in unixtime.")
            .register(this.collectorRegistry);
    lastSuccess.set(0);
    lastSuccess.setToCurrentTime();
  }

  void setUnsuccessfulMetrics() {
    setMetrics(ConsumerStatistics.zero(getBatchType()));
    batchAlert.inc();
  }

  void log() {

    logger.info("Pusher metrikker til {}", this.pushGatewayEndpointNameAndPort);
    try {
      new PushGateway(this.pushGatewayEndpointNameAndPort)
          .pushAdd(
              this.collectorRegistry,
              "kubernetes-pods",
              Collections.singletonMap("cronjob", getBatchName())
          );
    } catch (IOException e) {
      logger.error("Klarte ikke pushe metrikker, ukjent feil", e);
    }

    logger.info(
        "STATISTIKK: consumerStatistics ved avslutning av batchen: {}",
        this.consumerStatistics.toString()
    );
  }

  /**
   * TODO: Remove all this superfluous debugging and variable usage when problem solved.
   * @param consumerStatistics Basis for almost all logging content
   */
  private void setMetrics(final ConsumerStatistics consumerStatistics) {

    this.durationGaugeTimer.setDuration();
    this.oppgaverOpprettetGauge
        .set(consumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet());
    this.oppgaverOppdatertGauge
        .set(consumerStatistics.getAntallOppgaverSomMedSikkerhetErOppdatert());
    this.oppgaverFerdigstiltGauge
        .set(consumerStatistics.getAntallOppgaverSomMedSikkerhetErFerdigstilt());

    this.consumerStatistics = consumerStatistics;
  }

  private String getBatchName() {
    return getBatchType().getName();
  }

  private Constants.BATCH_TYPE getBatchType() {
    return this.batchType;
  }
}