package no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions;

import no.nav.okosynk.exceptions.AbstractBatchException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TooManyInputDataLinesBatchExceptionTest {

    @Test
    void when_instantiated_then_cause_should_always_be_null() {

        assertDoesNotThrow(() -> new TooManyInputDataLinesBatchException(1, 2));
        AbstractBatchException batchException = new TooManyInputDataLinesBatchException(1, 2);

        assertNull(batchException.getCause());
        assertTrue(batchException.toString().contains(" 1"));
        assertTrue(batchException.toString().contains(" 2"));
    }
}
