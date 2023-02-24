package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MappingRegelTest {

  private static final Logger enteringTestHeaderLogger =
      LoggerFactory.getLogger("EnteringTestHeader");

  @Test
  void when_same_object_then_the_objects_equal() {

    enteringTestHeaderLogger.debug(null);

    final MappingRegel mappingRegel1 =
        new MappingRegel("X", "Y", "Z");
    final MappingRegel mappingRegel2 = mappingRegel1;

    assertEquals(mappingRegel1, mappingRegel2);
  }

  @Test
  void when_the_other_object_is_string_then_the_objects_differ() {

    enteringTestHeaderLogger.debug(null);

    final MappingRegel mappingRegel1 =
        new MappingRegel("X", "Y", "Z");

    assertNotEquals(mappingRegel1, "");
  }

  @Test
  void when_the_other_object_is_null_then_the_objects_differ() {

    enteringTestHeaderLogger.debug(null);

    final MappingRegel mappingRegel1 =
        new MappingRegel("X", "Y", "Z");

    assertNotEquals(mappingRegel1, null);
  }

  @Test
  void when_all_fields_are_equal_then_the_objects_are_equal() {

    enteringTestHeaderLogger.debug(null);

    final MappingRegel mappingRegel1 =
        new MappingRegel("X", "Y", "Z");
    final MappingRegel mappingRegel2 =
        new MappingRegel("X", "Y", "Z");

    assertEquals(mappingRegel1, mappingRegel2);
  }

  @Test
  void when_behandlingstema_differ_then_the_objects_differ() {

    enteringTestHeaderLogger.debug(null);

    final MappingRegel mappingRegel1 =
        new MappingRegel("X", "Y", "Z");
    final MappingRegel mappingRegel2 =
        new MappingRegel("XX", "Y", "Z");

    assertNotEquals(mappingRegel1, mappingRegel2);
  }

  @Test
  void when_behandlingstype_differ_then_the_objects_differ() {

    enteringTestHeaderLogger.debug(null);

    final MappingRegel mappingRegel1 =
        new MappingRegel("X", "Y", "Z");
    final MappingRegel mappingRegel2 =
        new MappingRegel("X", "YY", "Z");

    assertNotEquals(mappingRegel1, mappingRegel2);
  }

  @Test
  void when_ansvarligEnhetId_differ_then_the_objects_differ() {

    enteringTestHeaderLogger.debug(null);

    final MappingRegel mappingRegel1 =
        new MappingRegel("X", "Y", "Z");
    final MappingRegel mappingRegel2 =
        new MappingRegel("X", "Y", "ZZ");

    assertNotEquals(mappingRegel1, mappingRegel2);
  }
}
