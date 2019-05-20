package no.nav.okosynk.consumer.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class DatoKonverteringExceptionTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");


    @Test
    public void testSuperCause() {

        enteringTestHeaderLogger.debug(null);

        final String expectedMessage = "Not correct colour";
        final Exception expectedException = new Exception();
        final DatoKonverteringException datoKonverteringException =
            new DatoKonverteringException(expectedException, expectedMessage);
        assertEquals(expectedException, datoKonverteringException.getCause());
        assertSame(expectedException, datoKonverteringException.getCause());
    }

    @Test
    public void testMessage() {

        enteringTestHeaderLogger.debug(null);

        final String expectedMessage = "Not correct colour";
        final Exception expectedException = new Exception();
        final DatoKonverteringException datoKonverteringException =
            new DatoKonverteringException(expectedException, expectedMessage);
        assertEquals(expectedMessage, datoKonverteringException.getMessage());
    }
}
