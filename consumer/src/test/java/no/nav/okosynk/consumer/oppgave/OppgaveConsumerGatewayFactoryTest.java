package no.nav.okosynk.consumer.oppgave;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppgaveConsumerGatewayFactoryTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    public void testThatTheFactoryCreatesSoapInstance(){

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final OppgaveV3 oppgaveV3Mock = mock(OppgaveV3.class);

        final OppgaveConsumerGatewayFactory oppgaveConsumerGatewayFactory =
            Mockito.spy(new OppgaveConsumerGatewayFactory(okosynkConfiguration));
        doReturn(oppgaveV3Mock).when(oppgaveConsumerGatewayFactory).getOppgaveV3(okosynkConfiguration, Constants.BATCH_TYPE.OS);

        okosynkConfiguration.setSystemProperty(Constants.SHOULD_USE_SOAP_KEY, "true");
        final IOppgaveConsumerGateway oppgaveGateway = oppgaveConsumerGatewayFactory.create(Constants.BATCH_TYPE.OS);

       assertTrue(oppgaveGateway instanceof OppgaveConsumerV3ServiceImpl);
    }

    @Test
    public void testThatTheFactoryCreatesRestInstance(){

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        okosynkConfiguration.setSystemProperty(
            Constants.CONSUMER_TYPE.OPPGAVE.getEndpointUrlKey(),
            "DumnmyPlaceholder");
        final OppgaveConsumerGatewayFactory oppgaveConsumerGatewayFactory =
            new OppgaveConsumerGatewayFactory(okosynkConfiguration);
        okosynkConfiguration.setSystemProperty(Constants.SHOULD_USE_SOAP_KEY, "false");

        final IOppgaveConsumerGateway oppgaveGateway = oppgaveConsumerGatewayFactory.create(Constants.BATCH_TYPE.OS);

        assertNull(oppgaveGateway);
    }
}
