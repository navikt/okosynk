package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PostPdlHentIdenterDataResponseJsonTest {
    @Test
    void jacksonShouldBeAbleToCreateObject() {
        assertDoesNotThrow(() ->
                new ObjectMapper().readValue("""
                        {
                            "hentIdenter": {
                                "id": "12345612345",
                                "name": "Test Name"
                            }
                        }
                        """, PostPdlHentIdenterDataResponseJson.class)
        );
    }
}