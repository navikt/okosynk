package no.nav.okosynk.consumer.oppgavebehandling;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppgaveBehandlingConsumerGatewayFactoryTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    public void testThatTheFactoryCreatesOsSoapInstance(){

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final OppgavebehandlingV3 oppgaveBehandlingV3Mock = mock(OppgavebehandlingV3.class);

        final OppgaveBehandlingConsumerGatewayFactory oppgaveBehandlingGatewayFactory =
            Mockito.spy(new OppgaveBehandlingConsumerGatewayFactory(okosynkConfiguration));
        doReturn(oppgaveBehandlingV3Mock).when(oppgaveBehandlingGatewayFactory)
            .getOppgaveBehandlingV3(Constants.BATCH_TYPE.OS);

        okosynkConfiguration.setSystemProperty(Constants.SHOULD_USE_SOAP_KEY, "true");
        final IOppgaveBehandlingConsumerGateway oppgaveBehandlingGateway =
            oppgaveBehandlingGatewayFactory.create(Constants.BATCH_TYPE.OS);

        assertTrue(oppgaveBehandlingGateway instanceof OppgaveBehandlingConsumerV3ServiceImpl);
    }

    @Test
    public void testThatTheFactoryCreatesUrSoapInstance(){

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final OppgavebehandlingV3 oppgaveBehandlingV3Mock = mock(OppgavebehandlingV3.class);

        final OppgaveBehandlingConsumerGatewayFactory oppgaveBehandlingGatewayFactory =
            Mockito.spy(new OppgaveBehandlingConsumerGatewayFactory(okosynkConfiguration));
        doReturn(oppgaveBehandlingV3Mock).when(oppgaveBehandlingGatewayFactory)
            .getOppgaveBehandlingV3(Constants.BATCH_TYPE.UR);

        okosynkConfiguration.setSystemProperty(Constants.SHOULD_USE_SOAP_KEY, "true");

        final IOppgaveBehandlingConsumerGateway oppgaveBehandlingGateway =
            oppgaveBehandlingGatewayFactory.create(Constants.BATCH_TYPE.UR);

        assertTrue(oppgaveBehandlingGateway instanceof OppgaveBehandlingConsumerV3ServiceImpl);
    }

    @Test
    public void testThatTheFactoryCreatesRestInstances(){

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final OppgaveBehandlingConsumerGatewayFactory oppgaveBehandlingGatewayFactory =
            new OppgaveBehandlingConsumerGatewayFactory(okosynkConfiguration);
        okosynkConfiguration.setSystemProperty(Constants.SHOULD_USE_SOAP_KEY, "false");
        Arrays
            .asList(Constants.BATCH_TYPE.values())
            .stream()
            .forEach(
                (batchType) -> {
                    final IOppgaveBehandlingConsumerGateway oppgaveBehandlingGateway =
                        oppgaveBehandlingGatewayFactory.create(batchType);

                    assertNull(oppgaveBehandlingGateway);
                }
            );
    }

    @Test
    public void testThatTheFactoryCreatesUrRestInstance(){

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final OppgaveBehandlingConsumerGatewayFactory oppgaveBehandlingGatewayFactory =
            new OppgaveBehandlingConsumerGatewayFactory(okosynkConfiguration);
        okosynkConfiguration.setSystemProperty(Constants.SHOULD_USE_SOAP_KEY, "false");

        final IOppgaveBehandlingConsumerGateway oppgaveBehandlingGateway =
            oppgaveBehandlingGatewayFactory.create(Constants.BATCH_TYPE.UR);

        assertNull(oppgaveBehandlingGateway);
    }
}
