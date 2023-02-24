package no.nav.okosynk.hentbatchoppgaver.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import no.nav.okosynk.hentbatchoppgaver.parselinje.AbstractMeldingBatchInputRecordBuilder;
import no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingBatchInputRecordBuilder;
import no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingBuilder;
import no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingTestGenerator;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.OsMeldingBatchInputRecordBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    void utledeGjelderType(String gjelderId, String expectedGjelderIdType, String inputMelding) {
        UrMelding urMelding = new UrMelding(inputMelding);
        final String gjelderIdType = urMelding.utledGjelderIdType();

        assertEquals(expectedGjelderIdType, gjelderIdType);
    }

    private static Stream<Arguments> getUrMeldingAndExpected() {
        return UrMeldingTestGenerator.urMeldingAndExpectedProvider();
    }

    @Test
    void equalsPaSammeObjektGirTrue() {

        enteringTestHeaderLogger.debug(null);

        UrMelding melding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());

        assertEquals(melding, melding);
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

        final Set<String> gjelderIder = new HashSet<>();
        gjelderIder.add("00154567893");
        gjelderIder.add("01874777894");

        final Set<String> behandlendeEnheter = new HashSet<>();
        behandlendeEnheter.add("4819");
        behandlendeEnheter.add("8020");

        final Set<String> gjelderIdTyper = new HashSet<>();
        gjelderIdTyper.add("PERSON");
        gjelderIdTyper.add("SAMHANDLER");
        gjelderIdTyper.add("ORGANISASJON");

        final Set<String> oppdragsKoder = new HashSet<>();
        oppdragsKoder.add("ANDRUTB");
        oppdragsKoder.add("BA");
        /*
        The following codes test OK,
        but they are removed of performance
        reasons. (Number of permutations)
        oppdragsKoder.add("GHBATCH");
        oppdragsKoder.add("GS");
        oppdragsKoder.add("INNT");
        oppdragsKoder.add("KORTTID");
        oppdragsKoder.add("KREDREF");
        oppdragsKoder.add("KS");
        oppdragsKoder.add("PEN");
        oppdragsKoder.add("REFARBG");
        oppdragsKoder.add("REFUTG");
        oppdragsKoder.add("SKATOPP");
        oppdragsKoder.add("SRPOST");
        oppdragsKoder.add("SUBATCH");
        oppdragsKoder.add("URKLUTB");
        oppdragsKoder.add("YSBATCH");
        */

        /*
        The following codes do not test ok om melding differences.
        That's probably OK.
        However, the equality method is somewhat complicated... :-)
        They are not in the ur_mapping_regler.properties,
        which means they are treated by the same "NAVEnhet".
        oppdragsKoder.add("BEBATCH");
        oppdragsKoder.add("BRBATCH");
        oppdragsKoder.add("PRED");
        */

        final Set<String> datoerPostert = new HashSet<>();
        datoerPostert.add("2011-01-21");
        datoerPostert.add("2015-03-17");

        final List<UrMelding> allHopefullyUniqueMeldinger = new ArrayList<>();

        final Random random = new Random(711);
        gjelderIder
            .forEach(
                gjelderId
                ->
                        behandlendeEnheter
                            .forEach(
                                behandlendeEnhet
                                ->
                                        gjelderIdTyper
                                            .forEach(
                                                gjelderIdType
                                                ->
                                                        oppdragsKoder
                                                            .forEach(
                                                                oppdragsKode
                                                                ->
                                                                        datoerPostert
                                                                            .forEach(
                                                                                datoPostert
                                                                                ->
                                                                                {
                                                                                    final UrMelding melding1 = createMelding(gjelderId, behandlendeEnhet, gjelderIdType, oppdragsKode, datoPostert, random);
                                                                                    final UrMelding melding2 = createMelding(gjelderId, behandlendeEnhet, gjelderIdType, oppdragsKode, datoPostert, random);
                                                                                    assertEquals(melding1, melding2, "In spite the fact that all equality involved fields are equal in the two messages, they are not equal.");
                                                                                    assertEquals(melding1.hashCode(), melding2.hashCode(), "The two messages are equal, but they do not produce the same hash. That's a Java contract breach.");
                                                                                    allHopefullyUniqueMeldinger.add(melding1);
                                                                                })
                                                            )
                                            )
                        )
            );

        // Test that all meldinger are different from all the others hopefully different ones:
        for (int i = 0; i < allHopefullyUniqueMeldinger.size(); i++) {
            final UrMelding melding1 = allHopefullyUniqueMeldinger.get(i);
            for (int j = 0; j < allHopefullyUniqueMeldinger.size(); j++) {
                if (i != j) {
                    final UrMelding melding2 = allHopefullyUniqueMeldinger.get(j);
                    assertNotEquals(melding1, melding2 ,
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
