package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerRespons;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.Melding;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.model.Oppgave;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Arrays;
import no.nav.okosynk.hentbatchoppgaver.parselinje.Util;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

@SuppressWarnings("ConstantConditions")
class OsOppgaveOppretterTest {

    private static final OsMelding OS_MELDING_1 = new OsMelding("10108000398022828640 2009-07-042009-09-26RETUK231B3502009-05-012009-07-31000000012300æ 8020         INNT    10108000398            ");
    private static final OsMelding OS_MELDING_2 = new OsMelding("06025812345029568753 2009-11-062009-11-30AVVEX123456 2009-11-012009-11-30000000072770æ 8020         INNT    06025812345            ");
    private static final OsMelding OS_MELDING_UTEN_MAPPING_TIL_OPPGAVE = new OsMelding("06025812345029568753 2009-11-062009-11-30AVVEX123456 2009-11-012009-11-30000000072770æ 8019         HELSEREF06025812345            ");
    private static final String OS_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE = "RETU;   1230kr;   beregningsdato/id:04.07.09/022828640;   periode:01.05.09-31.07.09;   feilkonto: ;   statusdato:26.09.09;   ;   UtbTil:10108000398;   K231B350";
    private static final String OS_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE = "AVVE;   7277kr;   beregningsdato/id:06.11.09/029568753;   periode:01.11.09-30.11.09;   feilkonto: ;   statusdato:30.11.09;   ;   UtbTil:06025812345;   X123456";
    protected final OkosynkConfiguration okosynkConfiguration;
    private final OppgaveOppretter osOppgaveOppretter;
    protected IAktoerClient aktoerClient = mock(IAktoerClient.class);

    OsOppgaveOppretterTest() throws IOException {
        okosynkConfiguration = mock(OkosynkConfiguration.class);
        osOppgaveOppretter = new OppgaveOppretter(this.aktoerClient);
        Mappingregelverk.init(Constants.BATCH_TYPE.OS.getMappingRulesPropertiesFileName());
    }

    @Test
    void lagBeskrivelseLagerForventetBeskrivelse() {
        assertThat(OS_MELDING_1.beskrivelseInfo().lagBeskrivelse()).isEqualTo(OS_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE);
        assertThat(OS_MELDING_2.beskrivelseInfo().lagBeskrivelse()).isEqualTo(OS_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE);
    }

    @Test
    @DisplayName("De to første tegnene i beskrivelsen skal være feltseparatorer for å unngå å skrive over beskrivelse")
    void toForsteTegnIBeskrivelseErFeltseparatorer() {
        List<Melding> meldingsliste = new ArrayList<>();
        meldingsliste.add(OS_MELDING_1);

        assertThat(this.osOppgaveOppretter.lagSamletBeskrivelse(meldingsliste)).isEqualTo(OS_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("RETU;", "RETU;;"));
    }

    @Test
    void applyReturnererOppgaveMedRiktigeVerdier() {
        final String expectedFolkeregisterIdent = "10108000398";
        final String expectedAktoerId = "123";
        Mockito.when(this.aktoerClient.hentGjeldendeAktoerId(expectedFolkeregisterIdent)).thenReturn(AktoerRespons.ok(expectedAktoerId));
        final Oppgave oppgave = this.osOppgaveOppretter.opprettOppgave(singletonList(OS_MELDING_1)).orElseThrow();

        assertThat(oppgave.oppgavetypeKode()).isEqualTo("OKO_OS");
        assertThat(oppgave.fagomradeKode()).isEqualTo("OKO");
        assertThat(oppgave.behandlingstema()).isNull();
        assertThat(oppgave.behandlingstype()).isEqualTo("ae0218");
        assertThat(oppgave.prioritetKode()).isEqualTo("LAV");
        assertThat(oppgave.beskrivelse()).isEqualTo(OS_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("RETU;", "RETU;;"));
        assertThat(oppgave.aktivFra()).isNotNull();
        assertThat(oppgave.aktivTil()).isNotNull();
        assertThat(oppgave.ansvarligEnhetId()).isEqualTo("4151");
        assertFalse(oppgave.lest());
        assertThat(oppgave.aktoerId()).isEqualTo(expectedAktoerId);
        assertThat(oppgave.folkeregisterIdent()).isNull();
    }

