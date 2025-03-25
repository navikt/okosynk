package no.nav.okosynk.comm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AzureAdTokenErrorResponseJsonTest {
    @Test
    void jacksonShouldBeAbleToCreateObject() {
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        assertDoesNotThrow(() -> objectMapper.readValue("""
                {
                    "error": "invalid_request",
                    "error_description": "The request is missing a required parameter.",
                    "error_codes": [400, 401],
                    "timestamp": "2023-10-01T12:00:00Z",
                    "trace_id": "abc123",
                    "correlation_id": "def456",
                    "error_uri": "https://example.com/error"
                }
                """, AzureAdTokenErrorResponseJson.class));
    }
}