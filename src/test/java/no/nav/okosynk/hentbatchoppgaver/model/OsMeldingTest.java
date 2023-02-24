package no.nav.okosynk.hentbatchoppgaver.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import no.nav.okosynk.hentbatchoppgaver.parselinje.AbstractMeldingBatchInputRecordBuilder;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.OsMeldingBatchInputRecordBuilder;
import no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OsMeldingTest extends AbstractMeldingTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    void osMeldingParserMeldingTilVariabler() {

        enteringTestHeaderLogger.debug(null);

        OsMelding melding = new OsMelding(OsMeldingTestGenerator.OsMeldingForPerson.getMelding());

        assertAll("OsMelding parsing til variabler",
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.personGjelderId, melding.gjelderId),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.datoForStatus, melding.datoForStatus.toString()),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.nyesteVentestatus, melding.nyesteVentestatus),
                () -> assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.brukerId, melding.brukerId),
                () -> Assertions.assertEquals(OsMeldingTestGenerator.OsMeldingForPerson.totaltNettoBelop, String.valueOf(melding.totaltNettoBelop)),
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
    void utledeGjelderType(String gjelderId, String expectedGjelderIdType, String inputMelding) {
        OsMelding osMelding = new OsMelding(inputMelding);
        final String gjelderIdType = osMelding.utledGjelderIdType();

        assertEquals(expectedGjelderIdType, gjelderIdType);
    }

    private static Stream<Arguments> getOsMeldingAndExpected() {
        return OsMeldingTestGenerator.osMeldingAndExpectedProvider();
    }

    @Test
    void equalsOgHashPaSammeObjekt() {

        enteringTestHeaderLogger.debug(null);

        OsMelding melding = new OsMelding(OsMeldingTestGenerator.withGjelderIdPerson());

        assertEquals(melding, melding);
        assertEquals(melding.hashCode(), melding.hashCode());
    }

    @Test
    void equalsNullObjektGirFalse() {

        enteringTestHeaderLogger.debug(null);

        OsMelding melding = new OsMelding(OsMeldingTestGenerator.withGjelderIdPerson());

        assertNotEquals(null, melding);
    }

    @Test
    void equalsAnnetObjektGirFalse() {

        enteringTestHeaderLogger.debug(null);

        OsMelding melding = new OsMelding(OsMeldingTestGenerator.withGjelderIdPerson());
        String annetObjekt = "";

        assertNotEquals(melding, annetObjekt);
    }

    @Test
    void equalsAnnenGjelderIdTypeGirFalse() {

        enteringTestHeaderLogger.debug(null);

        OsMelding personMelding = new OsMelding(OsMeldingTestGenerator.withGjelderIdPerson());
        OsMelding organisasjonMelding = new OsMelding(OsMeldingTestGenerator.withGjelderIdOrganiasjon());

        assertNotEquals(personMelding, organisasjonMelding);
        assertNotEquals(personMelding.hashCode(), organisasjonMelding.hashCode());
    }

    @Test
    @DisplayName("Fjern desimaler i hentNettoBelopSomStreng når totalt nettobelop er et heltall")
    void fjernDesimalerNarNettoBelopErEtHeltall() {

        enteringTestHeaderLogger.debug(null);

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

        enteringTestHeaderLogger.debug(null);

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

        enteringTestHeaderLogger.debug(null);

        OsMelding personMelding = new OsMelding(OsMeldingTestGenerator.withGjelderIdPerson());
        OsMelding organisasjonMelding = new OsMelding(OsMeldingTestGenerator.withGjelderIdPerson());

        assertEquals(personMelding, organisasjonMelding);
        assertEquals(personMelding.hashCode(), organisasjonMelding.hashCode());
    }

    @Test
    void settDefaultPaFlaggFeilkontoTilEtMellomrom() {

        enteringTestHeaderLogger.debug(null);

        final String osMeldingInput = "10108000398012345678 2010-10-102010-10-26RETUK231B3502009-05-012009-07-310000000" +
                "12306æ 8020         INNT    10108000398            ";
        final OsMelding melding = new OsMelding(osMeldingInput);

        assertEquals(" ", melding.flaggFeilkonto);
    }

    @Test
    void brukFlaggFeilkontoFraInputStringIOsMeldingHvisDenFinnes() {

        enteringTestHeaderLogger.debug(null);

        final String osMeldingInput = "10108000398012345678 2010-10-102010-10-26RETUK231B3502009-05-012009-07-310000000" +
                "12306æJ8020         INNT    10108000398            ";
        final OsMelding melding = new OsMelding(osMeldingInput);

        assertEquals("J", melding.flaggFeilkonto);
    }

    @Test
    @DisplayName("Assert that all equal meldinger hash to the same value")
    void test_that_all_equal_meldinger_hash_to_the_same_value() {

        enteringTestHeaderLogger.debug(null);

        final Set<String> gjelderIder = new HashSet<>();
        gjelderIder.add("01234567890");
        gjelderIder.add("01234777890");

        final Set<String> behandlendeEnheter = new HashSet<>();
        behandlendeEnheter.add("4819");
        behandlendeEnheter.add("8020");

        final Set<String> beregningsIder = new HashSet<>();
        beregningsIder.add("022838640");
        beregningsIder.add("022543210");

        final Set<String> beregningsDatoer = new HashSet<>();
        beregningsDatoer.add("2009-07-04");
        beregningsDatoer.add("2009-07-03");

        final Set<String> faggrupper = new HashSet<>();
        faggrupper.add("INNT");
        faggrupper.add("KREDDISP");

        final List<OsMelding> allHopefullyUniqueMeldinger = new ArrayList<>();

        final Random random = new Random(123);
        gjelderIder
            .stream()
            .forEach(
                gjelderId
                ->
                {
                    behandlendeEnheter
                        .stream()
                        .forEach(
                            behandlendeEnhet
                            ->
                            {
                                beregningsIder
                                    .stream()
                                    .forEach(
                                        beregningsId
                                        ->
                                        {
                                            beregningsDatoer
                                                .stream()
                                                .forEach(
                                                    beregningsDato
                                                    ->
                                                    {
                                                        faggrupper
                                                            .stream()
                                                            .forEach(
                                                                faggruppe
                                                                ->
                                                                {
                                                                    final OsMelding melding1 = createMelding(gjelderId, behandlendeEnhet, beregningsId, beregningsDato, faggruppe, random);
                                                                    final OsMelding melding2 = createMelding(gjelderId, behandlendeEnhet, beregningsId, beregningsDato, faggruppe, random);
                                                                    assertEquals(melding1, melding2, "In spite the fact that all equality involved fields are equal in the two messages, they are not equal.");
                                                                    assertEquals(melding1.hashCode(), melding2.hashCode(), "The two messages are equal, but they do not produce the same hash. That's a Java contract breach.");
                                                                    allHopefullyUniqueMeldinger.add(melding1);
                                                                }
                                                            );
                                                    }
                                                );
                                        }
                                    );
                            }
                        );
                }
        );

        // Test that all meldinger are different from all the others hopefully different ones:
        for (int i = 0; i < allHopefullyUniqueMeldinger.size(); i++) {
            final OsMelding melding1 = allHopefullyUniqueMeldinger.get(i);
            for (int j = 0; j < allHopefullyUniqueMeldinger.size(); j++) {
                if (i != j) {
                    final OsMelding melding2 = allHopefullyUniqueMeldinger.get(j);
                    assertNotEquals(melding1, melding2 , "Two OS meldinger expected to be different are equal");
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

        final OsMelding melding =
            OsMeldingBuilder
                .newBuilder()
                .withMeldingBatchInputRecordBuilder(
                    OsMeldingBatchInputRecordBuilder
                        .newBuilder()
                        .withGjelderId(gjelderId) // Involved in equality
                        .withBehandlendeEnhet(behandlendeEnhet) // Involved in equality
                        .withDatoForStatus(randomLocalDateTime(random))
                        .withNyesteVentestatus(randomAlphanumeric(totalRecordLength, random))
                        .withBrukerId(randomAlphanumeric(totalRecordLength, random))
                        .withTotaltNettoBelop(randomNumeric(5, random) + "æ")
                        .withBeregningsId(beregningsId) // Involved in equality
                        .withBeregningsDato(beregningsDato) // Involved in equality
                        .withFaggruppe(faggruppe) // Involved in equality
                        .withForsteFomIPeriode(randomLocalDate(random))
                        .withSisteTomIPeriode(randomLocalDate(random))
                        .withFlaggFeilkonto(randomAlphanumeric(totalRecordLength, random))
                        .withUtbetalesTilId(randomNumeric(11, random))
                        .withEtteroppgjor(randomAlphanumeric(totalRecordLength, random))
                )
                .build();

        return melding;
    }
}
