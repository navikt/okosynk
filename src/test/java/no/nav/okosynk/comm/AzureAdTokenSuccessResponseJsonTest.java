package no.nav.okosynk.comm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AzureAdTokenSuccessResponseJsonTest {

    @Test
    void jacksonShouldBeAbleToCreateObject() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        assertDoesNotThrow(() ->
        objectMapper.readValue("""
                {
                    "token_type": "Bearer",
                    "expires_in": 1000,
                    "ext_expires_in": 2000,
                    "access_token": "xyz324lkjklj234hkj32hkjb3"
                }
                """, AzureAdTokenSuccessResponseJson.class)
        );
    }

}