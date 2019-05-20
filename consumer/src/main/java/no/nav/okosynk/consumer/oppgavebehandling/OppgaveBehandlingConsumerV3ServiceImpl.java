package no.nav.okosynk.consumer.oppgavebehandling;

import static java.util.stream.Collectors.toList;
import static java.lang.Integer.parseInt;

import java.util.Collection;
import java.util.List;

import no.nav.okosynk.consumer.AbstractConsumerV3ServiceImpl;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.domain.Oppgave;
import no.nav.okosynk.consumer.util.ListeOppdeler;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppgaveBehandlingConsumerV3ServiceImpl
    extends AbstractConsumerV3ServiceImpl
    implements IOppgaveBehandlingConsumerGateway {

    private static final Logger logger = LoggerFactory.getLogger(OppgaveBehandlingConsumerV3ServiceImpl.class);

    private static final int ENHET_ID_FOR_ANDRE_EKSTERNE                        = 9999;
            static final int MAX_ANTALL_OPPGAVER_FOR_FERDIGSTILLING_PER_REQUEST = 1000;

    private final OppgavebehandlingV3   oppgavebehandlingV3;

    // ========================================================================

    public OppgaveBehandlingConsumerV3ServiceImpl(
        final IOkosynkConfiguration okosynkConfiguration,
        final OppgavebehandlingV3   oppgavebehandlingV3) {

        super(okosynkConfiguration);
        this.oppgavebehandlingV3  = oppgavebehandlingV3;
    }

    // ========================================================================
    // === BEGIN opprett related =======
    @Override
    public ConsumerStatistics opprettOppgaver(
        final IOkosynkConfiguration okosynkConfiguration,
        final Collection<Oppgave>   oppgaver) {

        final int maxAntallOppgaverForOpprettingPerRequest =
            getMaxAntallOppgaverForOpprettingPerRequest(okosynkConfiguration);

        final List<List<WSOpprettOppgave>> oppgaveLister =
            ListeOppdeler
                .delOppListe(
                    oppgaver.stream()
                        .map(OppgaveTilWSOpprettOppgaveMapper::lagWSOpprettOppgave)
                        .collect(toList()),
                    maxAntallOppgaverForOpprettingPerRequest);

        int totaltAntallOppgaverSomMedSikkerhetErOpprettet = 0;
        int totaltAntallOppgaverSomKanVaereOpprettet = 0;
        int numberOfExceptionReceivedDuringRun = 0;

        for (int i = 0; i < oppgaveLister.size(); i++) {

            final List<WSOpprettOppgave> oppgaveListe = oppgaveLister.get(i);
            logger.info("Oppretter oppgaveliste {} av {}.", i + 1, oppgaveLister.size());
            final WSOpprettOppgaveBolkRequest request =
                new WSOpprettOppgaveBolkRequest()
                    .withOpprettOppgaveListe(oppgaveListe)
                    .withOpprettetAvEnhetId(ENHET_ID_FOR_ANDRE_EKSTERNE);

            try {
                final WSOpprettOppgaveBolkResponse response =
                    oppgavebehandlingV3.opprettOppgaveBolk(request);
                final int antallOppgaverSikkertOpprettet = response.getOppgaveIdListe().size();
                totaltAntallOppgaverSomMedSikkerhetErOpprettet += antallOppgaverSikkertOpprettet;
                logger.info("Opprettet {} nye oppgaver.", antallOppgaverSikkertOpprettet);
            } catch (Throwable e) {
                logger.error("Én eller flere oppgaver er ikke opprettet som følge av en feil underveis. Fortsetter med ny(e) bolk(er)...", e);
                totaltAntallOppgaverSomKanVaereOpprettet += oppgaveListe.size();
                numberOfExceptionReceivedDuringRun++;
            }
        }

        final ConsumerStatistics consumerStatistics =
            ConsumerStatistics
                .builder()
                .antallOppgaverSomMedSikkerhetErOpprettet(totaltAntallOppgaverSomMedSikkerhetErOpprettet)
                .antallOppgaverSomKanVaereOpprettet(totaltAntallOppgaverSomKanVaereOpprettet)
                .numberOfExceptionReceivedDuringRun(numberOfExceptionReceivedDuringRun)
                .build();

        return consumerStatistics;
    }

    private int getMaxAntallOppgaverForOpprettingPerRequest(
        final IOkosynkConfiguration okosynkConfiguration) {

        final int maxAntallOppgaverForOpprettingPerRequest =
            parseInt(
                okosynkConfiguration
                    .getString(
                        Constants
                            .CONSUMER_TYPE.OPPGAVE_BEHANDLING
                            .getBulkSizeMaxForCreate()
                        , Constants
                            .CONSUMER_TYPE.OPPGAVE_BEHANDLING
                            .getBulkSizeDefaultForCreate()
                    )
            );

        return maxAntallOppgaverForOpprettingPerRequest;
    }
    // === END   opprett related =======
    // ========================================================================
    // === BEGIN oppdater related =======
    @Override
    public ConsumerStatistics oppdaterOppgaver(
        final IOkosynkConfiguration okosynkConfiguration,
        final Collection<Oppgave>   oppgaver) {

        final int maxAntallOppgaverForOppdateringPerRequest =
            getMaxAntallOppgaverForOppdateringPerRequest(okosynkConfiguration);

        final List<List<WSEndreOppgave>> oppgaveLister =
            ListeOppdeler
                .delOppListe(
                    oppgaver
                        .stream()
                        .map(OppgaveTilWSEndreOppgaveMapper::lagWSEndreOppgave)
                        .collect(toList()),
                    maxAntallOppgaverForOppdateringPerRequest);

        int totaltAntallOppgaverSomMedSikkerhetErOppdatert = 0;
        int totaltAntallOppgaverSomMedSikkerhetIkkeErOppdatert = 0;
        int totaltAntallOppgaverSomKanVaereOppdatert = 0;
        int numberOfExceptionReceivedDuringRun = 0;

        for (int i = 0; i < oppgaveLister.size(); i++) {
            final List<WSEndreOppgave> oppgaveListe = oppgaveLister.get(i);

            logger.info("Oppdaterer oppgaveliste {} av {}.", i + 1, oppgaveLister.size());

            final WSLagreOppgaveBolkRequest request = new WSLagreOppgaveBolkRequest()
                    .withEndreOppgaveListe(oppgaveListe)
                    .withEndretAvEnhetId(ENHET_ID_FOR_ANDRE_EKSTERNE);

            final WSLagreOppgaveBolkResponse response;
            try {
                response = oppgavebehandlingV3.lagreOppgaveBolk(request);
                final int antallOppgaverSomKanVaereOppdatert = oppgaveListe.size();
                logOppgaverSomIkkeBleOppdatert(response, antallOppgaverSomKanVaereOppdatert);
                final int antallOppgaverSomIkkeErOppdatert = response.getFeilListe().size();
                final int antallOppgaverSomErOppdatert =
                    antallOppgaverSomKanVaereOppdatert - antallOppgaverSomIkkeErOppdatert;
                totaltAntallOppgaverSomMedSikkerhetErOppdatert += antallOppgaverSomErOppdatert;
                totaltAntallOppgaverSomMedSikkerhetIkkeErOppdatert += antallOppgaverSomIkkeErOppdatert;
            } catch (Throwable e) {
                logger.error("Én eller flere oppgaver er ikke oppdatert som følge av en feil underveis. Fortsetter med ny(e) bolk(er)...", e);
                totaltAntallOppgaverSomKanVaereOppdatert += oppgaveListe.size();
                numberOfExceptionReceivedDuringRun++;
            }
        }

        final ConsumerStatistics consumerStatistics =
            ConsumerStatistics
                .builder()
                .antallOppgaverSomMedSikkerhetErOppdatert(totaltAntallOppgaverSomMedSikkerhetErOppdatert)
                .antallOppgaverSomKanVaereOppdatert(totaltAntallOppgaverSomKanVaereOppdatert)
                .antallOppgaverSomMedSikkerhetIkkeErOppdatert(totaltAntallOppgaverSomMedSikkerhetIkkeErOppdatert)
                .numberOfExceptionReceivedDuringRun(numberOfExceptionReceivedDuringRun)
                .build();

        return consumerStatistics;
    }

    private void logOppgaverSomIkkeBleOppdatert(
        final WSLagreOppgaveBolkResponse response,
        final int antallOppgaverSomKanVaereOppdatert) {

        final int antallOppgaverSomIkkeErOppdatert =
            response.getFeilListe().size();
        final int antallOppgaverSomErOppdatert =
            antallOppgaverSomKanVaereOppdatert - antallOppgaverSomIkkeErOppdatert;

        logger.info("Oppdatering av {} oppgaver var vellykket.", antallOppgaverSomErOppdatert);
        logger.info("Oppdatering av {} oppgaver var ikke vellykket.", antallOppgaverSomIkkeErOppdatert);
        if (!response.isTransaksjonOk()) {
            response
                .getFeilListe()
                .forEach(
                    bolkfeil ->
                        logger.error("Oppdatering av oppgave med id {} feilet. Feilkode {}: {}",
                            bolkfeil.getOppgaveId(),
                            bolkfeil.getFeilKode(),
                            bolkfeil.getFeilBeskrivelse()
                        )
                );
        }
    }

    private int getMaxAntallOppgaverForOppdateringPerRequest(
        final IOkosynkConfiguration okosynkConfiguration) {

        final int maxAntallOppgaverForOppdateringPerRequest =
            parseInt(
                okosynkConfiguration
                    .getString(
                        Constants
                            .CONSUMER_TYPE.OPPGAVE_BEHANDLING
                            .getBulkSizeMaxForUpdate()
                        , Constants
                            .CONSUMER_TYPE.OPPGAVE_BEHANDLING
                            .getBulkSizeDefaultForUpdate()
                    )
            );

        return maxAntallOppgaverForOppdateringPerRequest;
    }
    // === END   oppdater related =======
    // ========================================================================
    // === BEGIN ferdigstill related =======
    @Override
    public ConsumerStatistics ferdigstillOppgaver(final Collection<Oppgave> oppgaver) {

        final List<List<String>> oppgaveIdLister =
            ListeOppdeler
                .delOppListe(
                    oppgaver.stream()
                        .map(oppgave -> oppgave.oppgaveId)
                        .collect(toList()),
                    MAX_ANTALL_OPPGAVER_FOR_FERDIGSTILLING_PER_REQUEST);

        int totaltAntallOppgaverSomMedSikkerhetErFerdigstilt = 0;
        int totaltAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt = 0;
        int totaltAntallOppgaverSomKanVaereFerdigstilt = 0;
        int numberOfExceptionReceivedDuringRun = 0;

        for (int i = 0; i < oppgaveIdLister.size(); i++) {
            final List<String> oppgaveIdListe = oppgaveIdLister.get(i);
            logger.info("Ferdigstiller oppgaveliste {} av {}.", i + 1, oppgaveIdLister.size());
            final WSFerdigstillOppgaveBolkRequest request =
                new WSFerdigstillOppgaveBolkRequest()
                    .withOppgaveIdListe(oppgaveIdListe)
                    .withFerdigstiltAvEnhetId(ENHET_ID_FOR_ANDRE_EKSTERNE);

            final WSFerdigstillOppgaveBolkResponse response;
            try {
                response = oppgavebehandlingV3.ferdigstillOppgaveBolk(request);

                final int antallOppgaverSomKanVaereFerdigsilt = oppgaveIdListe.size();
                logOppgaverSomIkkeBleFerdigstilt(response, antallOppgaverSomKanVaereFerdigsilt);
                final int antallOppgaverSomIkkeErFerdigstilt = response.getFeilListe().size();
                final int antallOppgaverSomErFerdigstilt =
                    antallOppgaverSomKanVaereFerdigsilt - antallOppgaverSomIkkeErFerdigstilt;
                totaltAntallOppgaverSomMedSikkerhetErFerdigstilt += antallOppgaverSomErFerdigstilt;
                totaltAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt += antallOppgaverSomIkkeErFerdigstilt;
            } catch (Throwable e) {
                logger.error("Én eller flere oppgaver er ikke ferdigstilt som følge av en feil underveis. Fortsetter med ny(e) bolk(er)...", e);
                totaltAntallOppgaverSomKanVaereFerdigstilt += oppgaveIdListe.size();
                numberOfExceptionReceivedDuringRun++;
            }
        }

        final ConsumerStatistics consumerStatistics =
            ConsumerStatistics
                .builder()
                .antallOppgaverSomMedSikkerhetErFerdigstilt(totaltAntallOppgaverSomMedSikkerhetErFerdigstilt)
                .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(totaltAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt)
                .antallOppgaverSomKanVaereFerdigstilt(totaltAntallOppgaverSomKanVaereFerdigstilt)
                .numberOfExceptionReceivedDuringRun(numberOfExceptionReceivedDuringRun)
                .build();

        return consumerStatistics;
    }

    private void logOppgaverSomIkkeBleFerdigstilt(
        final WSFerdigstillOppgaveBolkResponse response,
        final int antallOppgaverSomKanVaereFerdigstilt) {

        final int antallOppgaverSomIkkeErFerdigstilt =
            response.getFeilListe().size();
        final int antallOppgaverSomErFerdigstilt =
            antallOppgaverSomKanVaereFerdigstilt - antallOppgaverSomIkkeErFerdigstilt;

        logger.info("Ferdigstilling av {} oppgaver var vellykket.", antallOppgaverSomErFerdigstilt);
        logger.info("Ferdigstilling av {} oppgaver var ikke vellykket.", antallOppgaverSomIkkeErFerdigstilt);

        if (!response.isTransaksjonOk()) {
            response
                .getFeilListe()
                .forEach(
                    bolkfeil ->
                        logger.error(
                            "Ferdigstilling av oppgave med id {} feilet. Feilkode {}: {}",
                            bolkfeil.getOppgaveId(),
                            bolkfeil.getFeilKode(),
                            bolkfeil.getFeilBeskrivelse()
                        )
                );
        }
    }
    // === END   ferdigstill related =======
    // ========================================================================
}
