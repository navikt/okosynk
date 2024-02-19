package no.nav.okosynk.synkroniserer.consumer.util;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class ListeOppdeler {
    private ListeOppdeler() {
    }

    public static <T> List<List<T>> delOppListe(List<T> liste, int sublisteStorrelse) {
        final List<List<T>> subLister = new ArrayList<>();

        int startIndex = 0;
        int stoppIndex = min(sublisteStorrelse, liste.size());

        while (startIndex < liste.size()) {
            subLister.add(liste.subList(startIndex, stoppIndex));
            startIndex += sublisteStorrelse;
            stoppIndex = min(stoppIndex + sublisteStorrelse, liste.size());
        }

        return subLister;
    }
}
