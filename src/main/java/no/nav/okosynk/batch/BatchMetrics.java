package no.nav.okosynk.batch;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

  private final Constants.BATCH_TYPE batchType;
  private final CollectorRegistry    collectorRegistry;
  private final Gauge                oppgaverOpprettetGauge;
  private final Gauge                oppgaverOppdatertGauge;
  private final Gauge                oppgaverFerdigstiltGauge;
  private final Gauge.Timer          durationGaugeTimer;
  private       ConsumerStatistics   consumerStatistics = null;

  BatchMetrics(final Constants.BATCH_TYPE batchType) {

    this.batchType = batchType;
    this.collectorRegistry = new CollectorRegistry();
     this.oppgaverOpprettetGauge =
         Gauge
             .build()
             .name("okosynk_job_oppgaver_opprettet")
             .help("Antall oppgaver opprettet")
             .register(this.collectorRegistry);
    this.oppgaverOppdatertGauge =
        Gauge
            .build()
            .name("okosynk_job_oppgaver_oppdatert")
            .help("Antall oppgaver oppdatert")
            .register(this.collectorRegistry);
    this.oppgaverFerdigstiltGauge =
        Gauge
            .build()
            .name("okosynk_job_oppgaver_ferdigstilt")
            .help("Antall oppgaver ferdigstilt")
            .register(this.collectorRegistry);
    final Gauge durationGauge =
        Gauge
            .build()
            .name("okosynk_job_duration_seconds")
            .help("Duration of okosynk batch job in seconds.")
            .register(this.collectorRegistry);
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
    lastSuccess.setToCurrentTime();
  }

  void setUnsuccessfulMetrics() {
    setMetrics(ConsumerStatistics.zero(getBatchType()));
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

    final int antallOppgaverSomMedSikkerhetErOpprettet =
        consumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet();
    final int antallOppgaverSomMedSikkerhetErOppdatert =
        consumerStatistics.getAntallOppgaverSomMedSikkerhetErOppdatert();
    final int antallOppgaverSomMedSikkerhetErFerdigstilt =
        consumerStatistics.getAntallOppgaverSomMedSikkerhetErFerdigstilt();

    this.oppgaverOpprettetGauge
        .set(antallOppgaverSomMedSikkerhetErOpprettet);
    this.oppgaverOppdatertGauge
        .set(antallOppgaverSomMedSikkerhetErOppdatert);
    this.oppgaverFerdigstiltGauge
        .set(antallOppgaverSomMedSikkerhetErFerdigstilt);
    this.consumerStatistics = consumerStatistics;

    logger.debug("antallOppgaverSomMedSikkerhetErOpprettet {}",
        antallOppgaverSomMedSikkerhetErOpprettet);
    logger.debug("antallOppgaverSomMedSikkerhetErOppdatert {}",
        antallOppgaverSomMedSikkerhetErOppdatert);
    logger.debug("antallOppgaverSomMedSikkerhetErFerdigstilt {}",
        antallOppgaverSomMedSikkerhetErFerdigstilt);
    logger.debug(
        "STATISTIKK: consumerStatistics ved avslutning av batchen: {}",
        consumerStatistics.toString()
    );
  }

  private String getBatchName() {
    return getBatchType().getName();
  }

  private Constants.BATCH_TYPE getBatchType() {
    return this.batchType;
  }
}