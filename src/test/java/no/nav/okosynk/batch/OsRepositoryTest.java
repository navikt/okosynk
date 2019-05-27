package no.nav.okosynk.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.domain.os.OsMapper;
import no.nav.okosynk.domain.os.OsMelding;
import no.nav.okosynk.domain.os.OsMeldingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OsRepositoryTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final long EKSEKVERINGS_ID = 0;

    private BatchRepository batchRepository;
    private IOkosynkConfiguration okosynkConfiguration;

    @BeforeEach
    void setUp() {

        this.okosynkConfiguration = new FakeOkosynkConfiguration();
        this.batchRepository = new BatchRepository();
        this.batchRepository.cleanTestRepository();
    }

    @Test
    void batchSomHarBlittLagtTilKanHentesUtMedEksekveringsId() throws Exception {

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final Batch<OsMelding> osBatch =
            new Batch<OsMelding>(
                okosynkConfiguration,
                Constants.BATCH_TYPE.OS,
                EKSEKVERINGS_ID,
                new OsMeldingReader(OsMelding::new),
                new OsMapper(mock(AktoerRestClient.class)));

        batchRepository.leggTil(osBatch);

        assertEquals(osBatch, batchRepository.hentBatch(osBatch.getExecutionId()).get());
    }

    @Test
    void batchSomHarBlittLagtTilKanHentesUtMedBatchNavn() {

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final Batch<OsMelding> osBatch =
            new Batch<OsMelding>(
                okosynkConfiguration,
                Constants.BATCH_TYPE.OS,
                EKSEKVERINGS_ID,
                new OsMeldingReader(OsMelding::new),
                new OsMapper(mock(AktoerRestClient.class)));

        batchRepository.leggTil(osBatch);

        assertEquals(osBatch, batchRepository.hentBatch(osBatch.getBatchName()).get());
    }
}
