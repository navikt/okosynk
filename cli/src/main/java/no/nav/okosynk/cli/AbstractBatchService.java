package no.nav.okosynk.cli;

import static no.nav.metrics.MetricsFactory.createTimer;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.Getter;
import no.nav.metrics.Timer;
import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.batch.BatchStatus;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.oppgave.IOppgaveConsumerGateway;
import no.nav.okosynk.consumer.oppgave.OppgaveConsumerGatewayFactory;
import no.nav.okosynk.consumer.oppgavebehandling.IOppgaveBehandlingConsumerGateway;
import no.nav.okosynk.consumer.oppgavebehandling.OppgaveBehandlingConsumerGatewayFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public abstract class AbstractBatchService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBatchService.class);

    @Getter(AccessLevel.PROTECTED)
    private final IOppgaveConsumerGateway oppgaveGateway;

    @Getter(AccessLevel.PROTECTED)
    private final IOppgaveBehandlingConsumerGateway oppgaveBehandlingGateway;

    @Getter(AccessLevel.PROTECTED)
    private final AbstractService service;

    @Getter(AccessLevel.PROTECTED)
    final IOkosynkConfiguration okosynkConfiguration;

    @Getter(AccessLevel.PRIVATE)
    final Constants.BATCH_TYPE batchType;

    public AbstractBatchService(
        final IOkosynkConfiguration okosynkConfiguration,
        final Constants.BATCH_TYPE  batchType) {

        final OppgaveConsumerGatewayFactory oppgaveConsumerGatewayFactory =
            new OppgaveConsumerGatewayFactory(okosynkConfiguration);
        final IOppgaveConsumerGateway oppgaveGateway = oppgaveConsumerGatewayFactory.create(batchType);
        final IOppgaveBehandlingConsumerGateway oppgaveBehandlingGateway =
            new OppgaveBehandlingConsumerGatewayFactory(okosynkConfiguration).create(batchType);
        final AbstractService service =
            createService(okosynkConfiguration, oppgaveGateway, oppgaveBehandlingGateway);

        this.oppgaveGateway           = oppgaveGateway;
        this.oppgaveBehandlingGateway = oppgaveBehandlingGateway;
        this.service                  = service;
        this.okosynkConfiguration     = okosynkConfiguration;
        this.batchType                = batchType;
    }

    private AbstractService createService(
        final IOkosynkConfiguration             okosynkConfiguration,
        final IOppgaveConsumerGateway           oppgaveGateway,
        final IOppgaveBehandlingConsumerGateway oppgaveBehandlingGateway) {

        final BatchRepository batchRepository = new BatchRepository();

        final AbstractService service =
            createService(okosynkConfiguration, oppgaveGateway, oppgaveBehandlingGateway, batchRepository);

        return service;
    }

    protected abstract AbstractService createService(
        final IOkosynkConfiguration             okosynkConfiguration,
        final IOppgaveConsumerGateway           oppgaveV3Service,
        final IOppgaveBehandlingConsumerGateway oppgavebehandlingV3Service,
        final BatchRepository                   batchRepository);

    public BatchStatus startBatchSynchronously() {

        // =====================================================================
        // BEGIN - prometheus:
        //
        final CollectorRegistry registry = new CollectorRegistry();
        final Gauge duration = Gauge.build()
            .name("my_batch_job_duration_seconds")
            .help("Duration of my batch job in seconds.")
            .register(registry);
        final Gauge.Timer durationTimer = duration.startTimer();
        //
        // END - prometheus
        // =====================================================================

        final Timer timer = createTimer(getStartBatchSynchronouslyTimerNavn());
        timer.start();
        MDC.put("batchnavn", getBatchNavn());

        final BatchStatus batchStatus;
        try {
            logger.info("Mottatt kall til " + this.getClass().getSimpleName() + ".startBatchSynchronously");
            final AbstractService service  = getService();
            batchStatus = service.startBatchSynchronously();

            // =====================================================================
            // BEGIN - prometheus:
            //
            final Gauge lastSuccess = Gauge.build()
                .name("my_batch_job_last_success_unixtime")
                .help("Last time my batch job succeeded, in unixtime.")
                .register(registry);
            lastSuccess.setToCurrentTime();
            //
            // END - prometheus
            // =====================================================================
        } catch (final Exception e) {
            timer.setFailed();
            throw e;
        } finally {
            MDC.remove(getBatchNavn());
            timer.stop();
            timer.report();

            // =====================================================================
            // BEGIN - prometheus:
            //
            durationTimer.setDuration();
            final PushGateway pushGateway = new PushGateway("127.0.0.1:9091");
            try {
                pushGateway.pushAdd(registry, "my_batch_job");
            } catch (IOException e) {
                // TODO: Now: OK. Because this is only some template code. But: Change when time comes.
            }
            //
            // END - prometheus
            // =====================================================================
        }

        return batchStatus;
    }

    private String getBatchNavn() {
        return getBatchType().getName();
    }

    private String getStartBatchSynchronouslyTimerNavn() {
        return getBatchType().getBatchRunSynchronouslyTimerName();
    }
}
