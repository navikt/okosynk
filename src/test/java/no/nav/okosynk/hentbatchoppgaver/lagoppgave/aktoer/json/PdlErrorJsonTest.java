package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.okosynk.comm.AzureAdTokenSuccessResponseJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PdlErrorJsonTest {

    @Test
    void jacksonShouldBeAbleToCreateObject() {
                    assertDoesNotThrow(() ->
                            new ObjectMapper().readValue("""
                                    {
                                        "message": "An error occurred",
                                        "locations": [
                                            {
                                                "line": 10,
                                                "column": 15
                                            }
                                        ],
                                        "path": [
                                            "data",
                                            "user",
                                            "name"
                                        ],
                                        "extensions": {
                                            "code": "BAD_REQUEST",
                                            "classification": "ClientError"
                                        }
                                    }ß
                                    """, AzureAdTokenSuccessResponseJson.class)
                    );
                }
}