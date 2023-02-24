package no.nav.okosynk;

import no.nav.okosynk.metrics.AbstractAlertMetrics;
import no.nav.okosynk.metrics.AlertMetricsFactory;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.PdlRestClient;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;
import no.nav.okosynk.hentbatchoppgaver.parselinje.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.IMeldingLinjeFileReader;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.MeldingLinjeSftpReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractService<MELDINGSTYPE extends AbstractMelding> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

    private final Constants.BATCH_TYPE batchType;
    private final IOkosynkConfiguration okosynkConfiguration;
    private boolean shouldRun;
    private BatchStatus lastBatchStatus;
    private IAktoerClient aktoerClient;
    private Batch<? extends AbstractMelding> batch;
    private IMeldingLinjeFileReader meldingLinjeFileReader;

    protected AbstractService(
            final Constants.BATCH_TYPE batchType,
            final IOkosynkConfiguration okosynkConfiguration) {

        this.batchType = batchType;
        this.okosynkConfiguration = okosynkConfiguration;
        this.shouldRun = true;
    }

    /**
     * Never throws. The outcome can be seen solely from the return code.
     *
     * @return The outcome of the run.
     */
    public BatchStatus run() {

        final Batch<? extends AbstractMelding> batch;
        BatchStatus batchStatus = null;
        try {
            batch = getBatch();
            batch.run();
            batchStatus = batch.getBatchStatus();
        } catch (ConfigureOrInitializeOkosynkIoException e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_CONFIGURATION;
        } finally {
            setLastBatchStatus(batchStatus);
            setShouldRun(batchStatus.failedButRerunningMaySucceed());
            setBatch(null);
        }
        return batchStatus;
    }

    public BatchStatus getLastBatchStatus() {
        return this.lastBatchStatus;
    }

    private void setLastBatchStatus(final BatchStatus batchStatus) {
        this.lastBatchStatus = batchStatus;
    }

    public AbstractService<MELDINGSTYPE> setShouldRun(final boolean shouldRun) {
        this.shouldRun = shouldRun;
        return this;
    }

    public boolean shouldRun() {
        return this.shouldRun;
    }

    public AbstractAlertMetrics getAlertMetrics() {
        return AlertMetricsFactory.get(getOkosynkConfiguration(), getBatchType());
    }

    public Constants.BATCH_TYPE getBatchType() {
        return this.batchType;
    }

    public Batch<MELDINGSTYPE> createAndConfigureBatch(
            final IOkosynkConfiguration okosynkConfiguration)
            throws ConfigureOrInitializeOkosynkIoException {

        final Batch<MELDINGSTYPE> batch = createBatch(okosynkConfiguration);

        final IMeldingLinjeFileReader meldingLinjeFileReader =
                getMeldingLinjeReader(okosynkConfiguration);

        batch.setUspesifikkMeldingLinjeReader(meldingLinjeFileReader);

        return batch;
    }

    protected IAktoerClient createAktoerClient() {
        return new PdlRestClient(getOkosynkConfiguration(), getBatchType());
    }

    protected abstract MeldingReader<MELDINGSTYPE> createMeldingReader();

    protected abstract IMeldingMapper<MELDINGSTYPE> createMeldingMapper(final IAktoerClient aktoerClient);

    protected IOkosynkConfiguration getOkosynkConfiguration() {
        return this.okosynkConfiguration;
    }

    private void setMeldingLinjeReader(final IMeldingLinjeFileReader meldingLinjeFileReader) {
        this.meldingLinjeFileReader = meldingLinjeFileReader;
    }

    private Batch<MELDINGSTYPE> createBatch(final IOkosynkConfiguration okosynkConfiguration) {

        final Batch<MELDINGSTYPE> batch =
                new Batch<>(
                        okosynkConfiguration,
                        getBatchType(),
                        createMeldingReader(),
                        createMeldingMapper(getAktoerClient())
                );

        return batch;
    }

    private Batch<? extends AbstractMelding> getBatch()
            throws ConfigureOrInitializeOkosynkIoException {
        if (this.batch == null) {
            setBatch(createAndConfigureBatch(getOkosynkConfiguration()));
        }
        return this.batch;
    }

    void setBatch(final Batch<? extends AbstractMelding> batch) {
        this.batch = batch;
    }

    private IAktoerClient getAktoerClient() {

        if (this.aktoerClient == null) {
            setAktoerClient(createAktoerClient());
        }
        return this.aktoerClient;
    }

    void setAktoerClient(final IAktoerClient aktoerClient) {
        this.aktoerClient = aktoerClient;
    }

    private IMeldingLinjeFileReader getMeldingLinjeReader(final IOkosynkConfiguration okosynkConfiguration)
            throws ConfigureOrInitializeOkosynkIoException {

        if (this.meldingLinjeFileReader == null) {
            final IMeldingLinjeFileReader meldingLinjeFileReader =
                    createMeldingLinjeSftpReader(okosynkConfiguration);
            setMeldingLinjeReader(meldingLinjeFileReader);
        }
        return this.meldingLinjeFileReader;
    }

    private IMeldingLinjeFileReader createMeldingLinjeSftpReader(
            final IOkosynkConfiguration okosynkConfiguration)
            throws ConfigureOrInitializeOkosynkIoException {

        final String fullyQualifiedInputFileName = getFtpInputFilePath(okosynkConfiguration);
        logger.info("Using SFTP for " + this.getClass().getSimpleName()
                + ", reading fullyQualifiedInputFileName: \"" + fullyQualifiedInputFileName + "\"");
        final IMeldingLinjeFileReader meldingLinjeFileReader =
                new MeldingLinjeSftpReader(okosynkConfiguration, getBatchType(),
                        fullyQualifiedInputFileName);

        return meldingLinjeFileReader;
    }

    private String getFtpInputFilePath(final IOkosynkConfiguration okosynkConfiguration)
            throws ConfigureOrInitializeOkosynkIoException {
        return MeldingLinjeSftpReader.getFtpInputFilePath(okosynkConfiguration.getFtpHostUrl(getBatchType()));
    }
}