package no.nav.okosynk.domain.ur;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRespons;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.domain.Oppgave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    private UrMapper urMapper;
    private UrMelding urMeldingSomSkalBliTilOppgave;
    private UrMelding annenUrMeldingSomSkalBliTilOppgave;
    private UrMelding urMeldingUtenMappingRegel;
    private UrMelding urMeldingEFOG;
    private AktoerRestClient aktoerRestClient = mock(AktoerRestClient.class);

    @BeforeEach
    void setUp() {
        urMapper = new UrMapper(aktoerRestClient);
        urMeldingSomSkalBliTilOppgave = new UrMelding(UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL);
        annenUrMeldingSomSkalBliTilOppgave = new UrMelding(ANNEN_UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL);
        urMeldingUtenMappingRegel = new UrMelding(UR_MELDING_UTEN_MAPPING_REGEL);
        urMeldingEFOG = new UrMelding(UR_MELDING_EFOG);
    }

    @Test
    @DisplayName("lagOppgaver returnerer én oppgave hvis den får inn én melding med oppdragsKode \"EFOG\" som skal bli til oppgave.")
    void lagUrOppgaveMedOppdragsKodeEFOG() {
        Mockito.reset(aktoerRestClient);
        enteringTestHeaderLogger.debug(null);

        when(aktoerRestClient.hentGjeldendeAktoerId("02029512345")).thenReturn(AktoerRespons.ok("123"));
        final List<Oppgave> oppgaver =
            urMapper
                .lagOppgaver(lagMeldinglisteMedEttElement(urMeldingEFOG));

        assertNotNull(oppgaver);
        assertEquals(1, oppgaver.size());
        assertEquals("ab0272", oppgaver.get(0).behandlingstema);
        assertNull(oppgaver.get(0).behandlingstype);
        assertEquals("4151", oppgaver.get(0).ansvarligEnhetId);
        assertEquals(oppgaver.get(0).aktoerId, "123");
    }

    @Test
    @DisplayName("lagOppgaver returnerer to oppgaver hvis den får inn to meldinger som skal bli til oppgaver og som ikke er like")
    void lagUrOppgaverFraUrMeldingListeReturnererToOppgaver() {
        Mockito.reset(aktoerRestClient);
        enteringTestHeaderLogger.debug(null);

        when(aktoerRestClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        when(aktoerRestClient.hentGjeldendeAktoerId("05073512345")).thenReturn(AktoerRespons.ok("1234"));
        List<Oppgave> oppgaver = urMapper
                .lagOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, annenUrMeldingSomSkalBliTilOppgave));

        assertNotNull(oppgaver);
        assertEquals(2, oppgaver.size());
        assertEquals(oppgaver.get(0).aktoerId, "1234");
        assertEquals(oppgaver.get(1).aktoerId, "123");
    }

    @Test
    @DisplayName("lagOppgaver returnerer en oppgave hvis den får inn to meldinger som er like")
    void lagUrOppgaverFraUrMeldingListeReturnererEnOppgave() {
        Mockito.reset(aktoerRestClient);
        enteringTestHeaderLogger.debug(null);

        when(aktoerRestClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));

        List<Oppgave> oppgaver = urMapper
                .lagOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, urMeldingSomSkalBliTilOppgave));

        assertNotNull(oppgaver);
        assertEquals(1, oppgaver.size());
        assertEquals(oppgaver.get(0).aktoerId, "123");
    }


    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med to UR-meldinger hvis den får inn to meldinger som skal bli til oppgaver og som ikke er like")
    void hentMeldingerSomSkalBliUrOppgaverReturnererToMeldinger() {

        enteringTestHeaderLogger.debug(null);

        Collection<List<UrMelding>> filtrerteMeldinger = urMapper
                .hentMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, annenUrMeldingSomSkalBliTilOppgave));

        assertNotNull(filtrerteMeldinger);
        assertEquals(2, filtrerteMeldinger.size());
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med en UR-melding hvis den får inn to meldinger der kun en skal bli til oppgaver")
    void hentMeldingerSomSkalBliUrOppgaverReturnererEnMelding() {

        enteringTestHeaderLogger.debug(null);

        Collection<List<UrMelding>> filtrerteMeldinger = urMapper
                .hentMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, urMeldingUtenMappingRegel));

        assertNotNull(filtrerteMeldinger);
        assertEquals(1, filtrerteMeldinger.size());
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med en UR-melding hvis den får inn to meldinger som er like")
    void hentMeldingerSomSkalBliUrOppgaverReturnererEnMeldingHvisInputErLike() {

        enteringTestHeaderLogger.debug(null);

        Collection<List<UrMelding>> filtrerteMeldinger = urMapper
                .hentMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, urMeldingSomSkalBliTilOppgave));

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
        List<UrMelding> urmeldinger = new ArrayList<>();
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
