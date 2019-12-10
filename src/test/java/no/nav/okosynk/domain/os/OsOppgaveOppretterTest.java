package no.nav.okosynk.domain.os;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import no.nav.okosynk.consumer.aktoer.AktoerRespons;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.domain.AbstractOppgaveOppretterTest;
import no.nav.okosynk.domain.Oppgave;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ConstantConditions")
class OsOppgaveOppretterTest extends AbstractOppgaveOppretterTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private AktoerRestClient aktoerRestClient = mock(AktoerRestClient.class);
    private final OsOppgaveOppretter osOppgaveOppretter = new OsOppgaveOppretter(new OsMappingRegelRepository(), aktoerRestClient);

    private static final OsMelding OS_MELDING_1 = new OsMelding("10108000398022828640 2009-07-042009-09-26RETUK231B3502009-05-012009-07-31000000012300æ 8020         INNT    10108000398            ");
    private static final OsMelding OS_MELDING_2 = new OsMelding("06025812345029568753 2009-11-062009-11-30AVVEX123456 2009-11-012009-11-30000000072770æ 8020         INNT    06025812345            ");
    private static final OsMelding OS_MELDING_UTEN_MAPPING_TIL_OPPGAVE = new OsMelding("06025812345029568753 2009-11-062009-11-30AVVEX123456 2009-11-012009-11-30000000072770æ 8019         HELSEREF06025812345            ");

    private static final String OS_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE = "RETU;   1230kr;   beregningsdato/id:04.07.09/022828640;   periode:01.05.09-31.07.09;   feilkonto: ;   statusdato:26.09.09;   ;   UtbTil:10108000398;   K231B350";
    private static final String OS_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE = "AVVE;   7277kr;   beregningsdato/id:06.11.09/029568753;   periode:01.11.09-30.11.09;   feilkonto: ;   statusdato:30.11.09;   ;   UtbTil:06025812345;   X123456";

    @Test
    void lagBeskrivelseLagerForventetBeskrivelse() {

        enteringTestHeaderLogger.debug(null);

        assertAll(
                () -> assertEquals(OS_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE, osOppgaveOppretter.lagBeskrivelse(OS_MELDING_1)),
                () -> assertEquals(OS_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE, osOppgaveOppretter.lagBeskrivelse(OS_MELDING_2))
        );
    }

    @Test
    @DisplayName("De to første tegnene i beskrivelsen skal være feltseparatorer for å unngå å skrive over beskrivelse")
    void toForsteTegnIBeskrivelseErFeltseparatorer() {

        enteringTestHeaderLogger.debug(null);

        List<OsMelding> meldingsliste = new ArrayList<>();
        meldingsliste.add(OS_MELDING_1);

        assertEquals(OS_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("RETU;", "RETU;;"),
                osOppgaveOppretter.lagSamletBeskrivelse(meldingsliste));
    }

    @Test
    void applyReturnererOppgaveMedRiktigeVerdier() {

        enteringTestHeaderLogger.debug(null);

        when(aktoerRestClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        Oppgave oppgave = osOppgaveOppretter.apply(Collections.singletonList(OS_MELDING_1)).get();
        assertAll(
                () -> assertEquals("123", oppgave.aktoerId),
                () -> assertEquals("OKO_OS", oppgave.oppgavetypeKode),
                () -> assertEquals("OKO", oppgave.fagomradeKode),
                () -> assertNull( oppgave.behandlingstema),
                () -> assertEquals("ae0218", oppgave.behandlingstype),
                () -> assertEquals("LAV", oppgave.prioritetKode),
                () -> assertEquals(OS_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("RETU;", "RETU;;"), oppgave.beskrivelse),
                () -> assertNotNull(oppgave.aktivFra),
                () -> assertNotNull(oppgave.aktivTil),
                () -> assertEquals("4151", oppgave.ansvarligEnhetId),
                () -> assertFalse(oppgave.lest)
        );
    }

    @Test
    void applyLagerRiktigSamletBeskrivelseGittFlereOsMeldinger() {

        enteringTestHeaderLogger.debug(null);

        String forventetSamletBeskrivelse = new StringBuffer()
                .append(OS_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("AVVE;", "AVVE;;"))
                .append(OsOppgaveOppretter.getRecordSeparator())
                .append(OS_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE)
                .toString();

        when(aktoerRestClient.hentGjeldendeAktoerId("06025812345")).thenReturn(AktoerRespons.ok("123"));
        assertEquals(forventetSamletBeskrivelse, osOppgaveOppretter.apply(Arrays.asList(OS_MELDING_1, OS_MELDING_2)).get().beskrivelse);
        assertEquals(forventetSamletBeskrivelse, osOppgaveOppretter.apply(Arrays.asList(OS_MELDING_2, OS_MELDING_1)).get().beskrivelse);
    }

    @Test
    void applySortererSamletBeskrivelseMedNyesteBeregningsDatoForstDersomNyesteDatoLeggesInnForst() {

        enteringTestHeaderLogger.debug(null);

        final String osMelding1Beregningsdato = OsOppgaveOppretter.formatAsNorwegianDate(OS_MELDING_1.beregningsDato);
        final String osMelding2Beregningsdato = OsOppgaveOppretter.formatAsNorwegianDate(OS_MELDING_2.beregningsDato);

        when(aktoerRestClient.hentGjeldendeAktoerId("06025812345")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = osOppgaveOppretter.apply(Arrays.asList(OS_MELDING_2, OS_MELDING_1)).get();

        assertAll(
                () -> assertTrue(oppgave.beskrivelse.contains(osMelding1Beregningsdato)),
                () -> assertTrue(oppgave.beskrivelse.contains(osMelding2Beregningsdato)),
                () -> assertTrue(oppgave.beskrivelse.indexOf(osMelding2Beregningsdato) < oppgave.beskrivelse.indexOf(osMelding1Beregningsdato))
        );
    }

    @Test
    void applySortererSamletBeskrivelseMedNyesteBeregningsDatoForstDersomNyesteDatoLeggesInnSist() {

        enteringTestHeaderLogger.debug(null);

        final String osMelding1Beregningsdato = OsOppgaveOppretter.formatAsNorwegianDate(OS_MELDING_1.beregningsDato);
        final String osMelding2Beregningsdato = OsOppgaveOppretter.formatAsNorwegianDate(OS_MELDING_2.beregningsDato);

        when(aktoerRestClient.hentGjeldendeAktoerId("06025812345")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = osOppgaveOppretter.apply(Arrays.asList(OS_MELDING_1, OS_MELDING_2)).get();

        assertAll(
                () -> assertTrue(oppgave.beskrivelse.contains(osMelding1Beregningsdato)),
                () -> assertTrue(oppgave.beskrivelse.contains(osMelding2Beregningsdato)),
                () -> assertTrue(oppgave.beskrivelse.indexOf(osMelding2Beregningsdato) < oppgave.beskrivelse.indexOf(osMelding1Beregningsdato))
        );
    }


    @Test
    void applyReturnererTomOptionalForTomListe() {

        enteringTestHeaderLogger.debug(null);

        assertFalse(osOppgaveOppretter.apply(Collections.emptyList()).isPresent());
    }

    @Test
    void applyReturnererTomOptionalForMeldingUtenMappingTilOppgave() {

        enteringTestHeaderLogger.debug(null);

        assertFalse(osOppgaveOppretter.apply(Collections.singletonList(OS_MELDING_UTEN_MAPPING_TIL_OPPGAVE)).isPresent());
    }

    @Test
    void brukBelopIHeleKronerIBeskrivelsesFeltet(){

        enteringTestHeaderLogger.debug(null);

        final OsMelding osMelding = new OsMelding("10108000398012345678 2015-07-212015-07-22AVVED133832 2015-07-012015-07-31" +
                "000000019400æ 8020         BA      10108000398            ");

        when(aktoerRestClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = osOppgaveOppretter.apply(Collections.singletonList(osMelding)).get();

        assertTrue(oppgave.beskrivelse.contains("1940kr"));
    }

    @Test
    void fjernDesimalerFraNettoBelopIBeskrivelsesFeltet() {

        enteringTestHeaderLogger.debug(null);

        final OsMelding osMelding = new OsMelding("10108000398012345678 2015-07-212015-07-22AVVED133832 2015-07-012015-07-31" +
                "000000019401æ 8020         BA      10108000398            ");

        when(aktoerRestClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = osOppgaveOppretter.apply(Collections.singletonList(osMelding)).get();

        assertTrue(oppgave.beskrivelse.contains("1940kr"));
    }
}