    @Test
    void applyLagerRiktigSamletBeskrivelseGittFlereOsMeldinger() {
        final String forventetSamletBeskrivelse =
                OS_MELDING_2_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE.replaceFirst("AVVE;", "AVVE;;") +
                        OppgaveOppretter.getRecordSeparator() +
                        OS_MELDING_1_FORVENTET_BESKRIVELSE_FRA_LAG_BESKRIVELSE;

        Mockito.when(this.aktoerClient.hentGjeldendeAktoerId("06025812345")).thenReturn(AktoerRespons.ok("123"));
        assertThat(this.osOppgaveOppretter.opprettOppgave(Arrays.asList(OS_MELDING_2, OS_MELDING_1)).orElseThrow().beskrivelse()).isEqualTo(forventetSamletBeskrivelse);
        assertThat(this.osOppgaveOppretter.opprettOppgave(Arrays.asList(OS_MELDING_1, OS_MELDING_2)).orElseThrow().beskrivelse()).isEqualTo(forventetSamletBeskrivelse);
    }

    @Test
    void applySortererSamletBeskrivelseMedNyesteBeregningsDatoForstDersomNyesteDatoLeggesInnForst() {
        final String osMelding1Beregningsdato = Util.formatAsNorwegianDate(LocalDate.parse("2009-07-04"));
        final String osMelding2Beregningsdato = Util.formatAsNorwegianDate(LocalDate.parse("2009-11-06"));

        Mockito.when(this.aktoerClient.hentGjeldendeAktoerId("06025812345")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = this.osOppgaveOppretter.opprettOppgave(Arrays.asList(OS_MELDING_2, OS_MELDING_1)).orElseThrow();


        assertThat(oppgave.beskrivelse())
                .contains(osMelding1Beregningsdato)
                .contains(osMelding2Beregningsdato)
                .matches(s -> s.indexOf(osMelding2Beregningsdato) < (oppgave.beskrivelse().indexOf(osMelding1Beregningsdato)), "seneste beregningsdato skal komme først");
    }

    @Test
    void applySortererSamletBeskrivelseMedNyesteBeregningsDatoForstDersomNyesteDatoLeggesInnSist() {
        final String osMelding1Beregningsdato = Util.formatAsNorwegianDate(LocalDate.parse("2009-07-04"));
        final String osMelding2Beregningsdato = Util.formatAsNorwegianDate(LocalDate.parse("2009-11-06"));

        Mockito.when(this.aktoerClient.hentGjeldendeAktoerId("06025812345")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = this.osOppgaveOppretter.opprettOppgave(Arrays.asList(OS_MELDING_1, OS_MELDING_2)).orElseThrow();


        assertThat(oppgave.beskrivelse())
                .contains(osMelding1Beregningsdato)
                .contains(osMelding2Beregningsdato)
                .matches(s -> s.indexOf(osMelding2Beregningsdato) < (oppgave.beskrivelse().indexOf(osMelding1Beregningsdato)), "seneste beregningsdato skal komme først");

    }


    @Test
    void applyReturnererTomOptionalForTomListe() {
        assertFalse(this.osOppgaveOppretter.opprettOppgave(Collections.emptyList()).isPresent());
    }

    @Test
    void applyReturnererTomOptionalForMeldingUtenMappingTilOppgave() {
        assertFalse(this.osOppgaveOppretter.opprettOppgave(singletonList(OS_MELDING_UTEN_MAPPING_TIL_OPPGAVE)).isPresent());
    }

    @Test
    void brukBelopIHeleKronerIBeskrivelsesFeltet() {
        final OsMelding osMelding = new OsMelding("10108000398012345678 2015-07-212015-07-22AVVED133832 2015-07-012015-07-31" +
                "000000019400æ 8020         BA      10108000398            ");

        Mockito.when(this.aktoerClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = this.osOppgaveOppretter.opprettOppgave(singletonList(osMelding)).orElseThrow();

        assertThat(oppgave.beskrivelse()).contains("1940kr");
    }

    @Test
    void fjernDesimalerFraNettoBelopIBeskrivelsesFeltet() {
        final OsMelding osMelding = new OsMelding("10108000398012345678 2015-07-212015-07-22AVVED133832 2015-07-012015-07-31" +
                "000000019401æ 8020         BA      10108000398            ");

        Mockito.when(aktoerClient.hentGjeldendeAktoerId("10108000398")).thenReturn(AktoerRespons.ok("123"));
        final Oppgave oppgave = this.osOppgaveOppretter.opprettOppgave(singletonList(osMelding)).orElseThrow();

        assertThat(oppgave.beskrivelse()).contains("1940kr");
    }

}
