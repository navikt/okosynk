package no.nav.okosynk.metrics;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.synkroniserer.consumer.ConsumerStatistics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BatchMetricsTest {

    protected final Constants.BATCH_TYPE batchType;
    protected final OkosynkConfiguration okosynkConfiguration;

    protected BatchMetricsTest(final Constants.BATCH_TYPE batchType) {
        this.batchType = batchType;
        this.okosynkConfiguration = mock(OkosynkConfiguration.class);
        when(okosynkConfiguration.getBatchType()).thenReturn(batchType);
    }

    @Test
    void when_instantiated_then_it_should_not_throw() {
        assertDoesNotThrow(() -> new BatchMetrics(okosynkConfiguration));
    }

    @Test
    void when_push_gateway_endpoint_is_not_configured_and_setting_successful_metrics_then_no_exception_should_be_thrown() {
        System.clearProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY);

        final BatchMetrics batchMetrics = new BatchMetrics(okosynkConfiguration);
        assertDoesNotThrow(() -> batchMetrics.setSuccessfulMetrics(ConsumerStatistics.zero(this.batchType.getName()))
        );
    }

    @Test
    void when_push_gateway_endpoint_is_configured_and_setting_successful_metrics_then_no_exception_should_be_thrown() {
        System.setProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY, "abc:9012");
        final BatchMetrics batchMetrics = new BatchMetrics(okosynkConfiguration);
        assertDoesNotThrow(
                () ->
                        batchMetrics.setSuccessfulMetrics(ConsumerStatistics.zero(this.batchType.getName()))
        );
    }

    @Test
    void when_push_gateway_endpoint_is_not_configured_and_setting_unsuccessful_metrics_then_no_exception_should_be_thrown() {
        System.clearProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY);
        final BatchMetrics batchMetrics = new BatchMetrics(okosynkConfiguration);
        assertDoesNotThrow(batchMetrics::setUnsuccessfulMetrics);
    }

    @Test
    void when_push_gateway_endpoint_is_configured_and_setting_unsuccessful_metrics_then_no_exception_should_be_thrown() {
        System.setProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY, "abc:9012");
        final BatchMetrics batchMetrics = new BatchMetrics(okosynkConfiguration);
        assertDoesNotThrow(batchMetrics::setUnsuccessfulMetrics);
    }

    @Test
    void when_logging_unsuccessful_metrics_and_push_gateway_endpoint_is_not_configured_then_no_exception_should_be_thrown() {
        System.clearProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY);
        final BatchMetrics batchMetrics = new BatchMetrics(okosynkConfiguration);
        batchMetrics.setUnsuccessfulMetrics();
        assertDoesNotThrow(batchMetrics::log);
    }

    @Test
    void when_logging_unsuccessful_metrics_and_push_gateway_endpoint_is_configured_with_an_invalid_value_then_no_exception_should_be_thrown() {
        System.setProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY, "abc:9012");
        final BatchMetrics batchMetrics = new BatchMetrics(okosynkConfiguration);
        batchMetrics.setUnsuccessfulMetrics();
        assertDoesNotThrow(batchMetrics::log);
    }

}
