package no.nav.okosynk;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public abstract class AbstractServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServiceTest.class);
    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private IOkosynkConfiguration okosynkConfiguration;
    private AbstractService<? extends AbstractMelding> service;

    @Test
    void when_batch_returns_a_specific_batch_status_then_the_service_should_return_the_same() {

        enteringTestHeaderLogger.debug(null);

        final AbstractService<? extends AbstractMelding> service = getService();
        final Batch<? extends AbstractMelding> mockedBatch = mock(Batch.class);

        Arrays
                .stream(BatchStatus.values())
                .forEach(
                        (final BatchStatus batchStatus) -> {
                            logger.info("Testing service when batch returns batchStatus {}", batchStatus);
                            doReturn(batchStatus).when(mockedBatch).getBatchStatus();
                            service.setBatch(mockedBatch);
                            assertDoesNotThrow(service::run);
                            assertEquals(batchStatus, service.getLastBatchStatus());
                            assertEquals(batchStatus.failedButRerunningMaySucceed(), service.shouldRun());
                        }
                );
    }

    @Test
    void when_batch_is_implicitly_created_then_the_service_should_run_without_exceptions() {

        enteringTestHeaderLogger.debug(null);

        final IAktoerClient mockedAktoerClient = mock(IAktoerClient.class);

        final AbstractService<? extends AbstractMelding> service = getService();
        assertNotNull(service.getAlertMetrics());
        getOkosynkConfiguration().setSystemProperty(Constants.REST_STS_URL_KEY, "liknlknlkn");
        getOkosynkConfiguration().setSystemProperty(Constants.FTP_USERNAME, "SomeDummyDude");
        getOkosynkConfiguration().setSystemProperty(Constants.FTP_PRIVATEKEY, "SomeDummyKey");
        getOkosynkConfiguration().setSystemProperty(Constants.FTP_HOST_URL_KEY, "lkjnlknkl");
        service.setAktoerClient(mockedAktoerClient);

        assertDoesNotThrow(service::run);
        assertEquals(BatchStatus.ENDED_WITH_ERROR_CONFIGURATION, service.getLastBatchStatus());
    }

    protected void commonBeforeEach() {
        this.okosynkConfiguration = new FakeOkosynkConfiguration();
    }

    protected AbstractService<? extends AbstractMelding> getService() {
        return this.service;
    }

    protected void setService(final AbstractService<? extends AbstractMelding> service) {
        this.service = service;
    }

    protected IOkosynkConfiguration getOkosynkConfiguration() {
        return this.okosynkConfiguration;
    }
}
