package no.nav.okosynk.batch;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.IMeldingReader;
import no.nav.okosynk.domain.MeldingUnreadableException;
import no.nav.okosynk.domain.Oppgave;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.LinjeUnreadableException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Batch<SPESIFIKKMELDINGTYPE extends AbstractMelding> implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(Batch.class);

  private static final int UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT = 25000;

  private final IOkosynkConfiguration okosynkConfiguration;
  final Constants.BATCH_TYPE batchType;
  private final long executionId;
  private BatchStatus status;
  private IMeldingLinjeFileReader uspesifikkMeldingLinjeReader;

  private IMeldingReader<SPESIFIKKMELDINGTYPE> spesifikkMeldingReader;
  private OppgaveSynkroniserer oppgaveSynkroniserer;
  private IMeldingMapper<SPESIFIKKMELDINGTYPE> spesifikkMapper;

  public Batch(final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType,
      final long executionId,
      final IMeldingReader<SPESIFIKKMELDINGTYPE> spesifikkMeldingReader,
      final IMeldingMapper<SPESIFIKKMELDINGTYPE> spesifikkMapper) {

    // Assume failure, set to ready by the descendant if successful:
    this.setStatus(BatchStatus.FEIL);

    this.okosynkConfiguration = okosynkConfiguration;
    this.batchType = batchType;
    this.executionId = executionId;
    this.uspesifikkMeldingLinjeReader = uspesifikkMeldingLinjeReader;
    this.spesifikkMeldingReader = spesifikkMeldingReader;
    this.oppgaveSynkroniserer = new OppgaveSynkroniserer(
        okosynkConfiguration,
        this::getStatus,
        new OppgaveRestClient(okosynkConfiguration, batchType));
    this.spesifikkMapper = spesifikkMapper;

    this.setStatus(BatchStatus.READY);
  }

  @Override
  public void run() {
    MDC.put("batchnavn", getBatchName());

    final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();

    final CollectorRegistry registry = new CollectorRegistry();

    final Gauge oppgaverOpprettet = Gauge.build()
        .name("okosynk_job_oppgaver_opprettet")
        .help("Antall oppgaver opprettet")
        .register(registry);

    final Gauge oppgaverOppdatert = Gauge.build()
        .name("okosynk_job_oppgaver_oppdatert")
        .help("Antall oppgaver oppdatert")
        .register(registry);

    final Gauge oppgaverFerdigstilt = Gauge.build()
        .name("okosynk_job_oppgaver_ferdigstilt")
        .help("Antall oppgaver ferdigstilt")
        .register(registry);

    final Gauge duration = Gauge.build()
        .name("okosynk_job_duration_seconds")
        .help("Duration of okosynk batch job in seconds.")
        .register(registry);

    final Gauge.Timer durationTimer = duration.startTimer();

    setStatus(BatchStatus.STARTET);
    logger.info("Batch " + getBatchName() + " har startet.");
    try {

      final List<Oppgave> alleOppgaverLestFraBatchen = hentBatchOppgaver();
      final int actualnumberOfOppgaverRetrievedFromBatchInput = alleOppgaverLestFraBatchen.size();
      if (actualnumberOfOppgaverRetrievedFromBatchInput
          > UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT) {
        final String msg =
            String.format(
                  "Batch input innedholder %d oppgaver. "
                + "Det største antallet okosynk er satt til å akseptere er %d oppgaver. "
                + "Okosynk avbrytes.",
                actualnumberOfOppgaverRetrievedFromBatchInput,
                UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT
            );
        throw new RuntimeException(msg);
      }

      final ConsumerStatistics consumerStatistics =
          getOppgaveSynkroniserer().synkroniser(alleOppgaverLestFraBatchen);

      BatchStatus status = BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL;
      setStatus(status);
      logger.info("Batch " + getBatchName() + " er fullført med batchStatus " + status);

      oppgaverOpprettet.set(consumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet());
      oppgaverOppdatert.set(consumerStatistics.getAntallOppgaverSomMedSikkerhetErOppdatert());
      oppgaverFerdigstilt.set(consumerStatistics.getAntallOppgaverSomMedSikkerhetErFerdigstilt());

      final Gauge lastSuccess = Gauge.build()
          .name("okosynk_batch_job_last_success_unixtime")
          .help("Last time okosynk batch job succeeded, in unixtime.")
          .register(registry);
      lastSuccess.setToCurrentTime();
    } catch (Throwable e) {
      logger.error("Noe uventet har gått galt under kjøring av " + getBatchName() + ". ", e);
      setStatus(BatchStatus.FEIL);
      oppgaverOpprettet.set(0);
      oppgaverOppdatert.set(0);
      oppgaverFerdigstilt.set(0);
    } finally {
      durationTimer.setDuration();
      String pushGateway = this.okosynkConfiguration.getRequiredString("PUSH_GATEWAY_ADDRESS");

      if (isNotBlank(pushGateway)) {
        logger.info("Pusher metrikker til {}", pushGateway);
        try {
          new PushGateway(pushGateway).pushAdd(registry, "kubernetes-pods",
              Collections.singletonMap("cronjob", getBatchName()));
        } catch (IOException e) {
          logger.error("Klarte ikke pushe metrikker, ukjent feil", e);
        }
      } else {
        logger.warn("Konfigurasjonsnøkkel PUSH_GATEWAY_ADDRESS mangler, får ikke pushet metrikker");
      }

      MDC.remove("batchnavn");
    }
  }

  /**
   * TODO: OBS! Is it possible to set a reader that conflicts with the batch type?
   * @param uspesifikkMeldingLinjeReader
   */
  public void setMeldingLinjeReader(final IMeldingLinjeFileReader uspesifikkMeldingLinjeReader) {

    Validate.notNull(
        uspesifikkMeldingLinjeReader,
        "The parameter IMeldingLinjeFileReader supplied is null");

    this.uspesifikkMeldingLinjeReader = uspesifikkMeldingLinjeReader;
    setStatus(
        (!IMeldingLinjeFileReader.Status.OK.equals(this.uspesifikkMeldingLinjeReader.getStatus()))
            ?
            BatchStatus.FEIL
            :
                BatchStatus.READY
    );
  }

  public synchronized void setStatus(final BatchStatus status) {
    if (this.status != BatchStatus.STOPPET) {
      this.status = status;
    }
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

  public void stopp() {
    setStatus(BatchStatus.STOPPET);
  }


  private void handterLinjeUnreadableException(LinjeUnreadableException e) {

    logger.error("Kunne ikke lese inn meldinger. Batch " + getBatchName() + " kan ikke fortsette.",
        e);
    setStatus(BatchStatus.FEIL);

    throw new BatchExecutionException(e);
  }

  private void handterMeldingUnreadableException(final MeldingUnreadableException e) {

    logger.error("Kunne ikke lese inn meldinger. Batch " + getBatchName() + " kan ikke fortsette.",
        e);
    setStatus(BatchStatus.FEIL);

    throw new BatchExecutionException(e);
  }

  private List<Oppgave> hentBatchOppgaver() {

    logger.debug("Entering Batch.hentBatchOppgaver...");

    final List<String> linjerMedUspesifikkeMeldinger = hentLinjerMedUspesifikkeMeldinger();
    final List<SPESIFIKKMELDINGTYPE> spesifikkeMeldinger = opprettSpesifikkeMeldinger(
        linjerMedUspesifikkeMeldinger);
    final List<Oppgave> batchOppgaver = getSpesifikkMapper().lagOppgaver(spesifikkeMeldinger);

    logger.debug("About to normally leave Batch.hentBatchOppgaver");

    return batchOppgaver;
  }

  private List<String> hentLinjerMedUspesifikkeMeldinger() {

    logger.debug("Entering Batch.hentLinjerMedUspesifikkeMeldinger...");

    List<String> linjerMedUspesifikkeMeldinger = null;
    try {
      linjerMedUspesifikkeMeldinger = getUspesifikkMeldingLinjeReader().read();
      logger.info(
          "STATISTIKK: {} meldinger ble lest inn. Batch name: {}",
          linjerMedUspesifikkeMeldinger.size(),
          getBatchName());
    } catch (LinjeUnreadableException e) {
      handterLinjeUnreadableException(e);
    }

    logger.debug("About to normally leave Batch.hentLinjerMedUspesifikkeMeldinger");

    return linjerMedUspesifikkeMeldinger;
  }

  private List<SPESIFIKKMELDINGTYPE> opprettSpesifikkeMeldinger(
      final List<String> linjerMedUspesifikkeMeldinger) {
    logger.debug("Entering Batch.opprettSpesifikkeMeldinger...");

    List<SPESIFIKKMELDINGTYPE> spesifikkeMeldinger = null;
    try {
      spesifikkeMeldinger =
          getSpesifikkMeldingReader()
              .opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(
                  linjerMedUspesifikkeMeldinger.stream());
    } catch (MeldingUnreadableException e) {
      handterMeldingUnreadableException(e);
    }

    logger.debug("About to normally leave Batch.opprettSpesifikkeMeldinger");

    return spesifikkeMeldinger;
  }

  public BatchStatus getStatus() {
    return status;
  }

  public IOkosynkConfiguration getOkosynkConfiguration() {
    return okosynkConfiguration;
  }

  private Constants.BATCH_TYPE getBatchType() {
    return batchType;
  }


  public long getExecutionId() {
    return executionId;
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
