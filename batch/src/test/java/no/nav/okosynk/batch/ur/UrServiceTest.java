package no.nav.okosynk.batch.ur;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import no.nav.okosynk.batch.Batch;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.batch.BatchStatus;
import no.nav.okosynk.batch.MeldingLinjeFileReaderMock;
import no.nav.okosynk.config.*;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.ur.UrMelding;
import no.nav.okosynk.domain.MeldingUnreadableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Disabled
class UrServiceTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String MOCK_UR_LINJE = "00003187051ORGANISASJON2004-01-19T06:22:4309          00000000084840æ0318KREDREFUR2302004-01-15134553997MOTTATT FRA FORSYSTEM                             00003187051";

    private static String inputFilPath = "mypath";


    private BatchRepository batchRepository;
    private UrService urService;
    private IOkosynkConfiguration okosynkConfiguration;

    @BeforeEach
    public void setUp() {

        this.okosynkConfiguration = new FakeOkosynkConfiguration();

        batchRepository = new BatchRepository();
        batchRepository.cleanTestRepository();
        urService = new UrService(okosynkConfiguration, batchRepository, new OppgaveRestClient(this.okosynkConfiguration, Constants.BATCH_TYPE.UR));
    }

    @Test
    public void opprettUrBatchLeggerBatchTilIRepository() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final Batch<? extends AbstractMelding> urBatch = urService.createAndConfigureBatch(this.okosynkConfiguration);

        assertNotNull(urBatch);
        assertTrue(batchRepository.hentBatch(urBatch.getExecutionId()).isPresent(), "Batchen finnes ikke i repositoryet");
    }

    @Test
    public void stoppBatchReturnererFalseForIkkeEksisterendeBatch() {

        enteringTestHeaderLogger.debug(null);

        assertFalse(urService.stoppBatch(), "En ikke-eksisterende batch har blitt stoppet");
    }

    @Test
    public void batchStatusEtterKallTilStoppBatchErStoppet() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final Batch<? extends AbstractMelding> urBatch = urService.createAndConfigureBatch(this.okosynkConfiguration);
        urBatch.setStatus(BatchStatus.STARTET);
        urService.stoppBatch();
        BatchStatus batchStatus = urService.pollBatch(urBatch.getExecutionId()).get();

        assertEquals(BatchStatus.STOPPET, batchStatus);
    }

    @Test
    public void batchSomIkkeHarStartetHarIkkeStatus() {

        enteringTestHeaderLogger.debug(null);

        assertFalse(urService.pollBatch(-1).isPresent(), "En batch som ikke har startet finnes i repositoryet");
    }

    @Test
    public void startAlleredeStartetBatchStopperOpprinneligBatch() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final long eksekveringsId = urService.startBatchAsynchronously();

        urService.startBatchAsynchronously();

        Assertions.assertEquals(BatchStatus.STOPPET, urService.pollBatch(eksekveringsId).get());
    }

    @Test
    public void batchSomHarKjortHarStatusFullfort() throws InterruptedException, MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        setUpBatchFullfortMock(this.okosynkConfiguration);
        final long eksekveringsId = urService.startBatchAsynchronously();

        sleep(250);

        Assertions.assertEquals(BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL, urService.pollBatch(eksekveringsId).get());
    }

    @Test
    public void pollBatchReturnererBatchStatusForEksekveringsIdSomErIBruk() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        final Batch<? extends AbstractMelding> urBatch = urService.createAndConfigureBatch(this.okosynkConfiguration);
        urBatch.setStatus(BatchStatus.STARTET);

        final Optional<BatchStatus> batchStatus = urService.pollBatch(urBatch.getExecutionId());

        assertTrue(batchStatus.isPresent(), "PollBatch fant ikke batchstatus for batch med eksekveringsid som er i bruk");
    }

    private void setUpBatchFullfortMock(final IOkosynkConfiguration okosynkConfiguration) throws MeldingUnreadableException {

        this.urService = spy(new UrService(okosynkConfiguration, batchRepository, new OppgaveRestClient(okosynkConfiguration, Constants.BATCH_TYPE.UR)));
        final Batch<UrMelding> batch = (Batch<UrMelding>)urService.createAndConfigureBatch(this.okosynkConfiguration);
        batch.setMeldingLinjeReader(new MeldingLinjeFileReaderMock(MOCK_UR_LINJE));
        when(urService.createAndConfigureBatch(this.okosynkConfiguration)).thenReturn((Batch)batch);
    }
}
