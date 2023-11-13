package no.nav.okosynk.synkroniserer;

import no.nav.okosynk.model.Oppgave;
import no.nav.okosynk.synkroniserer.consumer.ConsumerStatistics;
import no.nav.okosynk.synkroniserer.consumer.oppgave.OppgaveRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Very much the heart and soul orchestrator of the okosynk batch application.
 */
public record OppgaveSynkroniserer(String username, OppgaveRestClient oppgaveRestClient) {
    private static final Logger logger = LoggerFactory.getLogger(OppgaveSynkroniserer.class);

    public ConsumerStatistics synkroniser(final Collection<Oppgave> alleOppgaverLestFraBatchenFuncParm)
            throws IOException {

        logger.info("Bruker {} forsøker å synkronisere {} oppgaver.", username, alleOppgaverLestFraBatchenFuncParm.size());

        final Set<Oppgave> alleOppgaverLestFraBatchen =
                new HashSet<>(alleOppgaverLestFraBatchenFuncParm);

        final Set<Oppgave> oppgaverLestFraDatabasen = new HashSet<>();
        final ConsumerStatistics consumerStatisticsFinn =
                oppgaveRestClient.finnOppgaver(oppgaverLestFraDatabasen);

        final Set<Oppgave> oppgaverSomSkalFerdigstilles =
                Util.finnOppgaverSomSkalFerdigstilles(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);
        logger.info("Fant {} åpne oppgaver som ikke samsvarer med innhold i batchfil. Disse blir ferdigstilt", oppgaverSomSkalFerdigstilles.size());
        final Set<OppgaveOppdatering> oppgaverSomSkalOppdateres =
                Util.finnOppgaverSomSkalOppdateres(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);
        logger.info("Fant {} åpne oppgaver som samsvarer med innhold i batchfil. Disse oppdateres", oppgaverSomSkalOppdateres.size());
        final Set<Oppgave> oppgaverSomSkalOpprettes =
                Util.finnOppgaverSomSkalOpprettes(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);
        logger.info("Fant {} oppgaver i batchfil som ikke samsvarer med åpne oppgaver. Oppretter nye oppgaver for disse", oppgaverSomSkalOpprettes.size());
        final ConsumerStatistics consumerStatisticsFerdigstill =
                ferdigstillOppgaver(oppgaverSomSkalFerdigstilles);
        final ConsumerStatistics consumerStatisticsOppdater =
                oppdaterOppgaver(oppgaverSomSkalOppdateres);
        final ConsumerStatistics consumerStatisticsOpprett =
                opprettOppgaver(oppgaverSomSkalOpprettes);

        //Kan skrives om til å basere seg på patch resultatene
        final ConsumerStatistics consumerStatisticsAccumulated =
                ConsumerStatistics.addAll(
                        consumerStatisticsFinn,
                        consumerStatisticsFerdigstill,
                        consumerStatisticsOppdater,
                        consumerStatisticsOpprett);

        Util.loggAntallMeldingerMedOppgave(oppgaverSomSkalOppdateres, oppgaverSomSkalOpprettes);

        return consumerStatisticsAccumulated;
    }

    ConsumerStatistics ferdigstillOppgaver(final Set<Oppgave> oppgaver) {

        final String consumerStatisticsName = oppgaveRestClient.getBatchType().getConsumerStatisticsName();
        final ConsumerStatistics consumerStatistics;
        final String oppgaveType = oppgaveRestClient.getBatchType().getOppgaveType();
        final Predicate<Oppgave> harUendretOppgaveType = oppgave -> Objects
                .equals(oppgave.oppgavetypeKode(), oppgaveType);
        final Predicate<Oppgave> ikkeNyligEndret = oppgave -> Optional.ofNullable(oppgave.sistEndret())
                .filter(d -> d.isBefore(LocalDateTime.now().minusHours(8)))
                .isPresent();
        final Set<Oppgave> oppgaverSomSkalFerdigstilles = oppgaver.stream()
                .filter(ikkeNyligEndret)
                .filter(harUendretOppgaveType)
                .collect(Collectors.toSet());

        if (oppgaverSomSkalFerdigstilles.isEmpty()) {
            logger.info(
                    "Bruker {} forsøker å ferdigstille oppgaver, men det er ingen oppgaver å ferdigstille.",
                    username);
            consumerStatistics = ConsumerStatistics.zero(consumerStatisticsName);
        } else {
            logger.info("Bruker {} forsøker å ferdigstille {} oppgaver.", username,
                    oppgaverSomSkalFerdigstilles.size());
            consumerStatistics = oppgaveRestClient
                    .patchOppgaver(oppgaverSomSkalFerdigstilles, true);
            logger.info("Bruker {} har ferdigstilt {} oppgaver", username,
                    consumerStatistics.getAntallOppgaverSomMedSikkerhetErFerdigstilt());
        }

        return consumerStatistics;
    }

    ConsumerStatistics oppdaterOppgaver(final Set<OppgaveOppdatering> oppgaveOppdateringer) {

        final String consumerStatisticsName =
                oppgaveRestClient.getBatchType().getConsumerStatisticsName();

        final ConsumerStatistics consumerStatistics;
        if (oppgaveOppdateringer.isEmpty()) {
            logger.info("Bruker {} forsøker å oppdatere oppgaver, men det er ingen oppgaver å oppdatere.", username);
            consumerStatistics = ConsumerStatistics.zero(consumerStatisticsName);
        } else {
            logger.info("Bruker {} forsøker å oppdatere {} oppgaver.", username, oppgaveOppdateringer.size());
            final Set<Oppgave> oppgaverToBePatched =
                    oppgaveOppdateringer
                            .stream()
                            .map(OppgaveOppdatering::createOppgaveToBePatched)
                            .collect(Collectors.toSet());
            consumerStatistics = oppgaveRestClient.patchOppgaver(oppgaverToBePatched, false);
            logger.info("Bruker {} har oppdatert {} oppgaver", username,
                    consumerStatistics.getAntallOppgaverSomMedSikkerhetErOppdatert());
        }

        return consumerStatistics;
    }

    ConsumerStatistics opprettOppgaver(final Set<Oppgave> oppgaver) throws IOException {

        final String consumerStatisticsName = oppgaveRestClient.getBatchType().getConsumerStatisticsName();

        final ConsumerStatistics consumerStatistics;
        if (oppgaver.isEmpty()) {
            logger.info("Bruker {} forsøker å opprette oppgaver, men det er ingen oppgaver å opprette.", username);
            consumerStatistics = ConsumerStatistics.zero(consumerStatisticsName);
        } else {
            logger.info("Bruker {} forsøker å opprette {} oppgaver.", username, oppgaver.size());
            consumerStatistics = oppgaveRestClient.opprettOppgaver(oppgaver);
            logger.info("Bruker {} har opprettet {} oppgaver", username,
                    consumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet());
        }

        return consumerStatistics;
    }

}
