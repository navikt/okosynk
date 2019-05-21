package no.nav.okosynk.batch;

import static org.apache.commons.lang3.StringUtils.substring;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.domain.Oppgave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Very much the heart and soul orchestrator of the okosynk batch application.
 */
public class OppgaveSynkroniserer {

    private static final Logger logger = LoggerFactory.getLogger(OppgaveSynkroniserer.class);

    private Supplier<BatchStatus>                   batchStatusSupplier;

    public OppgaveSynkroniserer(final Supplier<BatchStatus> batchStatusSupplier) {
        this.batchStatusSupplier= batchStatusSupplier;
    }

    public void synkroniser(
        final IOkosynkConfiguration okosynkConfiguration,
        final Collection<Oppgave>   alleOppgaverLestFraBatchen_parm,
        final String                bruker) {

        logger.info("Bruker {} forsøker å synkronisere {} oppgaver lest fra batch input.", bruker, alleOppgaverLestFraBatchen_parm.size());

        final Set<Oppgave> alleOppgaverLestFraBatchen = new HashSet<>(alleOppgaverLestFraBatchen_parm);

        final Set<Oppgave> oppgaverLestFraDatabasen   = new HashSet<>();
        final ConsumerStatistics consumerStatistics_finn = ConsumerStatistics.zero(); //oppgaveGateway.finnOppgaver(bruker, oppgaverLestFraDatabasen);

        final Set<Oppgave> oppgaverSomSkalFerdigstilles = finnOppgaverSomSkalFerdigstilles(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);
        final Set<OppgaveOppdatering> oppgaverSomSkalOppdateres = finnOppgaverSomSkalOppdateres(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);
        final Set<Oppgave> oppgaverSomSkalOpprettes = finnOppgaverSomSkalOpprettes(alleOppgaverLestFraBatchen, oppgaverLestFraDatabasen);

        final ConsumerStatistics consumerStatistics_ferdigstill = ferdigstillOppgaver(okosynkConfiguration, oppgaverSomSkalFerdigstilles, bruker);
        final ConsumerStatistics consumerStatistics_oppdater = oppdaterOppgaver(okosynkConfiguration, oppgaverSomSkalOppdateres, bruker);
        final ConsumerStatistics consumerStatistics_opprett = opprettOppgaver(okosynkConfiguration, oppgaverSomSkalOpprettes, bruker);

        final ConsumerStatistics consumerStatistics_accumulated =
            ConsumerStatistics.addAll(
                consumerStatistics_finn,
                consumerStatistics_ferdigstill,
                consumerStatistics_oppdater,
                consumerStatistics_opprett);

        loggAntallMeldingerMedOppgave(oppgaverSomSkalOppdateres, oppgaverSomSkalOpprettes);
        loggAccumulatedConsumerStatistics(consumerStatistics_accumulated);

        // return consumerStatistics_accumulated;
    }

    private void loggAntallMeldingerMedOppgave(
        final Set<OppgaveOppdatering> oppgaverSomSkalOppdateres,
        final Set<Oppgave> oppgaverSomSkalOpprettes) {

        final Integer antallMeldingerSomHarEnOppdaterOppgave = oppgaverSomSkalOppdateres.stream()
                .map(oppgaveOppdatering -> oppgaveOppdatering.oppgaveLestFraBatchen.antallMeldinger)
                .reduce((sum, i) -> sum + i)
                .orElse(0);

        final Integer antallMeldingerSomHarEnOpprettOppgave = oppgaverSomSkalOpprettes.stream()
                .map(oppgave -> oppgave.antallMeldinger)
                .reduce((sum, i) -> sum + i)
                .orElse(0);

        final int antallOppgaver = oppgaverSomSkalOppdateres.size() + oppgaverSomSkalOpprettes.size();
        final int antallMeldinger = antallMeldingerSomHarEnOppdaterOppgave + antallMeldingerSomHarEnOpprettOppgave;

        logger.info(
            "STATISTIKK: Etter synkronisering finnes det {} åpne oppgaver basert på {} meldinger.",
            antallOppgaver,
            antallMeldinger
        );
    }

