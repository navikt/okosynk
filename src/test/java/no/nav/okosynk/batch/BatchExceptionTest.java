package no.nav.okosynk.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchExceptionTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    @DisplayName("Test that the string representation is as expected")
    void testTheStringRepresentationOfBatchExecutionException() {

        enteringTestHeaderLogger.debug(null);

        final RuntimeException runtimeExceptionCause =
            new RuntimeException("Too badd wheather");

        final Collection<? extends AbstractBatchException> exceptions =
            new ArrayList() {{
                add(new UninterpretableMeldingBatchException(runtimeExceptionCause));
                add(new IoBatchException(runtimeExceptionCause));
                add(new GeneralBatchException(runtimeExceptionCause));
                add(new InputDataNotFoundBatchException(runtimeExceptionCause));
            }};

        exceptions
            .stream()
            .forEach(
                (batchException) -> {
                    assertEquals(
                        runtimeExceptionCause.toString(),
                        batchException.toString(),
                        "The string representation of AbstractBatchException is NOT as expected"
                    );
                }
            );
    }
}
