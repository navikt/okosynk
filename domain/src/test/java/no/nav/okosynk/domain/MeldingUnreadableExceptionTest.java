package no.nav.okosynk.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import no.nav.okosynk.domain.MeldingUnreadableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeldingUnreadableExceptionTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    @DisplayName("Test that the message is as expected")
    void testMessage() {

        enteringTestHeaderLogger.debug(null);

        final String expectedMessage = "Bad taste";

        final MeldingUnreadableException meldingUnreadableException =
            new MeldingUnreadableException(expectedMessage);

        assertEquals(expectedMessage, meldingUnreadableException.getMessage());
    }

    @Test
    @DisplayName("Test that the cause is as expected")
    void testCause() {

        enteringTestHeaderLogger.debug(null);

        final RuntimeException expectedruntimeException = new RuntimeException();

        final MeldingUnreadableException meldingUnreadableException =
            new MeldingUnreadableException(expectedruntimeException);

        assertEquals(expectedruntimeException, meldingUnreadableException.getCause());
        assertSame(expectedruntimeException, meldingUnreadableException.getCause());
    }

    @Test
    @DisplayName("Test that the message and the cause are as expected")
    void testMessageAndCause() {

        enteringTestHeaderLogger.debug(null);

        final String expectedMessage = "Too cold for reptiles";
        final RuntimeException expectedruntimeException = new RuntimeException();

        final MeldingUnreadableException meldingUnreadableException =
            new MeldingUnreadableException(expectedMessage, expectedruntimeException);

        assertEquals(expectedMessage, meldingUnreadableException.getMessage());
        assertEquals(expectedruntimeException, meldingUnreadableException.getCause());
        assertSame(expectedruntimeException, meldingUnreadableException.getCause());
    }
}
