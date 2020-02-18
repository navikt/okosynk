    package no.nav.okosynk.batch.ur;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import no.nav.okosynk.batch.AbstractBatchTest;
import no.nav.okosynk.batch.Batch;
import no.nav.okosynk.batch.BatchStatus;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.domain.ur.UrMapper;
import no.nav.okosynk.domain.ur.UrMelding;
import no.nav.okosynk.domain.ur.UrMeldingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UrBatchTest extends AbstractBatchTest<UrMelding> {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @BeforeEach
    void beforeEach() {

        super.setMockedMeldingReader(mock(UrMeldingReader.class));
        super.setMockedMeldingMapper(mock(UrMapper.class));

        super.setBatch(
            new Batch<>(
                this.getOkosynkConfiguration(),
                Constants.BATCH_TYPE.UR,
                new UrMeldingReader(UrMelding::new),
                new UrMapper(mock(AktoerRestClient.class))
            )
        );
    }

    @Test
    void when_batch_is_created_with_null_configuration_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(NullPointerException.class,
            () ->
            new Batch<>(
                null,
                Constants.BATCH_TYPE.UR,
                new UrMeldingReader(UrMelding::new),
                new UrMapper(mock(AktoerRestClient.class))
            )
        );
    }

    @Test
    void when_batch_is_created_with_null_batch_type_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(NullPointerException.class,
            () ->
            new Batch<>(
                this.getOkosynkConfiguration(),
                null,
                new UrMeldingReader(UrMelding::new),
                new UrMapper(mock(AktoerRestClient.class))
            )
        );
    }

    @Test
    void when_batch_is_created_with_null_MeldingReader_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(NullPointerException.class,
            () ->
            new Batch<>(
                this.getOkosynkConfiguration(),
                Constants.BATCH_TYPE.UR,
                null,
                new UrMapper(mock(AktoerRestClient.class))
            )
        );
    }

    @Test
    void when_batch_is_created_with_null_Mapper_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(
            NullPointerException.class,
            () ->
            new Batch<>(
                this.getOkosynkConfiguration(),
                Constants.BATCH_TYPE.UR,
                new UrMeldingReader(UrMelding::new),
                null
            )
        );
    }

    @Test
    // Makes under "certain circumstances" the build JVM crash
    @Disabled
    void when_the_uspesifikkMeldingLinjeReader_is_not_set_and_calling_run_then_an_appropriate_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final Batch<UrMelding> batch =
            new Batch<>(
                this.getOkosynkConfiguration(),
                Constants.BATCH_TYPE.UR,
                new UrMeldingReader(UrMelding::new),
                new UrMapper(mock(AktoerRestClient.class))
            );

        assertDoesNotThrow(() -> { batch.run(); });
        final BatchStatus batchStatus = batch.getBatchStatus();
        assertFalse(batchStatus.failedButRerunningMaySucceed(), "Rerunning when the batch is erroneously configured may remedy the error situation.");
        assertEquals(BatchStatus.ENDED_WITH_ERROR_CONFIGURATION, batchStatus);
    }

    @Override
    protected String getValidLineOfInputData() {
        return "***REMOVED***PERSON      2020-01-21T12:38:3724GKA2960   00000000006860A8020GHBATCHUR2302020-01-21001618071Manuell retur - fra bank                          ***REMOVED***";
    }
}