package no.nav.okosynk.consumer.oppgavebehandling;

import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.AbstractConsumerV3ServiceImpl;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.domain.Oppgave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static no.nav.okosynk.config.Constants.OKOSYNK_SHOULD_RUN_DRY_DEFAULT_VALUE;
import static no.nav.okosynk.config.Constants.OKOSYNK_SHOULD_RUN_DRY_KEY;

public class OppgaveBehandlingConsumerGatewayImplFake
    extends AbstractConsumerV3ServiceImpl
    implements IOppgaveBehandlingConsumerGateway {

    private static final Logger logger = LoggerFactory.getLogger(OppgaveBehandlingConsumerGatewayImplFake.class);

    public OppgaveBehandlingConsumerGatewayImplFake(
        final IOkosynkConfiguration             okosynkConfiguration) {

        super(okosynkConfiguration);

        logger.info(this.getClass().getName() + " has been successfully instantiated");
    }

    @Override
    public ConsumerStatistics opprettOppgaver(
        final IOkosynkConfiguration okosynkConfiguration,
        final Collection<Oppgave>   oppgaver) {

        final int antallOppgaverSomMedSikkerhetErOpprettet = oppgaver.size();

        logger.info(
              System.lineSeparator()
            + this.getClass().getSimpleName() + ".opprettOppgaver "
            + "has been called." + System.lineSeparator()
            + "This is a fake, so no insertions "
            + "will be carried out, just this logging." + System.lineSeparator()
            + "This functionality is enabled because the system property {} "
            + "(or its corresponding environment variable) is set to: "
            + okosynkConfiguration.getBoolean(OKOSYNK_SHOULD_RUN_DRY_KEY, OKOSYNK_SHOULD_RUN_DRY_DEFAULT_VALUE)
            + System.lineSeparator()
            + "Number of oppgaver: {}"
            , antallOppgaverSomMedSikkerhetErOpprettet
            , OKOSYNK_SHOULD_RUN_DRY_KEY
        );

        return ConsumerStatistics
                .builder()
                .antallOppgaverSomMedSikkerhetErOpprettet(antallOppgaverSomMedSikkerhetErOpprettet)
                .build();
    }

    @Override
    public ConsumerStatistics oppdaterOppgaver(
        final IOkosynkConfiguration okosynkConfiguration,
        final Collection<Oppgave>   oppgaver) {

        final int antallOppgaverSomMedSikkerhetErOppdatert = oppgaver.size();

        logger.info(
            System.lineSeparator()
                + this.getClass().getSimpleName() + ".oppdaterOppgaver "
                + "has been called." + System.lineSeparator()
                + "This is a fake, so no updates "
                + "will be carried out, just this logging." + System.lineSeparator()
                + "This functionality is enabled because the system property {} "
                + "(or its corresponding environment variable) is set to: "
                + okosynkConfiguration.getBoolean(OKOSYNK_SHOULD_RUN_DRY_KEY, OKOSYNK_SHOULD_RUN_DRY_DEFAULT_VALUE)
                + System.lineSeparator()
                + "Number of oppgaver: {}"
            , antallOppgaverSomMedSikkerhetErOppdatert
            , OKOSYNK_SHOULD_RUN_DRY_KEY
        );

        return ConsumerStatistics
            .builder()
            .antallOppgaverSomMedSikkerhetErOppdatert(antallOppgaverSomMedSikkerhetErOppdatert)
            .build();
    }

    @Override
    public ConsumerStatistics ferdigstillOppgaver(
        final Collection<Oppgave> oppgaver) {

        final int antallOppgaverSomMedSikkerhetErFerdigstilt = oppgaver.size();

        logger.info(
            System.lineSeparator()
                + this.getClass().getSimpleName() + ".ferdigstillOppgaver "
                + "has been called." + System.lineSeparator()
                + "This is a fake, so no completions "
                + "will be carried out, just this logging." + System.lineSeparator()
                + "This functionality is enabled because the system property {} "
                + "(or its corresponding environment variable) is set to: "
                + getOkosynkConfiguration().getBoolean(OKOSYNK_SHOULD_RUN_DRY_KEY, OKOSYNK_SHOULD_RUN_DRY_DEFAULT_VALUE)
                + System.lineSeparator()
                + "Number of oppgaver: {}"
            , oppgaver.size()
            , OKOSYNK_SHOULD_RUN_DRY_KEY
        );

        return ConsumerStatistics
            .builder()
            .antallOppgaverSomMedSikkerhetErFerdigstilt(antallOppgaverSomMedSikkerhetErFerdigstilt)
            .build();
    }
}
