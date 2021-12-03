package no.nav.okosynk.batch.os;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import no.nav.okosynk.batch.AbstractBatchTest;
import no.nav.okosynk.batch.Batch;
import no.nav.okosynk.batch.BatchStatus;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.consumer.aktoer.IAktoerClient;
import no.nav.okosynk.domain.os.OsMapper;
import no.nav.okosynk.domain.os.OsMelding;
import no.nav.okosynk.domain.os.OsMeldingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OsBatchTest extends AbstractBatchTest<OsMelding> {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @BeforeEach
    void beforeEach() {

        super.setMockedMeldingReader(mock(OsMeldingReader.class));
        super.setMockedMeldingMapper(mock(OsMapper.class));

        super.setBatch(
            new Batch<>(
                this.getOkosynkConfiguration(),
                Constants.BATCH_TYPE.OS,
                new OsMeldingReader(OsMelding::new),
                    new OsMapper(mock(IAktoerClient.class), getOkosynkConfiguration())
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
                Constants.BATCH_TYPE.OS,
                new OsMeldingReader(OsMelding::new),
                    new OsMapper(mock(IAktoerClient.class), getOkosynkConfiguration())
            )
        );
    }

    @Test
    void when_batch_is_created_with_null_batch_type_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(NullPointerException.class, () -> new Batch<>(
            this.getOkosynkConfiguration(),
            null,
            new OsMeldingReader(OsMelding::new),
                new OsMapper(mock(IAktoerClient.class), getOkosynkConfiguration())
        ));
    }

    @Test
    void when_batch_is_created_with_null_MeldingReader_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(NullPointerException.class, () -> new Batch<>(
            this.getOkosynkConfiguration(),
            Constants.BATCH_TYPE.OS,
            null,
                new OsMapper(mock(IAktoerClient.class), getOkosynkConfiguration())
        ));
    }

    @Test
    void when_batch_is_created_with_null_Mapper_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(
            NullPointerException.class,
            () ->
            new Batch<>(
                this.getOkosynkConfiguration(),
                Constants.BATCH_TYPE.OS,
                new OsMeldingReader(OsMelding::new),
                null
            )
        );
    }

    @Test
    // Makes under "certain circumstances" the build JVM crash
    @Disabled
    void when_the_uspesifikkMeldingLinjeReader_is_not_set_and_calling_run_then_an_appropriate_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final Batch<OsMelding> batch =
            new Batch<>(
                this.getOkosynkConfiguration(),
                Constants.BATCH_TYPE.OS,
                new OsMeldingReader(OsMelding::new),
                    new OsMapper(mock(IAktoerClient.class), getOkosynkConfiguration())
            );

        assertDoesNotThrow(() -> {
            batch.run();
        });
        final BatchStatus batchStatus = batch.getBatchStatus();
        assertFalse(batchStatus.failedButRerunningMaySucceed(), "Rerunning when the batch is erroneously configured may remedy the error situation.");
        assertEquals(BatchStatus.ENDED_WITH_ERROR_CONFIGURATION, batchStatus);
    }

    @Override
    protected String getValidLineOfInputData() {
        return "***REMOVED***366572769 2019-12-232019-12-23AVVED128555 2019-12-012019-12-31000000001040A 4819         PENPOST ***REMOVED***                                                                                           ";
    }

    @Override
    protected Constants.BATCH_TYPE getBatchType() {
        return Constants.BATCH_TYPE.OS;
    }
}