package no.nav.okosynk;

import lombok.Getter;
import lombok.NonNull;
import no.nav.okosynk.comm.AzureAdAuthenticationClient;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerUt;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.IMeldingLinjeFileReader;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.*;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.IMeldingReader;
import no.nav.okosynk.metrics.AbstractBatchMetrics;
import no.nav.okosynk.metrics.BatchMetricsFactory;
import no.nav.okosynk.model.Oppgave;
import no.nav.okosynk.synkroniserer.OppgaveSynkroniserer;
import no.nav.okosynk.synkroniserer.consumer.ConsumerStatistics;
import no.nav.okosynk.synkroniserer.consumer.oppgave.OppgaveRestClient;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class Batch<T extends AbstractMelding> {

    static final int UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT = 25000;
    private static final Logger logger = LoggerFactory.getLogger(Batch.class);
    private static final Logger secureLog = LoggerFactory.getLogger("secureLog");
    @Getter
    private final IOkosynkConfiguration okosynkConfiguration;
    private final Constants.BATCH_TYPE batchType;
    private final IMeldingReader<T> spesifikkMeldingReader;
    private final IMeldingMapper<T> spesifikkMapper;
    private BatchStatus batchStatus;
    private IMeldingLinjeFileReader uspesifikkMeldingLinjeReader;
    private OppgaveSynkroniserer oppgaveSynkroniserer;

    public Batch(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType,
            final IMeldingReader<T> spesifikkMeldingReader,
            final IMeldingMapper<T> spesifikkMapper) {

        Validate.notNull(
                okosynkConfiguration,
                "The parameter okosynkConfiguration supplied is null");

        Validate.notNull(
                batchType,
                "The parameter batchType supplied is null");

        Validate.notNull(
                spesifikkMeldingReader,
                "The parameter spesifikkMeldingReader supplied is null");

        Validate.notNull(
                spesifikkMapper,
                "The parameter spesifikkMapper supplied is null");

        this.setBatchStatus(BatchStatus.ENDED_WITH_ERROR_GENERAL);

        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType = batchType;
        this.spesifikkMeldingReader = spesifikkMeldingReader;
        this.oppgaveSynkroniserer =
                new OppgaveSynkroniserer(
                        okosynkConfiguration,
                        new OppgaveRestClient(
                                okosynkConfiguration,
                                batchType,
                                new AzureAdAuthenticationClient(okosynkConfiguration)
                        )
                );
        this.spesifikkMapper = spesifikkMapper;

        this.setBatchStatus(BatchStatus.READY);
    }

    public void run() {

        final AbstractBatchMetrics batchMetrics = BatchMetricsFactory.get(getOkosynkConfiguration(), getBatchType());
        batchStatus = BatchStatus.STARTED;
        logger.info("Batch {} har startet.", getBatchName());
        String prefix = "Exception received when reading input data when running " + getBatchName() + ". Status is set to ";
        String postfix = ". The input data will not be removed.";
        try {
            final List<Oppgave> alleOppgaverLestFraBatchen = hentBatchOppgaver();
            logger.info("Hentet {} oppgvelinjer som skal behandles", alleOppgaverLestFraBatchen.size());
            final ConsumerStatistics consumerStatistics =
                    getOppgaveSynkroniserer().synkroniser(alleOppgaverLestFraBatchen);

            batchStatus = uspesifikkMeldingLinjeReader.removeInputData() ? BatchStatus.ENDED_WITH_OK
                    : BatchStatus.ENDED_WITH_WARNING_BATCH_INPUT_DATA_COULD_NOT_BE_DELETED_AFTER_OK_RUN;

            batchMetrics.setSuccessfulMetrics(consumerStatistics);
        } catch (NotFoundOkosynkIoException e) {
            batchStatus = BatchStatus.ENDED_WITH_WARNING_INPUT_DATA_NOT_FOUND;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error(prefix + batchStatus + ".", e);
        } catch (TooManyInputDataLinesBatchException e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_TOO_MANY_INPUT_DATA_LINES;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error(prefix + batchStatus + ".", e);
        } catch (MeldingUnreadableException e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_INPUT_DATA;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error(prefix + batchStatus + postfix, e);
        } catch (AuthenticationOkosynkIoException | NullPointerException e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_CONFIGURATION;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error(prefix + batchStatus + postfix, e);
        } catch (Exception e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_GENERAL;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error(prefix + batchStatus + postfix, e);
        } finally {
            setBatchStatus(batchStatus);
            batchMetrics.log();
        }
    }

    public String getBatchName() {
        return getBatchType().getName();
    }

    public synchronized BatchStatus getBatchStatus() {
        return batchStatus;
    }

    public synchronized void setBatchStatus(final BatchStatus status) {
        this.batchStatus = status;
    }

    private List<Oppgave> hentBatchOppgaver()
            throws TooManyInputDataLinesBatchException,
            NotFoundOkosynkIoException,
            IoOkosynkIoException,
            AuthenticationOkosynkIoException,
            ConfigureOrInitializeOkosynkIoException,
            MeldingUnreadableException {

        logger.debug("Entering Batch.hentBatchOppgaver...");
        final List<String> linjerMedUspesifikkeMeldinger = this.uspesifikkMeldingLinjeReader.read();

        final int actualnumberOfOppgaverRetrievedFromBatchInput = linjerMedUspesifikkeMeldinger.size();
        if (actualnumberOfOppgaverRetrievedFromBatchInput > UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT) {
            throw new TooManyInputDataLinesBatchException(
                    actualnumberOfOppgaverRetrievedFromBatchInput,
                    UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT);
        }
        logger.debug("linjerMedUspesifikkeMeldinger.size(): {}", linjerMedUspesifikkeMeldinger.size());
        final List<T> spesifikkeMeldinger =
                opprettSpesifikkeMeldinger(linjerMedUspesifikkeMeldinger);
        logger.info("Konverterer {} meldinger til oppgaver", spesifikkeMeldinger.size());
        final List<Oppgave> batchOppgaver = getSpesifikkMapper().lagOppgaver(spesifikkeMeldinger);

        batchOppgaver
                .stream()
                .filter(batchOppgave -> AktoerUt.isDnr(batchOppgave.folkeregisterIdent))
                .forEach(batchOppgave ->
                        secureLog.info("dnr found in the batch file: {}", batchOppgave.folkeregisterIdent.substring(0, 6) + "*****")
                );

        logger.debug("batchOppgaver.size(): {}", batchOppgaver.size());
        logger.debug("About to normally leave Batch.hentBatchOppgaver");

        return batchOppgaver;
    }

    Constants.BATCH_TYPE getBatchType() {
        return batchType;
    }

    private List<T> opprettSpesifikkeMeldinger(
            final List<String> linjerMedUspesifikkeMeldinger) throws MeldingUnreadableException {

        logger.debug("Entering Batch.opprettSpesifikkeMeldinger...");

        final List<T> spesifikkeMeldinger;
        spesifikkeMeldinger =
                getSpesifikkMeldingReader()
                        .opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(
                                linjerMedUspesifikkeMeldinger);


        logger.debug("About to normally leave Batch.opprettSpesifikkeMeldinger");

        return spesifikkeMeldinger;
    }

    public void setUspesifikkMeldingLinjeReader(final @NonNull IMeldingLinjeFileReader uspesifikkMeldingLinjeReader) {
        this.uspesifikkMeldingLinjeReader = requireNonNull(uspesifikkMeldingLinjeReader);
    }

    private IMeldingReader<T> getSpesifikkMeldingReader() {
        return this.spesifikkMeldingReader;
    }

    private OppgaveSynkroniserer getOppgaveSynkroniserer() {
        return oppgaveSynkroniserer;
    }

    public void setOppgaveSynkroniserer(final @NonNull OppgaveSynkroniserer oppgaveSynkroniserer) {
        this.oppgaveSynkroniserer = requireNonNull(oppgaveSynkroniserer);
    }

    private IMeldingMapper<T> getSpesifikkMapper() {
        return spesifikkMapper;
    }
}
