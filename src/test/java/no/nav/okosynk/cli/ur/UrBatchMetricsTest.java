package no.nav.okosynk.cli.ur;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import no.nav.okosynk.cli.AbstractBatchMetricsTest;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.junit.jupiter.api.Test;

public class UrBatchMetricsTest extends AbstractBatchMetricsTest {

  UrBatchMetricsTest() {
    super(Constants.BATCH_TYPE.UR);
  }

  @Test
  void when_instantiated_then_the_batch_type_should_be_correct() {

    final UrBatchMetrics batchMetrics =
        assertDoesNotThrow(() -> UrBatchMetrics.getSingletonInstance(mock(IOkosynkConfiguration.class)));

    assertEquals(Constants.BATCH_TYPE.UR, batchMetrics.getBatchType());
  }

}