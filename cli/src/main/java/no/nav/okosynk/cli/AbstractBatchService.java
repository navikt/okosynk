package no.nav.okosynk.cli;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import java.io.IOException;
import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.batch.BatchStatus;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.domain.Oppgave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public abstract class AbstractBatchService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractBatchService.class);

    private final AbstractService service;
    final IOkosynkConfiguration okosynkConfiguration;
    final Constants.BATCH_TYPE batchType;

    private AbstractService getService() {
        return service;
    }

    public IOkosynkConfiguration getOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    private Constants.BATCH_TYPE getBatchType() {
        return batchType;
    }

    public AbstractBatchService(final IOkosynkConfiguration okosynkConfiguration, final Constants.BATCH_TYPE  batchType) {

        final AbstractService service = createService(okosynkConfiguration, new OppgaveRestClient(okosynkConfiguration));

        this.service = service;
        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType = batchType;
    }

    private AbstractService createService(final IOkosynkConfiguration okosynkConfiguration, OppgaveRestClient oppgaveRestClient) {
        final BatchRepository batchRepository = new BatchRepository();

        return createService(okosynkConfiguration, batchRepository, oppgaveRestClient);
    }

    protected abstract AbstractService createService(final IOkosynkConfiguration okosynkConfiguration,
                                                     final BatchRepository batchRepository,
                                                     final OppgaveRestClient oppgaveRestClient);

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

//        final Timer timer = createTimer(getStartBatchSynchronouslyTimerNavn());
//        timer.start();
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
//            timer.setFailed();
            throw e;
        } finally {
            MDC.remove(getBatchNavn());
//            timer.stop();
//            timer.report();

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
