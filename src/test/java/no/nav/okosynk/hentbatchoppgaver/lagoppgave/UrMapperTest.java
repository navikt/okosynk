package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerRespons;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.Melding;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import no.nav.okosynk.model.Oppgave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrMapperTest {

    private static final String UR_MELDING_SOM_GJELDER_TSS = "89095112345PERSON      2011-02-01T06:11:4625          00000000033390æ8020UTPOST UR2302011-01-31343285958Kredit kontonummer ugyldig                        00963702833";
    private static final String UR_MELDING_SOM_GJELDER_TSS2 = "89095112345PERSON      2011-02-01T06:11:4625          00000000033490æ8020UTPOST UR2302011-02-31343285958Kredit kontonummer ugyldig                        00963702833";
    private static final String UR_MELDING_SOM_GJELDER_TSS3 = "89095112345PERSON      2011-02-01T06:11:4625          00000000033590æ8020UTPOST UR2302011-03-31343285958Kredit kontonummer ugyldig                        00963702833";
    private static final String UR_MELDING_SOM_GJELDER_TSS4 = "89095112345PERSON      2011-02-01T06:11:4625          00000000033590æ8020 ARBYT UR2302011-03-31343285958Kredit kontonummer ugyldig                        00963702833";
    private static final String UR_MELDING_UTEN_MAPPING_REGEL = "00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8019ANDRUTBUR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282";
    private static final String UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL = "10108000398PERSON      2011-02-01T06:11:4625          00000000033390æ8020UTPOST UR2302011-01-31343285958Kredit kontonummer ugyldig                        00963702833";
    private static final String ANNEN_UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL = "05073512345PERSON      2011-02-01T06:11:4625          00000000033390æ8020UTPOST UR2302011-01-31343285958Kredit kontonummer ugyldig                        00963702833";
    private static final String MELDING_ORGANISASJON_MED_MAPPING_REGEL = "00800000000ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8020UTPOST UR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282";
    private static final String UR_MELDING_EFOG = "02029512345PERSON      2019-02-14T21:13:5525          00000000080610æ8020EFOG   UR2302019-02-12600767010Stoppet utbetaling                                02029530095";

    private OppgaveOppretter urMapper;
    private UrMelding urMeldingSomSkalBliTilOppgave;
    private UrMelding annenUrMeldingSomSkalBliTilOppgave;
    private UrMelding urMeldingUtenMappingRegel;
    private UrMelding urMeldingEFOG;
    private final IAktoerClient aktoerClient = Mockito.mock(IAktoerClient.class);

    @BeforeEach
    void setUp() throws IOException {
        Mappingregelverk.init(Constants.BATCH_TYPE.UR.getMappingRulesPropertiesFileName());
        urMapper = new OppgaveOppretter(this.aktoerClient);
        urMeldingSomSkalBliTilOppgave = new UrMelding(UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL);
        annenUrMeldingSomSkalBliTilOppgave = new UrMelding(ANNEN_UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL);
        urMeldingUtenMappingRegel = new UrMelding(UR_MELDING_UTEN_MAPPING_REGEL);
        urMeldingEFOG = new UrMelding(UR_MELDING_EFOG);
    }

    @Test
    void lagUrOppgaveMedOppdragsKodeEFOG() {

        Mockito.reset(aktoerClient);

        final String expectedFolkeregisterIdent = "02029512345";
        final String expectedAktoerId = "123";
        Mockito.when(aktoerClient.hentGjeldendeAktoerId(expectedFolkeregisterIdent)).thenReturn(AktoerRespons.ok(expectedAktoerId));
        final List<Oppgave> oppgaver =
                urMapper.lagOppgaver(lagMeldinglisteMedEttElement(urMeldingEFOG));

        assertNotNull(oppgaver);
        assertEquals(1, oppgaver.size());
        assertEquals("ab0272", oppgaver.get(0).behandlingstema());
        assertNull(oppgaver.get(0).behandlingstype());
        assertEquals("4151", oppgaver.get(0).ansvarligEnhetId());

        assertEquals(expectedAktoerId, oppgaver.get(0).aktoerId());
        assertThat(oppgaver.get(0).folkeregisterIdent()).isNull();
    }

    @Test
    @DisplayName("lagOppgaver returnerer to oppgaver hvis den får inn to meldinger som skal bli til oppgaver og som ikke er like")
    void lagUrOppgaverFraUrMeldingListeReturnererToOppgaver() {

        Mockito.reset(aktoerClient);

        Mockito.when(aktoerClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        Mockito.when(aktoerClient.hentGjeldendeAktoerId("05073512345")).thenReturn(AktoerRespons.ok("1234"));
        List<Oppgave> oppgaver = urMapper
                .lagOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, annenUrMeldingSomSkalBliTilOppgave));

        assertThat(oppgaver)
                .isNotNull()
                .hasSize(2)
                .extracting(Oppgave::aktoerId).contains("123", "1234");
    }

    @Test
    @DisplayName("lagOppgaver returnerer en oppgave hvis den får inn to meldinger som er like")
    void lagUrOppgaverFraUrMeldingListeReturnererEnOppgave() {

        Mockito.reset(aktoerClient);

        Mockito.when(aktoerClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));

        List<Oppgave> oppgaver =
                urMapper.lagOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, urMeldingSomSkalBliTilOppgave));

        assertThat(oppgaver)
                .isNotNull()
                .hasSize(1)
                .extracting(Oppgave::aktoerId).contains("123");
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med to UR-meldinger hvis den får inn to meldinger som skal bli til oppgaver og som ikke er like")
    void hentMeldingerSomSkalBliUrOppgaverReturnererToMeldinger() {

        Collection<List<Melding>> filtrerteMeldinger = urMapper
                .groupMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, annenUrMeldingSomSkalBliTilOppgave));

        assertThat(filtrerteMeldinger)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med en UR-melding hvis den får inn to meldinger der kun en skal bli til oppgaver")
    void hentMeldingerSomSkalBliUrOppgaverReturnererEnMelding() {

        Collection<List<Melding>> filtrerteMeldinger = urMapper
                .groupMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, urMeldingUtenMappingRegel));

        assertThat(filtrerteMeldinger)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med en UR-melding hvis den får inn to meldinger som er like")
    void hentMeldingerSomSkalBliUrOppgaverReturnererEnMeldingHvisInputErLike() {

        Collection<List<Melding>> filtrerteMeldinger = urMapper
                .groupMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(urMeldingSomSkalBliTilOppgave, urMeldingSomSkalBliTilOppgave));

        assertThat(filtrerteMeldinger)
                .isNotNull()
                .hasSize(1)
                .flatExtracting(identity())
                .containsOnlyOnce(urMeldingSomSkalBliTilOppgave);
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med en UR-melding hvis den får inn to meldinger som er like")
    void treUlikeMeldingerSomHarSammeGjelderIdEnhetOgOppdragskodeBlirRegnetSomBareEn() {
        Collection<List<Melding>> filtrerteMeldinger = urMapper
                .groupMeldingerSomSkalBliOppgaver(Stream.of(UR_MELDING_SOM_GJELDER_TSS, UR_MELDING_SOM_GJELDER_TSS2, UR_MELDING_SOM_GJELDER_TSS3, UR_MELDING_SOM_GJELDER_TSS4).map(UrMelding::new).map(m -> (Melding) m).toList());

        assertThat(filtrerteMeldinger)
                .isNotNull()
                .as("Listen skal deles i to")
                .hasSize(2)
                .extracting(List::size)
                .as("Tre av meldingene skal slås sammen til en liste")
                .containsOnlyOnce(3)
                .as("den fjerde, som er en annen oppdragskode, skal komme i sin egen liste")
                .containsOnlyOnce(1);
        System.out.println("size = ");
    }

    @Test
    @DisplayName("UR-melding som gjelder TSS skal bli til oppgave")
    void urMeldingGjelderTssOgDetSkalOpprettesOppgaveForSamhandlere() {

        UrMelding urMeldingSomGjelderTss = new UrMelding(UR_MELDING_SOM_GJELDER_TSS);

        assertTrue(urMapper.meldingSkalBliOppgave().test(urMeldingSomGjelderTss), "AbstractMelding som gjelder TSS blir ikke oppgave");
    }

    @Test
    @DisplayName("UR-melding som mangler mappingregel skal ikke bli til oppgave")
    void urMeldingUtenMapping() {

        assertFalse(urMapper.meldingSkalBliOppgave().test(urMeldingUtenMappingRegel), "AbstractMelding som mangler mappingregel blir oppgave");
    }

    @Test
    @DisplayName("UR-melding som ikke gjelder TSS og har en mappingregel skal bli til oppgave")
    void urMeldingSomSkalBliTilOppgave() {

        assertTrue(urMapper.meldingSkalBliOppgave().test(urMeldingSomSkalBliTilOppgave),
                "Det blir ikke oppgave for melding som ikke gjelder TSS og som har mappingregel");
    }

    @Test
    @DisplayName("Map UR-melding som er organisasjon og ikke gjelder TSS til Oppgave")
    void MapOsMeldingOrganisasjonTilOppgave() {

        UrMelding urMelding = new UrMelding(MELDING_ORGANISASJON_MED_MAPPING_REGEL);

        assertTrue(urMapper.meldingSkalBliOppgave().test(urMelding),
                "Det blir oppgave for melding som har organisasjon");
    }

    private List<Melding> lagMeldinglisteMedToElementer(UrMelding melding1, UrMelding melding2) {
        final List<Melding> urmeldinger = new ArrayList<>();
        urmeldinger.add(melding1);
        urmeldinger.add(melding2);

        return urmeldinger;
    }

    private List<Melding> lagMeldinglisteMedEttElement(final UrMelding urMelding) {

        final List<Melding> urMeldinger = new ArrayList<>();
        urMeldinger.add(urMelding);

        return urMeldinger;
    }
}
