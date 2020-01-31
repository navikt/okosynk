package no.nav.okosynk.batch.ur;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import no.nav.okosynk.batch.Batch;
import no.nav.okosynk.batch.MeldingLinjeFileReaderMock;
import no.nav.okosynk.config.*;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.ur.UrMelding;
import no.nav.okosynk.domain.MeldingUnreadableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UrServiceTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String MOCK_UR_LINJE = "00003187051ORGANISASJON2004-01-19T06:22:4309          00000000084840æ0318KREDREFUR2302004-01-15134553997MOTTATT FRA FORSYSTEM                             00003187051";

    private static String inputFilPath = "mypath";

    private UrService urService;
    private IOkosynkConfiguration okosynkConfiguration;

    @BeforeEach
    public void setUp() {

        this.okosynkConfiguration = new FakeOkosynkConfiguration();

        okosynkConfiguration.setSystemProperty(
            Constants.FILE_READER_MAX_NUMBER_OF_READ_TRIES_KEY,
            "2");
        okosynkConfiguration.setSystemProperty(
            Constants.FILE_READER_RETRY_WAIT_TIME_IN_MILLISECONDS_KEY,
            "1000");
        urService = new UrService(okosynkConfiguration);
    }

    @Test
    public void when_a_batch_is_successfully_created_it_should_be_found_in_the_batch_repository() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        System.setProperty(Constants.REST_STS_URL_KEY, "https://security-token-service.nais.adeo.no/rest/v1/sts/token");
        System.setProperty("URFTPBASEURL_URL", "sftp://filmottak.adeo.no:22/home/srvokosynksftp/inbound/ur.input");
        System.setProperty("URFTPCREDENTIALS_USERNAME", "someShitUserIdNotBeingUsedByNeitherThisOrThat");
        System.setProperty("URFTPCREDENTIALS_PASSWORD", "someShitPasswordNotBeingUsedByNeitherThisOrThat");

        final UrService mockedUrService = mock(UrService.class);
        when(mockedUrService.createAndConfigureBatch(any())).thenCallRealMethod();
        when(mockedUrService.getBatchType()).thenReturn(BATCH_TYPE.UR);

        final Batch<? extends AbstractMelding> urBatch =
            mockedUrService.createAndConfigureBatch(this.okosynkConfiguration);

        assertNotNull(urBatch);
    }

//    @Test
//    public void batchStatusEtterKallTilStoppBatchErStoppet() throws MeldingUnreadableException {
//
//        enteringTestHeaderLogger.debug(null);
//
//        final Batch<? extends AbstractMelding> urBatch = urService.createAndConfigureBatch(this.okosynkConfiguration);
//        urBatch.setStatus(BatchStatus.STARTET);
//        urService.stoppBatch();
//        BatchStatus batchStatus = urService.pollBatch(urBatch.getExecutionId()).get();
//
//        assertEquals(BatchStatus.STOPPET, batchStatus);
//    }

//    @Test
//    public void startAlleredeStartetBatchStopperOpprinneligBatch() throws MeldingUnreadableException {
//
//        enteringTestHeaderLogger.debug(null);
//
//        final long eksekveringsId = urService.startBatchAsynchronously();
//
//        urService.startBatchAsynchronously();
//
//        assertEquals(BatchStatus.STOPPET, urService.pollBatch(eksekveringsId).get());
//    }

//    @Test
//    public void batchSomHarKjortHarStatusFullfort() throws InterruptedException, MeldingUnreadableException {
//
//        enteringTestHeaderLogger.debug(null);
//
//        setUpBatchFullfortMock(this.okosynkConfiguration);
//        final long eksekveringsId = urService.startBatchAsynchronously();
//
//        sleep(250);
//
//        assertEquals(BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL, urService.pollBatch(eksekveringsId).get());
//    }

//    @Test
//    public void pollBatchReturnererBatchStatusForEksekveringsIdSomErIBruk() throws MeldingUnreadableException {
//
//        enteringTestHeaderLogger.debug(null);
//
//        final Batch<? extends AbstractMelding> urBatch = urService.createAndConfigureBatch(this.okosynkConfiguration);
//        urBatch.setStatus(BatchStatus.STARTET);
//
//        final Optional<BatchStatus> batchStatus = urService.pollBatch(urBatch.getExecutionId());
//
//        assertTrue(batchStatus.isPresent(), "PollBatch fant ikke batchstatus for batch med eksekveringsid som er i bruk");
//    }

    private void setUpBatchFullfortMock(final IOkosynkConfiguration okosynkConfiguration) throws MeldingUnreadableException {

        this.urService = Mockito.spy(new UrService(okosynkConfiguration));
        final Batch<UrMelding> batch = urService.createAndConfigureBatch(this.okosynkConfiguration);
        batch.setMeldingLinjeReader(new MeldingLinjeFileReaderMock(MOCK_UR_LINJE));
        when(urService.createAndConfigureBatch(this.okosynkConfiguration)).thenReturn((Batch)batch);
    }
}
