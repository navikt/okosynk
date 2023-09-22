package no.nav.okosynk;

import lombok.Getter;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.PdlRestClient;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.FtpSettings;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.IMeldingLinjeFileReader;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.MeldingLinjeSftpReader;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;
import no.nav.okosynk.metrics.AbstractAlertMetrics;
import no.nav.okosynk.metrics.AlertMetricsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Stream;

import static no.nav.okosynk.exceptions.BatchStatus.ENDED_WITH_ERROR_GENERAL;
import static no.nav.okosynk.exceptions.BatchStatus.READY;
import static no.nav.okosynk.hentbatchoppgaver.lesfrafil.FileReaderStatus.OK;

public abstract class AbstractService<T extends AbstractMelding> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

    @Getter
    private final Constants.BATCH_TYPE batchType;
    private final IOkosynkConfiguration okosynkConfiguration;
    private boolean shouldRun;
    @Getter
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

        BatchStatus batchStatus = null;
        try {
            if (batch == null) {
                setBatch(createAndConfigureBatch(getOkosynkConfiguration()));
            }
            batch.run();
            batchStatus = batch.getBatchStatus();
        } catch (URISyntaxException e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_CONFIGURATION;
        } finally {
            setLastBatchStatus(batchStatus);
            setShouldRun(batchStatus != null && batchStatus.failedButRerunningMaySucceed());
            setBatch(null);
        }
        return batchStatus;
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

    public Batch<T> createAndConfigureBatch(
            final IOkosynkConfiguration okosynkConfiguration)
            throws URISyntaxException {

        if (aktoerClient == null) {
            setAktoerClient(new PdlRestClient(getOkosynkConfiguration(), getBatchType()));
        }
        final Batch<T> meldingstypeBatch = new Batch<>(
                okosynkConfiguration,
                getBatchType(),
                createMeldingReader(),
                createMeldingMapper(aktoerClient)
        );

        if (meldingLinjeFileReader == null) {
            URI uri = new URI(okosynkConfiguration.getFtpHostUrl(getBatchType()));

            String formatted = "Using SFTP for %s, reading fullyQualifiedInputFileName: \"%s\"".formatted(this.getClass().getSimpleName(), uri.getPath());
            logger.info(formatted);

            logger.info("Forsøker lesning av de tre nye variablene");

            record Tuple(String key, String value){}

            Stream.of("FTPCREDENTIALS_PRIVATE_KEY", "FTPCREDENTIALS_PRIVATE_KEY_PRIVATE_KEY",
                            "FTPCREDENTIALS_USERNAME", "FTPCREDENTIALS_PASSWORD",
                            "FTPCREDENTIALS_USERNAME_ALT", "FTPCREDENTIALS_PASSWORD_ALT")
                    .map(s -> new Tuple(s, okosynkConfiguration.getString(s)))
                    .forEach(t -> logger.info("Trying to access {}, lengde:{} ", t.key(), Objects.nonNull(t.value()) ? t.value().length() : "null")
            );
            logger.info("Ferdig med lesning av nye variabler");

            FtpSettings ftpSettings = new FtpSettings(
                    uri,
                    okosynkConfiguration.getFtpUser(getBatchType()),
                    okosynkConfiguration.getFtpPassword(getBatchType()),
                    okosynkConfiguration.getFtpCharsetName(getBatchType(), "ISO8859_1"));

            setMeldingLinjeReader(new MeldingLinjeSftpReader(ftpSettings, getBatchType()));

            meldingstypeBatch.setBatchStatus(
                    OK == meldingLinjeFileReader.getStatus() ? READY : ENDED_WITH_ERROR_GENERAL
            );
        }

        meldingstypeBatch.setUspesifikkMeldingLinjeReader(meldingLinjeFileReader);
        return meldingstypeBatch;
    }

    protected abstract MeldingReader<T> createMeldingReader();

    protected abstract IMeldingMapper<T> createMeldingMapper(final IAktoerClient aktoerClient);

    protected IOkosynkConfiguration getOkosynkConfiguration() {
        return this.okosynkConfiguration;
    }

    private void setMeldingLinjeReader(final IMeldingLinjeFileReader meldingLinjeFileReader) {
        this.meldingLinjeFileReader = meldingLinjeFileReader;
    }

    void setBatch(final Batch<? extends AbstractMelding> batch) {
        this.batch = batch;
    }

    void setAktoerClient(final IAktoerClient aktoerClient) {
        this.aktoerClient = aktoerClient;
    }

}
