package no.nav.okosynk.model;

import no.nav.okosynk.testutils.RandUt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OppgaveTest {

    private static final Random random = new Random(187610296876L);

    public static Oppgave.OppgaveBuilder generateRandomCompleteOppgaveBuilderInstance(final Random random) {

        return new Oppgave.OppgaveBuilder()
                .aktivFra(LocalDate.of(random.nextInt(2015) + 7, random.nextInt(12) + 1, random.nextInt(28) + 1))
                .aktivTil(LocalDate.of(random.nextInt(2015) + 11, random.nextInt(12) + 1, random.nextInt(28) + 1))
                .aktoerId(RandUt.constructRandomAlphaNumString(random.nextInt(13) + 1, random))
                .ansvarligEnhetId(RandUt.constructRandomAlphaNumString(random.nextInt(10) + 1, random))
                .ansvarligSaksbehandlerIdent(RandUt.constructRandomAlphaNumString(23, random))
                .antallMeldinger(random.nextInt(103))
                .behandlingstema(RandUt.constructRandomAlphaNumString(random.nextInt(7) + 1, random))
                .behandlingstype(RandUt.constructRandomAlphaNumString(random.nextInt(13) + 1, random))
                .beskrivelse(RandUt.constructRandomAlphaNumString(random.nextInt(219) + 1, random))
                .bnr(RandUt.constructRandomAlphaNumString(random.nextInt(11) + 1, random))
                .fagomradeKode(RandUt.constructRandomAlphaNumString(random.nextInt(17) + 1, random))
                .lest(RandUt.generateRandomBoolean(random))
                .mappeId(RandUt.constructRandomAlphaNumString(random.nextInt(19) + 1, random))
                .folkeregisterIdent(RandUt.constructRandomAlphaNumString(random.nextInt(11) + 1, random))
                .oppgaveId(RandUt.constructRandomAlphaNumString(random.nextInt(23) + 1, random))
                .oppgavetypeKode(RandUt.constructRandomAlphaNumString(random.nextInt(29) + 1, random))
                .orgnr(RandUt.constructRandomAlphaNumString(random.nextInt(9) + 1, random))
                .prioritetKode(RandUt.constructRandomAlphaNumString(random.nextInt(7) + 1, random))
                .samhandlernr(RandUt.constructRandomAlphaNumString(random.nextInt(31) + 1, random))
                .sistEndret(
                        LocalDateTime.of(
                                LocalDate.of(random.nextInt(2015) + 7, random.nextInt(12) + 1, random.nextInt(28) + 1),
                                LocalTime.of(random.nextInt(23) + 1, random.nextInt(59) + 1, random.nextInt(59) + 1, random.nextInt(999999999) + 1)
                        )
                )
                .versjon(1);
    }

    public static Stream<Arguments> provideEqualsRelatedValuesForOppgave() {
        return Stream.of(
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te2", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty3", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an2", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b2", "o1", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o2", "sh1",
                        false // equals
                ),
                Arguments.of(
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh2",
                        false // equals
                ),
                // -----------------------------------------------------------------------------------------------------
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId2", "folkeregisterIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent2", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId2", "folkeregisterIdent2", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te11", "ty2", "an1", null, "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te11", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of( // OK matrise
                        "te11", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te11", "ty2", "an1", null, "folkeregisterIdent1", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of( // OK matrise
                        "te11", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te11", "ty2", "an1", null, "folkeregisterIdent2", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", null, "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, "folkeregisterIdent1", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", null, "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, "folkeregisterIdent2", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent2", "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        true // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", null, "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, "folkeregisterIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", "aktoerId1", "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, "folkeregisterIdent1", "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", null, "folkeregisterIdent1", "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", "aktoerId1", null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", "aktoerId2", null, "b1", "o1", "sh1",
                        false // equals
                ),
                Arguments.of( // OK matrise
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        "te1", "ty2", "an1", null, null, "b1", "o1", "sh1",
                        true // equals
                )
        );
    }

    @Test
    void when_an_oppgave_is_compared_to_null_it_should_differ() {

        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .build();

        assertNotEquals(null, oppgave);
    }

    @Test
    void when_two_empty_oppgaver_are_compared_then_it_should_equal() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .build();

        assertEquals(oppgave1, oppgave2);
    }

    @Test
    void when_two_oppgaver_with_differing_ansvarligEnhetId_are_compared_then_they_should_differ() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .ansvarligEnhetId("APAPAPAPAPA")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .ansvarligEnhetId("yuyuyuyuuyuyuyu")
                        .build();

        assertNotEquals(oppgave1, oppgave2);
    }

    @Test
    void when_two_oppgaver_with_differing_behandlingstype_are_compared_then_they_should_differ() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .behandlingstype("OPOPOP")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .behandlingstype("nopnopnop")
                        .build();

        assertNotEquals(oppgave1, oppgave2);
    }

    @Test
    void when_two_oppgaver_with_differing_behandlingstema_are_compared_then_they_should_differ() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .behandlingstema("AIAIAI")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .behandlingstema("YESSSSSS")
                        .build();

        assertNotEquals(oppgave1, oppgave2);
    }

    @Test
    void when_two_oppgaver_with_differing_bnr_are_compared_then_they_should_differ() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .bnr("1237890")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .bnr("0890456")
                        .build();

        assertNotEquals(oppgave1, oppgave2);
    }

    @Test
    void when_two_oppgaver_with_differing_orgnr_are_compared_then_they_should_differ() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .orgnr("123789")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .orgnr("890456")
                        .build();

        assertNotEquals(oppgave1, oppgave2);
    }

    @Test
    void when_two_oppgaver_with_differing_folkeregisterIdent_are_compared_then_they_should_differ() {


        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .folkeregisterIdent("17023410293")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .folkeregisterIdent("17023410292")
                        .build();

        assertNotEquals(oppgave1, oppgave2);
    }

    @Test
    void when_two_oppgaver_with_differing_samhandlernr_are_compared_then_they_should_differ() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .samhandlernr("123")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .samhandlernr("456")
                        .build();

        assertNotEquals(oppgave1, oppgave2);
    }

    @Test
    void when_two_oppgaver_with_differing_aktoerId_are_compared_then_they_should_differ() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId("X")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId("Y")
                        .build();

        assertNotEquals(oppgave1, oppgave2);
    }

    @Test
    void when_only_one_aktoer_id_is_set_then_they_should_be_different() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId(null)
                        .folkeregisterIdent(null)
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId("Y")
                        .folkeregisterIdent(null)
                        .build();

        assertNotEquals(oppgave1, oppgave2);
    }

    @Test
    void when_only_one_fnr_is_set_then_they_should_be_different() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId(null)
                        .folkeregisterIdent(null)
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId(null)
                        .folkeregisterIdent("X")
                        .build();

        assertNotEquals(oppgave1, oppgave2);
    }

    @Test
    void when_one_oppgave_has_fnr_and_the_other_aktoer_then_they_should_be_different() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId(null)
                        .folkeregisterIdent("X")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId("Y")
                        .folkeregisterIdent(null)
                        .build();

        assertNotEquals(oppgave1, oppgave2);
    }

    @Test
    void when_no_fnr_and_no_aktoer_then_surprisingly_they_should_be_equal() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId(null)
                        .folkeregisterIdent(null)
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId(null)
                        .folkeregisterIdent(null)
                        .build();

        assertEquals(oppgave1, oppgave2);
    }


    @Test
    void when_two_oppgaver_with_no_differing_important_fields_are_compared_then_they_should_equal() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId("X")
                        .folkeregisterIdent("10293847563")
                        .samhandlernr("456")
                        .orgnr("890456")
                        .bnr("0890456")
                        .behandlingstema("YESSSSSS")
                        .behandlingstype("nopnopnop")
                        .ansvarligEnhetId("yuyuyuyuuyuyuyu")
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId("X")
                        .folkeregisterIdent("10293847563")
                        .samhandlernr("456")
                        .orgnr("890456")
                        .bnr("0890456")
                        .behandlingstema("YESSSSSS")
                        .behandlingstype("nopnopnop")
                        .ansvarligEnhetId("yuyuyuyuuyuyuyu")
                        .build();

        assertEquals(oppgave1, oppgave2);
    }

    @Test
    void when_two_oppgaver_with_no_differing_important_fields_and_some_differing_not_so_important_fields_are_compared_then_they_should_still_equal() {

        final Oppgave oppgave1 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId("X")
                        .folkeregisterIdent("10293847563")
                        .samhandlernr("456")
                        .orgnr("890456")
                        .bnr("0890456")
                        .behandlingstema("YESSSSSS")
                        .behandlingstype("nopnopnop")
                        .ansvarligEnhetId("yuyuyuyuuyuyuyu")
                        .oppgavetypeKode("GHHGHGHGHGHGHG")
                        .beskrivelse("ABC")
                        .aktivFra(LocalDate.now())
                        .aktivTil(LocalDate.now())
                        .lest(true)
                        .build();

        final Oppgave oppgave2 =
                new Oppgave.OppgaveBuilder()
                        .aktoerId("X")
                        .folkeregisterIdent("10293847563")
                        .samhandlernr("456")
                        .orgnr("890456")
                        .bnr("0890456")
                        .behandlingstema("YESSSSSS")
                        .behandlingstype("nopnopnop")
                        .ansvarligEnhetId("yuyuyuyuuyuyuyu")
                        .oppgavetypeKode("8g876g87g87g87g87g")
                        .beskrivelse("DEF")
                        .aktivFra(LocalDate.now().plusDays(1))
                        .aktivTil(LocalDate.now().plusDays(3))
                        .lest(false)
                        .build();

        assertEquals(oppgave1, oppgave2);
    }

    @Test
    void when_a_presentation_string_is_produced_from_an_oppgave_with_null_beskrivelse_then_it_should_not_fail() {

        final String expectedBeskrivelse = null;
        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .beskrivelse(expectedBeskrivelse)
                        .build();

        assertDoesNotThrow(oppgave::toString);
        final String presentationString = oppgave.toString();

        assertTrue(presentationString.contains("<null>"));
    }

    @Test
    void when_a_presentation_string_is_produced_from_an_oppgave_with_a_long_beskrivelse_then_it_should_be_presented_with_a_string_33_long() {

        final String originalBeskrivelse = "klmdscømsømdcølmsdløcmløsdmcølmsdølmvcølsdmølvmsdølmvølsdmølvmsølmvølsmølvmølsdmvømsdølvmøsdølvmsølmvølmsølvmølsmølvmsømvølsmdølvmsøldmvølmsdølvmølsdmøvlmsdølmvølsmølvmølsdmølvmsdlmvøsm";
        final String expectedBeskrivelse = originalBeskrivelse.substring(0, 30) + "...";
        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .beskrivelse(originalBeskrivelse)
                        .build();

        assertDoesNotThrow(oppgave::toString);
        final String presentationString = oppgave.toString();

        assertTrue(presentationString.contains(expectedBeskrivelse));
    }

    @Test
    void when_a_presentation_string_is_produced_from_an_oppgave_with_empty_beskrivelse_then_it_should_not_fail() {

        final String expectedBeskrivelse = "";
        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .beskrivelse(expectedBeskrivelse)
                        .build();

        assertDoesNotThrow(oppgave::toString);
    }

    @Test
    void when_a_presentation_string_is_produced_from_an_oppgave_then_it_should_contain_some_important_fields() {

        final LocalDate expectedAktivFra = LocalDate.now();
        final LocalDate expectedAktivTil = LocalDate.now().plusDays(1);
        final String expectedBeskrivelse = "xyz";
        final String expectedOppgavetypeKode = "pqr";
        final String expectedAnsvarligEnhetId = "lisdnjhpqr";
        final String expectedOppgaveId = "ABC";
        final String expectedAktoerId = "192837465";
        final String expectedFolkeregisterIdent = "10293847563";
        final String expectedSamhandlernr = "XYZabx999";
        final String expectedOrgnr = "111166666222234444";
        final String expectedBnr = "10102929383847475656";
        final String expectedBehandlingstype = "QPWOEIRURTY";

        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .aktivTil(expectedAktivTil)
                        .aktivFra(expectedAktivFra)
                        .beskrivelse(expectedBeskrivelse)
                        .oppgavetypeKode(expectedOppgavetypeKode)
                        .ansvarligEnhetId(expectedAnsvarligEnhetId)
                        .oppgaveId(expectedOppgaveId)
                        .folkeregisterIdent(expectedFolkeregisterIdent)
                        .aktoerId(expectedAktoerId)
                        .samhandlernr(expectedSamhandlernr)
                        .orgnr(expectedOrgnr)
                        .bnr(expectedBnr)
                        .behandlingstype(expectedBehandlingstype)
                        .build();

        final String presentationString = oppgave.toString();

        assertTrue(presentationString.contains(expectedAktivFra.toString()));
        assertTrue(presentationString.contains(expectedAktivTil.toString()));
        assertTrue(presentationString.contains(expectedBeskrivelse));
        assertTrue(presentationString.contains(expectedOppgavetypeKode));
        assertTrue(presentationString.contains(expectedAnsvarligEnhetId));
        assertTrue(presentationString.contains(expectedOppgaveId));
        assertTrue(presentationString.contains(expectedAktoerId));
        assertTrue(presentationString.contains(expectedFolkeregisterIdent));
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
            final String folkeregisterIdent_batch,
            final String bnr_batch,
            final String orgnr_batch,
            final String samhandlernr_batch,

            final String behandlingstema_db,
            final String behandlingstype_db,
            final String ansvarligEnhetId_db,
            final String aktoerId_db,
            final String folkeregisterIdent_db,
            final String bnr_db,
            final String orgnr_db,
            final String samhandlernr_db,

            final boolean shouldEqual
    ) {
        final Oppgave oppgave1 =
                OppgaveTest.generateRandomCompleteOppgaveBuilderInstance(OppgaveTest.random)
                        .behandlingstema(behandlingstema_batch)
                        .behandlingstype(behandlingstype_batch)
                        .ansvarligEnhetId(ansvarligEnhetId_batch)
                        .aktoerId(aktoerId_batch)
                        .folkeregisterIdent(folkeregisterIdent_batch)
                        .bnr(bnr_batch)
                        .orgnr(orgnr_batch)
                        .samhandlernr(samhandlernr_batch)
                        .build();

        final Oppgave oppgave2 =
                OppgaveTest.generateRandomCompleteOppgaveBuilderInstance(OppgaveTest.random)
                        .behandlingstema(behandlingstema_db)
                        .behandlingstype(behandlingstype_db)
                        .ansvarligEnhetId(ansvarligEnhetId_db)
                        .aktoerId(aktoerId_db)
                        .folkeregisterIdent(folkeregisterIdent_db)
                        .bnr(bnr_db)
                        .orgnr(orgnr_db)
                        .samhandlernr(samhandlernr_db)
                        .build();

        if (shouldEqual) {
            assertEquals(oppgave1, oppgave2, "equals");
            assertEquals(oppgave1.hashCode(), oppgave2.hashCode(), "hash");
        } else {
            assertNotEquals(oppgave1, oppgave2, "equals");
        }
    }
}
