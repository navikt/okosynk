package no.nav.okosynk.metrics.ur;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.metrics.AbstractBatchMetricsTest;
import no.nav.okosynk.config.Constants;
import org.junit.jupiter.api.Test;

class UrBatchMetricsTest extends AbstractBatchMetricsTest {

  UrBatchMetricsTest() {
    super(Constants.BATCH_TYPE.UR);
  }

  @Test
  void when_instantiated_then_the_batch_type_should_be_correct() {

    final UrBatchMetrics batchMetrics =
        assertDoesNotThrow(() -> UrBatchMetrics.getSingletonInstance(mock(OkosynkConfiguration.class)));

    assertEquals(Constants.BATCH_TYPE.UR, batchMetrics.getBatchType());
  }

}
