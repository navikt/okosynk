package no.nav.okosynk.cli.os;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import no.nav.okosynk.cli.AbstractAlertMetricsTest;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.junit.jupiter.api.Test;

public class OsAlertMetricsTest extends AbstractAlertMetricsTest {

  public OsAlertMetricsTest() {
    super(Constants.BATCH_TYPE.OS);
  }

  @Test
  void when_instantiated_then_the_batch_type_should_be_correct() {

    final OsAlertMetrics alertMetrics =
        assertDoesNotThrow(() -> OsAlertMetrics.getSingletonInstance(mock(IOkosynkConfiguration.class)));

    assertEquals(Constants.BATCH_TYPE.OS, alertMetrics.getBatchType());
  }

}