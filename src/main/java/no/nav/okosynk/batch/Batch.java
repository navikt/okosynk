package no.nav.okosynk.batch;

import java.util.List;
import no.nav.okosynk.cli.BatchMetrics;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.IMeldingReader;
import no.nav.okosynk.domain.MeldingUnreadableException;
import no.nav.okosynk.domain.Oppgave;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.OkosynkIoException;
import no.nav.okosynk.io.OkosynkIoException.ErrorCode;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Batch<SPESIFIKKMELDINGTYPE extends AbstractMelding> {

  private static final Logger logger = LoggerFactory.getLogger(Batch.class);

  private static final int UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT = 25000;

  private final IOkosynkConfiguration okosynkConfiguration;
  final Constants.BATCH_TYPE batchType;
  private BatchStatus batchStatus;
  private IMeldingLinjeFileReader uspesifikkMeldingLinjeReader;

  private IMeldingReader<SPESIFIKKMELDINGTYPE> spesifikkMeldingReader;
  private OppgaveSynkroniserer oppgaveSynkroniserer;
  private IMeldingMapper<SPESIFIKKMELDINGTYPE> spesifikkMapper;

  public Batch(final IOkosynkConfiguration okosynkConfiguration,

    final Constants.BATCH_TYPE batchType,
    final IMeldingReader<SPESIFIKKMELDINGTYPE> spesifikkMeldingReader,
    final IMeldingMapper<SPESIFIKKMELDINGTYPE> spesifikkMapper) {

    // Assume failure, set to ready by the descendant if successful:
    this.setBatchStatus(BatchStatus.ENDED_WITH_ERROR_GENERAL);

    this.okosynkConfiguration = okosynkConfiguration;
    this.batchType = batchType;
    this.spesifikkMeldingReader = spesifikkMeldingReader;
    this.oppgaveSynkroniserer =
        new OppgaveSynkroniserer(
          okosynkConfiguration,
          this::getBatchStatus,
          new OppgaveRestClient(okosynkConfiguration, batchType)
        );
    this.spesifikkMapper = spesifikkMapper;

    this.setBatchStatus(BatchStatus.READY);
  }

  public void run() {

    final BatchMetrics batchMetrics = new BatchMetrics(getOkosynkConfiguration(), getBatchType());
    BatchStatus batchStatus = BatchStatus.STARTED;
    setBatchStatus(batchStatus);
    logger.info("Batch " + getBatchName() + " har startet.");
    try {
      final List<Oppgave> alleOppgaverLestFraBatchen = hentBatchOppgaver();
      final int actualnumberOfOppgaverRetrievedFromBatchInput = alleOppgaverLestFraBatchen.size();
      if (actualnumberOfOppgaverRetrievedFromBatchInput
          > UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT) {
        final String msg =
            String.format(
                  "Batch input inneholder %d oppgaver. "
                + "Det største antallet okosynk er satt til å akseptere er %d oppgaver. "
                + "Okosynk avbrytes.",
                actualnumberOfOppgaverRetrievedFromBatchInput,
                UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT
            );
        throw new RuntimeException(msg);
      }
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
      logger.info("Batch " + getBatchName() + " er fullført med batchStatus " + batchStatus);
    } catch (BatchException e) {
      final Throwable cause = e.getCause();
      if (cause instanceof OkosynkIoException) {
        final OkosynkIoException okosynkIoException = (OkosynkIoException)cause;
        if (ErrorCode.NUMBER_OF_RETRIES_EXCEEDED.equals(okosynkIoException.getErrorCode())) {
          batchStatus = BatchStatus.ENDED_WITH_ERROR_NUMBER_OF_RETRIES_EXCEEDED;
        } else {
          batchStatus = BatchStatus.ENDED_WITH_ERROR_GENERAL;
        }
      } else {
        batchStatus = BatchStatus.ENDED_WITH_ERROR_GENERAL;
      }
      batchMetrics.setUnsuccessfulMetrics();
      logger.error("Exception received when reading lines from the input file when running " + getBatchName() + ". Status is set to " + batchStatus + ". The input file will not be renamed.", e);
    } catch (Throwable e) {
      batchStatus = BatchStatus.ENDED_WITH_ERROR_GENERAL;
      batchMetrics.setUnsuccessfulMetrics();
      logger.error("Exception received when reading lines from the input file when running " + getBatchName() + ". Status is set to " + batchStatus + ". The input file will not be renamed.", e);
    } finally {
      setBatchStatus(batchStatus);
      batchMetrics.log();
    }
  }

  /**
   * TODO: OBS! Is it possible to set a reader that conflicts with the batch type?
   * @param uspesifikkMeldingLinjeReader Self explanatory
   */
  public void setMeldingLinjeReader(final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader) {

    Validate.notNull(
        uspesifikkMeldingLinjeReader,
        "The parameter IMeldingLinjeFileReader supplied is null");

    this.uspesifikkMeldingLinjeReader = uspesifikkMeldingLinjeReader;
    setBatchStatus(
        (!IMeldingLinjeFileReader.Status.OK.equals(this.uspesifikkMeldingLinjeReader.getStatus()))
            ?
            BatchStatus.ENDED_WITH_ERROR_GENERAL
            :
            BatchStatus.READY
    );
  }

  public synchronized void setBatchStatus(final BatchStatus status) {
    this.batchStatus = status;
  }

  public String getBatchName() {
    return getBatchType().getName();
  }

  private String getBatchUser(final IOkosynkConfiguration okosynkConfiguration) {

    final String batchBruker =
        okosynkConfiguration
            .getString(
                getBatchType().getBatchBrukerKey(),
                getBatchType().getBatchBrukerDefaultValue()
            );

    return batchBruker;
  }

  private List<Oppgave> hentBatchOppgaver() throws BatchException {

    logger.debug("Entering Batch.hentBatchOppgaver...");

    final List<String> linjerMedUspesifikkeMeldinger = hentLinjerMedUspesifikkeMeldinger();
    final List<SPESIFIKKMELDINGTYPE> spesifikkeMeldinger =
        opprettSpesifikkeMeldinger(linjerMedUspesifikkeMeldinger);
    final List<Oppgave> batchOppgaver = getSpesifikkMapper().lagOppgaver(spesifikkeMeldinger);

    logger.debug("About to normally leave Batch.hentBatchOppgaver");

    return batchOppgaver;
  }

  private List<String> hentLinjerMedUspesifikkeMeldinger() throws BatchException {

    logger.debug("Entering Batch.hentLinjerMedUspesifikkeMeldinger...");

    List<String> linjerMedUspesifikkeMeldinger;
    try {
      linjerMedUspesifikkeMeldinger = getUspesifikkMeldingLinjeReader().read();
      logger.info(
          "STATISTIKK: {} meldinger ble lest inn. Batch name: {}",
          linjerMedUspesifikkeMeldinger.size(),
          getBatchName());
    } catch (OkosynkIoException e) {
      logger.error(
          "Kunne ikke lese inn meldinger. Batch " + getBatchName() + " kan ikke fortsette.",
          e);
      throw new BatchException(e);
    }

    logger.debug("About to normally leave Batch.hentLinjerMedUspesifikkeMeldinger");

    return linjerMedUspesifikkeMeldinger;
  }

  private List<SPESIFIKKMELDINGTYPE> opprettSpesifikkeMeldinger(
      final List<String> linjerMedUspesifikkeMeldinger) throws BatchException {

    logger.debug("Entering Batch.opprettSpesifikkeMeldinger...");

    List<SPESIFIKKMELDINGTYPE> spesifikkeMeldinger = null;
    try {
      spesifikkeMeldinger =
          getSpesifikkMeldingReader()
              .opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(
                  linjerMedUspesifikkeMeldinger.stream());
    } catch (MeldingUnreadableException e) {
      logger.error("Kunne ikke lese inn meldinger. Batch " + getBatchName() + " kan ikke fortsette.",
          e);
      throw new BatchException(e);
    }

    logger.debug("About to normally leave Batch.opprettSpesifikkeMeldinger");

    return spesifikkeMeldinger;
  }

  public BatchStatus getBatchStatus() {
    return batchStatus;
  }

  public IOkosynkConfiguration getOkosynkConfiguration() {
    return okosynkConfiguration;
  }

  private Constants.BATCH_TYPE getBatchType() {
    return batchType;
  }

  private IMeldingLinjeFileReader getUspesifikkMeldingLinjeReader() {
    return uspesifikkMeldingLinjeReader;
  }

  private IMeldingReader<SPESIFIKKMELDINGTYPE> getSpesifikkMeldingReader() {
    return spesifikkMeldingReader;
  }

  public void setSpesifikkMeldingReader(
      IMeldingReader<SPESIFIKKMELDINGTYPE> spesifikkMeldingReader) {
    this.spesifikkMeldingReader = spesifikkMeldingReader;
  }

  private OppgaveSynkroniserer getOppgaveSynkroniserer() {
    return oppgaveSynkroniserer;
  }

  public void setOppgaveSynkroniserer(OppgaveSynkroniserer oppgaveSynkroniserer) {
    this.oppgaveSynkroniserer = oppgaveSynkroniserer;
  }

  private IMeldingMapper<SPESIFIKKMELDINGTYPE> getSpesifikkMapper() {
    return spesifikkMapper;
  }

  public void setSpesifikkMapper(IMeldingMapper<SPESIFIKKMELDINGTYPE> spesifikkMapper) {
    this.spesifikkMapper = spesifikkMapper;
  }
}
