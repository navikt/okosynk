package no.nav.okosynk.metrics;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.config.Constants;
import org.junit.jupiter.api.Test;

class OsBatchMetricsTest extends BatchMetricsTest {

  OsBatchMetricsTest() {
    super(Constants.BATCH_TYPE.OS);
  }

  @Test
  void when_instantiated_then_the_batch_type_should_be_correct() {

    OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);
    when (okosynkConfiguration.getBatchType()).thenReturn(Constants.BATCH_TYPE.OS);

    final BatchMetrics batchMetrics = assertDoesNotThrow(() -> new BatchMetrics(okosynkConfiguration));

    assertEquals(Constants.BATCH_TYPE.OS, batchMetrics.getBatchType());
  }
}
