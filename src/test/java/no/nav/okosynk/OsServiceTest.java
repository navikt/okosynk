package no.nav.okosynk;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OsServiceTest {

    private static final Logger enteringTestHeaderLogger = LoggerFactory.getLogger("EnteringTestHeader");
    private static final Logger logger = LoggerFactory.getLogger(OsServiceTest.class);

    @Test
    void when_a_batch_is_created_it_should_not_be_null() throws URISyntaxException, ConfigureOrInitializeOkosynkIoException {

        OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);

        enteringTestHeaderLogger.debug(null);

        when(okosynkConfiguration.getString(Constants.REST_STS_URL_KEY)).thenReturn("https://security-token-service.nais.adeo.no/rest/v1/sts/token");
        when(okosynkConfiguration.getString(Constants.FTP_HOST_URL_KEY)).thenReturn("sftp://filmottak.adeo.no:22/home/srvokosynksftp/inbound/os.testset_001.input");
        when(okosynkConfiguration.getString(Constants.FTP_USERNAME)).thenReturn("someShitUserIdNotBeingUsedByNeitherThisOrThat");
        when(okosynkConfiguration.getString(Constants.OPPGAVE_USERNAME)).thenReturn("Executor");
        final OsService mockedOsService = mock(OsService.class);
        when(mockedOsService.createAndConfigureBatch(any())).thenCallRealMethod();
        when(mockedOsService.getBatchType()).thenReturn(BATCH_TYPE.OS);
        when(mockedOsService.createMeldingReader()).thenReturn(mock(MeldingReader.class));
        when(mockedOsService.createMeldingMapper(any())).thenReturn(mock(IMeldingMapper.class));

        final Batch<? extends AbstractMelding> osBatch =
                mockedOsService.createAndConfigureBatch(okosynkConfiguration);

        assertNotNull(osBatch);
    }

    @Test
    void when_batch_returns_a_specific_batch_status_then_the_service_should_return_the_same() {
        OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);
        AbstractService<? extends AbstractMelding> service = new OsService(okosynkConfiguration);

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

    @Test
    void when_batch_is_implicitly_created_then_the_service_should_run_without_exceptions() {
        OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);
        AbstractService<? extends AbstractMelding> service = new OsService(okosynkConfiguration);

        enteringTestHeaderLogger.debug(null);

        final IAktoerClient mockedAktoerClient = mock(IAktoerClient.class);

        when(okosynkConfiguration.getString(Constants.REST_STS_URL_KEY)).thenReturn("liknlknlkn");
        when(okosynkConfiguration.getString(Constants.FTP_USERNAME)).thenReturn("SomeDummyDude");
        when(okosynkConfiguration.getString(Constants.FTP_PRIVATEKEY)).thenReturn("SomeDummyKey");
        when(okosynkConfiguration.getString(Constants.FTP_HOST_URL_KEY)).thenReturn("lkjnlknkl");
        when(okosynkConfiguration.getString(Constants.OPPGAVE_USERNAME)).thenReturn("Executor");
        service.setAktoerClient(mockedAktoerClient);

        assertDoesNotThrow(service::run);
        assertEquals(BatchStatus.ENDED_WITH_ERROR_CONFIGURATION, service.getLastBatchStatus());
    }

}
