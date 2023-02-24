package no.nav.okosynk.synkroniserer.consumer.oppgave.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class IdentJsonUnitTest {

    @Test
    void when_compared_to_an_almost_identical_class_then_they_should_not_equal() {

        final IdentJson identJson = new IdentJson();
        final IdentJson dentJsonVariant = new IdentJson() {{
        }};

        assertNotEquals(identJson, dentJsonVariant);
    }

    @Test
    void when_compared_to_itself_then_it_should_equal_itself() {

        final IdentJson identJson = new IdentJson();
        assertEquals(identJson, identJson);
    }

    @Test
    void when_two_different_instances_with_the_same_values_are_comared_then_they_should_equal() {

        final String expectedIdent = "mlkmlkmdvklmklm";
        final IdentGruppeV2 expectedIdentGruppeV2 = IdentGruppeV2.NPID;

        final IdentJson identJson1 = new IdentJson();
        identJson1.setIdent(expectedIdent);
        identJson1.setGruppe(expectedIdentGruppeV2);

        assertEquals(expectedIdent, identJson1.getIdent());
        assertEquals(expectedIdentGruppeV2, identJson1.getGruppe());
    }

    @Test
    void when_set_then_get_should_equal() {

        final String expectedIdent = "mlkmlkmdvklmklm";

        final IdentJson identJson1 = new IdentJson();
        identJson1.setIdent(expectedIdent);

        final IdentJson identJson2 = new IdentJson();
        identJson2.setIdent(expectedIdent);

        assertEquals(identJson1, identJson2);
    }

    @Test
    void when_created_with_values_then_get_should_equal() {

        final String expectedIdent = "mlkmlkmdvklmklm";
        final IdentGruppeV2 expectedIdentGruppeV2 = IdentGruppeV2.NPID;

        final IdentJson identJson1 = new IdentJson(expectedIdentGruppeV2, expectedIdent);
        assertEquals(expectedIdent, identJson1.getIdent());
        assertEquals(expectedIdentGruppeV2, identJson1.getGruppe());
    }
}
