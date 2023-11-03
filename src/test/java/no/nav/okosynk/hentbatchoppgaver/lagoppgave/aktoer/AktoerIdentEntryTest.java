package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AktoerIdentEntryTest {

    @Test
    void when_setting_the_ident_then_the_same_value_should_be_obtained_when_getting_it() {

        final AktoerIdentEntry aktoerIdentEntry = new AktoerIdentEntry();
        final String expectedIdent = "abc123";
        aktoerIdentEntry.setIdent(expectedIdent);

        assertEquals(expectedIdent, aktoerIdentEntry.getIdent());
    }

    @Test
    void when_setting_ident_gruppe_then_the_same_value_should_be_obtained_when_getting_it() {

        final AktoerIdentEntry aktoerIdentEntry = new AktoerIdentEntry();
        final String expectedIdentgruppe = "123xyzABC-";
        aktoerIdentEntry.setIdentgruppe(expectedIdentgruppe);

        assertEquals(expectedIdentgruppe, aktoerIdentEntry.getIdentgruppe());
    }

    @Test
    void when_setting_gjeldende_then_the_same_value_should_be_obtained_when_getting_it() {

        final AktoerIdentEntry aktoerIdentEntry = new AktoerIdentEntry();
        final boolean expectedGjeldende_gjeldende = true;
        aktoerIdentEntry.setGjeldende(expectedGjeldende_gjeldende);

        assertEquals(expectedGjeldende_gjeldende, aktoerIdentEntry.isGjeldende());

        final boolean expectedGjeldende_not_gjeldende = false;
        aktoerIdentEntry.setGjeldende(expectedGjeldende_not_gjeldende);

        assertEquals(expectedGjeldende_not_gjeldende, aktoerIdentEntry.isGjeldende());
    }

    @Test
    void when_ident_is_equal_to_the_other_ident_then_the_objects_equal_and_not_otherwise() {

        final AktoerIdentEntry aktoerIdentEntry1a = new AktoerIdentEntry();
        aktoerIdentEntry1a.setIdent("x1");
        aktoerIdentEntry1a.setIdentgruppe("G1");
        aktoerIdentEntry1a.setGjeldende(true);

        assertThat(aktoerIdentEntry1a).isNotNull();
        assertThat(aktoerIdentEntry1a.toString()).isNotEmpty();

        final AktoerIdentEntry aktoerIdentEntry1b = new AktoerIdentEntry();
        aktoerIdentEntry1b.setIdent("x1");
        aktoerIdentEntry1b.setIdentgruppe("G1b");
        aktoerIdentEntry1b.setGjeldende(false);

        assertEquals(aktoerIdentEntry1a, aktoerIdentEntry1b);

        final AktoerIdentEntry aktoerIdentEntry2 = new AktoerIdentEntry();
        aktoerIdentEntry2.setIdent("x2");
        aktoerIdentEntry2.setIdentgruppe("G2");
        aktoerIdentEntry2.setGjeldende(false);

        assertNotEquals(aktoerIdentEntry1a, aktoerIdentEntry2);
        assertNotEquals(aktoerIdentEntry1b, aktoerIdentEntry2);

        final AktoerIdentEntry aktoerIdentEntry3 = new AktoerIdentEntry();
        aktoerIdentEntry2.setIdent(null);
        aktoerIdentEntry2.setIdentgruppe("G3");
        aktoerIdentEntry2.setGjeldende(false);

        assertNotEquals(aktoerIdentEntry3, aktoerIdentEntry3);
        assertNotEquals(aktoerIdentEntry1a, aktoerIdentEntry3);
        assertNotEquals(aktoerIdentEntry1b, aktoerIdentEntry3);
        assertNotEquals(aktoerIdentEntry2, aktoerIdentEntry3);

        final AktoerIdentEntry aktoerIdentEntry4 = new AktoerIdentEntry();
        aktoerIdentEntry2.setIdent(null);
        aktoerIdentEntry2.setIdentgruppe("G4");
        aktoerIdentEntry2.setGjeldende(true);

        assertNotEquals(aktoerIdentEntry3, aktoerIdentEntry4);
    }
}
