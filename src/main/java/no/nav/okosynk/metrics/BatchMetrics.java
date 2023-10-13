package no.nav.okosynk.metrics;

import io.prometheus.client.Gauge;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.synkroniserer.consumer.ConsumerStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchMetrics extends AbstractMetrics {

    private static final Logger logger = LoggerFactory.getLogger(BatchMetrics.class);

    private final Gauge oppgaverOpprettetGauge;
    private final Gauge oppgaverOppdatertGauge;
    private final Gauge oppgaverFerdigstiltGauge;
    private final Gauge.Timer durationGaugeTimer;
    private final Gauge lastSuccessGauge;
    private ConsumerStatistics consumerStatistics = null;

    public BatchMetrics(final OkosynkConfiguration okosynkConfiguration) {

        super(okosynkConfiguration);

        this.oppgaverOpprettetGauge =
                Gauge
                        .build()
                        .name("okosynk_job_oppgaver_opprettet")
                        .help("Antall oppgaver opprettet")
                        .register(getCollectorRegistry());
        this.oppgaverOpprettetGauge.set(0);

        this.oppgaverOppdatertGauge =
                Gauge
                        .build()
                        .name("okosynk_job_oppgaver_oppdatert")
                        .help("Antall oppgaver oppdatert")
                        .register(getCollectorRegistry());
        this.oppgaverOppdatertGauge.set(0);

        this.oppgaverFerdigstiltGauge =
                Gauge
                        .build()
                        .name("okosynk_job_oppgaver_ferdigstilt")
                        .help("Antall oppgaver ferdigstilt")
                        .register(getCollectorRegistry());
        this.oppgaverFerdigstiltGauge.set(0);

        final Gauge durationGauge =
                Gauge
                        .build()
                        .name("okosynk_job_duration_seconds")
                        .help("Duration of okosynk batch job in seconds.")
                        .register(getCollectorRegistry());
        durationGauge.set(0);

        this.lastSuccessGauge =
                Gauge
                        .build()
                        .name("okosynk_batch_job_last_success_unixtime")
                        .help("Last time okosynk batch job succeeded, in unixtime.")
                        .register(getCollectorRegistry());
        this.lastSuccessGauge.set(0);

        pushAdd();

        this.durationGaugeTimer = durationGauge.startTimer();
    }

    public void setSuccessfulMetrics(final ConsumerStatistics consumerStatistics) {

        setMetrics(consumerStatistics);
        this.lastSuccessGauge.setToCurrentTime();
    }

    public void setUnsuccessfulMetrics() {
        setMetrics(ConsumerStatistics.zero(getBatchType()));
    }

    public void log() {
        pushAdd();
        logger.info("STATISTIKK: consumerStatistics ved avslutning av batchen: {}", consumerStatistics);
    }

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
}
