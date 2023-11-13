package no.nav.okosynk.synkroniserer;

import no.nav.okosynk.model.Oppgave;

import static org.apache.commons.lang3.StringUtils.substring;

public record OppgaveOppdatering(Oppgave oppgaveLestFraBatchen, Oppgave oppgaveLestFraDatabasen) {

    private static String createNewBeskrivelseFromOppgaveLestFraBatchenUpdatedWithAValueExtractedFromTheBeskrivelseOfOppgaveLestFraDatabasen(
            final String beskrivelseFromOppgaveLestFraBatchen,
            final String beskrivelseFromOppgaveLestFraDatabasen
    ) {
        // Ta vare på ti tegn av oppgavebeskrivelsen
        // lagt til av brukere fra Pesys. De 10 tegnene brukes til koder
        // som sier hvorfor de ikke har lukket oppgaven enda.
        final String[] beskrivelseFelter = beskrivelseFromOppgaveLestFraDatabasen.split(";");
        final String kode = beskrivelseFelter.length > 2
                        ?
                        substring(beskrivelseFelter[1], 0, 10)
                        :
                        "";

        return beskrivelseFromOppgaveLestFraBatchen.replaceFirst(";;", ";" + kode + ";");
    }

    Oppgave createOppgaveToBePatched() {
        return oppgaveLestFraDatabasen
                .toBuilder()
                .beskrivelse(
                        createNewBeskrivelseFromOppgaveLestFraBatchenUpdatedWithAValueExtractedFromTheBeskrivelseOfOppgaveLestFraDatabasen(
                                oppgaveLestFraBatchen.beskrivelse(),
                                oppgaveLestFraDatabasen.beskrivelse()
                        )
                )
                .build();
    }
}
