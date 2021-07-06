    package no.nav.okosynk.consumer.security;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Getter
@ToString
@Builder
public class AzureAdTokenSuccessResponseJson {
    @JsonProperty("token_type")
    private final String tokenType;
    @JsonProperty("expires_in")
    private final int expiresIn;
    @JsonProperty("access_token")
    private final String accessToken;
}
