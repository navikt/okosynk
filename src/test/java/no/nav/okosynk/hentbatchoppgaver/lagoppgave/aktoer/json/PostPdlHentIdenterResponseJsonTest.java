package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostPdlHentIdenterResponseJsonTest {
    @Test
    void shouldBeAbleToCreateObjectFromJson() {
        assertDoesNotThrow(() ->
                new ObjectMapper().readValue("""
                        {
                            "data": {
                                "hentIdenter": {
                                    "identer": [
                                        {
                                            "ident": "12345612345",
                                            "gruppe": "FOLKEREGISTERIDENT"
                                        }
                                    ]
                                }
                            }
                        }
                        """, PostPdlHentIdenterResponseJson.class)
        );
    }
}