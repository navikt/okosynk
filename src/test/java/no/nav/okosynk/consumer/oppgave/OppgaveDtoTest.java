package no.nav.okosynk.consumer.oppgave;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class OppgaveDtoTest {

  @Test
  void when_instance_is_compared_to_null_then_the_result_should_be_unequal() {
    final OppgaveDto oppgaveDto = new OppgaveDto();
    assertFalse(oppgaveDto.equals(null));
  }

  @Test
  void when_instance_is_compared_to_itself_then_the_result_should_be_equal() {
    final OppgaveDto oppgaveDto = new OppgaveDto();
    assertTrue(oppgaveDto.equals(oppgaveDto));
  }

  @Test
  void when_instance_is_compared_to_a_subclass_then_the_result_should_be_unequal() {

    final OppgaveDto oppgaveDto1 = new OppgaveDto();
    final OppgaveDto oppgaveDto2 = new OppgaveDto() {{}};

    assertFalse(oppgaveDto1.equals(oppgaveDto2));
  }

  @Test
  void when_two_empty_instances_are_compared_then_the_result_should_be_equal() {

    final OppgaveDto oppgaveDto1 = new OppgaveDto();
    final OppgaveDto oppgaveDto2 = new OppgaveDto();

    assertTrue(oppgaveDto1.equals(oppgaveDto2));
  }

  @Test
  void when_selected_fields_differ_then_the_reult_should_be_unequal() {

    final OppgaveDto oppgaveDto1 = new OppgaveDto();
    final OppgaveDto oppgaveDto2 = new OppgaveDto();

    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setAktivDato("tull");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setAktivDato("tull");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setAktoerId("AB12");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setAktoerId("AB12");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setBehandlingstema("CD19");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setBehandlingstema("CD19");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setBehandlingstype("xy13");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setBehandlingstype("xy13");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setBeskrivelse("abc102");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setBeskrivelse("abc102");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setId("qpd99");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setId("qpd99");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setBnr("kl12ui");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setBnr("kl12ui");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setOppgavetype("ops0909");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setOppgavetype("ops0909");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setOrgnr("fikka13");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setOrgnr("fikka13");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setSamhandlernr("13io26");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setSamhandlernr("13io26");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setTema("1def987");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setTema("1def987");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setMappeId("ghi876");
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setMappeId("ghi876");
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setStatus(OppgaveStatus.OPPRETTET);
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setStatus(OppgaveStatus.OPPRETTET);
    assertTrue(oppgaveDto1.equals(oppgaveDto2));

    oppgaveDto1.setVersjon(13579);
    assertFalse(oppgaveDto1.equals(oppgaveDto2));
    oppgaveDto2.setVersjon(13579);
    assertTrue(oppgaveDto1.equals(oppgaveDto2));
  }

  @Test
  void when_x_then_y() {
  }
}