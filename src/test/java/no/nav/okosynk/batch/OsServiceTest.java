package no.nav.okosynk.batch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import no.nav.okosynk.batch.os.OsService;
import no.nav.okosynk.config.*;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.os.OsMelding;
import no.nav.okosynk.domain.MeldingUnreadableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OsServiceTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String MOCK_OS_LINJE = "10108000398009543471 2008-10-102008-10-10NEG K231B2962008-11-012008-11-30000000008820æ 4151         GS      10108000398            ";

    private static String inputFilPath = "mypath";

//    private IOppgaveConsumerGateway mockedOppgaveGateway =
//        mock(IOppgaveConsumerGateway.class);
//
//    private IOppgaveBehandlingConsumerGateway mockedOppgaveBehandlingGateway =
//        mock(IOppgaveBehandlingConsumerGateway.class);

    private OsService osService;
    private IOkosynkConfiguration okosynkConfiguration;

    @BeforeEach
    void setUp() {

        this.okosynkConfiguration = new FakeOkosynkConfiguration();

        okosynkConfiguration.setSystemProperty(
            Constants.FILE_READER_MAX_NUMBER_OF_READ_TRIES_KEY,
            "2");
        okosynkConfiguration.setSystemProperty(
            Constants.FILE_READER_RETRY_WAIT_TIME_IN_MILLISECONDS_KEY,
            "1000");
        osService = new OsService(okosynkConfiguration);
    }

    @Test
    public void when_a_batch_is_successfully_created_it_should_be_found_in_the_batch_repository() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        System.setProperty(Constants.REST_STS_URL_KEY, "https://security-token-service.nais.adeo.no/rest/v1/sts/token");
        System.setProperty("OSFTPBASEURL_URL", "sftp://filmottak.adeo.no:22/home/srvokosynksftp/inbound/os.input");
        System.setProperty("OSFTPCREDENTIALS_USERNAME", "someShitUserIdNotBeingUsedByNeitherThisOrThat");
        System.setProperty("OSFTPCREDENTIALS_PASSWORD", "someShitPasswordNotBeingUsedByNeitherThisOrThat");

        final OsService mockedOsService = mock(OsService.class);
        when(mockedOsService.createAndConfigureBatch(any())).thenCallRealMethod();
        when(mockedOsService.getBatchType()).thenReturn(BATCH_TYPE.OS);

        final Batch<? extends AbstractMelding> osBatch =
            mockedOsService.createAndConfigureBatch(this.okosynkConfiguration);

        assertNotNull(osBatch);
    }

//    @Test
//    void batchStatusEtterKallTilStoppBatchErStoppet() throws MeldingUnreadableException {
//
//        enteringTestHeaderLogger.debug(null);
//
//        final Batch<? extends AbstractMelding> osBatch = osService.createAndConfigureBatch(this.okosynkConfiguration);
//        osBatch.setStatus(BatchStatus.STARTET);
//        osService.stoppBatch();
//        BatchStatus batchStatus = osService.pollBatch(osBatch.getExecutionId()).get();
//
//        assertEquals(BatchStatus.STOPPET, batchStatus);
//    }

//    @Test
//    void startAlleredeStartetBatchStopperOpprinneligBatch() throws MeldingUnreadableException {
//
//        enteringTestHeaderLogger.debug(null);
//
//        final long eksekveringsId = osService.startBatchAsynchronously();
//
//        osService.startBatchAsynchronously();
//
//        assertEquals(BatchStatus.STOPPET, osService.pollBatch(eksekveringsId).get());
//    }

//    @Test
//    void batchSomHarKjortHarStatusFullfort() throws InterruptedException, MeldingUnreadableException {
//
//        enteringTestHeaderLogger.debug(null);
//
//        setUpBatchFullfortMock(this.okosynkConfiguration);
//        final long eksekveringsId = osService.startBatchAsynchronously();
//
//        sleep(250);
//
//        assertEquals(BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL, osService.pollBatch(eksekveringsId).get());
//    }

//    @Test
//    void pollBatchReturnererBatchStatusForEksekveringsIdSomErIBruk() throws MeldingUnreadableException {
//
//        enteringTestHeaderLogger.debug(null);
//
//        final Batch<? extends AbstractMelding> osBatch = osService.createAndConfigureBatch(this.okosynkConfiguration);
//        osBatch.setStatus(BatchStatus.STARTET);
//
//        final Optional<BatchStatus> batchStatus = osService.pollBatch(osBatch.getExecutionId());
//
//        assertTrue(batchStatus.isPresent(), "PollBatch fant ikke batchstatus for batch med eksekveringsid som er i bruk");
//    }

    private void setUpBatchFullfortMock(final IOkosynkConfiguration okosynkConfiguration) throws MeldingUnreadableException {

        this.osService = Mockito.spy(new OsService(okosynkConfiguration));
        final Batch<OsMelding> batch = (Batch<OsMelding>)osService.createAndConfigureBatch(this.okosynkConfiguration);
        batch.setMeldingLinjeReader(new MeldingLinjeFileReaderMock(MOCK_OS_LINJE));
        when(osService.createAndConfigureBatch(this.okosynkConfiguration)).thenReturn((Batch)batch);
    }
}
