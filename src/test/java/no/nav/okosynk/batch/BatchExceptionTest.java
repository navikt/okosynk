package no.nav.okosynk.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BatchExceptionTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    @DisplayName("Test that the string representation is as expected")
    void testTheStringRepresentationOfBatchExecutionException() {

        enteringTestHeaderLogger.debug(null);

        final RuntimeException runtimeExceptionCause =
            new RuntimeException("Too badd wheather");

        final BatchException batchException =
            new BatchException(runtimeExceptionCause);

        assertEquals(
            runtimeExceptionCause.toString(),
            batchException.toString(),
            "The string representation of BatchException is NOT as expected");
    }
}
