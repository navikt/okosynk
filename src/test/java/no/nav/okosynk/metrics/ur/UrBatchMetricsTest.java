package no.nav.okosynk.metrics.ur;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.metrics.BatchMetrics;
import no.nav.okosynk.metrics.BatchMetricsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UrBatchMetricsTest extends BatchMetricsTest {

    UrBatchMetricsTest() {
        super(Constants.BATCH_TYPE.UR);
    }

    @Test
    void when_instantiated_then_the_batch_type_should_be_correct() {

        final BatchMetrics batchMetrics = assertDoesNotThrow(() -> new BatchMetrics(okosynkConfiguration));

        assertEquals(Constants.BATCH_TYPE.UR, batchMetrics.getBatchType());
    }

}
