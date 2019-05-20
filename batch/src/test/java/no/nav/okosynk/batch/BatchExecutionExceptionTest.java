package no.nav.okosynk.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BatchExecutionExceptionTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    @DisplayName("Test that the string representation is as expected")
    void testTheStringRepresentationOfBatchExecutionException() {

        enteringTestHeaderLogger.debug(null);

        final RuntimeException runtimeExceptionCause =
            new RuntimeException("Too badd wheather");

        final BatchExecutionException batchExecutionException =
            new BatchExecutionException(runtimeExceptionCause);

        assertEquals(
            runtimeExceptionCause.toString(),
            batchExecutionException.toString(),
            "The string representation of BatchExecutionException is NOT as expected");
    }
}
