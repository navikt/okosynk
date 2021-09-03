package no.nav.okosynk.domain;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public abstract class AbstractOppgaveOppretterTest {

    protected final IOkosynkConfiguration okosynkConfiguration;

    protected AktoerRestClient aktoerRestClient = mock(AktoerRestClient.class);

    private boolean shouldConvertNavPersonIdentToAktoerId_saved = true;

    protected AbstractOppgaveOppretterTest(final IOkosynkConfiguration okosynkConfiguration) {
        this.okosynkConfiguration = okosynkConfiguration;
    }

    @BeforeEach
    void beforeEach() {
        this.shouldConvertNavPersonIdentToAktoerId_saved =
                this.okosynkConfiguration.shouldConvertNavPersonIdentToAktoerId();
        setShouldConvertNavPersonIdentToAktoerId(false);
    }

    @AfterEach
    void afterEach() {
        setShouldConvertNavPersonIdentToAktoerId(this.shouldConvertNavPersonIdentToAktoerId_saved);
    }

    protected void setShouldConvertNavPersonIdentToAktoerId(final boolean shouldConvertNavPersonIdentToAktoerId) {
        System.setProperty(
                Constants.SHOULD_CONVERT_NAVPERSONIDENT_TO_AKTOERID_KEY,
                Boolean.valueOf(shouldConvertNavPersonIdentToAktoerId).toString());
    }

    @Test
    void dummy() {
    }
}
