package no.nav.okosynk.consumer.oppgave;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

import lombok.AccessLevel;
import lombok.Getter;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.AbstractConsumerConfig;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppgaveConsumerConfig
    extends AbstractConsumerConfig<OppgaveV3> {

    private static final Logger logger = LoggerFactory.getLogger(OppgaveConsumerConfig.class);

    private static OppgaveConsumerConfig instance = null;

    @Getter(AccessLevel.PUBLIC)
    private final OppgaveV3 oppgaveV3;

    private OppgaveConsumerConfig(
        final IOkosynkConfiguration okosynkConfiguration,
        final String bruker) {

        super(okosynkConfiguration, Constants.CONSUMER_TYPE.OPPGAVE, OppgaveV3.class);

        final CXFClient<OppgaveV3> cxfClient = factory(bruker);
        final OppgaveV3   prod = cxfClient.build();
        final OppgaveMock mock      = new OppgaveMock();

        final OppgaveV3 oppgaveV3 =
            createMetricsProxyWithInstanceSwitcher(
                getConsumerType().getName(),
                prod,
                mock,
                getConsumerType().getMockKey(),
                OppgaveV3.class
            );
        this.oppgaveV3 = oppgaveV3;
    }

    /**
     * TODO: Find out why this is a singleton, but the corresponding OppgaveBehandlingConsumerConfig is not
     * TODO: Find out the coupling or non-coupling between no.nav.okosynk.io.os/ur and OppgaveConsumerConfig/OppgaveBehandlingConsumerConfig
     * @param okosynkConfiguration
     * @return
     */
    public static OppgaveConsumerConfig getInstance(
        final IOkosynkConfiguration okosynkConfiguration,
        final String bruker) {

        if (OppgaveConsumerConfig.instance == null) {
            OppgaveConsumerConfig.instance = new OppgaveConsumerConfig(okosynkConfiguration, bruker);
        }

        return OppgaveConsumerConfig.instance;
    }

    public Pingable ping() {

        return () -> {

            final String tjenesteBeskrivelseForPing = getTjenesteBeskrivelseForPing();
            Pingable.Ping pingablePing;
            try {
                oppgaveV3.ping();
                pingablePing = lyktes(tjenesteBeskrivelseForPing);
            } catch (Exception e) {
                pingablePing = feilet(tjenesteBeskrivelseForPing, e);
            }

            return pingablePing;
        };
    }
}
