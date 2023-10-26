package no.nav.okosynk;

import lombok.Getter;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.PdlRestClient;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.FtpSettings;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.IMeldingLinjeFileReader;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.TinyFtpReader;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.ConfigureOrInitializeOkosynkIoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static no.nav.okosynk.config.Constants.*;
import static no.nav.okosynk.exceptions.BatchStatus.ENDED_WITH_ERROR_GENERAL;
import static no.nav.okosynk.exceptions.BatchStatus.READY;
import static no.nav.okosynk.hentbatchoppgaver.lesfrafil.FileReaderStatus.OK;

public class Service {

    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    @Getter
    private final Constants.BATCH_TYPE batchType;
    private final OkosynkConfiguration okosynkConfiguration;
    private boolean shouldRun;
    @Getter
    private BatchStatus lastBatchStatus;
    private IAktoerClient aktoerClient;
    private Batch batch;
    private IMeldingLinjeFileReader meldingLinjeFileReader;

    protected Service(
            final OkosynkConfiguration okosynkConfiguration) {

        this.batchType = BATCH_TYPE.valueOf(okosynkConfiguration.getRequiredString(SHOULD_RUN_OS_OR_UR_KEY));
        this.okosynkConfiguration = okosynkConfiguration;
        this.shouldRun = true;
    }

    public BatchStatus run() {

        BatchStatus batchStatus = null;
        try {
            if (batch == null) {
                setBatch(createAndConfigureBatch(okosynkConfiguration));
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

    public Batch createAndConfigureBatch(final OkosynkConfiguration okosynkConfiguration)
            throws URISyntaxException, ConfigureOrInitializeOkosynkIoException {

        if (aktoerClient == null) {
            setAktoerClient(new PdlRestClient(okosynkConfiguration));
        }
        final Batch meldingstypeBatch = new Batch(okosynkConfiguration);

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

        meldingstypeBatch.setFileReader(meldingLinjeFileReader);
        return meldingstypeBatch;
    }

    private void setMeldingLinjeReader(final IMeldingLinjeFileReader meldingLinjeFileReader) {
        this.meldingLinjeFileReader = meldingLinjeFileReader;
    }

    void setBatch(final Batch batch) {
        this.batch = batch;
    }

    void setAktoerClient(final IAktoerClient aktoerClient) {
        this.aktoerClient = aktoerClient;
    }

}
