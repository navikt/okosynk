package no.nav.okosynk.consumer.oppgavebehandling;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

import lombok.AccessLevel;
import lombok.Getter;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.AbstractConsumerConfig;

import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppgaveBehandlingConsumerConfig
    extends AbstractConsumerConfig<OppgavebehandlingV3> {

    private static final Logger logger = LoggerFactory.getLogger(OppgaveBehandlingConsumerConfig.class);

    @Getter(AccessLevel.PUBLIC)
    private final OppgavebehandlingV3 oppgavebehandlingV3;

    private OppgaveBehandlingConsumerConfig(
        final IOkosynkConfiguration okosynkConfiguration,
        final String                bruker) {

        super(okosynkConfiguration, Constants.CONSUMER_TYPE.OPPGAVE_BEHANDLING, OppgavebehandlingV3.class);

        final OppgavebehandlingV3                  prod                = factory(bruker).build();
        final OppgaveBehandlingConsumerServiceMock mock                = new OppgaveBehandlingConsumerServiceMock();
        final OppgavebehandlingV3                  oppgavebehandlingV3 =
            createMetricsProxyWithInstanceSwitcher(
                getConsumerType().getName(),
                prod,
                mock,
                getConsumerType().getMockKey(),
                OppgavebehandlingV3.class
            );
        this.oppgavebehandlingV3 = oppgavebehandlingV3;
    }

    public static OppgaveBehandlingConsumerConfig getInstance(
        final IOkosynkConfiguration okosynkConfiguration,
        final String                bruker) {

        final OppgaveBehandlingConsumerConfig instance =
            new OppgaveBehandlingConsumerConfig(okosynkConfiguration, bruker);

        return instance;
    }

    public Pingable ping() {

        return () -> {

            final String tjenesteBeskrivelseForPing = getTjenesteBeskrivelseForPing();
            Pingable.Ping pingablePing;
            try {
                oppgavebehandlingV3.ping();
                pingablePing = lyktes(tjenesteBeskrivelseForPing);
            } catch (Exception e) {
                pingablePing = feilet(tjenesteBeskrivelseForPing, e);
            }

            return pingablePing;
        };
    }
}
