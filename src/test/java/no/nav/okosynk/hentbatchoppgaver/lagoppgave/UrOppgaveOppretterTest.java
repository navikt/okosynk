package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerRespons;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.Melding;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import no.nav.okosynk.model.Oppgave;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static no.nav.okosynk.hentbatchoppgaver.parselinje.Util.formatAsNorwegianDate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
class UrOppgaveOppretterTest {
    private static final UrMelding UR_MELDING_1 = new UrMelding("10108000398PERSON      2011-01-28T18:25:5825          00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398");
    private static final UrMelding UR_MELDING_2 = new UrMelding("00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8020INNT   UR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282");
    private static final UrMelding UR_MELDING_UTEN_MAPPING_TIL_OPPGAVE = new UrMelding("00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8019ANDRUTBUR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282");
    private static final String UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE = "25;   Mottakers konto er oppgjort;   postert/bilagsnummer:21.01.11/342552558;   1940kr;   statusdato:28.01.11;   UtbTil:10108000398;";
    private static final String UR_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE = "25;   Feil bruk av KID/ugyldig KID;   postert/bilagsnummer:31.01.11/343296727;   30416kr;   statusdato:01.02.11;   UtbTil:00837873282;";
    protected final OkosynkConfiguration okosynkConfiguration;
    private final OppgaveOppretter urOppgaveOppretter;
    protected IAktoerClient aktoerClient = Mockito.mock(IAktoerClient.class);

    UrOppgaveOppretterTest() throws IOException {
        Mappingregelverk.init(Constants.BATCH_TYPE.UR.getMappingRulesPropertiesFileName());
        okosynkConfiguration = Mockito.mock(OkosynkConfiguration.class);
        urOppgaveOppretter = new OppgaveOppretter(this.aktoerClient);
    }

