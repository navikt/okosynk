package no.nav.okosynk.domain.ur;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRespons;
import no.nav.okosynk.consumer.aktoer.IAktoerClient;
import no.nav.okosynk.domain.Oppgave;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class UrMapperTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String UR_MELDING_SOM_GJELDER_TSS = "89095112345PERSON      2011-02-01T06:11:4625          00000000033390æ8020UTPOST UR2302011-01-31343285958Kredit kontonummer ugyldig                        00963702833";
    private static final String UR_MELDING_UTEN_MAPPING_REGEL = "00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8019ANDRUTBUR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282";
    private static final String UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL = "10108000398PERSON      2011-02-01T06:11:4625          00000000033390æ8020UTPOST UR2302011-01-31343285958Kredit kontonummer ugyldig                        00963702833";
    private static final String ANNEN_UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL = "05073512345PERSON      2011-02-01T06:11:4625          00000000033390æ8020UTPOST UR2302011-01-31343285958Kredit kontonummer ugyldig                        00963702833";
    private static final String MELDING_ORGANISASJON_MED_MAPPING_REGEL = "00800000000ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8020UTPOST UR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282";
    private static final String UR_MELDING_EFOG =
        "02029512345PERSON      2019-02-14T21:13:5525          00000000080610æ8020EFOG   UR2302019-02-12600767010Stoppet utbetaling                                02029530095";

    private final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
    private UrMapper urMapper;
    private UrMelding urMeldingSomSkalBliTilOppgave;
    private UrMelding annenUrMeldingSomSkalBliTilOppgave;
    private UrMelding urMeldingUtenMappingRegel;
    private UrMelding urMeldingEFOG;
    private IAktoerClient aktoerClient = mock(IAktoerClient.class);
    private boolean shouldConvertFolkeregisterIdentToAktoerId_saved = true;

    @BeforeEach
    void setUp() {
        urMapper = new UrMapper(this.aktoerClient, this.okosynkConfiguration);
        urMeldingSomSkalBliTilOppgave = new UrMelding(UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL);
        annenUrMeldingSomSkalBliTilOppgave = new UrMelding(ANNEN_UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL);
        urMeldingUtenMappingRegel = new UrMelding(UR_MELDING_UTEN_MAPPING_REGEL);
        urMeldingEFOG = new UrMelding(UR_MELDING_EFOG);
    }

    @BeforeEach
    void beforeEach() {
        this.shouldConvertFolkeregisterIdentToAktoerId_saved = this.okosynkConfiguration.shouldConvertFolkeregisterIdentToAktoerId();
        setShouldConvertFolkeregisterIdentToAktoerId(true);
    }

    @AfterEach
    void afterEach() {
        setShouldConvertFolkeregisterIdentToAktoerId(this.shouldConvertFolkeregisterIdentToAktoerId_saved);
    }

    private void setShouldConvertFolkeregisterIdentToAktoerId(final boolean shouldConvertFolkeregisterIdentToAktoerId) {
        System.setProperty(
                Constants.SHOULD_CONVERT_FOLKEREGISTER_IDENT_TO_AKTOERID_KEY,
                Boolean.valueOf(shouldConvertFolkeregisterIdentToAktoerId).toString());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("lagOppgaver returnerer én oppgave hvis den får inn én melding med oppdragsKode \"EFOG\" som skal bli til oppgave.")
    void lagUrOppgaveMedOppdragsKodeEFOG(final boolean shouldConvertFolkeregisterIdentToAktoerId) {

        enteringTestHeaderLogger.debug(null);

        setShouldConvertFolkeregisterIdentToAktoerId(shouldConvertFolkeregisterIdentToAktoerId);

        Mockito.reset(aktoerClient);

        final String expectedFolkeregisterIdent = "02029512345";
        final String expectedAktoerId = "123";
        when(aktoerClient.hentGjeldendeAktoerId(expectedFolkeregisterIdent)).thenReturn(AktoerRespons.ok(expectedAktoerId));
        final List<Oppgave> oppgaver =
            urMapper.lagOppgaver(lagMeldinglisteMedEttElement(urMeldingEFOG));

        assertNotNull(oppgaver);
        assertEquals(1, oppgaver.size());
        assertEquals("ab0272", oppgaver.get(0).behandlingstema);
        assertNull(oppgaver.get(0).behandlingstype);
        assertEquals("4151", oppgaver.get(0).ansvarligEnhetId);

        if (this.okosynkConfiguration.shouldConvertFolkeregisterIdentToAktoerId()) {
            assertEquals(expectedAktoerId, oppgaver.get(0).aktoerId);
            assertEquals(null, oppgaver.get(0).folkeregisterIdent);
        } else {
            assertEquals(null, oppgaver.get(0).aktoerId);
            assertEquals(expectedFolkeregisterIdent, oppgaver.get(0).folkeregisterIdent);
        }
    }

    @Test
    @DisplayName("lagOppgaver returnerer to oppgaver hvis den får inn to meldinger som skal bli til oppgaver og som ikke er like")
    void lagUrOppgaverFraUrMeldingListeReturnererToOppgaver() {

        enteringTestHeaderLogger.debug(null);

        Mockito.reset(aktoerClient);

        when(aktoerClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        when(aktoerClient.hentGjeldendeAktoerId("05073512345")).thenReturn(AktoerRespons.ok("1234"));
        List<Oppgave> oppgaver = urMapper
                .lagOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, annenUrMeldingSomSkalBliTilOppgave));

        assertNotNull(oppgaver);
        assertEquals(2, oppgaver.size());
        assertEquals("1234", oppgaver.get(0).aktoerId);
        assertEquals("123", oppgaver.get(1).aktoerId);
    }

    @Test
    @DisplayName("lagOppgaver returnerer en oppgave hvis den får inn to meldinger som er like")
    void lagUrOppgaverFraUrMeldingListeReturnererEnOppgave() {

        enteringTestHeaderLogger.debug(null);

        Mockito.reset(aktoerClient);

        when(aktoerClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));

        List<Oppgave> oppgaver =
                urMapper.lagOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, urMeldingSomSkalBliTilOppgave));

        assertNotNull(oppgaver);
        assertEquals(1, oppgaver.size());
        assertEquals("123", oppgaver.get(0).aktoerId);
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med to UR-meldinger hvis den får inn to meldinger som skal bli til oppgaver og som ikke er like")
    void hentMeldingerSomSkalBliUrOppgaverReturnererToMeldinger() {

        enteringTestHeaderLogger.debug(null);

        Collection<List<UrMelding>> filtrerteMeldinger = urMapper
                .groupMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, annenUrMeldingSomSkalBliTilOppgave));

        assertNotNull(filtrerteMeldinger);
        assertEquals(2, filtrerteMeldinger.size());
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med en UR-melding hvis den får inn to meldinger der kun en skal bli til oppgaver")
    void hentMeldingerSomSkalBliUrOppgaverReturnererEnMelding() {

        enteringTestHeaderLogger.debug(null);

        Collection<List<UrMelding>> filtrerteMeldinger = urMapper
                .groupMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, urMeldingUtenMappingRegel));

        assertNotNull(filtrerteMeldinger);
        assertEquals(1, filtrerteMeldinger.size());
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med en UR-melding hvis den får inn to meldinger som er like")
    void hentMeldingerSomSkalBliUrOppgaverReturnererEnMeldingHvisInputErLike() {

        enteringTestHeaderLogger.debug(null);

        Collection<List<UrMelding>> filtrerteMeldinger = urMapper
                .groupMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, urMeldingSomSkalBliTilOppgave));

        assertNotNull(filtrerteMeldinger);
        assertEquals(1, filtrerteMeldinger.size());
    }

    @Test
    @DisplayName("UR-melding som gjelder TSS skal bli til oppgave")
    void urMeldingGjelderTssOgDetSkalOpprettesOppgaveForSamhandlere() {

        enteringTestHeaderLogger.debug(null);

        UrMelding urMeldingSomGjelderTss = new UrMelding(UR_MELDING_SOM_GJELDER_TSS);

        assertTrue(urMapper.urMeldingSkalBliOppgave().test(urMeldingSomGjelderTss), "AbstractMelding som gjelder TSS blir ikke oppgave");
    }

    @Test
    @DisplayName("UR-melding som mangler mappingregel skal ikke bli til oppgave")
    void urMeldingUtenMapping() {

        enteringTestHeaderLogger.debug(null);

        assertFalse(urMapper.urMeldingSkalBliOppgave().test(urMeldingUtenMappingRegel), "AbstractMelding som mangler mappingregel blir oppgave");
    }

    @Test
    @DisplayName("UR-melding som ikke gjelder TSS og har en mappingregel skal bli til oppgave")
    void urMeldingSomSkalBliTilOppgave() {

        enteringTestHeaderLogger.debug(null);

        assertTrue(urMapper.urMeldingSkalBliOppgave().test(urMeldingSomSkalBliTilOppgave),
                "Det blir ikke oppgave for melding som ikke gjelder TSS og som har mappingregel");
    }

    @Test
    @DisplayName("Map UR-melding som er organisasjon og ikke gjelder TSS til Oppgave")
    void MapOsMeldingOrganisasjonTilOppgave() {

        enteringTestHeaderLogger.debug(null);

        UrMelding urMelding = new UrMelding(MELDING_ORGANISASJON_MED_MAPPING_REGEL);

        assertTrue(urMapper.urMeldingSkalBliOppgave().test(urMelding),
                "Det blir oppgave for melding som har organisasjon");
    }

    private List<UrMelding> lagMeldinglisteMedToElementer(UrMelding melding1, UrMelding melding2) {
        final List<UrMelding> urmeldinger = new ArrayList<>();
        urmeldinger.add(melding1);
        urmeldinger.add(melding2);

        return urmeldinger;
    }

    private List<UrMelding> lagMeldinglisteMedEttElement(final UrMelding urMelding) {

        final List<UrMelding> urMeldinger = new ArrayList<>();
        urMeldinger.add(urMelding);

        return urMeldinger;
    }
}
