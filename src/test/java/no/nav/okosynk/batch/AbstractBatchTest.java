package no.nav.okosynk.batch;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.IMeldingReader;
import no.nav.okosynk.domain.MeldingUnreadableException;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.OkosynkIoException;
import no.nav.okosynk.io.OkosynkIoException.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBatchTest<SPESIFIKKMELDINGTYPE extends AbstractMelding> {

  private static final Logger enteringTestHeaderLogger =
      LoggerFactory.getLogger("EnteringTestHeader");

  private static final long EKSEKVERINGS_ID = 0;

  private Batch batch;

  private IMeldingReader<SPESIFIKKMELDINGTYPE> meldingReader;

  private IMeldingMapper<SPESIFIKKMELDINGTYPE> meldingMapper;

  public Batch getBatch() {
    return batch;
  }

  public void setBatch(Batch batch) {
    this.batch = batch;
  }

  private IMeldingReader<SPESIFIKKMELDINGTYPE> getMeldingReader() {
    return meldingReader;
  }

  @BeforeEach
  void beforeEach() {
    getOkosynkConfiguration().setSystemProperty(
        Constants.FILE_READER_MAX_NUMBER_OF_READ_TRIES_KEY,
        "2");
    getOkosynkConfiguration().setSystemProperty(
        Constants.FILE_READER_RETRY_WAIT_TIME_IN_MILLISECONDS_KEY,
        "1000");
  }

  void setMeldingReader(IMeldingReader<SPESIFIKKMELDINGTYPE> meldingReader) {
    this.meldingReader = meldingReader;
  }

  public IMeldingMapper<SPESIFIKKMELDINGTYPE> getMeldingMapper() {
    return meldingMapper;
  }

  void setMeldingMapper(IMeldingMapper<SPESIFIKKMELDINGTYPE> meldingMapper) {
    this.meldingMapper = meldingMapper;
  }

  public String getFsInputFilePathKey() {
    return fsInputFilePathKey;
  }

  public void setFsInputFilePathKey(String fsInputFilePathKey) {
    this.fsInputFilePathKey = fsInputFilePathKey;
  }

  private String fsInputFilePathKey;

  private final Constants.BATCH_TYPE batchType;

  AbstractBatchTest(final Constants.BATCH_TYPE batchType) {
    this.batchType = batchType;
  }

  public IMeldingLinjeFileReader getMockedUspesifikkMeldingLinjeReader() {
    return mockedUspesifikkMeldingLinjeReader;
  }

  private final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
      mock(IMeldingLinjeFileReader.class);

  public OppgaveSynkroniserer getOppgaveSynkroniserer() {
    return oppgaveSynkroniserer;
  }

  private final OppgaveSynkroniserer oppgaveSynkroniserer =
      mock(OppgaveSynkroniserer.class);

  public IOkosynkConfiguration getOkosynkConfiguration() {
    return okosynkConfiguration;
  }

  private final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

  // =========================================================================
  static long getEksekveringsId() {
    return EKSEKVERINGS_ID;
  }
  // =========================================================================

  void commonPostSetUp() throws OkosynkIoException, MeldingUnreadableException {

    getBatch().setMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
    getBatch().setSpesifikkMeldingReader(getMeldingReader());
    getBatch().setSpesifikkMapper(meldingMapper);
    getBatch().setOppgaveSynkroniserer(oppgaveSynkroniserer);

    when(mockedUspesifikkMeldingLinjeReader.read())
        .thenReturn(emptyList());
    when(getMeldingReader()
        .opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(anyCollection().stream()))
        .thenReturn(new ArrayList<>());
    when(meldingMapper.lagOppgaver(anyList()))
        .thenReturn(new ArrayList());
  }

  // =========================================================================

  @Test
  @DisplayName("Kaster IllegalStateException hvis filområde ikke er satt som system property")
  void batchStatusSetToFeilIfUspesifikkMeldingLinjeFileReaderIsNotOk() {

    enteringTestHeaderLogger.debug(null);

    getBatch().setMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
    when(
        mockedUspesifikkMeldingLinjeReader.getStatus()
    )
        .thenReturn(IMeldingLinjeFileReader.Status.ERROR);

    Assertions.assertEquals(BatchStatus.ERROR, getBatch().getBatchStatus());
  }

  @Test
  @DisplayName("Når batchen har kjørt ferdig skal den ha status FULLFØRT")
  @Disabled
  void runSetterStatusTilFullfortNarBatchErFerdig() {

    enteringTestHeaderLogger.debug(null);

    when(oppgaveSynkroniserer.synkroniser(anyCollection()))
        .thenReturn(ConsumerStatistics.zero(this.batchType));

    getBatch().run();

    assertEquals(BatchStatus.OK_ENDED_WITHOUT_UNEXPECTED_ERRORS, getBatch().getBatchStatus());
  }

  @Test
  @Disabled
  void runLeserFraFil() throws OkosynkIoException {

    enteringTestHeaderLogger.debug(null);

    getBatch().run();

    verify(mockedUspesifikkMeldingLinjeReader).read();
  }

  @Test
  @Disabled
  void runOppretterUrMeldingerFraFil() throws MeldingUnreadableException {

    enteringTestHeaderLogger.debug(null);

    getBatch().run();

    verify(getMeldingReader())
        .opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(anyCollection().stream());
  }

  @Test
  @Disabled
  void statusSettesTilFeilHvisLesingFraFilFeiler() throws OkosynkIoException {

    enteringTestHeaderLogger.debug(null);

    when(mockedUspesifikkMeldingLinjeReader.read())
        .thenThrow(new OkosynkIoException(ErrorCode.IO, new IOException("Noe gikk skeis")));

    getBatch().run();

    assertEquals(BatchStatus.ERROR, getBatch().getBatchStatus());
  }

  @Test
  @Disabled
  void runOppretterMeldingerFraFil() throws MeldingUnreadableException {

    enteringTestHeaderLogger.debug(null);

    getBatch().run();

    verify(getMeldingReader())
        .opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(anyCollection().stream());
  }

  @Test
  @Disabled
  void statusSettesTilFeilHvisOpprettingAvMeldingFeiler() throws MeldingUnreadableException {

    enteringTestHeaderLogger.debug(null);

    when(getMeldingReader()
        .opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(anyCollection().stream()))
        .thenThrow(new MeldingUnreadableException(new IOException("Noe gikk skeis")));

    getBatch().run();

    assertEquals(BatchStatus.ERROR, getBatch().getBatchStatus());
  }

  @Test
  @Disabled
  void runOppretterLokaleOppgaverFraMeldinger() {

    enteringTestHeaderLogger.debug(null);

    getBatch().run();

    verify(meldingMapper).lagOppgaver(anyList());
  }

  @Test
  @Disabled
  void runSynkronisererOppgaverMotGsak() {

    enteringTestHeaderLogger.debug(null);

    getBatch().run();

    final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

    verify(oppgaveSynkroniserer).synkroniser(anyCollection());
  }

  @Test
  void stoppSetterStatusTilStoppet() {

    enteringTestHeaderLogger.debug(null);

    getBatch().stopp();

    assertEquals(BatchStatus.STOPPET, getBatch().getBatchStatus());
  }

  @Test
  void setStatusOverskriverIkkeStatusDersomBatchErStoppet() {

    enteringTestHeaderLogger.debug(null);

    getBatch().stopp();
    getBatch().setBatchStatus(BatchStatus.ERROR);

    assertEquals(BatchStatus.STOPPET, getBatch().getBatchStatus());
  }

  @Test
  @DisplayName("Assert that a null pointer exception is thrown when trying to inject null for uspesifikkMeldingLinjeReader")
  void setUspesifikkMeldingLinjeReaderToNull() {

    enteringTestHeaderLogger.debug(null);

    assertThrows(NullPointerException.class, () -> {
      getBatch().setMeldingLinjeReader(null);
    });
  }
}
