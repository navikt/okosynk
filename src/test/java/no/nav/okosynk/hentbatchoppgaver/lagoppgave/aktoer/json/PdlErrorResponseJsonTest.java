package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PdlErrorResponseJsonTest {

    @Test
    void jacksonShouldBeAbleToCreateObject() {
        assertDoesNotThrow(() ->
                new ObjectMapper().readValue("""
                        {
                            "errors": [
                                {
                                    "message": "Some error message",
                                    "locations": [
                                        {
                                            "line": "1",
                                            "column": "2"
                                        }
                                    ]
                                }
                            ]
                        }
                        """, PdlErrorResponseJson.class)
        );
    }

}