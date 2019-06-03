package no.nav.okosynk.consumer.util;

import no.nav.okosynk.domain.Oppgave;

import java.util.ArrayList;
import java.util.List;

public class ListeOppdeler {

    public static List<List<Oppgave>> delOppListe(List<Oppgave> liste, int sublisteStorrelse) {
        final List<List<Oppgave>> subLister = new ArrayList<>();

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
