package no.nav.okosynk.batch;

import no.nav.okosynk.cli.AbstractBatchMetrics;
import no.nav.okosynk.cli.BatchMetricsFactory;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.consumer.security.AzureAdAuthenticationClient;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.IMeldingReader;
import no.nav.okosynk.domain.MeldingUnreadableException;
import no.nav.okosynk.domain.Oppgave;
import no.nav.okosynk.domain.util.AktoerUt;
import no.nav.okosynk.io.AuthenticationOkosynkIoException;
import no.nav.okosynk.io.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.io.EncodingOkosynkIoException;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.IoOkosynkIoException;
import no.nav.okosynk.io.NotFoundOkosynkIoException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class Batch<SPESIFIKKMELDINGTYPE extends AbstractMelding> {

    static final int UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT = 25000;
    private static final Logger logger = LoggerFactory.getLogger(Batch.class);
    private static final Logger secureLog = LoggerFactory.getLogger("secureLog");
    private final IOkosynkConfiguration okosynkConfiguration;
    private final Constants.BATCH_TYPE batchType;
    private final IMeldingReader<SPESIFIKKMELDINGTYPE> spesifikkMeldingReader;
    private final IMeldingMapper<SPESIFIKKMELDINGTYPE> spesifikkMapper;
    private BatchStatus batchStatus;
    private IMeldingLinjeFileReader uspesifikkMeldingLinjeReader;
    private OppgaveSynkroniserer oppgaveSynkroniserer;

    public Batch(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType,
            final IMeldingReader<SPESIFIKKMELDINGTYPE> spesifikkMeldingReader,
            final IMeldingMapper<SPESIFIKKMELDINGTYPE> spesifikkMapper) {

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
                        this::getBatchStatus,
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

        final AbstractBatchMetrics batchMetrics =
                BatchMetricsFactory.get(getOkosynkConfiguration(), getBatchType());
        BatchStatus batchStatus = BatchStatus.STARTED;
        setBatchStatus(batchStatus);
        logger.info("Batch " + getBatchName() + " har startet.");
        try {
            final List<Oppgave> alleOppgaverLestFraBatchen = hentBatchOppgaver();
            logger.info("Hentet {} oppgvelinjer som skal behandles", alleOppgaverLestFraBatchen.size());
            final ConsumerStatistics consumerStatistics =
                    getOppgaveSynkroniserer().synkroniser(alleOppgaverLestFraBatchen);

            // At this point in code the read and treat process has been successful,
            // and the input file may be renamed:
            batchStatus =
                    getUspesifikkMeldingLinjeReader().removeInputData()
                            ?
                            BatchStatus.ENDED_WITH_OK
                            :
                            BatchStatus.ENDED_WITH_WARNING_BATCH_INPUT_DATA_COULD_NOT_BE_DELETED_AFTER_OK_RUN;

            batchMetrics.setSuccessfulMetrics(consumerStatistics);
        } catch (InputDataNotFoundBatchException e) {
            batchStatus = BatchStatus.ENDED_WITH_WARNING_INPUT_DATA_NOT_FOUND;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error("Exception received when reading input data when running " + getBatchName() + ". Status is set to " + batchStatus + ".", e);
        } catch (TooManyInputDataLinesBatchException e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_TOO_MANY_INPUT_DATA_LINES;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error("Exception received when reading input data when running " + getBatchName() + ". Status is set to " + batchStatus + ".", e);
        } catch (IoBatchException e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_GENERAL;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error("Exception received when reading input data when running " + getBatchName() + ". Status is set to " + batchStatus + ". The input data will not be removed.", e);
        } catch (UninterpretableMeldingBatchException e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_INPUT_DATA;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error("Exception received when reading input data when running " + getBatchName() + ". Status is set to " + batchStatus + ". The input data will not be removed.", e);
        } catch (ConfigurationBatchException e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_CONFIGURATION;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error("Exception received when reading input data when running " + getBatchName() + ". Status is set to " + batchStatus + ". The input data will not be removed.", e);
        } catch (Throwable e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_GENERAL;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error("Exception received when reading input data when running " + getBatchName() + ". Status is set to " + batchStatus + ". The input data will not be removed.", e);
        } finally {
            setBatchStatus(batchStatus);
            batchMetrics.log();
        }
    }

    public String getBatchName() {
        return getBatchType().getName();
    }

    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    public synchronized void setBatchStatus(final BatchStatus status) {
        this.batchStatus = status;
    }

    public IOkosynkConfiguration getOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    private List<Oppgave> hentBatchOppgaver()
            throws UninterpretableMeldingBatchException,
            InputDataNotFoundBatchException,
            GeneralBatchException,
            IoBatchException,
            TooManyInputDataLinesBatchException,
            ConfigurationBatchException {

        logger.debug("Entering Batch.hentBatchOppgaver...");
        final List<String> linjerMedUspesifikkeMeldinger = hentLinjerMedUspesifikkeMeldinger();
        final int actualnumberOfOppgaverRetrievedFromBatchInput = linjerMedUspesifikkeMeldinger.size();
        if (actualnumberOfOppgaverRetrievedFromBatchInput > UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT) {
            throw new TooManyInputDataLinesBatchException(
                    actualnumberOfOppgaverRetrievedFromBatchInput,
                    UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT);
        }
        logger.debug("linjerMedUspesifikkeMeldinger.size(): {}", linjerMedUspesifikkeMeldinger.size());
        final List<SPESIFIKKMELDINGTYPE> spesifikkeMeldinger =
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

    private List<String> hentLinjerMedUspesifikkeMeldinger()
            throws InputDataNotFoundBatchException,
            IoBatchException,
            GeneralBatchException,
            ConfigurationBatchException {

        logger.debug("Entering Batch.hentLinjerMedUspesifikkeMeldinger...");

        List<String> linjerMedUspesifikkeMeldinger;
        final String errorMsg = "Kunne ikke lese inn meldinger. Batch " + getBatchName() + " kan ikke fortsette.";
        try {
            Validate.notNull(
                    getUspesifikkMeldingLinjeReader(),
                    "The uspesifikkMeldingLinjeReader is not set."
            );
            linjerMedUspesifikkeMeldinger = getUspesifikkMeldingLinjeReader().read();
            logger.info(
                    "{} meldinger ble lest inn fra fil. Batch name: {}",
                    linjerMedUspesifikkeMeldinger.size(),
                    getBatchName());
        } catch (IoOkosynkIoException e) {
            throw new IoBatchException(e);
        } catch (NotFoundOkosynkIoException e) {
            throw new InputDataNotFoundBatchException(e);
        } catch (AuthenticationOkosynkIoException | EncodingOkosynkIoException | NullPointerException e) {
            throw new ConfigurationBatchException(e);
        } catch (ConfigureOrInitializeOkosynkIoException e) {
            throw new GeneralBatchException(e);
        } catch (Throwable e) {
            throw new GeneralBatchException(e);
        }

        logger.debug("About to normally leave Batch.hentLinjerMedUspesifikkeMeldinger");

        return linjerMedUspesifikkeMeldinger;
    }

    private List<SPESIFIKKMELDINGTYPE> opprettSpesifikkeMeldinger(
            final List<String> linjerMedUspesifikkeMeldinger) throws UninterpretableMeldingBatchException {

        logger.debug("Entering Batch.opprettSpesifikkeMeldinger...");

        final List<SPESIFIKKMELDINGTYPE> spesifikkeMeldinger;
        try {
            spesifikkeMeldinger =
                    getSpesifikkMeldingReader()
                            .opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(
                                    linjerMedUspesifikkeMeldinger.stream());
        } catch (MeldingUnreadableException e) {
            logger.error("Kunne ikke lese inn meldinger. Batch " + getBatchName() + " kan ikke fortsette.",
                    e);
            throw new UninterpretableMeldingBatchException(e);
        }

        logger.debug("About to normally leave Batch.opprettSpesifikkeMeldinger");

        return spesifikkeMeldinger;
    }

    private IMeldingLinjeFileReader getUspesifikkMeldingLinjeReader() {
        return this.uspesifikkMeldingLinjeReader;
    }

    public void setUspesifikkMeldingLinjeReader(final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader) {

        Validate.notNull(uspesifikkMeldingLinjeReader, "Trying to set my IMeldingLinjeFileReader instance to null");
        Validate.validState(
                Objects.equals(uspesifikkMeldingLinjeReader.getBatchType(), getBatchType()),
                "Trying to set my IMeldingLinjeFileReader instance with"
                        + " a batch type different from the one with which this batch is instantiatted with");

        setBatchStatus(
                (IMeldingLinjeFileReader.Status.OK.equals(uspesifikkMeldingLinjeReader.getStatus()))
                        ?
                        BatchStatus.READY
                        :
                        BatchStatus.ENDED_WITH_ERROR_GENERAL
        );

        this.uspesifikkMeldingLinjeReader = uspesifikkMeldingLinjeReader;
    }

    private IMeldingReader<SPESIFIKKMELDINGTYPE> getSpesifikkMeldingReader() {
        return this.spesifikkMeldingReader;
    }

    private OppgaveSynkroniserer getOppgaveSynkroniserer() {
        return oppgaveSynkroniserer;
    }

    public void setOppgaveSynkroniserer(final OppgaveSynkroniserer oppgaveSynkroniserer) {

        Validate.notNull(
                oppgaveSynkroniserer,
                "The parameter OppgaveSynkroniserer supplied is null");

        this.oppgaveSynkroniserer = oppgaveSynkroniserer;
    }

    private IMeldingMapper<SPESIFIKKMELDINGTYPE> getSpesifikkMapper() {
        return spesifikkMapper;
    }
}