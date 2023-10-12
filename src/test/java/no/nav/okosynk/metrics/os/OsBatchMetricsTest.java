package no.nav.okosynk.metrics.os;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.metrics.AbstractBatchMetricsTest;
import no.nav.okosynk.config.Constants;
import org.junit.jupiter.api.Test;

class OsBatchMetricsTest extends AbstractBatchMetricsTest {

  OsBatchMetricsTest() {
    super(Constants.BATCH_TYPE.OS);
  }

  @Test
  void when_instantiated_then_the_batch_type_should_be_correct() {

    final OsBatchMetrics batchMetrics =
        assertDoesNotThrow(() -> OsBatchMetrics.getSingletonInstance(mock(OkosynkConfiguration.class)));

    assertEquals(Constants.BATCH_TYPE.OS, batchMetrics.getBatchType());
  }
}
