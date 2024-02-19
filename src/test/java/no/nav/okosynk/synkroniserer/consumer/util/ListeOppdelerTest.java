package no.nav.okosynk.synkroniserer.consumer.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ListeOppdelerTest {

    @Test
    void kanDeleListeIMindreSublister() {

        final List<String> strings = Arrays.asList("Hei", "på", "deg", "din", "test");
        final List<List<String>> lister = ListeOppdeler.delOppListe(strings, 2);

        assertThat(lister).hasSize(3);
        assertEquals(Arrays.asList("Hei", "på"), lister.get(0));
        assertEquals(Arrays.asList("deg", "din"), lister.get(1));
        assertEquals(Collections.singletonList("test"), lister.get(2));
    }

    @Test
    void kanDeleListeILikeStorSubliste() {

        final List<String> strings = Arrays.asList("Hei", "på", "deg", "din", "test");
        final List<List<String>> lister = ListeOppdeler.delOppListe(strings, 5);

        assertEquals(1, lister.size());
        assertEquals(lister.get(0), strings);
    }

    @Test
    void sublisteBlirIkkeStorreEnnOriginalListe() {

        final List<String> strings = Arrays.asList("Hei", "på", "deg", "din", "test");
        final List<List<String>> lister = ListeOppdeler.delOppListe(strings, 10);

        assertEquals(1, lister.size());
        assertEquals(lister.get(0), strings);
    }

    @Test
    void listeOppdelerReturnererTomListeVedTomInputliste() {

        final List<List<String>> lister = ListeOppdeler.delOppListe(new ArrayList<>(), 99);

        Assertions.assertTrue(lister.isEmpty());
    }
}
