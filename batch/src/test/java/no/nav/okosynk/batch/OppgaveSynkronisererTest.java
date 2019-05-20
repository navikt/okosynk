package no.nav.okosynk.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.consumer.oppgave.IOppgaveConsumerGateway;
import no.nav.okosynk.consumer.oppgavebehandling.IOppgaveBehandlingConsumerGateway;
import no.nav.okosynk.domain.Oppgave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OppgaveSynkronisererTest {

    private static final Logger logger = LoggerFactory.getLogger(OppgaveSynkronisererTest.class);

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String                BATCHBRUKER              = "srvokosynk";
    private static final String                OPPGAVEID_GSAK           = "185587300";
    private static final String                OPPGAVEID                = "185587998";
    private static final String                BRUKERID_GSAK            = "10108000398";
    private static final String                BRUKERID                 = "06025800174";
    public  static final String                EKSTERN_OPPGAVETYPE_KODE = "OKO_UTB";
    private static final IOkosynkConfiguration okosynkConfiguration     = new FakeOkosynkConfiguration();

    private final IOppgaveConsumerGateway mockedOppgaveGateway =
        mock(IOppgaveConsumerGateway.class);
    private final IOppgaveBehandlingConsumerGateway mockedOppgaveBehandlingGateway =
        mock(IOppgaveBehandlingConsumerGateway.class);
    private OppgaveSynkroniserer oppgaveSynkronisererWithInjectedMocks;
    private BatchStatus batchStatus;

    @BeforeEach
    void setUp() {

        logger.debug("About to create a new OppgaveSynkroniserer instance equipped with the mocked versions of IOppgaveConsumerGateway and IOppgaveBehandlingConsumerGateway...");

        this.oppgaveSynkronisererWithInjectedMocks =
            new OppgaveSynkroniserer(
                this.mockedOppgaveGateway,
                this.mockedOppgaveBehandlingGateway,
                this::getBatchStatus);

        this.batchStatus = BatchStatus.STARTET;

        final Set<Oppgave> oppgaveListe =
            lagOppgaveliste(OPPGAVEID_GSAK, BRUKERID_GSAK);
        when(
            this
                .mockedOppgaveGateway
                .finnOppgaver(anyString(), anyCollection())
        )
        .thenReturn(
            // TODO: As of now, just a placeholder:
            ConsumerStatistics.zero()
        )
        /*
        TODO: Quasi code for what is wanted as mock.
        .thenSetTheSeconParameterTo(
            oppgaveListe
        )
         */
        ;

        when(
            this
                .mockedOppgaveBehandlingGateway
                .opprettOppgaver(any(IOkosynkConfiguration.class), anyCollection())
        )
            .thenReturn(
                // TODO: As of now, just a placeholder:
                ConsumerStatistics.zero()
            )
        /*
        TODO: Quasi code for what is wanted as mock.
        .thenSetTheSeconParameterTo(
            oppgaveListe
        )
         */
        ;

        // =====================================================================

        this.okosynkConfiguration.clearSystemProperty("osbatch.bruker");
        this.okosynkConfiguration.clearSystemProperty("urbatch.bruker");

        // =====================================================================
    }

    // =========================================================================

    @Test
    void synkroniser_skal_hente_oppgaver_fra_oppgave_applikasjonen() {

        enteringTestHeaderLogger.debug(null);

        this.oppgaveSynkronisererWithInjectedMocks
            .synkroniser(
                this.okosynkConfiguration,
                lagOppgaveliste(OPPGAVEID, BRUKERID),
                BATCHBRUKER);

        final Collection<Oppgave> funneOppgaver = new HashSet<>();
        verify(this.mockedOppgaveGateway).finnOppgaver(BATCHBRUKER, funneOppgaver);
    }

    @Test
    @DisplayName("Hvis batchen er stoppet når synkroniser-metoden startes skal det ikke gjøres tjenestekall for ferdigstillOppgaver, opprettOppgaver eller oppdaterOppgaver")
    void synkroniserSkalIkkeKalleOppgavebehandlingHvisBatchErStoppet() {

        enteringTestHeaderLogger.debug(null);

        batchStatus = BatchStatus.STOPPET;

        this.oppgaveSynkronisererWithInjectedMocks
            .synkroniser(
                this.okosynkConfiguration,
                lagOppgaveliste(OPPGAVEID, BRUKERID),
                BATCHBRUKER);

        verify(mockedOppgaveBehandlingGateway, times(0)).ferdigstillOppgaver(       anyCollection());
        verify(mockedOppgaveBehandlingGateway, times(0)).opprettOppgaver    (any(), anyCollection());
        verify(mockedOppgaveBehandlingGateway, times(0)).oppdaterOppgaver   (any(), anyCollection());
    }

    @Test
    @DisplayName("Hvis batchen stoppes før oppretting av oppgaver starter skal det ikke gjøres tjenestekall for opprettOppgaver")
    void synkroniserSkalIkkeKalleOpprettHvisBatchStoppes() {

        enteringTestHeaderLogger.debug(null);

        batchStatus = BatchStatus.STOPPET;

        this.oppgaveSynkronisererWithInjectedMocks.opprettOppgaver(okosynkConfiguration, lagOppgaveliste(OPPGAVEID, BRUKERID), BATCHBRUKER);

        verify(mockedOppgaveBehandlingGateway, times(0)).opprettOppgaver(any(), anyCollection());
    }

    @Test
    @DisplayName("Hvis batchen stoppes før oppdatering av oppgaver starter skal det ikke gjøres tjenestekall for oppdaterOppgaver")
    void synkroniserSkalIkkeKalleOppdaterHvisBatchStoppes() {

        enteringTestHeaderLogger.debug(null);

        batchStatus = BatchStatus.STOPPET;

        this.oppgaveSynkronisererWithInjectedMocks.oppdaterOppgaver(okosynkConfiguration, lagOppgaveOppdatering(lagOppgave(OPPGAVEID, BRUKERID), lagOppgave(OPPGAVEID_GSAK, BRUKERID)), BATCHBRUKER);

        verify(mockedOppgaveBehandlingGateway, times(0)).oppdaterOppgaver(any(), anyCollection());
    }

    @Test
    @DisplayName("kall ferdigstillOppgaver, oppdaterOppgaver og opprettOppgaver i synkroniser(), rekkefølgen er ikke viktig")
    void kallAlleOppgaveOperasjonerISynkroniser() {

        enteringTestHeaderLogger.debug(null);

        this.oppgaveSynkronisererWithInjectedMocks = Mockito.spy(new OppgaveSynkroniserer(mockedOppgaveGateway, mockedOppgaveBehandlingGateway, this::getBatchStatus));

        this.oppgaveSynkronisererWithInjectedMocks
            .synkroniser(
                this.okosynkConfiguration,
                lagOppgaveliste(OPPGAVEID, BRUKERID),
                BATCHBRUKER);

        verify(this.oppgaveSynkronisererWithInjectedMocks, times(1)).ferdigstillOppgaver(any(), anySet(), anyString());
        verify(this.oppgaveSynkronisererWithInjectedMocks, times(1)).oppdaterOppgaver(any(), anySet(), anyString());
        verify(this.oppgaveSynkronisererWithInjectedMocks, times(1)).opprettOppgaver(any(), anySet(), anyString());
    }

    @Test
    void oppdaterOppdatererBeskrivelsen() {

        enteringTestHeaderLogger.debug(null);

        String nyBeskrivelse = "Beskrivelsen etter endring.";
        Oppgave ikkeOppdatertOppgave = lagOppgave(OPPGAVEID, BRUKERID);
        Oppgave oppdatertOppgave = new Oppgave.OppgaveBuilder()
                .withSameValuesAs(ikkeOppdatertOppgave)
                .withBeskrivelse(nyBeskrivelse)
                .build();

        this
            .oppgaveSynkronisererWithInjectedMocks
            .oppdaterOppgaver(
                okosynkConfiguration,
                this.oppgaveSynkronisererWithInjectedMocks
                    .finnOppgaverSomSkalOppdateres(
                        Collections
                            .singleton(oppdatertOppgave),
                        Collections
                            .singleton(ikkeOppdatertOppgave)),
                BATCHBRUKER);

        final ArgumentCaptor<Collection<Oppgave>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(mockedOppgaveBehandlingGateway,atLeast(1)).oppdaterOppgaver(any(), captor.capture());
        assertEquals(nyBeskrivelse, captor.getValue().iterator().next().beskrivelse);
    }

    @Test
    @DisplayName("i oppdaterOppgaver() forkast endringer i oppgave-applikasjonens oppgavebeskrivelser og behold beskrivelse fra oppgave lest fra b batch")
    void oppdater_oppgave_med_beskrivelse_fra_oppgave_lest_fra_batch() {

        enteringTestHeaderLogger.debug(null);

        String oppgaveBeskrivelseLestFraDatabasen = "ANNEN KODE;; Noen har endret på dette, det blir forkastet!";
        String oppgaveBeskrivelseLestFraBatch = "STATUS;;Dette skal beholdes";
        final Oppgave oppgaveLestFraBatch = lagOppgave(OPPGAVEID, BRUKERID, oppgaveBeskrivelseLestFraBatch);
        final Oppgave oppgaveLestFraDatabasen = new Oppgave.OppgaveBuilder()
                .withSameValuesAs(oppgaveLestFraBatch)
                .withBeskrivelse(oppgaveBeskrivelseLestFraDatabasen)
                .build();
        Set<OppgaveSynkroniserer.OppgaveOppdatering> oppgaver = this.oppgaveSynkronisererWithInjectedMocks.finnOppgaverSomSkalOppdateres(
                Collections.singleton(oppgaveLestFraBatch),
                Collections.singleton(oppgaveLestFraDatabasen));

        this.oppgaveSynkronisererWithInjectedMocks.oppdaterOppgaver(okosynkConfiguration, oppgaver, BATCHBRUKER);

        final ArgumentCaptor<Collection<Oppgave>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(mockedOppgaveBehandlingGateway, atLeast(1)).oppdaterOppgaver(any(), captor.capture());
        final String oppgaveBeskrivelse = captor.getValue().iterator().next().beskrivelse;
        assertEquals(oppgaveBeskrivelseLestFraBatch, oppgaveBeskrivelse);
    }


    @Test
    @DisplayName("i oppdaterOppgaver() kast endringer fra oppgavebeskrivelse i databasen, unntatt 10 tegn mellom to første semikolon")
    void oppdater_oppgave_med_kode_fra_oppgave_lest_fra_databasen(){

        enteringTestHeaderLogger.debug(null);

        final String lokalOppgaveBeskrivelse = "Oppgavestatus;;Oppsummering av meldinger som er slått sammen på oppgaven";
        final String oppgaveBeskrivelseLestFraDatabasen = "ikke viktig;PESYS KODE IKKEMED;Noen har endret på dette, det blir forkastet!";
        final String forventetBeskrivelse = "Oppgavestatus;PESYS KODE;Oppsummering av meldinger som er slått sammen på oppgaven";
        final Oppgave lokalOppgave = lagOppgave(OPPGAVEID, BRUKERID, lokalOppgaveBeskrivelse);
        final Oppgave oppgaveLestFraDatabasen =
            new Oppgave.OppgaveBuilder()
                .withSameValuesAs(lokalOppgave)
                .withBeskrivelse(oppgaveBeskrivelseLestFraDatabasen)
                .build();
        final Set<OppgaveSynkroniserer.OppgaveOppdatering> oppgaver = this.oppgaveSynkronisererWithInjectedMocks.finnOppgaverSomSkalOppdateres(
                Collections.singleton(lokalOppgave),
                Collections.singleton(oppgaveLestFraDatabasen));

        this.oppgaveSynkronisererWithInjectedMocks.oppdaterOppgaver(okosynkConfiguration, oppgaver, BATCHBRUKER);

        final ArgumentCaptor<Collection<Oppgave>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(mockedOppgaveBehandlingGateway,atLeast(1)).oppdaterOppgaver(any(), captor.capture());
        String oppgaveBeskrivelse = captor.getValue().iterator().next().beskrivelse;
        assertEquals(forventetBeskrivelse, oppgaveBeskrivelse);
    }

    @Test
    void oppdaterKallerTjenesteForOppgavebehandling() {

        enteringTestHeaderLogger.debug(null);

        String nyBeskrivelse = "Beskrivelsen etter endring.";

        Oppgave ikkeOppdatertOppgave = lagOppgave(OPPGAVEID, BRUKERID);
        Oppgave oppdatertOppgave = new Oppgave.OppgaveBuilder()
                .withSameValuesAs(ikkeOppdatertOppgave)
                .withBeskrivelse(nyBeskrivelse)
                .build();

        OppgaveSynkroniserer.OppgaveOppdatering oppgaveOppdatering= new OppgaveSynkroniserer.OppgaveOppdatering(ikkeOppdatertOppgave, oppdatertOppgave);

        this.oppgaveSynkronisererWithInjectedMocks.oppdaterOppgaver(okosynkConfiguration, Collections.singleton(oppgaveOppdatering), BATCHBRUKER);

        verify(mockedOppgaveBehandlingGateway).oppdaterOppgaver(any(), anyCollection());
    }

    @Test
    void opprettKallerTjenesteForOppgavebehandling() {

        enteringTestHeaderLogger.debug(null);

        this.oppgaveSynkronisererWithInjectedMocks.opprettOppgaver(okosynkConfiguration, lagOppgaveliste(OPPGAVEID, BRUKERID), BATCHBRUKER);

        verify(mockedOppgaveBehandlingGateway).opprettOppgaver(any(), anyCollection());
    }

    @Test
    @DisplayName("Hvis det ikke er noen oppgaver å opprette skal ikke tjenesten kalles")
    void opprettKallerIkkeTjenesteForOppgavebehandlingUtenOppgaver() {
        this.oppgaveSynkronisererWithInjectedMocks.opprettOppgaver(this.okosynkConfiguration, new HashSet<>(), BATCHBRUKER);

        verify(mockedOppgaveBehandlingGateway, times(0)).opprettOppgaver(any(), anyCollection());
    }

    @Test
    @DisplayName("ikke ferdigstillOppgaver oppgaver som har endret oppgavetype")
    void ikkeFerdigstillOppgaverMedEndretOppgaveType(){

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave = lagOppgave(OPPGAVEID, "srvbokosynk002");
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
            .ferdigstillOppgaver(
                this.okosynkConfiguration,
                oppgaverSomSkalFerdigstilles,
                "srvbokosynk002");

        final ArgumentCaptor<Collection<Oppgave>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(mockedOppgaveBehandlingGateway, times(0)).ferdigstillOppgaver(anyCollection());
    }

    @Test
    @DisplayName("ferdigstillOppgaver oppgave som har uendret oppgavetype")
    void ferdigstillOppgaveMedUendretOppgaveType(){

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave = lagOppgave(OPPGAVEID, "srvbokosynk001");

        final Set<Oppgave> oppgaverSomSkalFerdigstilles =
            this.oppgaveSynkronisererWithInjectedMocks
                .finnOppgaverSomSkalFerdigstilles(
                    new HashSet<>(),
                    Collections.singleton(oppgave));

        this.oppgaveSynkronisererWithInjectedMocks
            .ferdigstillOppgaver(
                this.okosynkConfiguration,
                oppgaverSomSkalFerdigstilles,
                "srvbokosynk001");

        final ArgumentCaptor<Collection<Oppgave>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(this.mockedOppgaveBehandlingGateway, times(1)).ferdigstillOppgaver(anyCollection());
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
        return lagOppgave(oppgaveId, brukerId, "STATUS;;oppsummer meldinger slått sammen til en oppgave");
    }

    private Oppgave lagOppgave(String oppgaveId, String brukerId, String beskrivelse) {
        Oppgave.OppgaveBuilder oppgaveBuilder = new Oppgave.OppgaveBuilder()
                .withOppgaveId(oppgaveId)
                .withBrukerId(brukerId)
                .withBrukertypeKode("PERSON")
                .withOppgavetypeKode("OKO_OS")
                .withFagomradeKode("BA")
                .withUnderkategoriKode("BA")
                .withPrioritetKode("LAV_OKO")
                .withBeskrivelse(beskrivelse)
                .withAnsvarligEnhetId("4151")
                .withLest(false)
                .withVersjon(1)
                .withSistEndret(LocalDateTime.of(1997, 2, 4, 7, 8, 36))
                .withAktivFra(LocalDate.of(1997, 2, 2))
                .withAktivTil(LocalDate.of(1997, 2, 9));

        return new Oppgave(oppgaveBuilder);
    }
}
