package no.nav.okosynk.cli;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import java.io.IOException;
import java.util.Collections;

import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.batch.BatchStatus;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
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

        final AbstractService service = createService(okosynkConfiguration);

        this.service = service;
        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType = batchType;
    }

    private AbstractService createService(final IOkosynkConfiguration okosynkConfiguration) {
        final BatchRepository batchRepository = new BatchRepository();

        return createService(okosynkConfiguration, batchRepository);
    }

    protected abstract AbstractService createService(final IOkosynkConfiguration okosynkConfiguration,
                                                     final BatchRepository batchRepository);

    public BatchStatus startBatchSynchronously() {
        final CollectorRegistry registry = new CollectorRegistry();
        final Gauge duration = Gauge.build()
            .name("okosynk_job_duration_seconds")
            .help("Duration of okosynk batch job in seconds.")
            .register(registry);
        final Gauge.Timer durationTimer = duration.startTimer();

        MDC.put("batchnavn", getBatchNavn());
        final BatchStatus batchStatus;
        try {
            logger.info("Mottatt kall til " + this.getClass().getSimpleName() + ".startBatchSynchronously");
            final AbstractService service = getService();
            batchStatus = service.startBatchSynchronously();

            final Gauge lastSuccess = Gauge.build()
                .name("okosynk_batch_job_last_success_unixtime")
                .help("Last time okosynk batch job succeeded, in unixtime.")
                .register(registry);
            lastSuccess.setToCurrentTime();
        } finally {
            durationTimer.setDuration();
            String pushGateway = this.okosynkConfiguration.getRequiredString("PUSH_GATEWAY_ADDRESS");
            logger.info("Pusher metrikker til {}", pushGateway);
            try {
                new PushGateway(pushGateway).pushAdd(registry, "kubernetes-pods", Collections.singletonMap("cronjob", getBatchNavn()));
            } catch (IOException e) {
                logger.error("Klarte ikke pushe metrikker", e);
            }

            MDC.remove(getBatchNavn());
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
