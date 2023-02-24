package no.nav.okosynk.metrics;

import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public abstract class AbstractAlertMetricsTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private final Constants.BATCH_TYPE batchType;

    public AbstractAlertMetricsTest(final BATCH_TYPE batchType) {
        this.batchType = batchType;
    }

    @Test
    void when_instantiated_then_it_should_not_throw() {

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        assertDoesNotThrow(() -> AlertMetricsFactory.get(okosynkConfiguration, this.batchType));
    }

    @Test
    void when_push_gateway_endpoint_is_not_configured_and_passing_non_alerting_batch_status_then_no_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        System.clearProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final AbstractAlertMetrics alertMetrics = AlertMetricsFactory.get(okosynkConfiguration, this.batchType);
        assertDoesNotThrow(
                () ->
                        alertMetrics.generateCheckTheLogAlertBasedOnBatchStatus(
                                BatchStatus.ENDED_WITH_WARNING_BATCH_INPUT_DATA_COULD_NOT_BE_DELETED_AFTER_OK_RUN)
        );
    }

    @Test
    void when_push_gateway_endpoint_is_not_configured_and_passing_alerting_batch_status_then_no_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        System.clearProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final AbstractAlertMetrics alertMetrics = AlertMetricsFactory.get(okosynkConfiguration, this.batchType);
        assertDoesNotThrow(
                () ->
                        alertMetrics.generateCheckTheLogAlertBasedOnBatchStatus(BatchStatus.ENDED_WITH_ERROR_GENERAL)
        );
    }

    @Test
    void when_push_gateway_endpoint_is_configured_and_and_passing_non_alerting_batch_status_then_no_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        System.setProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY, "abc:9012");
        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final AbstractAlertMetrics alertMetrics = AlertMetricsFactory.get(okosynkConfiguration, this.batchType);
        assertDoesNotThrow(
                () ->
                        alertMetrics.generateCheckTheLogAlertBasedOnBatchStatus(
                                BatchStatus.ENDED_WITH_WARNING_BATCH_INPUT_DATA_COULD_NOT_BE_DELETED_AFTER_OK_RUN)
        );
    }

    @Test
    void when_push_gateway_endpoint_is_configured_and_and_passing_alerting_batch_status_then_no_exception_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        System.setProperty(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY, "abc:9012");
        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final AbstractAlertMetrics alertMetrics = AlertMetricsFactory.get(okosynkConfiguration, this.batchType);
        assertDoesNotThrow(
                () ->
                        alertMetrics.generateCheckTheLogAlertBasedOnBatchStatus(
                                BatchStatus.ENDED_WITH_ERROR_GENERAL)
        );
    }
}