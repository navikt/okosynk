package no.nav.okosynk;

import lombok.Getter;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.PdlRestClient;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.FtpSettings;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.IMeldingLinjeFileReader;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.TinyFtpReader;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static no.nav.okosynk.config.Constants.*;
import static no.nav.okosynk.exceptions.BatchStatus.ENDED_WITH_ERROR_GENERAL;
import static no.nav.okosynk.exceptions.BatchStatus.READY;
import static no.nav.okosynk.hentbatchoppgaver.lesfrafil.FileReaderStatus.OK;

public abstract class AbstractService<T extends AbstractMelding> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

    @Getter
    private final Constants.BATCH_TYPE batchType;
    private final OkosynkConfiguration okosynkConfiguration;
    private boolean shouldRun;
    @Getter
    private BatchStatus lastBatchStatus;
    private IAktoerClient aktoerClient;
    private Batch<? extends AbstractMelding> batch;
    private IMeldingLinjeFileReader meldingLinjeFileReader;

    protected AbstractService(
            final Constants.BATCH_TYPE batchType,
            final OkosynkConfiguration okosynkConfiguration) {

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
        } catch (URISyntaxException | ConfigureOrInitializeOkosynkIoException e) {
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

    public Batch<T> createAndConfigureBatch(
            final OkosynkConfiguration okosynkConfiguration)
            throws URISyntaxException, ConfigureOrInitializeOkosynkIoException {

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
            URI uri = new URI(okosynkConfiguration.getString(FTP_HOST_URL_KEY));

            logger.info("Using SFTP for {}, reading fullyQualifiedInputFileName: {}", this.getClass().getSimpleName(), uri.getPath());

            FtpSettings ftpSettings = new FtpSettings(
                    uri,
                    okosynkConfiguration.getString(FTP_USERNAME),
                    okosynkConfiguration.getString(FTP_PRIVATEKEY),
                    ISO_8859_1,
                    okosynkConfiguration.getString(FTP_HOSTKEY)
                    );

            setMeldingLinjeReader(new TinyFtpReader(ftpSettings));

            meldingstypeBatch.setBatchStatus(
                    OK == meldingLinjeFileReader.getStatus() ? READY : ENDED_WITH_ERROR_GENERAL
            );
        }

        meldingstypeBatch.setUspesifikkMeldingLinjeReader(meldingLinjeFileReader);
        return meldingstypeBatch;
    }

    protected abstract MeldingReader<T> createMeldingReader();

    protected abstract IMeldingMapper<T> createMeldingMapper(final IAktoerClient aktoerClient);

    protected OkosynkConfiguration getOkosynkConfiguration() {
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