    private void loggAccumulatedConsumerStatistics(final ConsumerStatistics accumulatedConsumerStatistics) {

        logger.info(
            "STATISTIKK: accumulatedConsumerStatistics etter synkronisering: {}",
            accumulatedConsumerStatistics
        );
    }

    ConsumerStatistics ferdigstillOppgaver(
        final IOkosynkConfiguration okosynkConfiguration,
        final Set<Oppgave>          oppgaver,
        final String                bruker) {

        final ConsumerStatistics consumerStatistics;
        if (batchErStoppet()) {
            logger.info("Batchen er stoppet, avslutter uten å ferdigstille oppgaver");
            consumerStatistics = ConsumerStatistics.zero();
        } else {
            final String oppgaveType = brukerTilOppgaveType(okosynkConfiguration, bruker);
            final Predicate<Oppgave> harUendretOppgaveType = oppgave -> Objects.equals(oppgave.oppgavetypeKode, oppgaveType);
            final Predicate<Oppgave> ikkeNyligEndret = oppgave -> oppgave.sistEndret.isBefore(LocalDateTime.now().minusHours(8));
            final Set<Oppgave> oppgaverSomSkalFerdigstilles = oppgaver.stream()
                    .filter(ikkeNyligEndret)
                    .filter(harUendretOppgaveType)
                    .collect(Collectors.toSet());

            if (oppgaverSomSkalFerdigstilles.isEmpty()) {
                logger.info("Bruker {} forsøker å ferdigstille oppgaver, men det er ingen oppgaver å ferdigstille.", bruker);
                consumerStatistics = ConsumerStatistics.zero();
            } else {
                logger.info("Bruker {} forsøker å ferdigstille {} oppgaver.", bruker, oppgaverSomSkalFerdigstilles.size());
                consumerStatistics = ConsumerStatistics.zero();//oppgaveBehandlingGateway.ferdigstillOppgaver(oppgaverSomSkalFerdigstilles);
                logger.info("Bruker {} har ferdigstilt {} oppgaver", bruker, oppgaverSomSkalFerdigstilles.size());
            }
        }

        return consumerStatistics;
    }

    private String brukerTilOppgaveType(
        final IOkosynkConfiguration okosynkConfiguration,
        final String                bruker) {

        final String osBruker = this.hentOsBatchBruker(okosynkConfiguration);
        final Constants.BATCH_TYPE batchType =
            bruker.equals(osBruker) ? Constants.BATCH_TYPE.OS : Constants.BATCH_TYPE.UR;

        return batchType.getOppgaveType();
    }

    ConsumerStatistics oppdaterOppgaver(
        final IOkosynkConfiguration   okosynkConfiguration,
        final Set<OppgaveOppdatering> oppgaveOppdateringer,
        final String                  bruker) {

        final ConsumerStatistics consumerStatistics;
        if (batchErStoppet()) {
            logger.info("Batchen er stoppet, avslutter uten å oppdatere oppgaver");
            consumerStatistics = ConsumerStatistics.zero();
        } else if (oppgaveOppdateringer.isEmpty()) {
            logger.info("Bruker {} forsøker å oppdatere oppgaver, men det er ingen oppgaver å oppdatere.", bruker);
            consumerStatistics = ConsumerStatistics.zero();
        } else {
            logger.info("Bruker {} forsøker å oppdatere {} oppgaver.", bruker, oppgaveOppdateringer.size());
            Set<Oppgave> oppdaterteOppgaver = oppgaveOppdateringer.stream().map(OppgaveOppdatering::oppdater).collect(Collectors.toSet());
            consumerStatistics = ConsumerStatistics.zero(); //oppgaveBehandlingGateway.oppdaterOppgaver(okosynkConfiguration, oppdaterteOppgaver);
            logger.info("Bruker {} har oppdatert {} oppgaver", bruker, oppgaveOppdateringer.size());
        }

        return consumerStatistics;
    }

