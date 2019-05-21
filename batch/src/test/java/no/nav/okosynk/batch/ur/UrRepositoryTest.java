package no.nav.okosynk.batch.ur;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import no.nav.okosynk.batch.Batch;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.domain.ur.UrMapper;
import no.nav.okosynk.domain.ur.UrMelding;
import no.nav.okosynk.domain.ur.UrMeldingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UrRepositoryTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final long EKSEKVERINGS_ID = 0;

    private BatchRepository batchRepository;



    @BeforeEach
    void setUp() {
        this.batchRepository = new BatchRepository();
        this.batchRepository.cleanTestRepository();
    }

    @Test
    void batchSomHarBlittLagtTilKanHentesUtMedEksekveringsId() throws Exception {

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final Batch<UrMelding> urBatch =
            new Batch<UrMelding>(
                okosynkConfiguration,
                Constants.BATCH_TYPE.UR,
                EKSEKVERINGS_ID,
                new UrMeldingReader(UrMelding::new),
                new UrMapper());

        batchRepository.leggTil(urBatch);

        assertEquals(urBatch, batchRepository.hentBatch(urBatch.getExecutionId()).get());
    }

    @Test
    void batchSomHarBlittLagtTilKanHentesUtMedBatchNavn() {

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final Batch<UrMelding> urBatch =
            new Batch<UrMelding>(
                okosynkConfiguration,
                Constants.BATCH_TYPE.UR,
                EKSEKVERINGS_ID,
                new UrMeldingReader(UrMelding::new),
                new UrMapper());

        batchRepository.leggTil(urBatch);

        assertEquals(urBatch, batchRepository.hentBatch(urBatch.getBatchName()).get());
    }
}
