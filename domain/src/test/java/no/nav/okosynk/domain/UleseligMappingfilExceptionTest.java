package no.nav.okosynk.domain;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UleseligMappingfilExceptionTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    public void testToString() {

        enteringTestHeaderLogger.debug(null);

        final Exception exception = new Exception();
        final UleseligMappingfilException uleseligMappingfilException =
            new UleseligMappingfilException(exception);

        assertEquals(exception.toString(), uleseligMappingfilException.toString());
    }
}
