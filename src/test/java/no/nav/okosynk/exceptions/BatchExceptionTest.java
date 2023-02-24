package no.nav.okosynk.exceptions;

import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.InputDataNotFoundBatchException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.IoBatchException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.UninterpretableMeldingBatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BatchExceptionTest {

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
