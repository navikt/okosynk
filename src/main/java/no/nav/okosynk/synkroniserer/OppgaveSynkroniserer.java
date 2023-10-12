package no.nav.okosynk.synkroniserer;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.model.Oppgave;
import no.nav.okosynk.synkroniserer.consumer.ConsumerStatistics;
import no.nav.okosynk.synkroniserer.consumer.oppgave.OppgaveRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.substring;

/**
 * Very much the heart and soul orchestrator of the okosynk batch application.
 */
public class OppgaveSynkroniserer {

    private static final Logger logger =
            LoggerFactory.getLogger(OppgaveSynkroniserer.class);

    private final OkosynkConfiguration okosynkConfiguration;
    private final OppgaveRestClient oppgaveRestClient;

    public OppgaveSynkroniserer(
            final OkosynkConfiguration okosynkConfiguration,
            final OppgaveRestClient oppgaveRestClient) {

        this.okosynkConfiguration = okosynkConfiguration;
        this.oppgaveRestClient = oppgaveRestClient;
    }

    /**
     * Extracts for creation
     *
     * @param alleOppgaverLestFraBatchen meldinger fra inputfil
     * @param oppgaverLestFraDatabasen meldinger som allerede er i oppdragsystemet
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

    public ConsumerStatistics synkroniser(
            final Collection<Oppgave> alleOppgaverLestFraBatchenFuncParm
    ) throws IOException {
        final String bruker = okosynkConfiguration.getString(Constants.OPPGAVE_USERNAME);

        logger.info("Bruker {} forsøker å synkronisere {} oppgaver.", bruker,
                alleOppgaverLestFraBatchenFuncParm.size());

        final Set<Oppgave> alleOppgaverLestFraBatchen =
                new HashSet<>(alleOppgaverLestFraBatchenFuncParm);

        final Set<Oppgave> oppgaverLestFraDatabasen = new HashSet<>();
        final ConsumerStatistics consumerStatisticsFinn =
                getOppgaveRestClient().finnOppgaver(oppgaverLestFraDatabasen);

        final Set<Oppgave> oppgaverSomSkalFerdigstilles =
                finnOppgaverSomSkalFerdigstilles(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);
        logger.info("Fant {} åpne oppgaver som ikke samsvarer med innhold i batchfil. Disse blir ferdigstilt", oppgaverSomSkalFerdigstilles.size());
        final Set<OppgaveOppdatering> oppgaverSomSkalOppdateres =
                finnOppgaverSomSkalOppdateres(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);
        logger.info("Fant {} åpne oppgaver som samsvarer med innhold i batchfil. Disse oppdateres", oppgaverSomSkalOppdateres.size());
        final Set<Oppgave> oppgaverSomSkalOpprettes =
                finnOppgaverSomSkalOpprettes(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);
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

        loggAntallMeldingerMedOppgave(oppgaverSomSkalOppdateres, oppgaverSomSkalOpprettes);

        return consumerStatisticsAccumulated;
    }

    ConsumerStatistics ferdigstillOppgaver(final Set<Oppgave> oppgaver) {

        final String bruker = okosynkConfiguration.getString(Constants.OPPGAVE_USERNAME);
        final String consumerStatisticsName = getBatchType().getConsumerStatisticsName();
        final ConsumerStatistics consumerStatistics;
        final String oppgaveType = getBatchType().getOppgaveType();
        final Predicate<Oppgave> harUendretOppgaveType = oppgave -> Objects
                .equals(oppgave.oppgavetypeKode, oppgaveType);
        final Predicate<Oppgave> ikkeNyligEndret = oppgave -> Optional.ofNullable(oppgave.sistEndret)
                .filter(d -> d.isBefore(LocalDateTime.now().minusHours(8)))
                .isPresent();
        final Set<Oppgave> oppgaverSomSkalFerdigstilles = oppgaver.stream()
                .filter(ikkeNyligEndret)
                .filter(harUendretOppgaveType)
                .collect(Collectors.toSet());

        if (oppgaverSomSkalFerdigstilles.isEmpty()) {
            logger.info(
                    "Bruker {} forsøker å ferdigstille oppgaver, men det er ingen oppgaver å ferdigstille.",
                    bruker);
            consumerStatistics = ConsumerStatistics.zero(consumerStatisticsName);
        } else {
            logger.info("Bruker {} forsøker å ferdigstille {} oppgaver.", bruker,
                    oppgaverSomSkalFerdigstilles.size());
            consumerStatistics = getOppgaveRestClient()
                    .patchOppgaver(oppgaverSomSkalFerdigstilles, true);
            logger.info("Bruker {} har ferdigstilt {} oppgaver", bruker,
                    consumerStatistics.getAntallOppgaverSomMedSikkerhetErFerdigstilt());
        }

        return consumerStatistics;
    }

    ConsumerStatistics oppdaterOppgaver(final Set<OppgaveOppdatering> oppgaveOppdateringer) {

        final String bruker = okosynkConfiguration.getString(Constants.OPPGAVE_USERNAME);
        final String consumerStatisticsName =
                getBatchType().getConsumerStatisticsName();

        final ConsumerStatistics consumerStatistics;
        if (oppgaveOppdateringer.isEmpty()) {
            logger.info("Bruker {} forsøker å oppdatere oppgaver, men det er ingen oppgaver å oppdatere.",
                    bruker);
            consumerStatistics = ConsumerStatistics.zero(consumerStatisticsName);
        } else {
            logger.info("Bruker {} forsøker å oppdatere {} oppgaver.", bruker, oppgaveOppdateringer.size());
            final Set<Oppgave> oppgaverToBePatched =
                    oppgaveOppdateringer
                            .stream()
                            .map(OppgaveOppdatering::createOppgaveToBePatched)
                            .collect(Collectors.toSet());
            consumerStatistics = getOppgaveRestClient().patchOppgaver(oppgaverToBePatched, false);
            logger.info("Bruker {} har oppdatert {} oppgaver", bruker,
                    consumerStatistics.getAntallOppgaverSomMedSikkerhetErOppdatert());
        }

        return consumerStatistics;
    }

    ConsumerStatistics opprettOppgaver(final Set<Oppgave> oppgaver) throws IOException {

        final String bruker = okosynkConfiguration.getString(Constants.OPPGAVE_USERNAME);
        final String consumerStatisticsName =
                getBatchType().getConsumerStatisticsName();

        final ConsumerStatistics consumerStatistics;
        if (oppgaver.isEmpty()) {
            logger.info("Bruker {} forsøker å opprette oppgaver, men det er ingen oppgaver å opprette.",
                    bruker);
            consumerStatistics = ConsumerStatistics.zero(consumerStatisticsName);
        } else {
            logger.info("Bruker {} forsøker å opprette {} oppgaver.", bruker, oppgaver.size());
            consumerStatistics = getOppgaveRestClient().opprettOppgaver(oppgaver);
            logger.info("Bruker {} har opprettet {} oppgaver", bruker,
                    consumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet());
        }

        return consumerStatistics;
    }

    private void loggAntallMeldingerMedOppgave(
            final Set<OppgaveOppdatering> oppgaverSomSkalOppdateres,
            final Set<Oppgave> oppgaverSomSkalOpprettes) {

        final Integer antallMeldingerSomHarEnOppdaterOppgave = oppgaverSomSkalOppdateres.stream()
                .map(oppgaveOppdatering -> oppgaveOppdatering.oppgaveLestFraBatchen.antallMeldinger)
                .reduce(Integer::sum)
                .orElse(0);

        final Integer antallMeldingerSomHarEnOpprettOppgave = oppgaverSomSkalOpprettes.stream()
                .map(oppgave -> oppgave.antallMeldinger)
                .reduce(Integer::sum)
                .orElse(0);

        final int antallOppgaver = oppgaverSomSkalOppdateres.size() + oppgaverSomSkalOpprettes.size();
        final int antallMeldinger =
                antallMeldingerSomHarEnOppdaterOppgave + antallMeldingerSomHarEnOpprettOppgave;

        logger.info(
                "STATISTIKK: Etter synkronisering finnes det {} åpne oppgaver basert på {} meldinger.",
                antallOppgaver,
                antallMeldinger
        );
    }

    private OppgaveRestClient getOppgaveRestClient() {
        return this.oppgaveRestClient;
    }

    private Constants.BATCH_TYPE getBatchType() {
        return getOppgaveRestClient().getBatchType();
    }

    static class OppgaveOppdatering {

        private final Oppgave oppgaveLestFraBatchen;
        private final Oppgave oppgaveLestFraDatabasen;

        OppgaveOppdatering(
                final Oppgave oppgaveLestFraBatchen,
                final Oppgave oppgaveLestFraDatabasen) {

            this.oppgaveLestFraBatchen = oppgaveLestFraBatchen;
            this.oppgaveLestFraDatabasen = oppgaveLestFraDatabasen;
        }

        private static String createNewBeskrivelseFromOppgaveLestFraBatchenUpdatedWithAValueExtractedFromTheBeskrivelseOfOppgaveLestFraDatabasen(
                final String beskrivelseFromOppgaveLestFraBatchen,
                final String beskrivelseFromOppgaveLestFraDatabasen
        ) {
            // Ta vare på ti tegn av oppgavebeskrivelsen
            // lagt til av brukere fra Pesys. De 10 tegnene brukes til koder
            // som sier hvorfor de ikke har lukket oppgaven enda.
            final String[] beskrivelseFelter =
                    beskrivelseFromOppgaveLestFraDatabasen.split(";");
            final String kode =
                    beskrivelseFelter.length > 2
                            ?
                            substring(beskrivelseFelter[1], 0, 10)
                            :
                            "";

            return beskrivelseFromOppgaveLestFraBatchen.replaceFirst(";;", ";" + kode + ";");
        }

        Oppgave createOppgaveToBePatched() {
            return new Oppgave
                    .OppgaveBuilder()
                    .withSameValuesAs(oppgaveLestFraDatabasen)
                    .withBeskrivelse(
                            createNewBeskrivelseFromOppgaveLestFraBatchenUpdatedWithAValueExtractedFromTheBeskrivelseOfOppgaveLestFraDatabasen(
                                    oppgaveLestFraBatchen.beskrivelse,
                                    oppgaveLestFraDatabasen.beskrivelse
                            )
                    )
                    .build();
        }
    }
}
