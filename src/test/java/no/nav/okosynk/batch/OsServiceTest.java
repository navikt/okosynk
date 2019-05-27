package no.nav.okosynk.batch;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import no.nav.okosynk.config.*;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.os.OsMelding;
import no.nav.okosynk.domain.MeldingUnreadableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Disabled
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

    private BatchRepository batchRepository;
    private OsService osService;
    private IOkosynkConfiguration okosynkConfiguration;

    @BeforeEach
    public void setUp() {

        this.okosynkConfiguration = new FakeOkosynkConfiguration();

        batchRepository = new BatchRepository();
        batchRepository.cleanTestRepository();
        osService = new OsService(okosynkConfiguration, batchRepository);
    }

    @Test
    public void opprettOsBatchLeggerBatchTilIRepository() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final Batch<? extends AbstractMelding> osBatch = osService.createAndConfigureBatch(this.okosynkConfiguration);

        assertNotNull(osBatch);
        assertTrue(batchRepository.hentBatch(osBatch.getExecutionId()).isPresent(), "Batchen finnes ikke i repositoryet");
    }

    @Test
    public void stoppBatchReturnererFalseForIkkeEksisterendeBatch() {

        enteringTestHeaderLogger.debug(null);

        assertFalse(osService.stoppBatch(), "En ikke-eksisterende batch har blitt stoppet");
    }

    @Test
    public void batchStatusEtterKallTilStoppBatchErStoppet() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final Batch<? extends AbstractMelding> osBatch = osService.createAndConfigureBatch(this.okosynkConfiguration);
        osBatch.setStatus(BatchStatus.STARTET);
        osService.stoppBatch();
        BatchStatus batchStatus = osService.pollBatch(osBatch.getExecutionId()).get();

        assertEquals(BatchStatus.STOPPET, batchStatus);
    }

    @Test
    public void batchSomIkkeHarStartetHarIkkeStatus() {
        assertFalse(osService.pollBatch(-1).isPresent(), "En batch som ikke har startet finnes i repositoryet");
    }

    @Test
    public void startAlleredeStartetBatchStopperOpprinneligBatch() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final long eksekveringsId = osService.startBatchAsynchronously();

        osService.startBatchAsynchronously();

        assertEquals(BatchStatus.STOPPET, osService.pollBatch(eksekveringsId).get());
    }

    @Test
    public void batchSomHarKjortHarStatusFullfort() throws InterruptedException, MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        setUpBatchFullfortMock(this.okosynkConfiguration);
        final long eksekveringsId = osService.startBatchAsynchronously();

        sleep(250);

        assertEquals(BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL, osService.pollBatch(eksekveringsId).get());
    }

    @Test
    public void pollBatchReturnererBatchStatusForEksekveringsIdSomErIBruk() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final Batch<? extends AbstractMelding> osBatch = osService.createAndConfigureBatch(this.okosynkConfiguration);
        osBatch.setStatus(BatchStatus.STARTET);

        final Optional<BatchStatus> batchStatus = osService.pollBatch(osBatch.getExecutionId());

        assertTrue(batchStatus.isPresent(), "PollBatch fant ikke batchstatus for batch med eksekveringsid som er i bruk");
    }

    private void setUpBatchFullfortMock(final IOkosynkConfiguration okosynkConfiguration) throws MeldingUnreadableException {

        this.osService = Mockito.spy(new OsService(okosynkConfiguration, batchRepository));
        final Batch<OsMelding> batch = (Batch<OsMelding>)osService.createAndConfigureBatch(this.okosynkConfiguration);
        batch.setMeldingLinjeReader(new MeldingLinjeFileReaderMock(MOCK_OS_LINJE));
        when(osService.createAndConfigureBatch(this.okosynkConfiguration)).thenReturn((Batch)batch);
    }
}
