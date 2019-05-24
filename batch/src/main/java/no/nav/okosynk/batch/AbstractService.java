package no.nav.okosynk.batch;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.AbstractMeldingReader;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.MeldingLinjeReaderWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractService<MELDINGSTYPE extends AbstractMelding> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

    private final Constants.BATCH_TYPE batchType;
    private final IOkosynkConfiguration okosynkConfiguration;
    private final OppgaveRestClient oppgaveRestClient;
    private final BatchRepository batchRepository;
    private final AtomicLong nextExecutionId;

    protected AbstractService(final Constants.BATCH_TYPE batchType,
                              final IOkosynkConfiguration okosynkConfiguration,
                              final BatchRepository batchRepository,
                              final OppgaveRestClient oppgaveRestClient) {

        this.batchType = batchType;
        this.okosynkConfiguration = okosynkConfiguration;
        this.batchRepository = batchRepository;
        this.oppgaveRestClient = oppgaveRestClient;
        this.nextExecutionId = new AtomicLong(this.getBatchType().getExecutionIdOffset() + System.currentTimeMillis());
    }

    public BatchStatus startBatchSynchronously() {

        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        BatchStatus     batchStatus;
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        try {
            stoppBatch();
            final Batch<? extends AbstractMelding> batch = createAndConfigureBatch(okosynkConfiguration);
            final Callable<BatchStatus> task = () -> {
                batch.run();
                final BatchStatus batchStatusTemp = batch.getStatus();

                return batchStatusTemp;
            };

            logger.info("job thread about to be started...");
            final Future<BatchStatus> future = executor.submit(task);
            logger.info("Waiting for job thread to finish...");
            batchStatus = future.get();
            logger.info("job thread finished normally with outcome: " + batchStatus);
            stoppBatch();
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Throwable e) {
            logger.error("Exception received when waiting for job thread to finish.", e);
            batchStatus = BatchStatus.FEIL;
        } finally {
            executor.shutdownNow();
        }

        return batchStatus;
    }

    public Long startBatchAsynchronously() {

        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();

        stoppBatch();
        final Batch<MELDINGSTYPE> batch          = createAndConfigureBatch(okosynkConfiguration);
        final Long                eksekveringsId = batch.getExecutionId();
        new Thread(batch).start();

        return eksekveringsId;
    }

    public Optional<BatchStatus> pollBatch(final long executionId) {

        final Optional<Batch<? extends AbstractMelding>> batch = this.batchRepository.hentBatch(executionId);
        if (batch.isPresent()) {
            logger.debug("Status for batch med eksekverings-id " + executionId + " ble etterspurt.");
        } else {
            logger.warn("Status for batch med eksekverings-id " + executionId + " ble etterspurt, men ingen batch med eksekverings-id " + executionId + " kjorer.");
        }
        return batch.map(Batch::getStatus);
    }

    public boolean stoppBatch() {

        final Optional<Batch<? extends AbstractMelding>> batch = this.batchRepository.hentBatch(this.getBatchType().getName());
        final boolean batchStoppet = batch.isPresent();
        if (batchStoppet) {
            batch.get().stopp();
            logger.info("Batch " + this.getBatchType().getName() + " ble stoppet.");
        } else {
            logger.warn("Batch med batchNavn " + this.getBatchType().getName() + " ble forsøkt stoppet, men batch kjører ikke.");
        }
        return batchStoppet;
    }

    public Batch<MELDINGSTYPE> createAndConfigureBatch(final IOkosynkConfiguration okosynkConfiguration) {

        final Batch<MELDINGSTYPE> batch = this.createBatch(okosynkConfiguration);
        batch.setMeldingLinjeReader(createMeldingLinjeReader(okosynkConfiguration));
        this.getBatchRepository().leggTil(batch);

        return batch;
    }

    protected abstract AbstractMeldingReader<MELDINGSTYPE> createMeldingReader();

    protected abstract IMeldingMapper<MELDINGSTYPE> createMeldingMapper();

    private IMeldingLinjeFileReader createMeldingLinjeReader(final IOkosynkConfiguration okosynkConfiguration) {
        return new MeldingLinjeReaderWrapper(okosynkConfiguration, this.getBatchType());
    }

    private Batch<MELDINGSTYPE> createBatch(final IOkosynkConfiguration okosynkConfiguration){

        final Batch<MELDINGSTYPE> batch =
            new Batch<>(
                okosynkConfiguration,
                getBatchType(),
                getNextExecutionId().getAndIncrement(),
                this.getOppgaveRestClient(),
                createMeldingReader(),
                createMeldingMapper()
            );

        return batch;
    }

    protected Constants.BATCH_TYPE getBatchType() {
        return batchType;
    }

    public IOkosynkConfiguration getOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    public BatchRepository getBatchRepository() {
        return batchRepository;
    }

    private AtomicLong getNextExecutionId() {
        return nextExecutionId;
    }

    private OppgaveRestClient getOppgaveRestClient() {
        return oppgaveRestClient;
    }
}
