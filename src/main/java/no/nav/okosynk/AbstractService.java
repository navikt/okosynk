package no.nav.okosynk;

import no.nav.okosynk.hentbatchoppgaver.lesfrafil.FtpSettings;
import no.nav.okosynk.metrics.AbstractAlertMetrics;
import no.nav.okosynk.metrics.AlertMetricsFactory;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.PdlRestClient;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.IMeldingLinjeFileReader;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.MeldingLinjeSftpReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

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

    public BatchStatus run() {

        final Batch<? extends AbstractMelding> batch;
        BatchStatus batchStatus = null;
        try {
            batch = getBatch();
            batch.run();
            batchStatus = batch.getBatchStatus();
        } catch (ConfigureOrInitializeOkosynkIoException | URISyntaxException e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_CONFIGURATION;
        } finally {
            setLastBatchStatus(batchStatus);
            setShouldRun(batchStatus != null && batchStatus.failedButRerunningMaySucceed());
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

    public void setShouldRun(final boolean shouldRun) {
        this.shouldRun = shouldRun;
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
            throws URISyntaxException {

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
        return new Batch<>(
                okosynkConfiguration,
                getBatchType(),
                createMeldingReader(),
                createMeldingMapper(getAktoerClient())
        );
    }

    private Batch<? extends AbstractMelding> getBatch()
            throws ConfigureOrInitializeOkosynkIoException, URISyntaxException {
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
            throws URISyntaxException {

        if (this.meldingLinjeFileReader == null) {
            final IMeldingLinjeFileReader meldingLinjeFileReader =
                    createMeldingLinjeSftpReader(okosynkConfiguration);
            setMeldingLinjeReader(meldingLinjeFileReader);
        }
        return this.meldingLinjeFileReader;
    }

    private IMeldingLinjeFileReader createMeldingLinjeSftpReader(final IOkosynkConfiguration okosynkConfiguration)
            throws URISyntaxException {
        URI uri = new URI(okosynkConfiguration.getFtpHostUrl(getBatchType()));

        logger.info("Using SFTP for " + this.getClass().getSimpleName()
                + ", reading fullyQualifiedInputFileName: \"" + uri.getPath() + "\"");

        FtpSettings ftpSettings = new FtpSettings(
                uri,
                okosynkConfiguration.getFtpUser(getBatchType()),
                okosynkConfiguration.getFtpPassword(getBatchType()),
                okosynkConfiguration.getFtpCharsetName(getBatchType(), "ISO8859_1"));

        return new MeldingLinjeSftpReader(ftpSettings, getBatchType());
    }
}
