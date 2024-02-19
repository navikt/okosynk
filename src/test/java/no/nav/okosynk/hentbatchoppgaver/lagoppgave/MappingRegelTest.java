package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.MappingRegel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MappingRegelTest {

    @Test
    void when_the_other_object_is_string_then_the_objects_differ() {

        final MappingRegel mappingRegel1 =
                new MappingRegel("X", "Y", "Z");

        assertThat(mappingRegel1).isNotNull();
    }

    @Test
    void when_the_other_object_is_null_then_the_objects_differ() {

        final MappingRegel mappingRegel1 =
                new MappingRegel("X", "Y", "Z");

        assertThat(mappingRegel1).isNotNull();
    }

    @Test
    void when_all_fields_are_equal_then_the_objects_are_equal() {

        final MappingRegel mappingRegel1 =
                new MappingRegel("X", "Y", "Z");
        final MappingRegel mappingRegel2 =
                new MappingRegel("X", "Y", "Z");

        assertEquals(mappingRegel1, mappingRegel2);
    }

    @Test
    void when_behandlingstema_differ_then_the_objects_differ() {

        final MappingRegel mappingRegel1 =
                new MappingRegel("X", "Y", "Z");
        final MappingRegel mappingRegel2 =
                new MappingRegel("XX", "Y", "Z");

        assertNotEquals(mappingRegel1, mappingRegel2);
    }

    @Test
    void when_behandlingstype_differ_then_the_objects_differ() {

        final MappingRegel mappingRegel1 =
                new MappingRegel("X", "Y", "Z");
        final MappingRegel mappingRegel2 =
                new MappingRegel("X", "YY", "Z");

        assertNotEquals(mappingRegel1, mappingRegel2);
    }

    @Test
    void when_ansvarligEnhetId_differ_then_the_objects_differ() {

        final MappingRegel mappingRegel1 =
                new MappingRegel("X", "Y", "Z");
        final MappingRegel mappingRegel2 =
                new MappingRegel("X", "Y", "ZZ");

        assertNotEquals(mappingRegel1, mappingRegel2);
    }
}
