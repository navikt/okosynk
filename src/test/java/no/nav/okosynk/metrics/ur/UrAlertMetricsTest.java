package no.nav.okosynk.metrics.ur;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import no.nav.okosynk.metrics.AbstractAlertMetricsTest;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.junit.jupiter.api.Test;

class UrAlertMetricsTest extends AbstractAlertMetricsTest {

  public UrAlertMetricsTest() {
    super(Constants.BATCH_TYPE.UR);
  }

  @Test
  void when_instantiated_then_the_batch_type_should_be_correct() {

    final UrAlertMetrics alertMetrics =
        assertDoesNotThrow(() -> UrAlertMetrics.getSingletonInstance(mock(IOkosynkConfiguration.class)));

    assertEquals(Constants.BATCH_TYPE.UR, alertMetrics.getBatchType());
  }

}