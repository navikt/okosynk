package no.nav.okosynk.domain;

import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.IAktoerClient;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public abstract class AbstractOppgaveOppretterTest {

    protected final IOkosynkConfiguration okosynkConfiguration;

    protected IAktoerClient aktoerClient = mock(IAktoerClient.class);

    protected AbstractOppgaveOppretterTest(final IOkosynkConfiguration okosynkConfiguration) {
        this.okosynkConfiguration = okosynkConfiguration;
    }

    @Test
    void dummy() {
    }
}
