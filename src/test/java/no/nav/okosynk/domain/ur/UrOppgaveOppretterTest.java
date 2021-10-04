package no.nav.okosynk.domain.ur;

import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRespons;
import no.nav.okosynk.domain.AbstractOppgaveOppretterTest;
import no.nav.okosynk.domain.Oppgave;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
class UrOppgaveOppretterTest extends AbstractOppgaveOppretterTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");
    private static final UrMelding UR_MELDING_1 = new UrMelding("10108000398PERSON      2011-01-28T18:25:5825          00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398");
    private static final UrMelding UR_MELDING_2 = new UrMelding("00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8020INNT   UR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282");
    private static final UrMelding UR_MELDING_UTEN_MAPPING_TIL_OPPGAVE = new UrMelding("00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8019ANDRUTBUR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282");
    private static final String UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE = "25;   Mottakers konto er oppgjort;   postert/bilagsnummer:21.01.11/342552558;   1940kr;   statusdato:28.01.11;   UtbTil:10108000398;";
    private static final String UR_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE = "25;   Feil bruk av KID/ugyldig KID;   postert/bilagsnummer:31.01.11/343296727;   30416kr;   statusdato:01.02.11;   UtbTil:00837873282;";
    private final UrOppgaveOppretter urOppgaveOppretter;

    UrOppgaveOppretterTest() {
        super(new FakeOkosynkConfiguration());
        this.urOppgaveOppretter = new UrOppgaveOppretter(new UrMappingRegelRepository(), this.aktoerRestClient, this.okosynkConfiguration);
    }

    @Test
    void lagBeskrivelseLagerForventetBeskrivelse() {

        enteringTestHeaderLogger.debug(null);

        assertAll(
                () -> assertEquals(UrOppgaveOppretterTest.UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE, this.urOppgaveOppretter.lagBeskrivelse(UrOppgaveOppretterTest.UR_MELDING_1)),
                () -> assertEquals(UR_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE, this.urOppgaveOppretter.lagBeskrivelse(UrOppgaveOppretterTest.UR_MELDING_2))
        );
    }

    @Test
    @DisplayName("De to første tegnene i beskrivelsen skal være feltseparatorer for å unngå å skrive over beskrivelse")
    void toForsteTegnIBeskrivelseErFeltseparatorer() {

        enteringTestHeaderLogger.debug(null);

        final List<UrMelding> meldingsliste = new ArrayList<>();
        meldingsliste.add(UrOppgaveOppretterTest.UR_MELDING_1);

        assertEquals(UrOppgaveOppretterTest.UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("25;", "25;;"),
                this.urOppgaveOppretter.lagSamletBeskrivelse(meldingsliste));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void applyReturnererOppgaveMedRiktigeVerdier(final boolean shouldConvertFolkeregisterIdentToAktoerId) {

        enteringTestHeaderLogger.debug(null);

        setShouldConvertFolkeregisterIdentToAktoerId(shouldConvertFolkeregisterIdentToAktoerId);

        final String expectedFolkeregisterIdent = "10108000398";
        final String expectedAktoerId = "123";
        when(this.aktoerRestClient.hentGjeldendeAktoerId(expectedFolkeregisterIdent)).thenReturn(AktoerRespons.ok(expectedAktoerId));
        final Oppgave oppgave = this.urOppgaveOppretter.apply(Collections.singletonList(UrOppgaveOppretterTest.UR_MELDING_1)).get();
        assertAll(
                () -> assertEquals("OKO_UR", oppgave.oppgavetypeKode),
                () -> assertEquals("OKO", oppgave.fagomradeKode),
                () -> assertNull(null, oppgave.behandlingstema),
                () -> assertEquals("ae0218", oppgave.behandlingstype),
                () -> assertEquals("LAV", oppgave.prioritetKode),
                () -> assertEquals(UrOppgaveOppretterTest.UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("25;", "25;;"), oppgave.beskrivelse),
                () -> assertNotNull(oppgave.aktivFra),
                () -> assertNotNull(oppgave.aktivTil),
                () -> assertEquals("4151", oppgave.ansvarligEnhetId),
                () -> assertFalse(oppgave.lest)
        );

        if (this.okosynkConfiguration.shouldConvertFolkeregisterIdentToAktoerId()) {
            assertEquals(expectedAktoerId, oppgave.aktoerId);
            assertEquals(null, oppgave.folkeregisterIdent);
        } else {
            assertEquals(null, oppgave.aktoerId);
            assertEquals(expectedFolkeregisterIdent, oppgave.folkeregisterIdent);
        }
    }

    @Test
    void applyLagerRiktigSamletBeskrivelseGittFlereUrMeldinger() {

        enteringTestHeaderLogger.debug(null);

        final String forventetSamletBeskrivelse = new StringBuffer()
                .append(UR_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("25;", "25;;"))
                .append(UrOppgaveOppretter.getRecordSeparator())
                .append(UrOppgaveOppretterTest.UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE)
                .toString();


        assertEquals(forventetSamletBeskrivelse, this.urOppgaveOppretter.apply(Arrays.asList(UrOppgaveOppretterTest.UR_MELDING_1, UrOppgaveOppretterTest.UR_MELDING_2)).get().beskrivelse);
        assertEquals(forventetSamletBeskrivelse, this.urOppgaveOppretter.apply(Arrays.asList(UrOppgaveOppretterTest.UR_MELDING_2, UrOppgaveOppretterTest.UR_MELDING_1)).get().beskrivelse);
    }

    @Test
    void applySortererSamletBeskrivelseMedNyesteDatoPostertForstDersomNyesteDatoLeggesInnForst() {

        enteringTestHeaderLogger.debug(null);

        final String urMelding1DatoPostert = UrOppgaveOppretter.formatAsNorwegianDate(UrOppgaveOppretterTest.UR_MELDING_1.datoPostert);
        final String urMelding2DatoPostert = UrOppgaveOppretter.formatAsNorwegianDate(UrOppgaveOppretterTest.UR_MELDING_2.datoPostert);

        final Oppgave oppgave = this.urOppgaveOppretter.apply(Arrays.asList(UrOppgaveOppretterTest.UR_MELDING_2, UrOppgaveOppretterTest.UR_MELDING_1)).get();

        assertAll(
                () -> assertTrue(oppgave.beskrivelse.contains(urMelding1DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse.contains(urMelding2DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse.indexOf(urMelding2DatoPostert) < oppgave.beskrivelse.indexOf(urMelding1DatoPostert))
        );
    }

    @Test
    void applySortererSamletBeskrivelseMedNyesteDatoPostertForstDersomNyesteDatoLeggesInnSist() {

        enteringTestHeaderLogger.debug(null);

        final String urMelding1DatoPostert = UrOppgaveOppretter.formatAsNorwegianDate(UrOppgaveOppretterTest.UR_MELDING_1.datoPostert);
        final String urMelding2DatoPostert = UrOppgaveOppretter.formatAsNorwegianDate(UrOppgaveOppretterTest.UR_MELDING_2.datoPostert);

        final Oppgave oppgave = this.urOppgaveOppretter.apply(Arrays.asList(UrOppgaveOppretterTest.UR_MELDING_1, UrOppgaveOppretterTest.UR_MELDING_2)).get();

        assertAll(
                () -> assertTrue(oppgave.beskrivelse.contains(urMelding1DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse.contains(urMelding2DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse.indexOf(urMelding2DatoPostert) < oppgave.beskrivelse.indexOf(urMelding1DatoPostert))
        );
    }

    @Test
    void applyReturnererTomOptionalForTomListe() {

        enteringTestHeaderLogger.debug(null);

        assertFalse(this.urOppgaveOppretter.apply(Collections.emptyList()).isPresent());
    }

    @Test
    void applyReturnererTomOptionalForMeldingUtenMappingTilOppgave() {
        assertFalse(this.urOppgaveOppretter.apply(Collections.singletonList(UrOppgaveOppretterTest.UR_MELDING_UTEN_MAPPING_TIL_OPPGAVE)).isPresent());
    }

    @Test
    void brukBelopIHeleKronerIBeskrivelsesFeltet() {

        enteringTestHeaderLogger.debug(null);

        final UrMelding osMelding = new UrMelding("10108000398PERSON      2011-01-28T18:25:5825          " +
                "00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398");

        when(this.aktoerRestClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = this.urOppgaveOppretter.apply(Collections.singletonList(osMelding)).get();

        assertTrue(oppgave.beskrivelse.contains("1940kr"));
    }

    @Test
    void fjernDesimalerFraNettoBelopIBeskrivelsesFeltet() {

        enteringTestHeaderLogger.debug(null);

        final UrMelding osMelding = new UrMelding("10108000398PERSON      2011-01-28T18:25:5825          " +
                "00000000019401æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398");

        when(aktoerRestClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = this.urOppgaveOppretter.apply(Collections.singletonList(osMelding)).get();

        assertTrue(oppgave.beskrivelse.contains("1940kr"));
    }
}
