package no.nav.okosynk.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OppgaveTest {

  private static final Logger enteringTestHeaderLogger =
      LoggerFactory.getLogger("EnteringTestHeader");

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
}
