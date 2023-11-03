package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AktoerResponsTest {

    @Test
    void when_creating_an_ok_object_then_the_id_should_be_set_but_not_feilmelding() {

        final String expectedAktoerId = "I1";
        final AktoerRespons aktoerRespons = AktoerRespons.ok(expectedAktoerId);

        assertEquals(expectedAktoerId, aktoerRespons.getAktoerId());
        assertNull(aktoerRespons.getFeilmelding());
    }

    @Test
    void when_creating_a_feil_object_then_the_id_should_not_be_set_but_the_feilmelding() {

        final String expectedFeilmelding = "abc 123uuu.";
        final AktoerRespons aktoerRespons = AktoerRespons.feil(expectedFeilmelding);

        assertEquals(expectedFeilmelding, aktoerRespons.getFeilmelding());
        assertNull(aktoerRespons.getAktoerId());
    }
}
