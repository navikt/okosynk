package no.nav.okosynk.consumer.oppgave;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Collection;
import java.util.stream.Collectors;
import no.nav.okosynk.consumer.AbstractConsumerV3ServiceImpl;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.domain.Oppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppgaveConsumerV3ServiceImpl
    extends AbstractConsumerV3ServiceImpl
    implements IOppgaveConsumerGateway {

    private static final Logger logger = LoggerFactory.getLogger(OppgaveConsumerV3ServiceImpl.class);
    private static final String FAGOMRADE_OKONOMI_KODE = "OKO";

    @Getter(AccessLevel.PRIVATE)
    private final OppgaveV3             oppgaveV3;

    public OppgaveConsumerV3ServiceImpl(
        final IOkosynkConfiguration okosynkConfiguration,
        final OppgaveV3             oppgaveV3) {

        super(okosynkConfiguration);
        this.oppgaveV3            = oppgaveV3;
    }

    @Override
    public ConsumerStatistics finnOppgaver(
        final String opprettetAv,
        final Collection<Oppgave> funneOppgaver) {

        final WSFinnOppgaveListeRequest request =
            new WSFinnOppgaveListeRequest()
                .withSok(new WSFinnOppgaveListeSok()
                             .withFagomradeKodeListe(FAGOMRADE_OKONOMI_KODE)
                )
                .withFilter(new WSFinnOppgaveListeFilter()
                                .withOpprettetAv(opprettetAv));
        try {
            final WSFinnOppgaveListeResponse response = oppgaveV3.finnOppgaveListe(request);
            final int antallOppgaverSomErHentetFraDatabasen = response.getTotaltAntallTreff();
            logger.info("Hentet {} oppgaver fra databasen.", antallOppgaverSomErHentetFraDatabasen);

            funneOppgaver.addAll(
                response
                    .getOppgaveListe()
                    .stream()
                    .map(WSOppgaveTilOppgaveMapper::lagOppgave)
                    .collect(Collectors.toList())
            );

            final ConsumerStatistics consumerStatistics =
                ConsumerStatistics
                    .builder()
                    .antallOppgaverSomErHentetFraDatabasen(antallOppgaverSomErHentetFraDatabasen)
                    .build();

            return consumerStatistics;

        } catch (Throwable e) {
            logger.error("Kunne ikke hente oppgaver som følge av en feil underveis. Batch må abrytes.", e);
            throw e;
        }
    }
}
