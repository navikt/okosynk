package no.nav.okosynk.consumer.oppgavebehandling;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.AbstractConsumerGatewayFactory;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;

public class OppgaveBehandlingConsumerGatewayFactory
    extends AbstractConsumerGatewayFactory {

    public OppgaveBehandlingConsumerGatewayFactory(final IOkosynkConfiguration okosynkConfiguration) {
        super(okosynkConfiguration);
    }

    public IOppgaveBehandlingConsumerGateway create(final Constants.BATCH_TYPE batchType) {

        final IOppgaveBehandlingConsumerGateway oppgaveBehandlingGateway;

        if (okosynkShouldRunDry()) {
            oppgaveBehandlingGateway =
                new OppgaveBehandlingConsumerGatewayImplFake(getOkosynkConfiguration());
        } else {
            if (shouldUseSoap()) {
                final OppgavebehandlingV3 oppgavebehandlingV3 = getOppgaveBehandlingV3(batchType);
                oppgaveBehandlingGateway =
                    new OppgaveBehandlingConsumerV3ServiceImpl(getOkosynkConfiguration(), oppgavebehandlingV3);
            } else {
                oppgaveBehandlingGateway = null;
            }
        }

        return oppgaveBehandlingGateway;
    }

    OppgavebehandlingV3 getOppgaveBehandlingV3(final Constants.BATCH_TYPE batchType) {

        final String bruker =
            getOkosynkConfiguration()
                .getString(
                    batchType.getBatchBrukerKey(),
                    batchType.getBatchBrukerDefaultValue()
                );

        final OppgavebehandlingV3 oppgavebehandlingV3 =
            OppgaveBehandlingConsumerConfig
                .getInstance(
                    getOkosynkConfiguration(),
                    bruker
                )
                .getOppgavebehandlingV3();

        return oppgavebehandlingV3;
    }

    private boolean okosynkShouldRunDry() {

        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        return okosynkConfiguration.getBoolean(
            Constants.OKOSYNK_SHOULD_RUN_DRY_KEY,
            Constants.OKOSYNK_SHOULD_RUN_DRY_DEFAULT_VALUE);
    }
}
