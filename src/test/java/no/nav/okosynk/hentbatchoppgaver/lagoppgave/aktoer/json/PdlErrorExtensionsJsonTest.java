package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdlErrorExtensionsJsonTest {
    @Test
    void jacksonShouldBeAbleToCreateObject() {
        assertDoesNotThrow(() ->
                new ObjectMapper().readValue("""
                        {
                            "code": "some-code",
                            "classification": "some-classification"
                        }
                        """, PdlErrorExtensionsJson.class)
        );
    }
}