package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PostPdlHentIdenterHentIdenterResponseJsonTest {
    @Test
    void jacksonShouldBeAbleToCreateObject() {
        assertDoesNotThrow(() ->
                new ObjectMapper().readValue("""
                        {
                            "identer": [
                                {
                                    "ident": "12345",
                                    "gruppe": "AKTORID"
                                }
                            ]
                        }
                        """, PostPdlHentIdenterHentIdenterResponseJson.class)
        );
    }
}