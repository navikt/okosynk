package no.nav.okosynk.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.domain.Oppgave;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OppgaveSynkronisererTest {

  private static final Logger logger =
      LoggerFactory.getLogger(OppgaveSynkronisererTest.class);
  private static final Logger enteringTestHeaderLogger =
      LoggerFactory.getLogger("EnteringTestHeader");

  public  static final String EKSTERN_OPPGAVETYPE_KODE = "OKO_UTB";
  private static final Constants.BATCH_TYPE BATCH_TYPE = Constants.BATCH_TYPE.OS;
  private static final String OPPGAVEID_GSAK = "185587300";
  private static final String OPPGAVEID = "185587998";
  private static final String BRUKERID_GSAK = "10108000398";
  private static final String BRUKERID = "06025800174";

  private static final IOkosynkConfiguration okosynkConfiguration =
      new FakeOkosynkConfiguration();

  private OppgaveSynkroniserer oppgaveSynkronisererWithInjectedMocks;
  private OppgaveRestClient mockedOppgaveRestClient;
  private BatchStatus batchStatus;

  private static String getBatchBruker(final IOkosynkConfiguration okosynkConfiguration) {

    final String batchBruker =
        okosynkConfiguration
            .getString(
                BATCH_TYPE.getBatchBrukerKey(),
                BATCH_TYPE.getBatchBrukerDefaultValue()
            );

    return batchBruker;
  }

  @BeforeEach
  void setUp() {

    logger.debug("About to create a new OppgaveSynkroniserer instance equipped with the mocked versions of OppgaveRestClient...");

    mockedOppgaveRestClient = mock(OppgaveRestClient.class);

    this.oppgaveSynkronisererWithInjectedMocks =
        new OppgaveSynkroniserer(
            OppgaveSynkronisererTest.okosynkConfiguration,
            this::getBatchStatus,
            mockedOppgaveRestClient);

    this.batchStatus = BatchStatus.STARTET;

    final Set<Oppgave> oppgaveListe =
        lagOppgaveliste(OPPGAVEID_GSAK, BRUKERID_GSAK);

    when(this.mockedOppgaveRestClient.finnOppgaver(anySet()))
      .thenReturn(
          // TODO: As of now, just a placeholder:
          ConsumerStatistics.zero(OppgaveSynkronisererTest.BATCH_TYPE.getConsumerStatisticsName())
      )
    /*
    TODO: Quasi code for what is wanted as mock.
    .thenSetTheSeconParameterTo(
        oppgaveListe
    )
    */
    ;

    when(this.mockedOppgaveRestClient.opprettOppgaver(anyCollection()))
        .thenReturn(
            ConsumerStatistics.zero(OppgaveSynkronisererTest.BATCH_TYPE.getConsumerStatisticsName())
        )
  //        /*
  //        TODO: Quasi code for what is wanted as mock.
  //        .thenSetTheSeconParameterTo(
  //            oppgaveListe
  //        )
  //         */
    ;

    when(this.mockedOppgaveRestClient.patchOppgaver(anySet(), anyBoolean()))
        .thenReturn(
            ConsumerStatistics.zero(OppgaveSynkronisererTest.BATCH_TYPE.getConsumerStatisticsName())
        )
    //        /*
    //        TODO: Quasi code for what is wanted as mock.
    //        .thenSetTheSeconParameterTo(
    //            oppgaveListe
    //        )
    //         */
    ;

    when(this.mockedOppgaveRestClient.getBatchType())
      .thenReturn(OppgaveSynkronisererTest.BATCH_TYPE)
//        /*
//        TODO: Quasi code for what is wanted as mock.
//        .thenSetTheSeconParameterTo(
//            oppgaveListe
//        )
//         */
    ;
//
//        // =====================================================================
//
//        this.okosynkConfiguration.clearSystemProperty("osbatch.bruker");
//        this.okosynkConfiguration.clearSystemProperty("urbatch.bruker");
//
//        // =====================================================================
  }

  // =========================================================================

  @Test
  void when_the_batch_is_started_when_synchronize_is_called_then_service_calls_to_patchOppgave_or_opprettOppgaver_should_be_made() {

    enteringTestHeaderLogger.debug(null);

    final String batchBruker = getBatchBruker(OppgaveSynkronisererTest.okosynkConfiguration);
    this.batchStatus = BatchStatus.STARTET;
    this.oppgaveSynkronisererWithInjectedMocks
        .synkroniser(lagOppgaveliste(OPPGAVEID, BRUKERID));

    final Set<Oppgave> funneOppgaver = new HashSet<>();
    verify(this.mockedOppgaveRestClient).finnOppgaver(funneOppgaver);
  }

  @Test
  void when_the_batch_is_stopped_when_synchronize_is_called_then_no_service_calls_to_patchOppgave_or_opprettOppgaver_should_be_made() {

    enteringTestHeaderLogger.debug(null);

    this.batchStatus = BatchStatus.STOPPET;
    this.oppgaveSynkronisererWithInjectedMocks
        .synkroniser(lagOppgaveliste(OPPGAVEID, BRUKERID));
    verify(this.mockedOppgaveRestClient, times(0)).patchOppgaver   (anySet(), anyBoolean());
    verify(this.mockedOppgaveRestClient, times(0)).opprettOppgaver (anyCollection());
  }

  @Test
  void when_the_batch_is_stopped_then_synchronizer_opprettOppgaver_should_not_call_rest_clients_opprettOppgaver() {

      enteringTestHeaderLogger.debug(null);

      batchStatus = BatchStatus.STOPPET;
      this.oppgaveSynkronisererWithInjectedMocks
          .opprettOppgaver(lagOppgaveliste(OPPGAVEID, BRUKERID));
      verify(this.mockedOppgaveRestClient, times(0)).opprettOppgaver(anyCollection());
  }

  @Test
  void when_the_batch_is_stopped_then_synchronizer_oppdaterOppgaver_should_not_call_rest_clients_oppdaterOppgaver() {

      enteringTestHeaderLogger.debug(null);

      batchStatus = BatchStatus.STOPPET;
      this.oppgaveSynkronisererWithInjectedMocks
          .oppdaterOppgaver(lagOppgaveOppdatering(lagOppgave(OPPGAVEID, BRUKERID), lagOppgave(OPPGAVEID_GSAK, BRUKERID)));
      verify(this.mockedOppgaveRestClient, times(0)).patchOppgaver(anySet(), anyBoolean());
  }

  @Test
  void when_synkroniser_is_called_then_all_rest_client_methods_should_be_called_once() {

      enteringTestHeaderLogger.debug(null);

      this.oppgaveSynkronisererWithInjectedMocks =
          Mockito.spy(new OppgaveSynkroniserer(
              OppgaveSynkronisererTest.okosynkConfiguration,
              this::getBatchStatus,
              mockedOppgaveRestClient));

      this.oppgaveSynkronisererWithInjectedMocks
          .synkroniser(lagOppgaveliste(OPPGAVEID, BRUKERID));

      verify(this.oppgaveSynkronisererWithInjectedMocks, times(1)).ferdigstillOppgaver(anySet());
      verify(this.oppgaveSynkronisererWithInjectedMocks, times(1)).oppdaterOppgaver(anySet());
      verify(this.oppgaveSynkronisererWithInjectedMocks, times(1)).opprettOppgaver(anySet());
  }

  @Test
  void when_beskrivelse_is_updated_then_it_should_be_reflected_in_the_resulting_oppgaver_som_skal_oppdateres() {

    enteringTestHeaderLogger.debug(null);

    final String nyBeskrivelse = "Beskrivelsen etter endring.";
    final Oppgave ikkeOppdatertOppgave = lagOppgave(OPPGAVEID, BRUKERID);
    final Oppgave oppdatertOppgave = new Oppgave.OppgaveBuilder()
              .withSameValuesAs(ikkeOppdatertOppgave)
              .withBeskrivelse(nyBeskrivelse)
              .build();
    this
        .oppgaveSynkronisererWithInjectedMocks
        .oppdaterOppgaver(
            OppgaveSynkroniserer.finnOppgaverSomSkalOppdateres(
                    Collections.singleton(oppdatertOppgave),
                    Collections.singleton(ikkeOppdatertOppgave)
            )
        );

    final ArgumentCaptor<Set<Oppgave>> captor = forClass((Class) List.class);
    verify(this.mockedOppgaveRestClient, atLeast(1))
        .patchOppgaver(captor.capture(), anyBoolean());
    assertEquals(nyBeskrivelse, captor.getValue().iterator().next().beskrivelse);
  }

  @Test
  void when_batch_beskrivelse_differs_from_database_and_db_has_no_code_then_the_batch_beskrivelse_should_override() {

    enteringTestHeaderLogger.debug(null);

    final String lokalOppgaveBeskrivelse =
        "STATUS;;Dette skal beholdes";
    final String oppgaveBeskrivelseLestFraDatabasen =
        "ANNEN KODE;; Noen har endret på dette, det blir forkastet!";
    final String expectedtBeskrivelse =
        lokalOppgaveBeskrivelse;

    final Oppgave lokalOppgave =
        lagOppgave(OPPGAVEID, BRUKERID, lokalOppgaveBeskrivelse);
    final Oppgave oppgaveLestFraDatabasen =
        new Oppgave.OppgaveBuilder()
          .withSameValuesAs(lokalOppgave)
          .withBeskrivelse(oppgaveBeskrivelseLestFraDatabasen)
          .build();
    final Set<OppgaveSynkroniserer.OppgaveOppdatering> oppgaver =
        this.oppgaveSynkronisererWithInjectedMocks.finnOppgaverSomSkalOppdateres(
            Collections.singleton(lokalOppgave),
            Collections.singleton(oppgaveLestFraDatabasen)
        );

    this.oppgaveSynkronisererWithInjectedMocks.oppdaterOppgaver(oppgaver);

    final ArgumentCaptor<Set<Oppgave>> captor =
        ArgumentCaptor.<Object>forClass((Class) Set.class);

    verify(
        this.mockedOppgaveRestClient,
        atLeast(1)
    )
        .patchOppgaver(captor.capture(), anyBoolean());

    final String actualBeskrivelse =
        captor.getValue().iterator().next().beskrivelse;
    assertEquals(expectedtBeskrivelse, actualBeskrivelse);
  }

  @Test
  void when_batch_beskrivelse_differs_from_database_and_db_has_code_then_the_batch_beskrivelse_should_override_with_code_from_the_db_inserted(){

    enteringTestHeaderLogger.debug(null);

    final String lokalOppgaveBeskrivelse =
        "Oppgavestatus;;Oppsummering av meldinger som er slått sammen på oppgaven";
    final String oppgaveBeskrivelseLestFraDatabasen =
        "ikke viktig;PESYS KODE IKKEMED;Noen har endret på dette, det blir forkastet!";
    final String expectedtBeskrivelse =
        "Oppgavestatus;PESYS KODE;Oppsummering av meldinger som er slått sammen på oppgaven";
    final Oppgave lokalOppgave =
        lagOppgave(OPPGAVEID, BRUKERID, lokalOppgaveBeskrivelse);
    final Oppgave oppgaveLestFraDatabasen =
        new Oppgave.OppgaveBuilder()
            .withSameValuesAs(lokalOppgave)
            .withBeskrivelse(oppgaveBeskrivelseLestFraDatabasen)
            .build();
    final Set<OppgaveSynkroniserer.OppgaveOppdatering> oppgaver =
        this.oppgaveSynkronisererWithInjectedMocks.finnOppgaverSomSkalOppdateres(
            Collections.singleton(lokalOppgave),
            Collections.singleton(oppgaveLestFraDatabasen)
        );

    this.oppgaveSynkronisererWithInjectedMocks.oppdaterOppgaver(oppgaver);

    final ArgumentCaptor<Set<Oppgave>> captor =
        ArgumentCaptor.forClass((Class) Set.class);

    verify(
        this.mockedOppgaveRestClient,
        atLeast(1)
    )
        .patchOppgaver(captor.capture(), anyBoolean());

    final String actualBeskrivelse =
        captor.getValue().iterator().next().beskrivelse;
    assertEquals(expectedtBeskrivelse, actualBeskrivelse);
  }

  @Test
  void when_synkronisators_oppdater_is_called_then_the_rest_client_should_be_called_with_oppgaver_that_should_be_pathed() {

    enteringTestHeaderLogger.debug(null);

    final String  nyBeskrivelse        = "Beskrivelsen etter endring.";
    final Oppgave ikkeOppdatertOppgave = lagOppgave(OPPGAVEID, BRUKERID);
    final Oppgave oppdatertOppgave     =
        new Oppgave.OppgaveBuilder()
            .withSameValuesAs(ikkeOppdatertOppgave)
            .withBeskrivelse(nyBeskrivelse)
            .build();

    final OppgaveSynkroniserer.OppgaveOppdatering oppgaveOppdatering =
          new OppgaveSynkroniserer.OppgaveOppdatering(ikkeOppdatertOppgave, oppdatertOppgave);

      this.oppgaveSynkronisererWithInjectedMocks
          .oppdaterOppgaver(Collections.singleton(oppgaveOppdatering));

      verify(this.mockedOppgaveRestClient).patchOppgaver(anySet(), anyBoolean());
  }

  @Test
  void when_synkroniserers_opprett_is_called_then_the_rest_clients_opprett_should_also_be_called() {

      enteringTestHeaderLogger.debug(null);

      this.oppgaveSynkronisererWithInjectedMocks.opprettOppgaver(lagOppgaveliste(OPPGAVEID, BRUKERID));

      verify(this.mockedOppgaveRestClient).opprettOppgaver(anyCollection());
  }

  @Test
  void when_synkroniserer_is_called_with_no_oppgaver_then_rest_client_should_not_be_called() {
      this.oppgaveSynkronisererWithInjectedMocks.opprettOppgaver(new HashSet<>());

      verify(this.mockedOppgaveRestClient, times(0)).opprettOppgaver(anySet());
  }

  @Test
  void when_batch_indicates_changed_oppgavetype_then_the_oppgave_should_not_be_ferdigstilt(){

      enteringTestHeaderLogger.debug(null);

      final Oppgave oppgave =
          lagOppgave(
              OPPGAVEID,
              OppgaveSynkronisererTest.getBatchBruker(OppgaveSynkronisererTest.okosynkConfiguration)
          );
      final Oppgave oppgaveMedEndretOppgaveType =
          new Oppgave
              .OppgaveBuilder()
              .withSameValuesAs(oppgave)
              .withOppgavetypeKode(EKSTERN_OPPGAVETYPE_KODE)
              .build();
      final Set<Oppgave> oppgaverSomSkalFerdigstilles =
              this.oppgaveSynkronisererWithInjectedMocks
                  .finnOppgaverSomSkalFerdigstilles(
                      new HashSet<>(),
                      Collections.singleton(oppgaveMedEndretOppgaveType));

      this.oppgaveSynkronisererWithInjectedMocks
          .ferdigstillOppgaver(oppgaverSomSkalFerdigstilles);

      final ArgumentCaptor<Set<Oppgave>> captor =
          ArgumentCaptor.forClass((Class) Set.class);
      verify(
          this.mockedOppgaveRestClient,
          times(0)
      )
          .patchOppgaver(anySet(), anyBoolean());
  }

  @Test
  void when_batch_indicates_not_changed_oppgavetype_then_the_oppgave_should_be_ferdigstilt(){

      enteringTestHeaderLogger.debug(null);

      final Oppgave oppgave = lagOppgave(OPPGAVEID, "srvbokosynk001");

      final Set<Oppgave> oppgaverSomSkalFerdigstilles =
          this.oppgaveSynkronisererWithInjectedMocks
              .finnOppgaverSomSkalFerdigstilles(
                  new HashSet<>(),
                  Collections.singleton(oppgave));

      this.oppgaveSynkronisererWithInjectedMocks
          .ferdigstillOppgaver(oppgaverSomSkalFerdigstilles);

      final ArgumentCaptor<Collection<Oppgave>> captor = ArgumentCaptor.forClass((Class) List.class);
      verify(this.mockedOppgaveRestClient, times(1)).patchOppgaver(anySet(), anyBoolean());
  }

  // =========================================================================

  BatchStatus getBatchStatus() {
    return this.batchStatus;
  }

  private Set<Oppgave> lagOppgaveliste(String oppgaveId, String brukerId) {
    Set<Oppgave> oppgaveliste = new HashSet<>();
    oppgaveliste.add(lagOppgave(oppgaveId, brukerId));

    return oppgaveliste;
  }

  private Set<OppgaveSynkroniserer.OppgaveOppdatering> lagOppgaveOppdatering(
      final Oppgave oppgaveLestFraBatch,
      final Oppgave oppgaveLestFraDatabasen) {

      final Set<OppgaveSynkroniserer.OppgaveOppdatering> oppgaveOppdateringsListe = new HashSet<>();
      oppgaveOppdateringsListe.add(new OppgaveSynkroniserer.OppgaveOppdatering(oppgaveLestFraBatch, oppgaveLestFraDatabasen));

      return oppgaveOppdateringsListe;
  }

  private Oppgave lagOppgave(String oppgaveId, String brukerId) {
    return lagOppgave(oppgaveId, brukerId,
        "STATUS;;oppsummer meldinger slått sammen til en oppgave");
  }

  private Oppgave lagOppgave(
      final String oppgaveId,
      final String brukerId,
      final String beskrivelse) {

    final Oppgave oppgave =
        new Oppgave.OppgaveBuilder()
          .withOppgaveId(oppgaveId)
          //.withBrukerId(brukerId)
          //.withBrukertypeKode("PERSON")
          .withOppgavetypeKode("OKO_OS")
          .withFagomradeKode("BA")
          //.withUnderkategoriKode("BA")
          .withPrioritetKode("LAV_OKO")
          .withBeskrivelse(beskrivelse)
          .withAnsvarligEnhetId("4151")
          .withLest(false)
          .withVersjon(1)
          .withSistEndret(LocalDateTime.of(1997, 2, 4, 7, 8, 36))
          .withAktivFra(LocalDate.of(1997, 2, 2))
          .withAktivTil(LocalDate.of(1997, 2, 9))
          .build();

    return oppgave;
  }
}
