package no.nav.okosynk.cli;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.batch.BatchStatus;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static no.nav.okosynk.batch.BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL;

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
        MDC.put("batchnavn", getBatchNavn());
        final BatchStatus batchStatus;
        try {
            logger.info("Mottatt kall til " + this.getClass().getSimpleName() + ".startBatchSynchronously");
            final AbstractService service = getService();
            batchStatus = service.startBatchSynchronously();
        } finally {
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
