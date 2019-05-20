package no.nav.okosynk.consumer.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListeOppdelerTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    void kanDeleListeIMindreSublister() {

        enteringTestHeaderLogger.debug(null);

        final List<String> strings = Arrays.asList("Hei", "på", "deg", "din", "test");
        final List<List<String>> lister = ListeOppdeler.delOppListe(strings, 2);

        assertEquals(lister.size(), 3);
        assertEquals(lister.get(0), Arrays.asList("Hei", "på"));
        assertEquals(lister.get(1), Arrays.asList("deg", "din"));
        assertEquals(lister.get(2), Collections.singletonList("test"));
    }

    @Test
    void kanDeleListeILikeStorSubliste() {

        enteringTestHeaderLogger.debug(null);

        final List<String> strings = Arrays.asList("Hei", "på", "deg", "din", "test");
        final List<List<String>> lister = ListeOppdeler.delOppListe(strings, 5);

        assertEquals(lister.size(), 1);
        assertEquals(lister.get(0), strings);
    }

    @Test
    void sublisteBlirIkkeStorreEnnOriginalListe() {

        enteringTestHeaderLogger.debug(null);

        final List<String> strings = Arrays.asList("Hei", "på", "deg", "din", "test");
        final List<List<String>> lister = ListeOppdeler.delOppListe(strings, 10);

        assertEquals(lister.size(), 1);
        assertEquals(lister.get(0), strings);
    }

    @Test
    void listeOppdelerReturnererTomListeVedTomInputliste() {

        enteringTestHeaderLogger.debug(null);

        final List<List<String>> lister = ListeOppdeler.delOppListe(new ArrayList<String>(), 99);

        assert (lister.isEmpty());
    }
}
