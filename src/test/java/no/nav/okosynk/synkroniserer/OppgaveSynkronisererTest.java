package no.nav.okosynk.synkroniserer;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.OsMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerRespons;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.MeldingUnreadableException;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.IMeldingReader;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;
import no.nav.okosynk.model.Oppgave;
import no.nav.okosynk.model.OppgaveTest;
import no.nav.okosynk.synkroniserer.consumer.ConsumerStatistics;
import no.nav.okosynk.synkroniserer.consumer.oppgave.OppgaveRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

class OppgaveSynkronisererTest {

    private static final Logger logger = LoggerFactory.getLogger(OppgaveSynkronisererTest.class);
    private static final Logger enteringTestHeaderLogger = LoggerFactory.getLogger("EnteringTestHeader");

    private static final Random random = new Random(18766876876L);

    public static final String EKSTERN_OPPGAVETYPE_KODE = "OKO_UTB";
    private static final Constants.BATCH_TYPE BATCH_TYPE = Constants.BATCH_TYPE.OS;
    private static final String OPPGAVEID = "185587998";

    private static final OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);

    private OppgaveSynkroniserer oppgaveSynkronisererWithInjectedMocks;
    private OppgaveRestClient mockedOppgaveRestClient;

    private static Stream<Arguments> provideEqualsRelatedValuesForOppgave() {
        return OppgaveTest.provideEqualsRelatedValuesForOppgave();
    }

    @BeforeEach
    void setUp() throws IOException {

        logger.debug("About to create a new OppgaveSynkroniserer instance equipped with the mocked versions of OppgaveRestClient...");

        mockedOppgaveRestClient = mock(OppgaveRestClient.class);

        this.oppgaveSynkronisererWithInjectedMocks = new OppgaveSynkroniserer(OppgaveSynkronisererTest.okosynkConfiguration, mockedOppgaveRestClient);

        when(this.mockedOppgaveRestClient.finnOppgaver(anySet())).thenReturn(
                // Just a placeholder:
                ConsumerStatistics.zero(OppgaveSynkronisererTest.BATCH_TYPE.getConsumerStatisticsName()));

        when(this.mockedOppgaveRestClient.opprettOppgaver(anyCollection())).thenReturn(ConsumerStatistics.zero(OppgaveSynkronisererTest.BATCH_TYPE.getConsumerStatisticsName()));

        when(this.mockedOppgaveRestClient.patchOppgaver(anySet(), anyBoolean())).thenReturn(ConsumerStatistics.zero(OppgaveSynkronisererTest.BATCH_TYPE.getConsumerStatisticsName()));

        when(this.mockedOppgaveRestClient.getBatchType()).thenReturn(OppgaveSynkronisererTest.BATCH_TYPE);
    }

    @Test
    void when_simulating_OS_batch_file_line_to_oppgave_then_no_oppgave_should_be_found_for_opprett() throws MeldingUnreadableException {

        final String expectedAktoerId = "1933953921119";
        final String expectedFolkeregisterIdent = "25067823770";

        final IAktoerClient aktoerClient = mock(IAktoerClient.class);
        when(aktoerClient.hentGjeldendeAktoerId(any())).thenReturn(AktoerRespons.ok(expectedAktoerId));

        final String linjeMedUspesifikkMelding = expectedFolkeregisterIdent + "422070401 2021-05-102021-05-31RETUK231B3522021-05-012021-05-31000000015170æ 8020         REFARBG 00981002431            ";
        final IMeldingReader<OsMelding> meldingReader = new MeldingReader<>(OsMelding::new);
        final List<OsMelding> spesifikkMeldingFraLinjeMedUspesifikkMelding =
                meldingReader.opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(List.of(linjeMedUspesifikkMelding));
        final IMeldingMapper<OsMelding> spesifikkMapper = new OsMapper(aktoerClient);
        final Set<Oppgave> batchOppgaver = new HashSet<>(spesifikkMapper.lagOppgaver(spesifikkMeldingFraLinjeMedUspesifikkMelding));

        final Set<Oppgave> oppgaverLestFraDatabasen = new HashSet<>();
        oppgaverLestFraDatabasen.add(new Oppgave.OppgaveBuilder().withBehandlingstema(null).withBehandlingstype("ae0215").withAnsvarligEnhetId("4151").withAktoerId(expectedAktoerId).withFolkeregisterIdent(expectedFolkeregisterIdent).withBnr(null).withOrgnr(null).withSamhandlernr(null).build());
        assertTrue(batchOppgaver.containsAll(oppgaverLestFraDatabasen));
        assertTrue(oppgaverLestFraDatabasen.containsAll(batchOppgaver));

        final Collection<Oppgave> oppgaverSomSkalOpprettes = OppgaveSynkroniserer.finnOppgaverSomSkalOpprettes(batchOppgaver, oppgaverLestFraDatabasen);
        assertEquals(0, oppgaverSomSkalOpprettes.size());
    }

    @Test
    void when_the_batch_is_started_when_synchronize_is_called_then_service_calls_to_patchOppgave_or_opprettOppgaver_should_be_made() throws IOException {

        enteringTestHeaderLogger.debug(null);

        this.oppgaveSynkronisererWithInjectedMocks.synkroniser(lagOppgaveliste());

        final Set<Oppgave> funneOppgaver = new HashSet<>();
        verify(this.mockedOppgaveRestClient).finnOppgaver(funneOppgaver);
    }

    @Test
    void when_synkroniser_is_called_then_all_rest_client_methods_should_be_called_once() throws IOException {

        enteringTestHeaderLogger.debug(null);

        this.oppgaveSynkronisererWithInjectedMocks = Mockito.spy(new OppgaveSynkroniserer(OppgaveSynkronisererTest.okosynkConfiguration, mockedOppgaveRestClient));

        this.oppgaveSynkronisererWithInjectedMocks.synkroniser(lagOppgaveliste());

        verify(this.oppgaveSynkronisererWithInjectedMocks, times(1)).ferdigstillOppgaver(anySet());
        verify(this.oppgaveSynkronisererWithInjectedMocks, times(1)).oppdaterOppgaver(anySet());
        verify(this.oppgaveSynkronisererWithInjectedMocks, times(1)).opprettOppgaver(anySet());
    }

    @Test
    void when_beskrivelse_is_updated_then_it_should_be_reflected_in_the_resulting_oppgaver_som_skal_oppdateres() {

        enteringTestHeaderLogger.debug(null);

        final String nyBeskrivelse = "Beskrivelsen etter endring.";
        final Oppgave ikkeOppdatertOppgave = lagOppgaveMedBruker();
        final Oppgave oppdatertOppgave = new Oppgave.OppgaveBuilder().withSameValuesAs(ikkeOppdatertOppgave).withBeskrivelse(nyBeskrivelse).build();
        this.oppgaveSynkronisererWithInjectedMocks.oppdaterOppgaver(OppgaveSynkroniserer.finnOppgaverSomSkalOppdateres(Collections.singleton(oppdatertOppgave), Collections.singleton(ikkeOppdatertOppgave)));

        final ArgumentCaptor<Collection<Oppgave>> captor = forClass(Collection.class);
        verify(this.mockedOppgaveRestClient, atLeast(1)).patchOppgaver(captor.capture(), anyBoolean());
        assertEquals(nyBeskrivelse, captor.getValue().iterator().next().beskrivelse);
    }

    @Test
    void when_batch_beskrivelse_differs_from_database_and_db_has_no_code_then_the_batch_beskrivelse_should_override() {

        enteringTestHeaderLogger.debug(null);

        final String lokalOppgaveBeskrivelse = "STATUS;;Dette skal beholdes";
        final String oppgaveBeskrivelseLestFraDatabasen = "ANNEN KODE;; Noen har endret på dette, det blir forkastet!";

        final Oppgave lokalOppgave = lagOppgaveMedBeskrivelse(lokalOppgaveBeskrivelse);
        final Oppgave oppgaveLestFraDatabasen = new Oppgave.OppgaveBuilder().withSameValuesAs(lokalOppgave).withBeskrivelse(oppgaveBeskrivelseLestFraDatabasen).build();
        final Set<OppgaveSynkroniserer.OppgaveOppdatering> oppgaver = OppgaveSynkroniserer.finnOppgaverSomSkalOppdateres(Collections.singleton(lokalOppgave), Collections.singleton(oppgaveLestFraDatabasen));

        this.oppgaveSynkronisererWithInjectedMocks.oppdaterOppgaver(oppgaver);

        final ArgumentCaptor<Set<Oppgave>> captor = forClass(Set.class);

        verify(this.mockedOppgaveRestClient, atLeast(1)).patchOppgaver(captor.capture(), anyBoolean());

        final String actualBeskrivelse = captor.getValue().iterator().next().beskrivelse;
        assertEquals(lokalOppgaveBeskrivelse, actualBeskrivelse);
    }

    @Test
    void when_batch_beskrivelse_differs_from_database_and_db_has_code_then_the_batch_beskrivelse_should_override_with_code_from_the_db_inserted() {

        enteringTestHeaderLogger.debug(null);

        final String lokalOppgaveBeskrivelse = "Oppgavestatus;;Oppsummering av meldinger som er slått sammen på oppgaven";
        final String oppgaveBeskrivelseLestFraDatabasen = "ikke viktig;PESYS KODE IKKEMED;Noen har endret på dette, det blir forkastet!";
        final String expectedtBeskrivelse = "Oppgavestatus;PESYS KODE;Oppsummering av meldinger som er slått sammen på oppgaven";
        final Oppgave lokalOppgave = lagOppgaveMedBeskrivelse(lokalOppgaveBeskrivelse);
        final Oppgave oppgaveLestFraDatabasen = new Oppgave.OppgaveBuilder().withSameValuesAs(lokalOppgave).withBeskrivelse(oppgaveBeskrivelseLestFraDatabasen).build();
        final Set<OppgaveSynkroniserer.OppgaveOppdatering> oppgaver = OppgaveSynkroniserer.finnOppgaverSomSkalOppdateres(Collections.singleton(lokalOppgave), Collections.singleton(oppgaveLestFraDatabasen));

        this.oppgaveSynkronisererWithInjectedMocks.oppdaterOppgaver(oppgaver);

        final ArgumentCaptor<Set<Oppgave>> captor = forClass(Set.class);

        verify(this.mockedOppgaveRestClient, atLeast(1)).patchOppgaver(captor.capture(), anyBoolean());

        final String actualBeskrivelse = captor.getValue().iterator().next().beskrivelse;
        assertEquals(expectedtBeskrivelse, actualBeskrivelse);
    }

    @Test
    void when_synkronisators_oppdater_is_called_then_the_rest_client_should_be_called_with_oppgaver_that_should_be_pathed() {

        enteringTestHeaderLogger.debug(null);

        final String nyBeskrivelse = "Beskrivelsen etter endring.";
        final Oppgave ikkeOppdatertOppgave = lagOppgaveMedBruker();
        final Oppgave oppdatertOppgave = new Oppgave.OppgaveBuilder().withSameValuesAs(ikkeOppdatertOppgave).withBeskrivelse(nyBeskrivelse).build();

        final OppgaveSynkroniserer.OppgaveOppdatering oppgaveOppdatering = new OppgaveSynkroniserer.OppgaveOppdatering(ikkeOppdatertOppgave, oppdatertOppgave);

        this.oppgaveSynkronisererWithInjectedMocks.oppdaterOppgaver(Collections.singleton(oppgaveOppdatering));

        verify(this.mockedOppgaveRestClient).patchOppgaver(anySet(), anyBoolean());
    }

    @Test
    void when_synkroniserers_opprett_is_called_then_the_rest_clients_opprett_should_also_be_called() throws IOException {

        enteringTestHeaderLogger.debug(null);

        this.oppgaveSynkronisererWithInjectedMocks.opprettOppgaver(lagOppgaveliste());

        verify(this.mockedOppgaveRestClient).opprettOppgaver(anyCollection());
    }

    @Test
    void when_synkroniserer_is_called_with_no_oppgaver_then_rest_client_should_not_be_called() throws IOException {
        this.oppgaveSynkronisererWithInjectedMocks.opprettOppgaver(new HashSet<>());

        verify(this.mockedOppgaveRestClient, times(0)).opprettOppgaver(anySet());
    }

    @Test
    void when_batch_indicates_changed_oppgavetype_then_the_oppgave_should_not_be_ferdigstilt() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave = lagOppgaveMedBruker();
        final Oppgave oppgaveMedEndretOppgaveType = new Oppgave.OppgaveBuilder().withSameValuesAs(oppgave).withOppgavetypeKode(EKSTERN_OPPGAVETYPE_KODE).build();
        final Set<Oppgave> oppgaverSomSkalFerdigstilles = OppgaveSynkroniserer.finnOppgaverSomSkalFerdigstilles(new HashSet<>(), Collections.singleton(oppgaveMedEndretOppgaveType));

        this.oppgaveSynkronisererWithInjectedMocks.ferdigstillOppgaver(oppgaverSomSkalFerdigstilles);

        verify(this.mockedOppgaveRestClient, times(0)).patchOppgaver(anySet(), anyBoolean());
    }

    @Test
    void when_batch_indicates_not_changed_oppgavetype_then_the_oppgave_should_be_ferdigstilt() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave = lagOppgaveMedBruker();

        final Set<Oppgave> oppgaverSomSkalFerdigstilles = OppgaveSynkroniserer.finnOppgaverSomSkalFerdigstilles(new HashSet<>(), Collections.singleton(oppgave));

        this.oppgaveSynkronisererWithInjectedMocks.ferdigstillOppgaver(oppgaverSomSkalFerdigstilles);

        verify(this.mockedOppgaveRestClient, times(1)).patchOppgaver(anySet(), anyBoolean());
    }

    @ParameterizedTest
    @MethodSource("provideEqualsRelatedValuesForOppgave")
    void when_finnOppgaverSomSkalFerdigstilles_then_only_db_oppgaver_not_matching_batch_oppgaver_should_be_selected(final String behandlingstema_batch, final String behandlingstype_batch, final String ansvarligEnhetId_batch, final String aktoerId_batch, final String folkeregisterIdent_batch, final String bnr_batch, final String orgnr_batch, final String samhandlernr_batch,

                                                                                                                    final String behandlingstema_db, final String behandlingstype_db, final String ansvarligEnhetId_db, final String aktoerId_db, final String folkeregisterIdent_db, final String bnr_db, final String orgnr_db, final String samhandlernr_db,

                                                                                                                    final boolean shouldEqual) {
        final int expectedNumberOfOppgaverSomSkalFerdigStilles = (shouldEqual ? 0 : 1);

        final Oppgave oppgaveLestFraBatchen = OppgaveTest.generateRandomCompleteOppgaveBuilderInstance(OppgaveSynkronisererTest.random).withBehandlingstema(behandlingstema_batch).withBehandlingstype(behandlingstype_batch).withAnsvarligEnhetId(ansvarligEnhetId_batch).withAktoerId(aktoerId_batch).withFolkeregisterIdent(folkeregisterIdent_batch).withBnr(bnr_batch).withOrgnr(orgnr_batch).withSamhandlernr(samhandlernr_batch).build();
        final Set<Oppgave> alleOppgaverLestFraBatchen = new HashSet<>();
        alleOppgaverLestFraBatchen.add(oppgaveLestFraBatchen);

        final Oppgave oppgaveLestFraDatabasen = OppgaveTest.generateRandomCompleteOppgaveBuilderInstance(OppgaveSynkronisererTest.random).withBehandlingstema(behandlingstema_db).withBehandlingstype(behandlingstype_db).withAnsvarligEnhetId(ansvarligEnhetId_db).withAktoerId(aktoerId_db).withFolkeregisterIdent(folkeregisterIdent_db).withBnr(bnr_db).withOrgnr(orgnr_db).withSamhandlernr(samhandlernr_db).build();
        final Set<Oppgave> oppgaverLestFraDatabasen = new HashSet<>();
        oppgaverLestFraDatabasen.add(oppgaveLestFraDatabasen);

        final Set<Oppgave> oppgaverSomSkalFerdigstilles = OppgaveSynkroniserer.finnOppgaverSomSkalFerdigstilles(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);

        assertEquals(expectedNumberOfOppgaverSomSkalFerdigStilles, oppgaverSomSkalFerdigstilles.size());
    }

    @ParameterizedTest
    @MethodSource("provideEqualsRelatedValuesForOppgave")
    void when_finnOppgaverSomSkalOppdateres_then_only_db_oppgaver_not_matching_batch_oppgaver_should_be_selected(final String behandlingstema_batch, final String behandlingstype_batch, final String ansvarligEnhetId_batch, final String aktoerId_batch, final String folkeregisterIdent_batch, final String bnr_batch, final String orgnr_batch, final String samhandlernr_batch,

                                                                                                                 final String behandlingstema_db, final String behandlingstype_db, final String ansvarligEnhetId_db, final String aktoerId_db, final String folkeregisterIdent_db, final String bnr_db, final String orgnr_db, final String samhandlernr_db,

                                                                                                                 final boolean shouldEqual) {
        final int expectedNumberOfOppgaverSomSkalOppdateres = (shouldEqual ? 1 : 0);

        final Oppgave oppgaveLestFraBatchen = OppgaveTest.generateRandomCompleteOppgaveBuilderInstance(OppgaveSynkronisererTest.random).withBehandlingstema(behandlingstema_batch).withBehandlingstype(behandlingstype_batch).withAnsvarligEnhetId(ansvarligEnhetId_batch).withAktoerId(aktoerId_batch).withFolkeregisterIdent(folkeregisterIdent_batch).withBnr(bnr_batch).withOrgnr(orgnr_batch).withSamhandlernr(samhandlernr_batch).build();
        final Set<Oppgave> alleOppgaverLestFraBatchen = new HashSet<>();
        alleOppgaverLestFraBatchen.add(oppgaveLestFraBatchen);

        final Oppgave oppgaveLestFraDatabasen = OppgaveTest.generateRandomCompleteOppgaveBuilderInstance(OppgaveSynkronisererTest.random).withBehandlingstema(behandlingstema_db).withBehandlingstype(behandlingstype_db).withAnsvarligEnhetId(ansvarligEnhetId_db).withAktoerId(aktoerId_db).withFolkeregisterIdent(folkeregisterIdent_db).withBnr(bnr_db).withOrgnr(orgnr_db).withSamhandlernr(samhandlernr_db).build();
        final Set<Oppgave> oppgaverLestFraDatabasen = new HashSet<>();
        oppgaverLestFraDatabasen.add(oppgaveLestFraDatabasen);

        final Set<OppgaveSynkroniserer.OppgaveOppdatering> oppgaverSomSkalOppdateres = OppgaveSynkroniserer.finnOppgaverSomSkalOppdateres(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);

        assertEquals(expectedNumberOfOppgaverSomSkalOppdateres, oppgaverSomSkalOppdateres.size());
    }

    @ParameterizedTest
    @MethodSource("provideEqualsRelatedValuesForOppgave")
    void when_finnOppgaverSomSkalOpprettes_then_only_db_oppgaver_not_matching_batch_oppgaver_should_be_selected(final String behandlingstema_batch, final String behandlingstype_batch, final String ansvarligEnhetId_batch, final String aktoerId_batch, final String folkeregisterIdent_batch, final String bnr_batch, final String orgnr_batch, final String samhandlernr_batch,

                                                                                                                final String behandlingstema_db, final String behandlingstype_db, final String ansvarligEnhetId_db, final String aktoerId_db, final String folkeregisterIdent_db, final String bnr_db, final String orgnr_db, final String samhandlernr_db,

                                                                                                                final boolean shouldEqual) {
        final int expectedNumberOfOppgaverSomSkalOppdateres = (shouldEqual ? 0 : 1);

        final Oppgave oppgaveLestFraBatchen = OppgaveTest.generateRandomCompleteOppgaveBuilderInstance(OppgaveSynkronisererTest.random).withBehandlingstema(behandlingstema_batch).withBehandlingstype(behandlingstype_batch).withAnsvarligEnhetId(ansvarligEnhetId_batch).withAktoerId(aktoerId_batch).withFolkeregisterIdent(folkeregisterIdent_batch).withBnr(bnr_batch).withOrgnr(orgnr_batch).withSamhandlernr(samhandlernr_batch).build();
        final Set<Oppgave> alleOppgaverLestFraBatchen = new HashSet<>();
        alleOppgaverLestFraBatchen.add(oppgaveLestFraBatchen);

        final Oppgave oppgaveLestFraDatabasen = OppgaveTest.generateRandomCompleteOppgaveBuilderInstance(OppgaveSynkronisererTest.random).withBehandlingstema(behandlingstema_db).withBehandlingstype(behandlingstype_db).withAnsvarligEnhetId(ansvarligEnhetId_db).withAktoerId(aktoerId_db).withFolkeregisterIdent(folkeregisterIdent_db).withBnr(bnr_db).withOrgnr(orgnr_db).withSamhandlernr(samhandlernr_db).build();
        final Set<Oppgave> oppgaverLestFraDatabasen = new HashSet<>();
        oppgaverLestFraDatabasen.add(oppgaveLestFraDatabasen);

        final Set<Oppgave> oppgaverSomSkalOpprettes = OppgaveSynkroniserer.finnOppgaverSomSkalOpprettes(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);

        assertEquals(expectedNumberOfOppgaverSomSkalOppdateres, oppgaverSomSkalOpprettes.size());
    }

    private Set<Oppgave> lagOppgaveliste() {
        Set<Oppgave> oppgaveliste = new HashSet<>();
        oppgaveliste.add(lagOppgaveMedBruker());

        return oppgaveliste;
    }

    private Oppgave lagOppgaveMedBruker() {
        return lagOppgaveMedBeskrivelse("STATUS;;oppsummer meldinger slått sammen til en oppgave");
    }

    private Oppgave lagOppgaveMedBeskrivelse(final String beskrivelse) {

        return new Oppgave.OppgaveBuilder().withOppgaveId(OppgaveSynkronisererTest.OPPGAVEID).withOppgavetypeKode("OKO_OS").withFagomradeKode("BA").withPrioritetKode("LAV_OKO").withBeskrivelse(beskrivelse).withAnsvarligEnhetId("4151").withLest(false).withVersjon(1).withSistEndret(LocalDateTime.of(1997, 2, 4, 7, 8, 36)).withAktivFra(LocalDate.of(1997, 2, 2)).withAktivTil(LocalDate.of(1997, 2, 9)).build();
    }
}
