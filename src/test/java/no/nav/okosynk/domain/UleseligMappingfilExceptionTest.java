package no.nav.okosynk.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UleseligMappingfilExceptionTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    void testToString() {

        enteringTestHeaderLogger.debug(null);

        final Exception exception = new Exception();
        final UleseligMappingfilException uleseligMappingfilException =
            new UleseligMappingfilException(exception);

        assertEquals(exception.toString(), uleseligMappingfilException.toString());
    }
}