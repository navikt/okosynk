package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AktoerIdentTest {

    @Test
    void when_setting_feilmelding_gruppe_then_the_same_value_should_be_obtained_when_getting_it() {

        final AktoerIdent aktoerIdent = new AktoerIdent();
        final String expectedFeilmelding = "Abc 67 +09777//.";
        aktoerIdent.setFeilmelding(expectedFeilmelding);
        assertEquals(expectedFeilmelding, aktoerIdent.getFeilmelding());
    }

    @Test
    void when_setting_identer_gruppe_then_the_same_values_should_be_obtained_when_getting_it() {

        final AktoerIdent aktoerIdent = new AktoerIdent();

        final List<AktoerIdentEntry> expectedIdentEntries = new ArrayList<>();

        final AktoerIdentEntry aktoerIdentEntry1 = new AktoerIdentEntry();
        aktoerIdentEntry1.setIdent("x1");
        aktoerIdentEntry1.setIdentgruppe("G1");
        aktoerIdentEntry1.setGjeldende(true);

        final AktoerIdentEntry aktoerIdentEntry2 = new AktoerIdentEntry();
        aktoerIdentEntry2.setIdent("x2");
        aktoerIdentEntry2.setIdentgruppe("G2");
        aktoerIdentEntry2.setGjeldende(false);

        expectedIdentEntries.add(aktoerIdentEntry1);
        expectedIdentEntries.add(aktoerIdentEntry2);

        aktoerIdent.setIdenter(expectedIdentEntries);

        assertEquals(expectedIdentEntries.size(), aktoerIdent.getIdenter().size());
        assertTrue(aktoerIdent.getIdenter().contains(aktoerIdentEntry1));
        assertTrue(aktoerIdent.getIdenter().contains(aktoerIdentEntry2));

        final AktoerIdentEntry aktoerIdentEntry3 = new AktoerIdentEntry();
        aktoerIdentEntry3.setIdent("x3");
        aktoerIdentEntry3.setIdentgruppe("G3");
        aktoerIdentEntry3.setGjeldende(false);

        assertFalse(aktoerIdent.getIdenter().contains(aktoerIdentEntry3));
    }
}
