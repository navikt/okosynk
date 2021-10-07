package no.nav.okosynk.domain;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.IAktoerClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public abstract class AbstractOppgaveOppretterTest {

    protected final IOkosynkConfiguration okosynkConfiguration;

    protected IAktoerClient aktoerClient = mock(IAktoerClient.class);

    private boolean shouldConvertFolkeregisterIdentToAktoerId_saved = true;

    protected AbstractOppgaveOppretterTest(final IOkosynkConfiguration okosynkConfiguration) {
        this.okosynkConfiguration = okosynkConfiguration;
    }

    @BeforeEach
    void beforeEach() {
        this.shouldConvertFolkeregisterIdentToAktoerId_saved =
                this.okosynkConfiguration.shouldConvertFolkeregisterIdentToAktoerId();
        setShouldConvertFolkeregisterIdentToAktoerId(false);
    }

    @AfterEach
    void afterEach() {
        setShouldConvertFolkeregisterIdentToAktoerId(this.shouldConvertFolkeregisterIdentToAktoerId_saved);
    }

    protected void setShouldConvertFolkeregisterIdentToAktoerId(final boolean shouldConvertFolkeregisterIdentToAktoerId) {
        System.setProperty(
                Constants.SHOULD_CONVERT_FOLKEREGISTER_IDENT_TO_AKTOERID_KEY,
                Boolean.valueOf(shouldConvertFolkeregisterIdentToAktoerId).toString());
    }

    @Test
    void dummy() {
    }
}
