package no.nav.okosynk;

import lombok.Getter;
import lombok.NonNull;
import no.nav.okosynk.comm.AzureAdAuthenticationClient;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.Mappingregelverk;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.OppgaveOppretter;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerUt;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.PdlRestClient;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.IMeldingLinjeFileReader;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.AuthenticationOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.IoOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.NotFoundOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.TooManyInputDataLinesBatchException;
import no.nav.okosynk.hentbatchoppgaver.model.Melding;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import no.nav.okosynk.metrics.BatchMetrics;
import no.nav.okosynk.model.Oppgave;
import no.nav.okosynk.synkroniserer.OppgaveSynkroniserer;
import no.nav.okosynk.synkroniserer.consumer.ConsumerStatistics;
import no.nav.okosynk.synkroniserer.consumer.oppgave.OppgaveRestClient;
import org.apache.commons.lang3.Validate;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static no.nav.okosynk.config.Constants.OPPGAVE_PASSWORD;
import static no.nav.okosynk.config.Constants.OPPGAVE_URL_KEY;
import static no.nav.okosynk.config.Constants.OPPGAVE_USERNAME;

public class Batch {

    static final int MAX_ANTALL_LINJER = 25000;
    private static final Logger logger = LoggerFactory.getLogger(Batch.class);
    private static final Logger secureLog = LoggerFactory.getLogger("secureLog");
    @Getter
    private final OkosynkConfiguration okosynkConfiguration;
    private BatchStatus batchStatus;
    private IMeldingLinjeFileReader fileReader;
    private final OppgaveSynkroniserer oppgaveSynkroniserer;

    public Batch(final OkosynkConfiguration okosynkConfiguration) throws ConfigureOrInitializeOkosynkIoException {

        Validate.notNull(
                okosynkConfiguration,
                "The parameter okosynkConfiguration supplied is null");

        this.setBatchStatus(BatchStatus.ENDED_WITH_ERROR_GENERAL);
        this.okosynkConfiguration = okosynkConfiguration;
        try {
            this.oppgaveSynkroniserer =
                    new OppgaveSynkroniserer(
                            okosynkConfiguration.getString(OPPGAVE_USERNAME),
                            new OppgaveRestClient(
                                    new UsernamePasswordCredentials(
                                            okosynkConfiguration.getString(OPPGAVE_USERNAME),
                                            okosynkConfiguration.getString(OPPGAVE_PASSWORD)),
                                    new URI(okosynkConfiguration.getString(OPPGAVE_URL_KEY)),
                                    okosynkConfiguration.getNaisAppName(),
                                    okosynkConfiguration.getBatchType(),
                                    new AzureAdAuthenticationClient(okosynkConfiguration)
                            )
                    );
        } catch (IllegalArgumentException | URISyntaxException e) {
            throw new ConfigureOrInitializeOkosynkIoException(e.getMessage());
        }

        this.setBatchStatus(BatchStatus.READY);
    }

    public void run() {

        final BatchMetrics batchMetrics = new BatchMetrics(getOkosynkConfiguration());
        batchStatus = BatchStatus.STARTED;
        logger.info("Batch {} har startet.", getBatchName());
        String prefix = "Exception received when reading input data when running " + getBatchName() + ". Status is set to ";
        String postfix = ". The input data will not be removed.";
        try {
            final List<Oppgave> alleOppgaverLestFraBatchen = hentBatchOppgaver();
            logger.info("Hentet {} oppgvelinjer som skal behandles", alleOppgaverLestFraBatchen.size());
            final ConsumerStatistics consumerStatistics =
                    getOppgaveSynkroniserer().synkroniser(alleOppgaverLestFraBatchen);

            boolean removedInputData = fileReader.removeInputData();

            batchStatus =
                    consumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet() >= 0 ? BatchStatus.ENDED_WITH_WARNING_OVER_5000_OPPGAVER_OPPRETTET
                    : !removedInputData ? BatchStatus.ENDED_WITH_WARNING_BATCH_INPUT_DATA_COULD_NOT_BE_DELETED_AFTER_OK_RUN
                    : BatchStatus.ENDED_WITH_OK;

            batchMetrics.setSuccessfulMetrics(consumerStatistics);
        } catch (NotFoundOkosynkIoException e) {
            batchStatus = BatchStatus.ENDED_WITH_WARNING_INPUT_DATA_NOT_FOUND;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error(prefix + batchStatus + ".", e);
        } catch (TooManyInputDataLinesBatchException e) {
            batchStatus = BatchStatus.ENDED_WITH_ERROR_TOO_MANY_INPUT_DATA_LINES;
            batchMetrics.setUnsuccessfulMetrics();
            logger.error(prefix + batchStatus + ".", e);
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
        return okosynkConfiguration.getBatchType().getName();
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
            IOException {

        logger.debug("Entering Batch.hentBatchOppgaver...");
        final List<String> linjer = this.fileReader.read();

        if (linjer.size() > MAX_ANTALL_LINJER) {
            throw new TooManyInputDataLinesBatchException(linjer.size(), MAX_ANTALL_LINJER);
        }
        logger.debug("linjer.size(): {}", linjer.size());
        final List<Melding> meldinger = parseLinjer(linjer);
        logger.info("Konverterer {} meldinger til oppgaver", meldinger.size());

        PdlRestClient pdlRestClient = new PdlRestClient(okosynkConfiguration);
        Mappingregelverk.init(okosynkConfiguration.getBatchType().getMappingRulesPropertiesFileName());

        List<Oppgave> batchOppgaver = new ArrayList<>(new OppgaveOppretter(pdlRestClient).lagOppgaver(meldinger));

        batchOppgaver
                .stream()
                .filter(batchOppgave -> AktoerUt.isDnr(batchOppgave.folkeregisterIdent()))
                .forEach(batchOppgave ->
                        secureLog.info("dnr found in the batch file: {}", batchOppgave.folkeregisterIdent().substring(0, 6) + "*****")
                );

        logger.debug("batchOppgaver.size(): {}", batchOppgaver.size());
        logger.debug("About to normally leave Batch.hentBatchOppgaver");

        return batchOppgaver;
    }

    private List<Melding> parseLinjer(final List<String> linjer) {
        logger.debug("Entering Batch.opprettSpesifikkeMeldinger...");
        Function<? super String, Melding> mapper = (okosynkConfiguration.getBatchType() == Constants.BATCH_TYPE.OS) ? OsMelding::new : UrMelding::new;
        return linjer.stream().map(mapper).toList();
    }

    public void setFileReader(final @NonNull IMeldingLinjeFileReader fileReader) {
        this.fileReader = requireNonNull(fileReader);
    }

    private OppgaveSynkroniserer getOppgaveSynkroniserer() {
        return oppgaveSynkroniserer;
    }

}
