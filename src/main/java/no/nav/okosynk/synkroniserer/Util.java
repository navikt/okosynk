package no.nav.okosynk.synkroniserer;

import no.nav.okosynk.model.Oppgave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    private Util() {}

    /**
     * Extracts for creation
     *
     * @param alleOppgaverLestFraBatchen meldinger fra inputfil
     * @param oppgaverLestFraDatabasen   meldinger som allerede er i oppdragsystemet
     * @return oppgaver read frm the batch file that are not in the database
     */
    static Set<Oppgave> finnOppgaverSomSkalOpprettes(
            final Set<Oppgave> alleOppgaverLestFraBatchen,
            final Set<Oppgave> oppgaverLestFraDatabasen) {

        final Set<Oppgave> oppgaverSomSkalOpprettes = new HashSet<>(alleOppgaverLestFraBatchen);
        oppgaverSomSkalOpprettes.removeAll(oppgaverLestFraDatabasen);

        return oppgaverSomSkalOpprettes;
    }

    /**
     * Ekstraherer for ferdigstilling de som finnes i basen, men som ikke finnes i batchfila
     */
    static Set<Oppgave> finnOppgaverSomSkalFerdigstilles(
            final Set<Oppgave> alleOppgaverLestFraBatchen,
            final Set<Oppgave> oppgaverLestFraDatabasen) {

        final Set<Oppgave> oppgaverSomSkalFerdigstilles = new HashSet<>(oppgaverLestFraDatabasen);
        oppgaverSomSkalFerdigstilles.removeAll(alleOppgaverLestFraBatchen);

        return oppgaverSomSkalFerdigstilles;
    }

    /**
     * Ekstraherer for oppdatering de som er lest fra batchfila og som også finnes i databasen
     */
    static Set<OppgaveOppdatering> finnOppgaverSomSkalOppdateres(
            final Set<Oppgave> alleOppgaverLestFraBatchen,
            final Set<Oppgave> oppgaverLestFraDatabasen) {

        final Map<Oppgave, Oppgave> oppgaverLestFraDatabasenMap = new HashMap<>();
        oppgaverLestFraDatabasen
                .forEach(
                        oppgaveLestFraDatabasen ->
                                oppgaverLestFraDatabasenMap
                                        .put(oppgaveLestFraDatabasen, oppgaveLestFraDatabasen));

        return alleOppgaverLestFraBatchen
                .stream()
                .filter(oppgaverLestFraDatabasenMap::containsKey)
                .map(oppgaveLestFraBatchenOgSomFinnesIDatabasesn ->
                        new OppgaveOppdatering(
                                oppgaveLestFraBatchenOgSomFinnesIDatabasesn,
                                oppgaverLestFraDatabasenMap
                                        .get(oppgaveLestFraBatchenOgSomFinnesIDatabasesn)
                        )
                )
                .collect(Collectors.toSet());
    }

    static void loggAntallMeldingerMedOppgave(
            final Set<OppgaveOppdatering> oppgaverSomSkalOppdateres,
            final Set<Oppgave> oppgaverSomSkalOpprettes) {

        final Integer antallMeldingerSomHarEnOppdaterOppgave = oppgaverSomSkalOppdateres.stream()
                .map(OppgaveOppdatering::oppgaveLestFraBatchen)
                .map(o -> o.antallMeldinger)
                .reduce(Integer::sum)
                .orElse(0);

        final Integer antallMeldingerSomHarEnOpprettOppgave = oppgaverSomSkalOpprettes.stream()
                .map(oppgave -> oppgave.antallMeldinger)
                .reduce(Integer::sum)
                .orElse(0);

        final int antallOppgaver = oppgaverSomSkalOppdateres.size() + oppgaverSomSkalOpprettes.size();
        final int antallMeldinger = antallMeldingerSomHarEnOppdaterOppgave + antallMeldingerSomHarEnOpprettOppgave;

        logger.info(
                "STATISTIKK: Etter synkronisering finnes det {} åpne oppgaver basert på {} meldinger.",
                antallOppgaver,
                antallMeldinger
        );
    }
}
