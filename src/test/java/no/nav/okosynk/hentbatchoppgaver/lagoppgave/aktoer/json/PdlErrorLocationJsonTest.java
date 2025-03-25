package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdlErrorLocationJsonTest {

    @Test
    void jacksonShouldBeAbleToCreateObject() {
        assertDoesNotThrow(() ->
                new ObjectMapper().readValue("""
                        {
                            "line": "1",
                            "column": "2"
                        }
                        """, PdlErrorLocationJson.class)
        );
    }

}