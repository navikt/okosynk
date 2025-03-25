package no.nav.okosynk.comm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AzureAdAuthenticationClientTest {
    @Test
    void testGetToken() throws JsonProcessingException {
        final String postResponseEntityAsString = """
                {
                  "token_type": "Bearer",
                  "expires_in": 3599,
                  "ext_expires_in": 3599,
                  "access_token": "keylotsofcharacters"
                }
        """;
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        objectMapper.readValue(postResponseEntityAsString, AzureAdTokenSuccessResponseJson.class);

    }
}