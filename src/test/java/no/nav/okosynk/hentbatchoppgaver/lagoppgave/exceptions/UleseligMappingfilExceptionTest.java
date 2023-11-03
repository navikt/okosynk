package no.nav.okosynk.hentbatchoppgaver.lagoppgave.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UleseligMappingfilExceptionTest {

    @Test
    void testToString() {

        final Exception exception = new Exception();
        final UleseligMappingfilException uleseligMappingfilException =
                new UleseligMappingfilException(exception);

        assertEquals(exception.toString(), uleseligMappingfilException.toString());
    }
}
