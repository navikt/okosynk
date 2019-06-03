package no.nav.okosynk.consumer.util;

import java.util.ArrayList;
import java.util.List;

public class ListeOppdeler {

    public static <T> List<List<T>> delOppListe(List<T> liste, int sublisteStorrelse) {
        final List<List<T>> subLister = new ArrayList<>();

        int startIndex = 0;
        int stoppIndex = sublisteStorrelse < liste.size() ? sublisteStorrelse : liste.size();

        while (startIndex < liste.size()) {
            subLister.add(liste.subList(startIndex, stoppIndex));
            startIndex += sublisteStorrelse;
            stoppIndex = stoppIndex+sublisteStorrelse < liste.size() ? stoppIndex+sublisteStorrelse : liste.size();
        }

        return subLister;
    }
}
