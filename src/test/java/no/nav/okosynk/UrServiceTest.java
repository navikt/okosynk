package no.nav.okosynk;

import io.prometheus.client.CollectorRegistry;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Arrays;

import static no.nav.okosynk.config.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrServiceTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");
    private static final Logger logger = LoggerFactory.getLogger(UrServiceTest.class);

    @Test
    void when_a_batch_is_created_it_should_not_be_null() throws URISyntaxException, ConfigureOrInitializeOkosynkIoException {

        OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);

        when(okosynkConfiguration.getString(REST_STS_URL_KEY)).thenReturn("https://security-token-service.nais.adeo.no/rest/v1/sts/token");
        when(okosynkConfiguration.getString(FTP_HOST_URL_KEY)).thenReturn("sftp://filmottak.adeo.no:22/home/srvokosynksftp/inbound/ur.testset_001.input");
        when(okosynkConfiguration.getString(FTP_USERNAME)).thenReturn("someShitUserIdNotBeingUsedByNeitherThisOrThat");
        when(okosynkConfiguration.getString(FTP_PRIVATEKEY)).thenReturn("somePrivacyKPlsThnx");
        when(okosynkConfiguration.getString(OPPGAVE_USERNAME)).thenReturn("Eurystheus");

        final UrService mockedUrService = mock(UrService.class);
        when(mockedUrService.createAndConfigureBatch(any())).thenCallRealMethod();
        when(mockedUrService.getBatchType()).thenReturn(BATCH_TYPE.UR);
        when(mockedUrService.createMeldingReader()).thenReturn(mock(MeldingReader.class));
        when(mockedUrService.createMeldingMapper(any())).thenReturn(mock(IMeldingMapper.class));

        final Batch<? extends AbstractMelding> urBatch = mockedUrService.createAndConfigureBatch(okosynkConfiguration);

        assertNotNull(urBatch);
    }

    @Test
    void when_batch_returns_a_specific_batch_status_then_the_service_should_return_the_same() {
        OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);
        AbstractService<? extends AbstractMelding> service = new UrService(okosynkConfiguration);

        enteringTestHeaderLogger.debug(null);

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

    @BeforeEach
    @AfterEach
    public void cleanup() {
        CollectorRegistry.defaultRegistry.clear();
    }

    @Test
    void when_batch_is_implicitly_created_then_the_service_should_run_without_exceptions() {
        OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);

        when(okosynkConfiguration.getString(REST_STS_URL_KEY)).thenReturn("liknlknlkn");
        when(okosynkConfiguration.getString(FTP_USERNAME)).thenReturn("SomeDummyDude");
        when(okosynkConfiguration.getString(FTP_PRIVATEKEY)).thenReturn("SomeDummyKey");
        when(okosynkConfiguration.getString(FTP_HOST_URL_KEY)).thenReturn("lkjnlknkl");
        when(okosynkConfiguration.getString(OPPGAVE_USERNAME)).thenReturn("Tavernkeeper");

        AbstractService<? extends AbstractMelding> service = new UrService(okosynkConfiguration);

        final IAktoerClient mockedAktoerClient = mock(IAktoerClient.class);

        service.setAktoerClient(mockedAktoerClient);

        assertDoesNotThrow(service::run);
        assertEquals(BatchStatus.ENDED_WITH_ERROR_CONFIGURATION, service.getLastBatchStatus());
    }


}
