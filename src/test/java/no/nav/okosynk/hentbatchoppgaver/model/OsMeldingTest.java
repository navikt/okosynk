package no.nav.okosynk.hentbatchoppgaver.model;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.OsMeldingBatchInputRecordBuilder;
import no.nav.okosynk.hentbatchoppgaver.parselinje.AbstractMeldingBatchInputRecordBuilder;
import no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingBuilder;
import no.nav.okosynk.model.GjelderIdType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class OsMeldingTest extends AbstractMeldingTest {


    private static Stream<Arguments> getOsMeldingAndExpected() {
        return OsMeldingTestGenerator.osMeldingAndExpectedProvider();
    }

    @Test
    void osMeldingParserMeldingTilVariabler() {

        OsMelding melding = new OsMelding(OsMeldingTestGenerator.OsMeldingForPerson.getMelding());

        Assertions.assertAll("OsMelding parsing til variabler",
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.personGjelderId, melding.gjelderId),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.datoForStatus, melding.datoForStatus.toString()),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.nyesteVentestatus, melding.nyesteVentestatus),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.brukerId, melding.brukerId),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.totaltNettoBelop, String.valueOf(melding.totaltNettoBelop)),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.behandleneEnhet, melding.behandlendeEnhet),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.beregningsId, melding.beregningsId),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.beregningsdato, melding.beregningsDato.toString()),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.forsteFomIPeriode, melding.forsteFomIPeriode.toString()),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.sisteTomIPeriode, melding.sisteTomIPeriode.toString()),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.flaggFeilkonto, melding.flaggFeilkonto),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.faggruppe, melding.faggruppe),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.utbetalesTilId, melding.utbetalesTilId),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.etteroppgjor, melding.etteroppgjor)
        );
    }

    @ParameterizedTest(name = "gjelderId = {0}, gjelderType = {1}")
    @MethodSource("getOsMeldingAndExpected")
    void utledeGjelderType(String gjelderId, GjelderIdType expectedGjelderIdType, String _inputMelding) {
        final GjelderIdType gjelderIdType = GjelderIdType.fra(gjelderId);

        assertThat(expectedGjelderIdType).isSameAs(gjelderIdType);
    }

    @Test
    void equalsNullObjektGirFalse() {

        OsMelding melding = new OsMelding(OsMeldingTestGenerator.withGjelderIdPerson());

        assertNotEquals(null, melding);
    }

    @Test
    void equalsAnnetObjektGirFalse() {

        OsMelding melding = new OsMelding(OsMeldingTestGenerator.withGjelderIdPerson());
        String annetObjekt = "";

        assertNotEquals(melding, annetObjekt);
    }

    @Test
    void equalsAnnenGjelderIdTypeGirFalse() {

        OsMelding personMelding = new OsMelding(OsMeldingTestGenerator.withGjelderIdPerson());
        OsMelding organisasjonMelding = new OsMelding(OsMeldingTestGenerator.withGjelderIdOrganiasjon());

        assertNotEquals(personMelding, organisasjonMelding);
        assertNotEquals(personMelding.hashCode(), organisasjonMelding.hashCode());
    }

    @Test
    @DisplayName("Fjern desimaler i hentNettoBelopSomStreng når totalt nettobelop er et heltall")
    void fjernDesimalerNarNettoBelopErEtHeltall() {

        final String osMeldingInput = "10108000398012345678 2010-10-102010-10-26RETUK231B3502009-05-012009-07-310000000" +
                "12300æ 8020         INNT    10108000398            ";
        final OsMelding melding = new OsMelding(osMeldingInput);

        assertAll(
                () -> assertEquals(1230.0, melding.totaltNettoBelop),
                () -> assertEquals("1230", melding.hentNettoBelopSomStreng())
        );
    }

    @Test
    @DisplayName("Fjern desimaler i hentNettoBelopSomStreng når totalt nettobelop har desimaler")
    void fjernDesimalerNarNettoBelopHarDesimaler() {

        final String osMeldingInput = "10108000398012345678 2010-10-102010-10-26RETUK231B3502009-05-012009-07-310000000" +
                "12306æ 8020         INNT    10108000398            ";
        final OsMelding melding = new OsMelding(osMeldingInput);

        assertAll(
                () -> assertEquals(1230.6
                        , melding.totaltNettoBelop),
                () -> assertEquals("1230", melding.hentNettoBelopSomStreng())
        );
    }


    @Test
    void equalsLikMeldingGirTrue() {

        OsMelding personMelding = new OsMelding(OsMeldingTestGenerator.withGjelderIdPerson());
        OsMelding organisasjonMelding = new OsMelding(OsMeldingTestGenerator.withGjelderIdPerson());

        assertEquals(personMelding, organisasjonMelding);
        assertEquals(personMelding.hashCode(), organisasjonMelding.hashCode());
    }

    @Test
    void settDefaultPaFlaggFeilkontoTilEtMellomrom() {

        final String osMeldingInput = "10108000398012345678 2010-10-102010-10-26RETUK231B3502009-05-012009-07-310000000" +
                "12306æ 8020         INNT    10108000398            ";
        final OsMelding melding = new OsMelding(osMeldingInput);

        assertEquals(" ", melding.flaggFeilkonto);
    }

    @Test
    void brukFlaggFeilkontoFraInputStringIOsMeldingHvisDenFinnes() {

        final String osMeldingInput = "10108000398012345678 2010-10-102010-10-26RETUK231B3502009-05-012009-07-310000000" +
                "12306æJ8020         INNT    10108000398            ";
        final OsMelding melding = new OsMelding(osMeldingInput);

        assertEquals("J", melding.flaggFeilkonto);
    }

    @Test
    @DisplayName("Assert that all equal meldinger hash to the same value")
    void test_that_all_equal_meldinger_hash_to_the_same_value() {

        final List<OsMelding> allHopefullyUniqueMeldinger = new ArrayList<>();

        final Random random = new Random(123);
        asList("01234567890", "01234777890").forEach(gjelderId ->
                asList("4819", "8020").forEach(behandlendeEnhet ->
                        asList("022838640", "022543210").forEach(beregningsId ->
                                asList("2009-07-04", "2009-07-03").forEach(beregningsDato ->
                                        asList("INNT", "KREDDISP").forEach(faggruppe -> {
                                            final OsMelding melding1 = createMelding(gjelderId, behandlendeEnhet, beregningsId, beregningsDato, faggruppe, random);
                                            final OsMelding melding2 = createMelding(gjelderId, behandlendeEnhet, beregningsId, beregningsDato, faggruppe, random);
                                            assertEquals(melding1, melding2, "Fields are equal in the two messages,but they are not equal.");
                                            assertEquals(melding1.hashCode(), melding2.hashCode(), "The two messages are equal, but they do not produce the same hash.");
                                            allHopefullyUniqueMeldinger.add(melding1);
                                        })))));

        for (int i = 0; i < allHopefullyUniqueMeldinger.size(); i++) {
            final OsMelding melding1 = allHopefullyUniqueMeldinger.get(i);
            for (int j = 0; j < allHopefullyUniqueMeldinger.size(); j++) {
                if (i != j) {
                    final OsMelding melding2 = allHopefullyUniqueMeldinger.get(j);
                    assertNotEquals(melding1, melding2, "Two OS meldinger expected to be different are equal");
                }
            }
        }
    }

    private OsMelding createMelding(
            final String gjelderId,
            final String behandlendeEnhet,
            final String beregningsId,
            final String beregningsDato,
            final String faggruppe,
            final Random random) {

        final int totalRecordLength =
                Math.max(
                        AbstractMeldingBatchInputRecordBuilder.SUPER_FIELD_DEF.getUrRecordLength(),
                        OsMeldingBatchInputRecordBuilder.SUB_FIELD_DEF.getRecordLength()
                );

        return OsMeldingBuilder
                .newBuilder()
                .withMeldingBatchInputRecordBuilder(
                        OsMeldingBatchInputRecordBuilder
                                .newBuilder()
                                .withGjelderId(gjelderId) // Involved in equality
                                .withBehandlendeEnhet(behandlendeEnhet) // Involved in equality
                                .withDatoForStatus(randomLocalDateTime(random))
                                .withNyesteVentestatus("AVAV")
                                .withBrukerId(randomAlphanumeric(totalRecordLength, random))
                                .withTotaltNettoBelop(randomNumeric(5, random) + "æ")
                                .withBeregningsId(beregningsId) // Involved in equality
                                .withBeregningsDato(beregningsDato) // Involved in equality
                                .withFaggruppe(faggruppe) // Involved in equality
                                .withForsteFomIPeriode("2006-06-06")
                                .withSisteTomIPeriode("2006-06-06")
                                .withFlaggFeilkonto(randomAlphanumeric(totalRecordLength, random))
                                .withUtbetalesTilId(randomNumeric(11, random))
                                .withEtteroppgjor(randomAlphanumeric(totalRecordLength, random))
                )
                .build();
    }
}
