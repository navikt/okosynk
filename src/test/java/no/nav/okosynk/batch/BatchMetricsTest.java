package no.nav.okosynk.batch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchMetricsTest {

  private static final Logger enteringTestHeaderLogger =
      LoggerFactory.getLogger("EnteringTestHeader");

  @Test
  void when_instantiated_then_it_should_not_throw() {

    enteringTestHeaderLogger.debug(null);

    assertDoesNotThrow(() -> new BatchMetrics(new FakeOkosynkConfiguration(), BATCH_TYPE.UR));
  }

  @Test
  void when_push_gateway_endpoint_is_not_configured_and_setting_successful_metrics_then_no_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    System.clearProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY);
    final BatchMetrics batchMetrics = new BatchMetrics(new FakeOkosynkConfiguration(), BATCH_TYPE.UR);
    assertDoesNotThrow(
        () ->
            batchMetrics.setSuccessfulMetrics(ConsumerStatistics.zero(BATCH_TYPE.UR.getName()))
    );
  }

  @Test
  void when_push_gateway_endpoint_is_configured_and_setting_successful_metrics_then_no_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    System.setProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY, "abc:9012");
    final BatchMetrics batchMetrics = new BatchMetrics(new FakeOkosynkConfiguration(), BATCH_TYPE.UR);
    assertDoesNotThrow(
        () ->
            batchMetrics.setSuccessfulMetrics(ConsumerStatistics.zero(BATCH_TYPE.UR.getName()))
    );
  }

  @Test
  void when_push_gateway_endpoint_is_not_configured_and_setting_unsuccessful_metrics_then_no_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    System.clearProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY);
    final BatchMetrics batchMetrics = new BatchMetrics(new FakeOkosynkConfiguration(), BATCH_TYPE.UR);
    assertDoesNotThrow(
        () -> batchMetrics.setUnsuccessfulMetrics()
    );
  }

  @Test
  void when_push_gateway_endpoint_is_configured_and_setting_unsuccessful_metrics_then_no_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    System.setProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY, "abc:9012");
    final BatchMetrics batchMetrics = new BatchMetrics(new FakeOkosynkConfiguration(), BATCH_TYPE.UR);
    assertDoesNotThrow(
        () -> batchMetrics.setUnsuccessfulMetrics()
    );
  }

  @Test
  void when_logging_unsuccessful_metrics_and_push_gateway_endpoint_is_not_configured_then_no_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    System.clearProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY);
    final BatchMetrics batchMetrics = new BatchMetrics(new FakeOkosynkConfiguration(), BATCH_TYPE.UR);
    batchMetrics.setUnsuccessfulMetrics();
    assertDoesNotThrow(
        () -> batchMetrics.log()
    );
  }

  @Test
  void when_logging_unsuccessful_metrics_and_push_gateway_endpoint_is_configured_with_an_invalid_value_then_no_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    System.setProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY, "abc:9012");
    final BatchMetrics batchMetrics = new BatchMetrics(new FakeOkosynkConfiguration(), BATCH_TYPE.UR);
    batchMetrics.setUnsuccessfulMetrics();
    assertDoesNotThrow(
        () -> batchMetrics.log()
    );
  }
}