    @Test
    void lagBeskrivelseLagerForventetBeskrivelse() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(UrOppgaveOppretterTest.UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE, UrOppgaveOppretterTest.UR_MELDING_1.beskrivelseInfo().lagBeskrivelse()),
                () -> Assertions.assertEquals(UR_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE, UrOppgaveOppretterTest.UR_MELDING_2.beskrivelseInfo().lagBeskrivelse())
        );
    }

    @Test
    @DisplayName("De to første tegnene i beskrivelsen skal være feltseparatorer for å unngå å skrive over beskrivelse")
    void toForsteTegnIBeskrivelseErFeltseparatorer() {
        final List<Melding> meldingsliste = new ArrayList<>();
        meldingsliste.add(UrOppgaveOppretterTest.UR_MELDING_1);

        Assertions.assertEquals(UrOppgaveOppretterTest.UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("25;", "25;;"),
                this.urOppgaveOppretter.lagSamletBeskrivelse(meldingsliste));
    }


    @Test
    void applyReturnererOppgaveMedRiktigeVerdier() {
        final String expectedFolkeregisterIdent = "10108000398";
        final String expectedAktoerId = "123";
        when(this.aktoerClient.hentGjeldendeAktoerId(expectedFolkeregisterIdent)).thenReturn(AktoerRespons.ok(expectedAktoerId));
        final Oppgave oppgave = this.urOppgaveOppretter.opprettOppgave(Collections.singletonList(UrOppgaveOppretterTest.UR_MELDING_1)).orElseThrow();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(oppgave.oppgavetypeKode()).isEqualTo("OKO_UR");
        softly.assertThat(oppgave.fagomradeKode()).isEqualTo("OKO");
        softly.assertThat(oppgave.behandlingstema()).isNull();
        softly.assertThat(oppgave.behandlingstype()).isEqualTo("ae0218");
        softly.assertThat(oppgave.prioritetKode()).isEqualTo("LAV");
        softly.assertThat(oppgave.beskrivelse()).isEqualTo(UrOppgaveOppretterTest.UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("25;", "25;;"));
        softly.assertThat(oppgave.aktivFra()).isNotNull();
        softly.assertThat(oppgave.aktivTil()).isNotNull();
        softly.assertThat(oppgave.ansvarligEnhetId()).isEqualTo("4151");
        softly.assertThat(oppgave.lest()).isFalse();
        softly.assertThat(oppgave.aktoerId()).isEqualTo(expectedAktoerId);
        softly.assertThat(oppgave.folkeregisterIdent()).isNull();
        softly.assertAll();
    }

    @Test
    void applyLagerRiktigSamletBeskrivelseGittFlereUrMeldinger() {
        final String forventetSamletBeskrivelse =
                UR_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("25;", "25;;") +
                        OppgaveOppretter.getRecordSeparator() +
                        UrOppgaveOppretterTest.UR_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE;

        Assertions.assertEquals(forventetSamletBeskrivelse, this.urOppgaveOppretter.opprettOppgave(Arrays.asList(UrOppgaveOppretterTest.UR_MELDING_1, UrOppgaveOppretterTest.UR_MELDING_2)).orElseThrow().beskrivelse());
        Assertions.assertEquals(forventetSamletBeskrivelse, this.urOppgaveOppretter.opprettOppgave(Arrays.asList(UrOppgaveOppretterTest.UR_MELDING_2, UrOppgaveOppretterTest.UR_MELDING_1)).orElseThrow().beskrivelse());
    }

    @Test
    void applySortererSamletBeskrivelseMedNyesteDatoPostertForstDersomNyesteDatoLeggesInnForst() {
        final String urMelding1DatoPostert = formatAsNorwegianDate(UrOppgaveOppretterTest.UR_MELDING_1.datoPostert);
        final String urMelding2DatoPostert = formatAsNorwegianDate(UrOppgaveOppretterTest.UR_MELDING_2.datoPostert);

        final Oppgave oppgave = this.urOppgaveOppretter.opprettOppgave(Arrays.asList(UrOppgaveOppretterTest.UR_MELDING_2, UrOppgaveOppretterTest.UR_MELDING_1)).orElseThrow();

        Assertions.assertAll(
                () -> assertTrue(oppgave.beskrivelse().contains(urMelding1DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse().contains(urMelding2DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse().indexOf(urMelding2DatoPostert) < oppgave.beskrivelse().indexOf(urMelding1DatoPostert))
        );
    }

    @Test
    void applySortererSamletBeskrivelseMedNyesteDatoPostertForstDersomNyesteDatoLeggesInnSist() {
        final String urMelding1DatoPostert = formatAsNorwegianDate(UrOppgaveOppretterTest.UR_MELDING_1.datoPostert);
        final String urMelding2DatoPostert = formatAsNorwegianDate(UrOppgaveOppretterTest.UR_MELDING_2.datoPostert);

        final Oppgave oppgave = this.urOppgaveOppretter.opprettOppgave(Arrays.asList(UrOppgaveOppretterTest.UR_MELDING_1, UrOppgaveOppretterTest.UR_MELDING_2)).orElseThrow();

        Assertions.assertAll(
                () -> assertTrue(oppgave.beskrivelse().contains(urMelding1DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse().contains(urMelding2DatoPostert)),
                () -> assertTrue(oppgave.beskrivelse().indexOf(urMelding2DatoPostert) < oppgave.beskrivelse().indexOf(urMelding1DatoPostert))
        );
    }

    @Test
    void applyReturnererTomOptionalForTomListe() {
        assertFalse(this.urOppgaveOppretter.opprettOppgave(Collections.emptyList()).isPresent());
    }

    @Test
    void applyReturnererTomOptionalForMeldingUtenMappingTilOppgave() {
        assertFalse(this.urOppgaveOppretter.opprettOppgave(Collections.singletonList(UrOppgaveOppretterTest.UR_MELDING_UTEN_MAPPING_TIL_OPPGAVE)).isPresent());
    }

    @Test
    void brukBelopIHeleKronerIBeskrivelsesFeltet() {
        final UrMelding osMelding = new UrMelding("10108000398PERSON      2011-01-28T18:25:5825          " +
                "00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398");

        when(this.aktoerClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = this.urOppgaveOppretter.opprettOppgave(Collections.singletonList(osMelding)).orElseThrow();

        assertTrue(oppgave.beskrivelse().contains("1940kr"));
    }

    @Test
    void fjernDesimalerFraNettoBelopIBeskrivelsesFeltet() {
        final UrMelding osMelding = new UrMelding("10108000398PERSON      2011-01-28T18:25:5825          " +
                "00000000019401æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398");

        when(aktoerClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = this.urOppgaveOppretter.opprettOppgave(Collections.singletonList(osMelding)).orElseThrow();

        assertTrue(oppgave.beskrivelse().contains("1940kr"));
    }
}
