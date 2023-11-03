package no.nav.okosynk.comm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StsOidcResponseTest {

    @Test
    void when_expectedString_is_deserialized_then_a_valid_object_should_be_produced() {

        final String expectedAccessToken = "eyJraWQiOiJmMDhiYzdhZC0yZDZmLTQwNjYtOTVkYy02ZjY0MzdmYjNmYmMiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzcnZib2tvc3luazAwMiIsImF1ZCI6WyJzcnZib2tvc3luazAwMiIsInByZXByb2QubG9jYWwiXSwidmVyIjoiMS4wIiwibmJmIjoxNTc4NjcyMTI1LCJhenAiOiJzcnZib2tvc3luazAwMiIsImlkZW50VHlwZSI6IlN5c3RlbXJlc3N1cnMiLCJhdXRoX3RpbWUiOjE1Nzg2NzIxMjUsImlzcyI6Imh0dHBzOlwvXC9zZWN1cml0eS10b2tlbi1zZXJ2aWNlLm5haXMucHJlcHJvZC5sb2NhbCIsImV4cCI6MTU3ODY3NTcyNSwiaWF0IjoxNTc4NjcyMTI1LCJqdGkiOiIzMDc5MDRlMi1iNjc5LTQ0MGEtOWJkNS0yNTE1MzMzNDMzOWYifQ.H7Zz3tnoWnSZRBqVeCP2k7uJZx_gmL_RrtYfM4feSzOk6o_1ev0RYvZPeSTS_fcQ0QwguCkcufLod6JLS-pH8o8XM9X6rkKBcbd1l3ZHIGqYx0IfESrAs4cCUaYdKzTuM9EC0erpdvVivXZ-jyq8DMLRpRBZ0k9v7o0JKD2WpHscNqbnTwheyyDukYqTpavQiubCmXQVLjF_Pj_Dyhn-6yl4qZ1JmqhiASqpGXQqJUrcukYkEJUfRW8fPKgJYCbcM8Qd-I2CYNxqYKVZkp1HJ2FfZZuO08FjpEuuqIjv1UhY8cX-RxbFgH-NTDhVyKpdIwPHtiMX6ot_mj3UNdihuQ";
        final String expectedTokenType = "Bearer";
        final Integer expectedExpiresIn = 3600;

        final String expectedStsOidcResponseAsString =
                "{"
                        + "    \"access_token\":\"" + expectedAccessToken + "\","
                        + "    \"token_type\":\"" + expectedTokenType + "\","
                        + "    \"expires_in\":" + expectedExpiresIn
                        + "}";

        final StsOidcResponse actualStsOidcResponse =
                assertDoesNotThrow(
                        () ->
                                new ObjectMapper().readValue(expectedStsOidcResponseAsString, StsOidcResponse.class)
                );

        final String actualAccessToken = actualStsOidcResponse.getAccessToken();
        assertEquals(expectedAccessToken, actualAccessToken);
        final String actualTokenType = actualStsOidcResponse.getTokenType();
        assertEquals(expectedTokenType, actualTokenType);
        final Integer actualExpiresIn = actualStsOidcResponse.getExpiresIn();
        assertEquals(expectedExpiresIn, actualExpiresIn);
    }

    @Test
    void when_StsOidcResponse_is_serialized_then_a_valid_string_should_be_produced()
            throws JsonProcessingException {

        final String expectedAccessToken = "eyJraWQiOiJmMDhiYzdhZC0yZDZmLTQwNjYtOTVkYy02ZjY0MzdmYjNmYmMiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzcnZib2tvc3luazAwMiIsImF1ZCI6WyJzcnZib2tvc3luazAwMiIsInByZXByb2QubG9jYWwiXSwidmVyIjoiMS4wIiwibmJmIjoxNTc4NjcyMTI1LCJhenAiOiJzcnZib2tvc3luazAwMiIsImlkZW50VHlwZSI6IlN5c3RlbXJlc3N1cnMiLCJhdXRoX3RpbWUiOjE1Nzg2NzIxMjUsImlzcyI6Imh0dHBzOlwvXC9zZWN1cml0eS10b2tlbi1zZXJ2aWNlLm5haXMucHJlcHJvZC5sb2NhbCIsImV4cCI6MTU3ODY3NTcyNSwiaWF0IjoxNTc4NjcyMTI1LCJqdGkiOiIzMDc5MDRlMi1iNjc5LTQ0MGEtOWJkNS0yNTE1MzMzNDMzOWYifQ.H7Zz3tnoWnSZRBqVeCP2k7uJZx_gmL_RrtYfM4feSzOk6o_1ev0RYvZPeSTS_fcQ0QwguCkcufLod6JLS-pH8o8XM9X6rkKBcbd1l3ZHIGqYx0IfESrAs4cCUaYdKzTuM9EC0erpdvVivXZ-jyq8DMLRpRBZ0k9v7o0JKD2WpHscNqbnTwheyyDukYqTpavQiubCmXQVLjF_Pj_Dyhn-6yl4qZ1JmqhiASqpGXQqJUrcukYkEJUfRW8fPKgJYCbcM8Qd-I2CYNxqYKVZkp1HJ2FfZZuO08FjpEuuqIjv1UhY8cX-RxbFgH-NTDhVyKpdIwPHtiMX6ot_mj3UNdihuQ";
        final String expectedTokenType = "Bearer";
        final Integer expectedExpiresIn = 3600;

        final StsOidcResponse expectedStsOidcResponse = new StsOidcResponse();
        expectedStsOidcResponse.setAccessToken(expectedAccessToken);
        expectedStsOidcResponse.setExpiresIn(expectedExpiresIn);
        expectedStsOidcResponse.setTokenType(expectedTokenType);

        final String actualStsOidcResponseAsString =
                new ObjectMapper().writeValueAsString(expectedStsOidcResponse);

        assertTrue(actualStsOidcResponseAsString.contains("access_token"));
        assertTrue(actualStsOidcResponseAsString.contains(expectedAccessToken));
        assertTrue(actualStsOidcResponseAsString.contains("token_type"));
        assertTrue(actualStsOidcResponseAsString.contains(expectedTokenType));
        assertTrue(actualStsOidcResponseAsString.contains("expires_in"));
        assertTrue(actualStsOidcResponseAsString.contains(String.valueOf(expectedExpiresIn)));
    }

    @Test
    void when_StsOidcResponse_is_serialized_and_deserialized_then_an_equal_object_should_result()
            throws JsonProcessingException {

        final String expectedAccessToken = "eyJraWQiOiJmMDhiYzdhZC0yZDZmLTQwNjYtOTVkYy02ZjY0MzdmYjNmYmMiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzcnZib2tvc3luazAwMiIsImF1ZCI6WyJzcnZib2tvc3luazAwMiIsInByZXByb2QubG9jYWwiXSwidmVyIjoiMS4wIiwibmJmIjoxNTc4NjcyMTI1LCJhenAiOiJzcnZib2tvc3luazAwMiIsImlkZW50VHlwZSI6IlN5c3RlbXJlc3N1cnMiLCJhdXRoX3RpbWUiOjE1Nzg2NzIxMjUsImlzcyI6Imh0dHBzOlwvXC9zZWN1cml0eS10b2tlbi1zZXJ2aWNlLm5haXMucHJlcHJvZC5sb2NhbCIsImV4cCI6MTU3ODY3NTcyNSwiaWF0IjoxNTc4NjcyMTI1LCJqdGkiOiIzMDc5MDRlMi1iNjc5LTQ0MGEtOWJkNS0yNTE1MzMzNDMzOWYifQ.H7Zz3tnoWnSZRBqVeCP2k7uJZx_gmL_RrtYfM4feSzOk6o_1ev0RYvZPeSTS_fcQ0QwguCkcufLod6JLS-pH8o8XM9X6rkKBcbd1l3ZHIGqYx0IfESrAs4cCUaYdKzTuM9EC0erpdvVivXZ-jyq8DMLRpRBZ0k9v7o0JKD2WpHscNqbnTwheyyDukYqTpavQiubCmXQVLjF_Pj_Dyhn-6yl4qZ1JmqhiASqpGXQqJUrcukYkEJUfRW8fPKgJYCbcM8Qd-I2CYNxqYKVZkp1HJ2FfZZuO08FjpEuuqIjv1UhY8cX-RxbFgH-NTDhVyKpdIwPHtiMX6ot_mj3UNdihuQ";
        final String expectedTokenType = "Bearer";
        final Integer expectedExpiresIn = 3600;

        final StsOidcResponse expectedStsOidcResponse = new StsOidcResponse();
        expectedStsOidcResponse.setAccessToken(expectedAccessToken);
        expectedStsOidcResponse.setExpiresIn(expectedExpiresIn);
        expectedStsOidcResponse.setTokenType(expectedTokenType);
        final String stsOidcResponseAsString =
                new ObjectMapper().writeValueAsString(expectedStsOidcResponse);
        final StsOidcResponse actualStsOidcResponse =
                assertDoesNotThrow(
                        () ->
                                new ObjectMapper().readValue(stsOidcResponseAsString, StsOidcResponse.class)
                );
        assertEquals(expectedStsOidcResponse.getAccessToken(), actualStsOidcResponse.getAccessToken());
        assertEquals(expectedStsOidcResponse.getTokenType(), actualStsOidcResponse.getTokenType());
        assertEquals(expectedStsOidcResponse.getExpiresIn(), actualStsOidcResponse.getExpiresIn());
    }
}
