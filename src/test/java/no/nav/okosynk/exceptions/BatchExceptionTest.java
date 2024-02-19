package no.nav.okosynk.exceptions;

import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.InputDataNotFoundBatchException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.IoBatchException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.UninterpretableMeldingBatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BatchExceptionTest {

    @Test
    @DisplayName("Test that the string representation is as expected")
    void testTheStringRepresentationOfBatchExecutionException() {

        final RuntimeException runtimeExceptionCause =
                new RuntimeException("Too badd wheather");

        final Collection<? extends AbstractBatchException> exceptions =
                asList(
                        new UninterpretableMeldingBatchException(runtimeExceptionCause),
                        new IoBatchException(runtimeExceptionCause),
                        new GeneralBatchException(runtimeExceptionCause),
                        new InputDataNotFoundBatchException(runtimeExceptionCause)
                );

        exceptions
                .forEach(
                        (batchException) -> assertEquals(
                                runtimeExceptionCause.toString(),
                                batchException.toString(),
                                "The string representation of AbstractBatchException is NOT as expected"
                        )
                );
    }
}
