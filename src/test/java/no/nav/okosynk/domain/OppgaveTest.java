package no.nav.okosynk.domain;

import no.nav.okosynk.testutils.RandUt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OppgaveTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private static final Random random = new Random(187610296876L);

    public static Oppgave.OppgaveBuilder generateRandomCompleteOppgaveBuilderInstance(final Random random) {

        final Oppgave.OppgaveBuilder oppgaveBuilder =
                new Oppgave.OppgaveBuilder()
                        .withAktivFra(LocalDate.of(random.nextInt(2015) + 7, random.nextInt(12) + 1, random.nextInt(28) + 1))
                        .withAktivTil(LocalDate.of(random.nextInt(2015) + 11, random.nextInt(12) + 1, random.nextInt(28) + 1))
                        .withAktoerId(RandUt.constructRandomAlphaNumString(random.nextInt(13) + 1, random))
                        .withAnsvarligEnhetId(RandUt.constructRandomAlphaNumString(random.nextInt(10) + 1, random))
                        .withAnsvarligSaksbehandlerIdent(RandUt.constructRandomAlphaNumString(23, random))
                        .withAntallMeldinger(random.nextInt(103))
                        .withBehandlingstema(RandUt.constructRandomAlphaNumString(random.nextInt(7) + 1, random))
                        .withBehandlingstype(RandUt.constructRandomAlphaNumString(random.nextInt(13) + 1, random))
                        .withBeskrivelse(RandUt.constructRandomAlphaNumString(random.nextInt(219) + 1, random))
                        .withBnr(RandUt.constructRandomAlphaNumString(random.nextInt(11) + 1, random))
                        .withFagomradeKode(RandUt.constructRandomAlphaNumString(random.nextInt(17) + 1, random))
                        .withLest(RandUt.generateRandomBoolean(random))
                        .withMappeId(RandUt.constructRandomAlphaNumString(random.nextInt(19) + 1, random))
                        .withNavPersonIdent(RandUt.constructRandomAlphaNumString(random.nextInt(11) + 1, random))
                        .withOppgaveId(RandUt.constructRandomAlphaNumString(random.nextInt(23) + 1, random))
                        .withOppgavetypeKode(RandUt.constructRandomAlphaNumString(random.nextInt(29) + 1, random))
                        .withOrgnr(RandUt.constructRandomAlphaNumString(random.nextInt(9) + 1, random))
                        .withPrioritetKode(RandUt.constructRandomAlphaNumString(random.nextInt(7) + 1, random))
                        .withSamhandlernr(RandUt.constructRandomAlphaNumString(random.nextInt(31) + 1, random))
                        .withSistEndret(
                                LocalDateTime.of(
                                        LocalDate.of(random.nextInt(2015) + 7, random.nextInt(12) + 1, random.nextInt(28) + 1),
                                        LocalTime.of(random.nextInt(23) + 1, random.nextInt(59) + 1, random.nextInt(59) + 1, random.nextInt(999999999) + 1)
                                )
                        )
                        .withVersjon(1);

        return oppgaveBuilder;
    }

    public static Stream<Arguments> provideEqualsRelatedValuesForOppgave() {
        return Stream.of(
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te2", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty3", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an2", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b2", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o2", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh2",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId2", "navPersonIdent1", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent2", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of(
                        "te11", "ty2", "an1", null, "navPersonIdent1", "b1", "o1", "sh1",
                        "te11", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of(
                        "te11", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te11", "ty2", "an1", null, "navPersonIdent1", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", null, "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, "navPersonIdent1", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent2", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", null, "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, "navPersonIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, "navPersonIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", null, "navPersonIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        true // equals
                )
        );
    }

    @Test
    void when_an_oppgave_is_compared_to_null_it_should_differ() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .build();

        assertFalse(oppgave.equals(null));
    }

    @Test
    void when_an_oppgave_is_compared_to_a_string_it_should_differ() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .build();

        assertFalse(oppgave.equals(""));
    }

    @Test
    void when_two_empty_oppgaver_are_compared_then_it_should_equal() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .build();

        assertTrue(oppgave1.equals(oppgave2));
    }

    @Test
    void when_an_oppgave_is_compared_to_itself_then_it_should_equal() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .build();

        assertTrue(oppgave.equals(oppgave));
    }

    @Test
    void when_two_oppgaver_with_differing_ansvarligEnhetId_are_compared_then_they_should_differ() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .withAnsvarligEnhetId("APAPAPAPAPA")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .withAnsvarligEnhetId("yuyuyuyuuyuyuyu")
                        .build();

        assertFalse(oppgave1.equals(oppgave2));
    }

    @Test
    void when_two_oppgaver_with_differing_behandlingstype_are_compared_then_they_should_differ() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .withBehandlingstype("OPOPOP")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .withBehandlingstype("nopnopnop")
                        .build();

        assertFalse(oppgave1.equals(oppgave2));
    }

    @Test
    void when_two_oppgaver_with_differing_behandlingstema_are_compared_then_they_should_differ() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .withBehandlingstema("AIAIAI")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .withBehandlingstema("YESSSSSS")
                        .build();

        assertFalse(oppgave1.equals(oppgave2));
    }

    @Test
    void when_two_oppgaver_with_differing_bnr_are_compared_then_they_should_differ() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .withBnr("1237890")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .withBnr("0890456")
                        .build();

        assertFalse(oppgave1.equals(oppgave2));
    }

    @Test
    void when_two_oppgaver_with_differing_orgnr_are_compared_then_they_should_differ() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .withOrgnr("123789")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .withOrgnr("890456")
                        .build();

        assertFalse(oppgave1.equals(oppgave2));
    }

    @Test
    void when_two_oppgaver_with_differing_navPersonIdent_are_compared_then_they_should_differ() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .withNavPersonIdent("17023410293")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .withNavPersonIdent("17023410292")
                        .build();

        assertFalse(oppgave1.equals(oppgave2));
    }

    @Test
    void when_two_oppgaver_with_differing_samhandlernr_are_compared_then_they_should_differ() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .withSamhandlernr("123")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .withSamhandlernr("456")
                        .build();

        assertFalse(oppgave1.equals(oppgave2));
    }

    @Test
    void when_two_oppgaver_with_differing_aktoerId_are_compared_then_they_should_differ() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .withAktoerId("X")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .withAktoerId("Y")
                        .build();

        assertFalse(oppgave1.equals(oppgave2));
    }

    @Test
    void when_two_oppgaver_with_no_differing_important_fields_are_compared_then_they_should_equal() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .withAktoerId("X")
                        .withNavPersonIdent("10293847563")
                        .withSamhandlernr("456")
                        .withOrgnr("890456")
                        .withBnr("0890456")
                        .withBehandlingstema("YESSSSSS")
                        .withBehandlingstype("nopnopnop")
                        .withAnsvarligEnhetId("yuyuyuyuuyuyuyu")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .withAktoerId("X")
                        .withNavPersonIdent("10293847563")
                        .withSamhandlernr("456")
                        .withOrgnr("890456")
                        .withBnr("0890456")
                        .withBehandlingstema("YESSSSSS")
                        .withBehandlingstype("nopnopnop")
                        .withAnsvarligEnhetId("yuyuyuyuuyuyuyu")
                        .build();

        assertTrue(oppgave1.equals(oppgave2));
    }

    @Test
    void when_two_oppgaver_with_no_differing_important_fields_and_some_differing_not_so_important_fields_are_compared_then_they_should_still_equal() {

        enteringTestHeaderLogger.debug(null);

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .withAktoerId("X")
                        .withNavPersonIdent("10293847563")
                        .withSamhandlernr("456")
                        .withOrgnr("890456")
                        .withBnr("0890456")
                        .withBehandlingstema("YESSSSSS")
                        .withBehandlingstype("nopnopnop")
                        .withAnsvarligEnhetId("yuyuyuyuuyuyuyu")

                        .withOppgavetypeKode("GHHGHGHGHGHGHG")
                        .withBeskrivelse("ABC")
                        .withAktivFra(LocalDate.now())
                        .withAktivTil(LocalDate.now())
                        .withLest(true)
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .withAktoerId("X")
                        .withNavPersonIdent("10293847563")
                        .withSamhandlernr("456")
                        .withOrgnr("890456")
                        .withBnr("0890456")
                        .withBehandlingstema("YESSSSSS")
                        .withBehandlingstype("nopnopnop")
                        .withAnsvarligEnhetId("yuyuyuyuuyuyuyu")

                        .withOppgavetypeKode("8g876g87g87g87g87g")
                        .withBeskrivelse("DEF")
                        .withAktivFra(LocalDate.now().plusDays(1))
                        .withAktivTil(LocalDate.now().plusDays(3))
                        .withLest(false)
                        .build();

        assertTrue(oppgave1.equals(oppgave2));
    }

    @Test
    void when_a_presentation_string_is_produced_from_an_oppgave_with_null_beskrivelse_then_it_should_not_fail() {

        enteringTestHeaderLogger.debug(null);

        final String expectedBeskrivelse = null;
        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .withBeskrivelse(expectedBeskrivelse)
                        .build();

        assertDoesNotThrow(() -> oppgave.toString());
        final String presentationString = oppgave.toString();

        assertTrue(presentationString.contains("<null>"));
    }

    @Test
    void when_a_presentation_string_is_produced_from_an_oppgave_with_a_long_beskrivelse_then_it_should_be_presented_with_a_string_33_long() {

        enteringTestHeaderLogger.debug(null);

        final String originalBeskrivelse = "klmdscømsømdcølmsdløcmløsdmcølmsdølmvcølsdmølvmsdølmvølsdmølvmsølmvølsmølvmølsdmvømsdølvmøsdølvmsølmvølmsølvmølsmølvmsømvølsmdølvmsøldmvølmsdølvmølsdmøvlmsdølmvølsmølvmølsdmølvmsdlmvøsm";
        final String expectedBeskrivelse = originalBeskrivelse.substring(0, 30) + "...";
        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .withBeskrivelse(originalBeskrivelse)
                        .build();

        assertDoesNotThrow(() -> oppgave.toString());
        final String presentationString = oppgave.toString();

        assertTrue(presentationString.contains(expectedBeskrivelse));
    }

    @Test
    void when_a_presentation_string_is_produced_from_an_oppgave_with_empty_beskrivelse_then_it_should_not_fail() {

        enteringTestHeaderLogger.debug(null);

        final String expectedBeskrivelse = "";
        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .withBeskrivelse(expectedBeskrivelse)
                        .build();

        assertDoesNotThrow(() -> oppgave.toString());
    }

    @Test
    void when_a_presentation_string_is_produced_from_an_oppgave_then_it_should_contain_some_important_fields() {

        enteringTestHeaderLogger.debug(null);

        final LocalDate expectedAktivFra = LocalDate.now();
        final LocalDate expectedAktivTil = LocalDate.now().plusDays(1);
        final String expectedBeskrivelse = "xyz";
        final String expectedOppgavetypeKode = "pqr";
        final String expectedAnsvarligEnhetId = "lisdnjhpqr";
        final String expectedOppgaveId = "ABC";
        final String expectedAktoerId = "192837465";
        final String expectedNavPersonIdent = "10293847563";
        final String expectedSamhandlernr = "XYZabx999";
        final String expectedOrgnr = "111166666222234444";
        final String expectedBnr = "10102929383847475656";
        final String expectedBehandlingstype = "QPWOEIRURTY";

        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .withAktivTil(expectedAktivTil)
                        .withAktivFra(expectedAktivFra)
                        .withBeskrivelse(expectedBeskrivelse)
                        .withOppgavetypeKode(expectedOppgavetypeKode)
                        .withAnsvarligEnhetId(expectedAnsvarligEnhetId)
                        .withOppgaveId(expectedOppgaveId)
                        .withNavPersonIdent(expectedNavPersonIdent)
                        .withAktoerId(expectedAktoerId)
                        .withSamhandlernr(expectedSamhandlernr)
                        .withOrgnr(expectedOrgnr)
                        .withBnr(expectedBnr)
                        .withBehandlingstype(expectedBehandlingstype)
                        .build();

        final String presentationString = oppgave.toString();

        assertTrue(presentationString.contains(expectedAktivFra.toString()));
        assertTrue(presentationString.contains(expectedAktivTil.toString()));
        assertTrue(presentationString.contains(expectedBeskrivelse));
        assertTrue(presentationString.contains(expectedOppgavetypeKode));
        assertTrue(presentationString.contains(expectedAnsvarligEnhetId));
        assertTrue(presentationString.contains(expectedOppgaveId));
        assertTrue(presentationString.contains(expectedAktoerId));
        assertTrue(presentationString.contains(expectedNavPersonIdent));
        assertTrue(presentationString.contains(expectedSamhandlernr));
        assertTrue(presentationString.contains(expectedOrgnr));
        assertTrue(presentationString.contains(expectedBnr));
        assertTrue(presentationString.contains(expectedBehandlingstype));
    }

    @ParameterizedTest
    @MethodSource("provideEqualsRelatedValuesForOppgave")
    void when_two_oppgaver_should_be_regarded_as_equal_then_equals_and_hash_methods_should_reflect_that_correctly(
            final String behandlingstema_batch,
            final String behandlingstype_batch,
            final String ansvarligEnhetId_batch,
            final String aktoerId_batch,
            final String navPersonIdent_batch,
            final String bnr_batch,
            final String orgnr_batch,
            final String samhandlernr_batch,

            final String behandlingstema_db,
            final String behandlingstype_db,
            final String ansvarligEnhetId_db,
            final String aktoerId_db,
            final String navPersonIdent_db,
            final String bnr_db,
            final String orgnr_db,
            final String samhandlernr_db,

            final boolean shouldEqual
    ) {
        final Oppgave oppgave1 =
                OppgaveTest.generateRandomCompleteOppgaveBuilderInstance(OppgaveTest.random)
                        .withBehandlingstema(behandlingstema_batch)
                        .withBehandlingstype(behandlingstype_batch)
                        .withAnsvarligEnhetId(ansvarligEnhetId_batch)
                        .withAktoerId(aktoerId_batch)
                        .withNavPersonIdent(navPersonIdent_batch)
                        .withBnr(bnr_batch)
                        .withOrgnr(orgnr_batch)
                        .withSamhandlernr(samhandlernr_batch)
                        .build();

        final Oppgave oppgave2 =
                OppgaveTest.generateRandomCompleteOppgaveBuilderInstance(OppgaveTest.random)
                        .withBehandlingstema(behandlingstema_db)
                        .withBehandlingstype(behandlingstype_db)
                        .withAnsvarligEnhetId(ansvarligEnhetId_db)
                        .withAktoerId(aktoerId_db)
                        .withNavPersonIdent(navPersonIdent_db)
                        .withBnr(bnr_db)
                        .withOrgnr(orgnr_db)
                        .withSamhandlernr(samhandlernr_db)
                        .build();

        if (shouldEqual) {
            assertTrue(Objects.equals(oppgave1, oppgave2), "equals");
            assertTrue(Objects.equals(oppgave1.hashCode(), oppgave2.hashCode()), "hash");
        } else {
            assertFalse(Objects.equals(oppgave1, oppgave2), "equals");
        }
    }
}
