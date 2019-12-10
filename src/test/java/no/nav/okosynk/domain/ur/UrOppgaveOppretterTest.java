package no.nav.okosynk.domain.ur;

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
class UrOppgaveOppretterTest extends AbstractOppgaveOppretterTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private AktoerRestClient aktoerRestClient = mock(AktoerRestClient.class);
    private final UrOppgaveOppretter urOppgaveOppretter = new UrOppgaveOppretter(new UrMappingRegelRepository(), aktoerRestClient);

    private static final UrMelding UR_MELDING_1 = new UrMelding("10108000398PERSON      2011-01-28T18:25:5825          00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398");
    private static final UrMelding UR_MELDING_2 = new UrMelding("00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8020INNT   UR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282");
    private static final UrMelding UR_MELDING_UTEN_MAPPING_TIL_OPPGAVE = new UrMelding("00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8019ANDRUTBUR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282");

    private static final String UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE = "25;   Mottakers konto er oppgjort;   postert/bilagsnummer:21.01.11/342552558;   1940kr;   statusdato:28.01.11;   UtbTil:10108000398;";
    private static final String UR_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE = "25;   Feil bruk av KID/ugyldig KID;   postert/bilagsnummer:31.01.11/343296727;   30416kr;   statusdato:01.02.11;   UtbTil:00837873282;";

    @Test
    void lagBeskrivelseLagerForventetBeskrivelse() {

        enteringTestHeaderLogger.debug(null);

        assertAll(
                () -> assertEquals(UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE, urOppgaveOppretter.lagBeskrivelse(UR_MELDING_1)),
                () -> assertEquals(UR_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE, urOppgaveOppretter.lagBeskrivelse(UR_MELDING_2))
        );
    }

    @Test
    @DisplayName("De to første tegnene i beskrivelsen skal være feltseparatorer for å unngå å skrive over beskrivelse")
    void toForsteTegnIBeskrivelseErFeltseparatorer() {

        enteringTestHeaderLogger.debug(null);

        List<UrMelding> meldingsliste = new ArrayList<>();
        meldingsliste.add(UR_MELDING_1);

        assertEquals(UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("25;", "25;;"),
                urOppgaveOppretter.lagSamletBeskrivelse(meldingsliste));
    }

    @Test
    void applyReturnererOppgaveMedRiktigeVerdier() {

        enteringTestHeaderLogger.debug(null);

        when(aktoerRestClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        Oppgave oppgave = urOppgaveOppretter.apply(Collections.singletonList(UR_MELDING_1)).get();
        assertAll(
                () -> assertEquals("123", oppgave.aktoerId),
                () -> assertEquals("OKO_UR", oppgave.oppgavetypeKode),
                () -> assertEquals("OKO", oppgave.fagomradeKode),
                () -> assertNull(null, oppgave.behandlingstema),
                () -> assertEquals("ae0218", oppgave.behandlingstype),
                () -> assertEquals("LAV", oppgave.prioritetKode),
                () -> assertEquals(UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("25;", "25;;"), oppgave.beskrivelse),
                () -> assertNotNull(oppgave.aktivFra),
                () -> assertNotNull(oppgave.aktivTil),
                () -> assertEquals("4151", oppgave.ansvarligEnhetId),
                () -> assertFalse(oppgave.lest)
        );
    }

    @Test
    void applyLagerRiktigSamletBeskrivelseGittFlereUrMeldinger() {

        enteringTestHeaderLogger.debug(null);

        String forventetSamletBeskrivelse = new StringBuffer()
                .append(UR_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("25;", "25;;"))
                .append(UrOppgaveOppretter.getRecordSeparator())
                .append(UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE)
                .toString();

        assertEquals(forventetSamletBeskrivelse, urOppgaveOppretter.apply(Arrays.asList(UR_MELDING_1, UR_MELDING_2)).get().beskrivelse);
        assertEquals(forventetSamletBeskrivelse, urOppgaveOppretter.apply(Arrays.asList(UR_MELDING_2, UR_MELDING_1)).get().beskrivelse);
    }

    @Test
    void applySortererSamletBeskrivelseMedNyesteDatoPostertForstDersomNyesteDatoLeggesInnForst() {

        enteringTestHeaderLogger.debug(null);

        final String urMelding1DatoPostert = UrOppgaveOppretter.formatAsNorwegianDate(UR_MELDING_1.datoPostert);
        final String urMelding2DatoPostert = UrOppgaveOppretter.formatAsNorwegianDate(UR_MELDING_2.datoPostert);

        final Oppgave oppgave = urOppgaveOppretter.apply(Arrays.asList(UR_MELDING_2, UR_MELDING_1)).get();

        assertAll(
                () -> assertTrue(oppgave.beskrivelse.contains(urMelding1DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse.contains(urMelding2DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse.indexOf(urMelding2DatoPostert) < oppgave.beskrivelse.indexOf(urMelding1DatoPostert))
        );
    }

    @Test
    void applySortererSamletBeskrivelseMedNyesteDatoPostertForstDersomNyesteDatoLeggesInnSist() {

        enteringTestHeaderLogger.debug(null);

        final String urMelding1DatoPostert = UrOppgaveOppretter.formatAsNorwegianDate(UR_MELDING_1.datoPostert);
        final String urMelding2DatoPostert = UrOppgaveOppretter.formatAsNorwegianDate(UR_MELDING_2.datoPostert);

        final Oppgave oppgave = urOppgaveOppretter.apply(Arrays.asList(UR_MELDING_1, UR_MELDING_2)).get();

        assertAll(
                () -> assertTrue(oppgave.beskrivelse.contains(urMelding1DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse.contains(urMelding2DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse.indexOf(urMelding2DatoPostert) < oppgave.beskrivelse.indexOf(urMelding1DatoPostert))
        );
    }

    @Test
    void applyReturnererTomOptionalForTomListe() {

        enteringTestHeaderLogger.debug(null);

        assertFalse(urOppgaveOppretter.apply(Collections.emptyList()).isPresent());
    }

    @Test
    void applyReturnererTomOptionalForMeldingUtenMappingTilOppgave() {
        assertFalse(urOppgaveOppretter.apply(Collections.singletonList(UR_MELDING_UTEN_MAPPING_TIL_OPPGAVE)).isPresent());
    }

    @Test
    void brukBelopIHeleKronerIBeskrivelsesFeltet() {

        enteringTestHeaderLogger.debug(null);

        final UrMelding osMelding = new UrMelding("10108000398PERSON      2011-01-28T18:25:5825          " +
                "00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398");

        when(aktoerRestClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = urOppgaveOppretter.apply(Collections.singletonList(osMelding)).get();

        assertTrue(oppgave.beskrivelse.contains("1940kr"));
    }

    @Test
    void fjernDesimalerFraNettoBelopIBeskrivelsesFeltet() {

        enteringTestHeaderLogger.debug(null);

        final UrMelding osMelding = new UrMelding("10108000398PERSON      2011-01-28T18:25:5825          " +
                "00000000019401æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398");

        when(aktoerRestClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = urOppgaveOppretter.apply(Collections.singletonList(osMelding)).get();

        assertTrue(oppgave.beskrivelse.contains("1940kr"));
    }
}
