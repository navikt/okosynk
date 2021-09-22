package no.nav.okosynk.domain.os;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRespons;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private AktoerRestClient aktoerRestClient = mock(AktoerRestClient.class);
    private boolean shouldConvertNavPersonIdentToAktoerId_saved = true;

    @BeforeEach
    void setUp() {
        osMapper = new OsMapper(this.aktoerRestClient, this.okosynkConfiguration);
        osMeldingSomSkalBliTilOppgave = new OsMelding(OS_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING);
        annenOsMeldingSomSkalBliTilOppgave = new OsMelding(ANNEN_OS_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING);
        osMeldingSomIkkeHarMapping = new OsMelding(OS_MELDING_SOM_IKKE_HAR_MAPPING);
        osMeldingEFOG = new OsMelding(OS_MELDING_EFOG);
    }

    @BeforeEach
    void beforeEach() {
        this.shouldConvertNavPersonIdentToAktoerId_saved = this.okosynkConfiguration.shouldConvertNavPersonIdentToAktoerId();
        setShouldConvertNavPersonIdentToAktoerId(true);
    }

    @AfterEach
    void afterEach() {
        setShouldConvertNavPersonIdentToAktoerId(this.shouldConvertNavPersonIdentToAktoerId_saved);
    }

    private void setShouldConvertNavPersonIdentToAktoerId(final boolean shouldConvertNavPersonIdentToAktoerId) {
        System.setProperty(
                Constants.SHOULD_CONVERT_NAVPERSONIDENT_TO_AKTOERID_KEY,
                Boolean.valueOf(shouldConvertNavPersonIdentToAktoerId).toString());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("lagOppgaver returnerer én oppgave hvis den får inn én melding med faggruppe \"EFOG\" som skal bli til oppgave.")
    void lagUrOppgaveMedFaggruppeEFOG(final boolean shouldConvertNavPersonIdentToAktoerId) {

        enteringTestHeaderLogger.debug(null);

        setShouldConvertNavPersonIdentToAktoerId(shouldConvertNavPersonIdentToAktoerId);

        Mockito.reset(aktoerRestClient);

        final String expectedNavPersonIdent = "01017812345";
        final String expectedAktoerId = "123";
        when(aktoerRestClient.hentGjeldendeAktoerId(expectedNavPersonIdent)).thenReturn(AktoerRespons.ok(expectedAktoerId));

        final List<Oppgave> oppgaver =
                osMapper
                        .lagOppgaver(lagMeldinglisteMedEttElement(osMeldingEFOG));

        assertNotNull(oppgaver);
        assertEquals(1, oppgaver.size());
        assertEquals("ab0272", oppgaver.get(0).behandlingstema);
        assertNull(oppgaver.get(0).behandlingstype);
        assertEquals("4151", oppgaver.get(0).ansvarligEnhetId);

        if (this.okosynkConfiguration.shouldConvertNavPersonIdentToAktoerId()) {
            assertEquals(expectedAktoerId, oppgaver.get(0).aktoerId);
            assertEquals(null, oppgaver.get(0).navPersonIdent);
        } else {
            assertEquals(null, oppgaver.get(0).aktoerId);
            assertEquals(expectedNavPersonIdent, oppgaver.get(0).navPersonIdent);
        }
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

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("lagOppgaver returnerer en oppgave hvis den får inn to meldinger som er like")
    void lagOsOppgaverFraOsMeldingListeReturnererEnOppgave(final boolean shouldConvertNavPersonIdentToAktoerId) {

        enteringTestHeaderLogger.debug(null);

        setShouldConvertNavPersonIdentToAktoerId(shouldConvertNavPersonIdentToAktoerId);

        Mockito.reset(aktoerRestClient);

        final String expectedNavPersonIdent = "07063012345";
        final String expectedAktoerId = "123";

        when(aktoerRestClient.hentGjeldendeAktoerId(expectedNavPersonIdent)).thenReturn(AktoerRespons.ok(expectedAktoerId));
        final List<Oppgave> oppgaver = osMapper
                .lagOppgaver(lagMeldinglisteMedToElementer(osMeldingSomSkalBliTilOppgave, osMeldingSomSkalBliTilOppgave));

        assertNotNull(oppgaver);
        assertEquals(1, oppgaver.size());
        if (this.okosynkConfiguration.shouldConvertNavPersonIdentToAktoerId()) {
            assertEquals(expectedAktoerId, oppgaver.get(0).aktoerId);
            assertEquals(null, oppgaver.get(0).navPersonIdent);
        } else {
            assertEquals(null, oppgaver.get(0).aktoerId);
            assertEquals(expectedNavPersonIdent, oppgaver.get(0).navPersonIdent);
        }
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
