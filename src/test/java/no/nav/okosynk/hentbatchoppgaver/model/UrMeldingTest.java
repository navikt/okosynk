package no.nav.okosynk.hentbatchoppgaver.model;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.OsMeldingBatchInputRecordBuilder;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.UrMeldingTestGenerator;
import no.nav.okosynk.hentbatchoppgaver.parselinje.AbstractMeldingBatchInputRecordBuilder;
import no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingBatchInputRecordBuilder;
import no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingBuilder;
import no.nav.okosynk.model.GjelderIdType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UrMeldingTest extends AbstractMeldingTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    void urMeldingParserMeldingTilVariabler() {

        enteringTestHeaderLogger.debug(null);

        UrMelding melding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());

        assertAll("UrMelding parsing i konstruktør",
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.personGjelderId, melding.gjelderId),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.datoForStatus, melding.datoForStatus.toString()),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.nyesteVentestatus, melding.nyesteVentestatus),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.brukerId, melding.brukerId),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.totaltNettoBelop, String.valueOf(melding.totaltNettoBelop)),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.behandlendeEnhet, melding.behandlendeEnhet),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.gjelderIdType, melding.gjelderIdType),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.oppdragsKode, melding.oppdragsKode),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.kilde, melding.kilde),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.datoPostert, melding.datoPostert.toString()),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.bilagsId, melding.bilagsId),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.arsaksTekst, melding.arsaksTekst),
                () -> assertEquals(UrMeldingTestGenerator.EksempelMelding.mottakerId, melding.mottakerId)
        );
    }

    @ParameterizedTest(name = "gjelderId = {0}, gjelderType = {1}")
    @MethodSource("getUrMeldingAndExpected")
    void utledeGjelderType(String gjelderId, GjelderIdType expectedGjelderIdType, String _inputMelding) {
        final GjelderIdType gjelderIdType = GjelderIdType.fra(gjelderId);

        assertThat(gjelderIdType).isSameAs(expectedGjelderIdType);
    }

    private static Stream<Arguments> getUrMeldingAndExpected() {
        return UrMeldingTestGenerator.urMeldingAndExpectedProvider();
    }

    @Test
    void hashPaSammeObjektGirTrue() {

        enteringTestHeaderLogger.debug(null);

        final UrMelding melding =
                new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());

        assertEquals(melding.hashCode(), melding.hashCode());
    }

    @Test
    void equalsNullObjektGirFalse() {

        enteringTestHeaderLogger.debug(null);

        UrMelding melding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());

        assertNotEquals(null, melding);
    }

    @Test
    void equalsAnnetObjektGirFalse() {

        enteringTestHeaderLogger.debug(null);

        UrMelding melding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());
        String annetObjekt = "";

        assertNotEquals(melding, annetObjekt);
    }

    @Test
    void equalsAnnenGjelderIdGirFalse() {

        enteringTestHeaderLogger.debug(null);

        UrMelding melding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding("10108000398"));
        UrMelding melding2 = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding("06025800174"));

        assertNotEquals(melding, melding2);
        assertNotEquals(melding.hashCode(), melding2.hashCode());
    }

    @Test
    void equalsLikMeldingGirTrue() {

        enteringTestHeaderLogger.debug(null);

        UrMelding melding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());
        UrMelding melding2 = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());

        assertEquals(melding, melding2);
        assertEquals(melding, melding2);
    }

    @Test
    @DisplayName("meldinger er like hvis de har ulike behandlende enheter men får lik ansvarlig enhet i oppgave-applikasjonen")
    void equalsUlikBehandlendeEnhetLikAnsvarligEnhetGirTrue() {

        enteringTestHeaderLogger.debug(null);

        UrMelding melding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());
        UrMelding melding2 = new UrMelding(UrMeldingTestGenerator.EksempelMelding.withBehandlendeEnhet("4817"));

        assertEquals(melding, melding2);
    }

    @Test
    @DisplayName("meldinger er ulike hvis de har ulike behandlende enheter og får ulike ansvarlige enheter i oppgave-applikasjonen")
    void equals_ulik_behandlende_enhet_ulik_ansvarlig_enhet_gir_false() {

        enteringTestHeaderLogger.debug(null);

        UrMelding melding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());
        UrMelding melding2 = new UrMelding(UrMeldingTestGenerator.EksempelMelding.withBehandlendeEnhet("8020"));

        assertNotEquals(melding, melding2);
    }

    @Test
    void equalsErFalseHvisMeldingErNull() {

        enteringTestHeaderLogger.debug(null);

        UrMelding melding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());
        UrMelding melding2 = null;

        assertNotEquals(melding, melding2);
    }

    @Test
    @DisplayName("Fjern desimaler i hentNettoBelopSomStreng når totalt nettobelop er et heltall")
    void fjernDesimalerNarNettoBelopErEtHeltall() {

        enteringTestHeaderLogger.debug(null);

        final String urMeldingInput = "10108000398PERSON      2011-01-28T18:25:5825          000000000" +
                "19400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398";

        final UrMelding melding = new UrMelding(urMeldingInput);

        assertAll(
                () -> assertEquals(1940.0, melding.totaltNettoBelop),
                () -> assertEquals("1940", melding.hentNettoBelopSomStreng())
        );
    }

    @Test
    @DisplayName("Fjern desimaler i hentNettoBelopSomStreng når totalt nettobelop har desimaler")
    void fjernDesimalerNarNettoBelopHarDesimaler() {

        enteringTestHeaderLogger.debug(null);

        final String urMeldingInput = "10108000398PERSON      2011-01-28T18:25:5825          000000000" +
                "19401æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398";

        final UrMelding melding = new UrMelding(urMeldingInput);

        assertAll(
                () -> assertEquals(1940.1, melding.totaltNettoBelop),
                () -> assertEquals("1940", melding.hentNettoBelopSomStreng())
        );
    }

    @Test
    @DisplayName("Assert that all equal meldinger hash to the same value")
    void test_that_all_equal_meldinger_hash_to_the_same_value() {

        enteringTestHeaderLogger.debug(null);

        final List<String> gjelderIder = asList("00154567893", "01874777894");
        final List<String> behandlendeEnheter = asList("4819", "8020");
        final List<String> gjelderIdTyper = asList("PERSON", "SAMHANDLER", "ORGANISASJON");
        final List<String> oppdragsKoder = asList("ANDRUTB", "BA");
        final List<String> datoerPostert = asList("2011-01-21", "2015-03-17");

        final List<UrMelding> allHopefullyUniqueMeldinger = new ArrayList<>();

        final Random random = new Random(711);
        for (String gjelderId : gjelderIder) {
            for (String behandlendeEnhet : behandlendeEnheter) {
                for (String gjelderIdType : gjelderIdTyper) {
                    for (String oppdragsKode : oppdragsKoder) {
                        for (String datoPostert : datoerPostert) {
                            final UrMelding melding1 = createMelding(gjelderId, behandlendeEnhet, gjelderIdType, oppdragsKode, datoPostert, random);
                            final UrMelding melding2 = createMelding(gjelderId, behandlendeEnhet, gjelderIdType, oppdragsKode, datoPostert, random);
                            System.out.println("likhetsgreier(melding1) = " + likhetsgreier(melding1));
                            System.out.println("likhetsgreier(melding2) = " + likhetsgreier(melding2));
                            assertEquals(melding1, melding2, "In spite the fact that all equality involved fields are equal in the two messages, they are not equal.");
                            assertEquals(melding1.hashCode(), melding2.hashCode(), "The two messages are equal, but they do not produce the same hash. That's a Java contract breach.");
                            allHopefullyUniqueMeldinger.add(melding1);
                        }
                    }
                }
            }
        }

        // Test that all meldinger are different from all the others hopefully different ones:
        for (int i = 0; i < allHopefullyUniqueMeldinger.size(); i++) {
            final UrMelding melding1 = allHopefullyUniqueMeldinger.get(i);
            for (int j = 0; j < allHopefullyUniqueMeldinger.size(); j++) {
                if (i != j) {
                    final UrMelding melding2 = allHopefullyUniqueMeldinger.get(j);
                    assertNotEquals(melding1, melding2,
                            "Two UR meldinger expected to be different are equal" + System.lineSeparator() + System.lineSeparator() +
                                    "melding1:" + System.lineSeparator() +
                                    "=========" + System.lineSeparator() +
                                    melding1.toString() + System.lineSeparator() + System.lineSeparator() +
                                    "melding2:" + System.lineSeparator() +
                                    "========" + System.lineSeparator() +
                                    melding2.toString() + System.lineSeparator() + System.lineSeparator()
                    );
                }
            }
        }
    }

    private String likhetsgreier(UrMelding melding1) {
        return Stream.of(melding1.gjelderId, melding1.gjelderIdType, melding1.oppdragsKode, melding1.datoPostert, melding1.navEnhet())
                .map(Object::toString)
                .collect(joining(", "));
    }

    private UrMelding createMelding(
            final String gjelderId,
            final String behandlendeEnhet,
            final String gjelderIdType,
            final String oppdragsKode,
            final String datoPostert,
            final Random random) {

        final int totalRecordLength =
                Math.max(
                        AbstractMeldingBatchInputRecordBuilder.SUPER_FIELD_DEF.getUrRecordLength(),
                        OsMeldingBatchInputRecordBuilder.SUB_FIELD_DEF.getRecordLength()
                );

        return UrMeldingBuilder
                .newBuilder()
                .withMeldingBatchInputRecordBuilder(
                        UrMeldingBatchInputRecordBuilder
                                .newBuilder()
                                .withGjelderId(gjelderId) // Involved in equality
                                .withBehandlendeEnhet(behandlendeEnhet) // Involved in equality
                                .withDatoForStatus(AbstractMeldingTest.randomLocalDateTime(random))
                                .withNyesteVentestatus(AbstractMeldingTest.randomAlphanumeric(totalRecordLength, random))
                                .withBrukerId(AbstractMeldingTest.randomAlphanumeric(totalRecordLength, random))
                                .withTotaltNettoBelop(AbstractMeldingTest.randomNumeric(5, random) + "æ")
                                .withGjelderIdType(gjelderIdType) // Involved in equality
                                .withOppdragsKode(oppdragsKode) // Involved in equality
                                .withDatoPostert(datoPostert) // Involved in equality
                                .withKilde(AbstractMeldingTest.randomAlphanumeric(totalRecordLength, random))
                                .withBilagsId(AbstractMeldingTest.randomNumeric(totalRecordLength, random))
                                .withArsaksTekst(AbstractMeldingTest.randomAlphanumeric(totalRecordLength, random))
                                .withMottakerId(AbstractMeldingTest.randomNumeric(11, random))
                )
                .build();
    }
}
