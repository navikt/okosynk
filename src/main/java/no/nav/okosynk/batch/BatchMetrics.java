package no.nav.okosynk.batch;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import java.io.IOException;
import java.util.Collections;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchMetrics {

  private static final Logger logger = LoggerFactory.getLogger(BatchMetrics.class);

  private final Constants.BATCH_TYPE batchType;
  private final CollectorRegistry    collectorRegistry;
  private final Gauge                oppgaverOpprettetGauge;
  private final Gauge                oppgaverOppdatertGauge;
  private final Gauge                oppgaverFerdigstiltGauge;
  private final Gauge                osBatchAlert;
  private final Gauge                urBatchAlert;
  private final Gauge.Timer          durationGaugeTimer;
  private       ConsumerStatistics   consumerStatistics = null;

  /**
   * Ref. setting 0: https://prometheus.io/docs/practices/instrumentation/#avoid-missing-metrics
   * @param batchType
   */
  BatchMetrics(final Constants.BATCH_TYPE batchType) {

    this.batchType = batchType;
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
    this.osBatchAlert =
        Gauge
          .build()
          .name("okosynk_os_batch_alert")
          .help("The (Kibana) log for OKOSYNK-OS should be checked. Takes on only the values 0 and 1. If 1, the log should be checked. It may be that the job succeeded e.g. by later retries, or e.g. that there may be some warnings that should be manually looked at.")
          .register(this.collectorRegistry);
    this.osBatchAlert.set(0);
    this.urBatchAlert =
        Gauge
          .build()
          .name("okosynk_ur_batch_alert")
          .help("The (Kibana) log for OKOSYNK-UR should be checked. Takes on only the values 0 and 1. If 1, the log should be checked. It may be that the job succeeded e.g. by later retries, or e.g. that there may be some warnings that should be manually looked at.")
          .register(this.collectorRegistry);
    this.urBatchAlert.set(0);
    final Gauge durationGauge =
        Gauge
            .build()
            .name("okosynk_job_duration_seconds")
            .help("Duration of okosynk batch job in seconds.")
            .register(this.collectorRegistry);
    durationGauge.set(0);
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
    // TODO: This is an anti OO pattern:
    if (BATCH_TYPE.OS.equals(getBatchType())) {
      osBatchAlert.inc();
    } else {
      urBatchAlert.inc();
    }
  }

  void log(final IOkosynkConfiguration okosynkConfiguration) {
    try {
      final String pushGatewayEndpointNameAndPort =
        okosynkConfiguration.getRequiredString(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY);
      if (isNotBlank(pushGatewayEndpointNameAndPort)) {
        logger.info("Pusher metrikker til {}", pushGatewayEndpointNameAndPort);
        try {
          new PushGateway(pushGatewayEndpointNameAndPort)
              .pushAdd(
                  this.collectorRegistry,
                  "kubernetes-pods",
                  Collections.singletonMap("cronjob", getBatchName())
              );
        } catch (IOException e) {
          logger.error("Klarte ikke pushe metrikker, ukjent feil", e);
        }
      } else {
        throw new IllegalStateException(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY + " is not set.");
      }
    } catch (IllegalStateException e) {
      logger.warn(
            "Konfigurasjonsnøkkel "
          + Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY
          + " mangler, får ikke pushet metrikker."
      );
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