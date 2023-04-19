package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerRespons;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.model.Oppgave;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OsMapperTest {

    private static final Logger enteringTestHeaderLogger = LoggerFactory.getLogger("EnteringTestHeader");

    private static final String OS_MELDING_SOM_GJELDER_TSS = "80000437552017087784 2009-04-012009-06-24RETUK231B3502009-03-012009-03-31000000005000æ 4819         PEN     80000437552            ";
    private static final String OS_MELDING_SOM_IKKE_HAR_MAPPING = "10108000398029568753 2009-11-062009-11-30AVVEX123456 2009-11-012009-11-30000000072770æ 8019         HELSEREF10108000398            ";
    private static final String OS_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING = "07063012345009543471 2008-10-102008-10-10NEG K231B2962008-11-012008-11-30000000008820å 4151         GS      07063012345            ";
    private static final String ANNEN_OS_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING = "06128012345009543471 2008-10-102008-10-10NEG K231B2962008-11-012008-11-30000000008820å 4151         GS      06128012345            ";
    private static final String OS_MELDING_ORGANISASJON = "00990000000251889728 2017-01-232017-01-24AVVEK231B2622016-09-012016-09-30000000000003E 8020         KTPOST  00994932691            ";
    private static final String OS_MELDING_EFOG =
            "01017812345333374207 2019-02-132019-02-14AVVMK231B26E2018-10-012019-02-28000000090040æ 8020         EFOG    01017812345            ";

    private final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

    private OsMapper osMapper;
    private OsMelding osMeldingSomSkalBliTilOppgave;
    private OsMelding annenOsMeldingSomSkalBliTilOppgave;
    private OsMelding osMeldingSomIkkeHarMapping;
    private OsMelding osMeldingEFOG;
    private IAktoerClient aktoerRestClient = mock(IAktoerClient.class);
    private boolean shouldConvertFolkeregisterIdentToAktoerId_saved = true;

    @BeforeEach
    void setUp() {
        osMapper = new OsMapper(this.aktoerRestClient);
        osMeldingSomSkalBliTilOppgave = new OsMelding(OS_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING);
        annenOsMeldingSomSkalBliTilOppgave = new OsMelding(ANNEN_OS_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING);
        osMeldingSomIkkeHarMapping = new OsMelding(OS_MELDING_SOM_IKKE_HAR_MAPPING);
        osMeldingEFOG = new OsMelding(OS_MELDING_EFOG);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("lagOppgaver returnerer én oppgave hvis den får inn én melding med faggruppe \"EFOG\" som skal bli til oppgave.")
    void lagUrOppgaveMedFaggruppeEFOG(final boolean shouldConvertFolkeregisterIdentToAktoerId) {

        enteringTestHeaderLogger.debug(null);

        Mockito.reset(aktoerRestClient);

        final String expectedfFlkeregisterIdent = "01017812345";
        final String expectedAktoerId = "123";
        when(aktoerRestClient.hentGjeldendeAktoerId(expectedfFlkeregisterIdent)).thenReturn(AktoerRespons.ok(expectedAktoerId));

        final List<Oppgave> oppgaver =
                osMapper
                        .lagOppgaver(lagMeldinglisteMedEttElement(osMeldingEFOG));

        assertNotNull(oppgaver);
        assertEquals(1, oppgaver.size());
        assertEquals("ab0272", oppgaver.get(0).behandlingstema);
        assertNull(oppgaver.get(0).behandlingstype);
        assertEquals("4151", oppgaver.get(0).ansvarligEnhetId);

        assertEquals(expectedAktoerId, oppgaver.get(0).aktoerId);
        assertEquals(null, oppgaver.get(0).folkeregisterIdent);
    }

    @Test
    @DisplayName("lagOppgaver returnerer to oppgaver hvis den får inn to meldinger som skal bli til oppgaver og som ikke er like")
    void lagOsOppgaverFraOsMeldingListeReturnererToOppgaver() {

        enteringTestHeaderLogger.debug(null);

        Mockito.reset(aktoerRestClient);

        when(aktoerRestClient.hentGjeldendeAktoerId("07063012345")).thenReturn(AktoerRespons.ok("123"));
        when(aktoerRestClient.hentGjeldendeAktoerId("06128012345")).thenReturn(AktoerRespons.ok("1234"));
        List<Oppgave> oppgaver = osMapper
                .lagOppgaver(lagMeldinglisteMedToElementer(osMeldingSomSkalBliTilOppgave, annenOsMeldingSomSkalBliTilOppgave));

        assertNotNull(oppgaver);
        assertEquals(2, oppgaver.size());
        assertEquals("1234", oppgaver.get(0).aktoerId);
        assertEquals("123", oppgaver.get(1).aktoerId);
    }

    @Test
    @DisplayName("lagOppgaver returnerer oppgave med ett innslag i beskrivelsen for hver statuskode")
    void lagFire() {
        final List<OsMelding> FIRE = Stream.of(""+
                        "05029745821495681278 2023-03-142023-03-14AVRKK231B2622022-12-012022-12-31000000059480æ 8020         ARBYT   05029745821            ",
                        "05029745821495681278 2023-03-142023-03-14AVAVK231B2622023-01-012023-01-31000000015040æ 8020         ARBYT   05029745821            ",
                        "05029745821495681278 2023-03-142023-03-14AVRKK231B2622023-01-012023-01-31000000153760æ 8020         ARBYT   05029745821            ",
                        "05029745821495681278 2023-03-142023-03-14AVAVK231B2622023-02-012023-03-31000000216490æ 8020         ARBYT   05029745821            ")
                .map(OsMelding::new)
                .collect(Collectors.toList());
        enteringTestHeaderLogger.debug(null);

        Mockito.reset(aktoerRestClient);

        when(aktoerRestClient.hentGjeldendeAktoerId("05029745821")).thenReturn(AktoerRespons.ok("123"));
        List<Oppgave> oppgaver = osMapper.lagOppgaver(FIRE);

        assertNotNull(oppgaver);

        assertEquals(1, oppgaver.size());
        Oppgave oppgave = oppgaver.get(0);
        assertEquals(1, oppgave.beskrivelse.split("AVRK").length-1);
        assertEquals(1, oppgave.beskrivelse.split("AVAV").length-1);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("lagOppgaver returnerer en oppgave hvis den får inn to meldinger som er like")
    void lagOsOppgaverFraOsMeldingListeReturnererEnOppgave(final boolean shouldConvertFolkeregisterIdentToAktoerId) {

        enteringTestHeaderLogger.debug(null);

        Mockito.reset(aktoerRestClient);

        final String expectedFolkeregisterIdent = "07063012345";
        final String expectedAktoerId = "123";

        when(aktoerRestClient.hentGjeldendeAktoerId(expectedFolkeregisterIdent)).thenReturn(AktoerRespons.ok(expectedAktoerId));
        final List<Oppgave> oppgaver = osMapper
                .lagOppgaver(lagMeldinglisteMedToElementer(osMeldingSomSkalBliTilOppgave, osMeldingSomSkalBliTilOppgave));

        assertNotNull(oppgaver);
        assertEquals(1, oppgaver.size());
        assertEquals(expectedAktoerId, oppgaver.get(0).aktoerId);
        assertEquals(null, oppgaver.get(0).folkeregisterIdent);
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med to OS-meldinger hvis den får inn to meldinger som skal bli til oppgaver og som ikke er like")
    void hentMeldingerSomSkalBliOsOppgaverReturnererToMeldinger() {

        enteringTestHeaderLogger.debug(null);

        Collection<List<OsMelding>> filtrerteMeldinger = osMapper
                .groupMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(osMeldingSomSkalBliTilOppgave, annenOsMeldingSomSkalBliTilOppgave));

        assertNotNull(filtrerteMeldinger);
        assertEquals(2, filtrerteMeldinger.size());
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med en OS-melding hvis den får inn to meldinger der kun en skal bli til oppgaver")
    void hentMeldingerSomSkalBliOsOppgaverReturnererEnMelding() {

        enteringTestHeaderLogger.debug(null);

        Collection<List<OsMelding>> filtrerteMeldinger = osMapper
                .groupMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(osMeldingSomSkalBliTilOppgave, osMeldingSomIkkeHarMapping));

        assertNotNull(filtrerteMeldinger);
        assertEquals(1, filtrerteMeldinger.size());
    }

    @Test
    @DisplayName("hentMeldingerSomSkalBliOppgaver returnerer en samling med en OS-melding hvis den får inn to meldinger som er like")
    void hentMeldingerSomSkalBliOsOppgaverReturnererEnMeldingHvisInputErLike() {

        enteringTestHeaderLogger.debug(null);

        Collection<List<OsMelding>> filtrerteMeldinger = osMapper
                .groupMeldingerSomSkalBliOppgaver(lagMeldinglisteMedToElementer(osMeldingSomSkalBliTilOppgave, osMeldingSomSkalBliTilOppgave));

        assertNotNull(filtrerteMeldinger);
        assertEquals(1, filtrerteMeldinger.size());
    }


    @Test
    @DisplayName("OS-melding som gjelder TSS skal bli til oppgave")
    void osMeldingGjelderTssOgDetSkalOpprettesOppgaveForSamhandlere() {

        enteringTestHeaderLogger.debug(null);

        OsMelding osMeldingSomGjelderTss = new OsMelding(OS_MELDING_SOM_GJELDER_TSS);

        assertTrue(osMapper.osMeldingSkalBliOppgave().test(osMeldingSomGjelderTss), "AbstractMelding som gjelder TSS blir ikke oppgave");
    }

    @Test
    @DisplayName("OS-melding som mangler mapping skal ikke bli til oppgave")
    void osMeldingUtenMapping() {

        enteringTestHeaderLogger.debug(null);

        assertFalse(osMapper.osMeldingSkalBliOppgave().test(osMeldingSomIkkeHarMapping), "AbstractMelding som mangler mapping blir oppgave");
    }

    @Test
    @DisplayName("OS-melding som ikke gjelder TSS og har mapping skal bli til oppgave")
    void osMeldingSomSkalBliTilOppgave() {

        enteringTestHeaderLogger.debug(null);
        assertTrue(osMapper.osMeldingSkalBliOppgave().test(osMeldingSomSkalBliTilOppgave),
                "Det blir ikke oppgave for melding som ikke gjelder TSS og som har mapping");
    }

    @Test
    @DisplayName("Map OS-melding som er organisasjon og ikke gjelder TSS til Oppgave")
    void MapOsMeldingOrganisasjonTilOppgave() {
        OsMelding osMelding = new OsMelding(OS_MELDING_ORGANISASJON);

        assertTrue(osMapper.osMeldingSkalBliOppgave().test(osMelding),
                "Det blir oppgave for melding som har organisasjon");
    }

    private List<OsMelding> lagMeldinglisteMedToElementer(OsMelding melding1, OsMelding melding2) {
        List<OsMelding> osmeldinger = new ArrayList<>();
        osmeldinger.add(melding1);
        osmeldinger.add(melding2);
        return osmeldinger;
    }

    private List<OsMelding> lagMeldinglisteMedEttElement(final OsMelding osMelding) {

        final List<OsMelding> osMeldinger = new ArrayList<>();
        osMeldinger.add(osMelding);

        return osMeldinger;
    }
}
