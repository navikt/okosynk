package no.nav.okosynk.consumer.oppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.AbstractConsumerGatewayFactory;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppgaveConsumerGatewayFactory
    extends AbstractConsumerGatewayFactory {

    private static final Logger logger = LoggerFactory.getLogger(OppgaveConsumerGatewayFactory.class);

    public OppgaveConsumerGatewayFactory(final IOkosynkConfiguration okosynkConfiguration) {
        super(okosynkConfiguration);
    }

    public IOppgaveConsumerGateway create(Constants.BATCH_TYPE batchType) {

        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        final IOppgaveConsumerGateway gateway;
        if (shouldUseSoap()) {
            final OppgaveV3 oppgaveV3 = getOppgaveV3(okosynkConfiguration, batchType);
            gateway =
                new OppgaveConsumerV3ServiceImpl(okosynkConfiguration, oppgaveV3);
        } else {
            gateway = null;
        }

        return gateway;
    }

    OppgaveV3 getOppgaveV3(final IOkosynkConfiguration okosynkConfiguration,
                           final Constants.BATCH_TYPE batchType) {
        final String bruker =
            getOkosynkConfiguration()
                .getString(
                    batchType.getBatchBrukerKey(),
                    batchType.getBatchBrukerDefaultValue()
                );

        final OppgaveV3 oppgaveV3 =
            OppgaveConsumerConfig
                .getInstance(okosynkConfiguration, bruker)
                .getOppgaveV3();

        return oppgaveV3;
    }
}