    ConsumerStatistics opprettOppgaver(
        final IOkosynkConfiguration okosynkConfiguration,
        final Set<Oppgave>          oppgaver,
        final String                bruker) {

        final ConsumerStatistics consumerStatistics;
        if (batchErStoppet()) {
            logger.info("Batchen er stoppet, avslutter uten å opprette oppgaver");
            consumerStatistics = ConsumerStatistics.zero();
        } else if (oppgaver.isEmpty()) {
            logger.info("Bruker {} forsøker å opprette oppgaver, men det er ingen oppgaver å opprette.", bruker);
            consumerStatistics = ConsumerStatistics.zero();
        } else {
            logger.info("Bruker {} forsøker å opprette {} oppgaver.", bruker, oppgaver.size());
            consumerStatistics = ConsumerStatistics.zero(); //oppgaveBehandlingGateway.opprettOppgaver(okosynkConfiguration, oppgaver);
            logger.info("Bruker {} har opprettet {} oppgaver", bruker, oppgaver.size());
        }

        return consumerStatistics;
    }

    static Set<Oppgave> finnOppgaverSomSkalOpprettes(
        final Set<Oppgave> alleOppgaverLestFraBatchen,
        final Set<Oppgave> oppgaverLestFraDatabasen) {

        final Set<Oppgave> oppgaverSomSkalOpprettes = new HashSet<>(alleOppgaverLestFraBatchen);
        oppgaverSomSkalOpprettes.removeAll(oppgaverLestFraDatabasen);

        return oppgaverSomSkalOpprettes;
    }

    static Set<Oppgave> finnOppgaverSomSkalFerdigstilles(
        final Set<Oppgave> alleOppgaverLestFraBatchen,
        final Set<Oppgave> oppgaverLestFraDatabasen) {
        
        final Set<Oppgave> oppgaverSomSkalFerdigstilles = new HashSet<>(oppgaverLestFraDatabasen);
        oppgaverSomSkalFerdigstilles.removeAll(alleOppgaverLestFraBatchen);

        return oppgaverSomSkalFerdigstilles;
    }

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
                .map(oppgaveLestFraBatchen ->
                    new OppgaveOppdatering(
                        oppgaveLestFraBatchen,
                        oppgaverLestFraDatabasenMap.get(oppgaveLestFraBatchen)
                    )
                )
                .collect(Collectors.toSet());
    }

    private boolean batchErStoppet() {
        return BatchStatus.STOPPET.equals(batchStatusSupplier.get());
    }

    static class OppgaveOppdatering {

        private Oppgave oppgaveLestFraBatchen;
        private Oppgave oppgaveLestFraDatabasen;

        OppgaveOppdatering(
            final Oppgave oppgaveLestFraBatchen,
            final Oppgave oppgaveLestFraDatabasen) {

            this.oppgaveLestFraBatchen = oppgaveLestFraBatchen;
            this.oppgaveLestFraDatabasen = oppgaveLestFraDatabasen;
        }

        Oppgave oppdater() {
            return new Oppgave
                        .OppgaveBuilder()
                        .withSameValuesAs(oppgaveLestFraDatabasen)
                        .withBeskrivelse(oppdaterBeskrivelse())
                        .build();
        }

        private String oppdaterBeskrivelse() {
            //Ta vare på ti tegn av oppgavebeskrivelsen lagt til av brukere fra Pesys. De 10 tegnene brukes til koder
            // som sier hvorfor de ikke har lukket oppgaven enda.
            String[] beskrivelseFelter = oppgaveLestFraDatabasen.beskrivelse.split(";");
            String kode = beskrivelseFelter.length > 2 ? substring(beskrivelseFelter[1], 0, 10) : "";

            return oppgaveLestFraBatchen.beskrivelse.replaceFirst(";;", ";" + kode + ";");
        }
    }

    private static String hentOsBatchBruker(final IOkosynkConfiguration okosynkConfiguration) {

        final String batchBruker =
            okosynkConfiguration
                .getString(
                    Constants.BATCH_TYPE.OS.getBatchBrukerKey(),
                    Constants.BATCH_TYPE.OS.getBatchBrukerDefaultValue()
                );

        return batchBruker;
    }
}
