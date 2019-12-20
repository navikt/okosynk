package no.nav.okosynk.consumer.aktoer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AktoerResponsTest {

  private static final Logger enteringTestHeaderLogger =
      LoggerFactory.getLogger("EnteringTestHeader");

  @Test
  void when_creating_an_ok_object_then_the_id_should_be_set_but_not_feilmelding() {

    enteringTestHeaderLogger.debug(null);

    final String expectedAktoerId = "I1";
    final AktoerRespons aktoerRespons = AktoerRespons.ok(expectedAktoerId);

    assertEquals(expectedAktoerId, aktoerRespons.getAktoerId());
    assertEquals(null, aktoerRespons.getFeilmelding());
  }

  @Test
  void when_creating_a_feil_object_then_the_id_should_not_be_set_but_the_feilmelding() {

    enteringTestHeaderLogger.debug(null);

    final String expectedFeilmelding = "abc 123uuu.";
    final AktoerRespons aktoerRespons = AktoerRespons.feil(expectedFeilmelding);

    assertEquals(expectedFeilmelding, aktoerRespons.getFeilmelding());
    assertNull(aktoerRespons.getAktoerId());
  }
}
