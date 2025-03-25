package no.nav.okosynk.comm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureAdTokenSuccessResponseJson {
    @JsonProperty("token_type")
    private final String tokenType;
    @JsonProperty("expires_in")
    private final int expiresIn;
    @JsonProperty("ext_expires_in")
    private final int extExpiresIn;
    @JsonProperty("access_token")
    private final String accessToken;

    public AzureAdTokenSuccessResponseJson() {
        this.tokenType = null;
        this.expiresIn = 0;
        this.extExpiresIn = 0;
        this.accessToken = null;
    }

    public AzureAdTokenSuccessResponseJson(String tokenType, int expiresIn, int extExpiresIn, String accessToken) {
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.extExpiresIn = extExpiresIn;
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public int getExtExpiresIn() {
        return extExpiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String toString() {
        return "AzureAdTokenSuccessResponseJson{" +
                "tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", extExpiresIn=" + extExpiresIn +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }

    public static class Builder {
        private String tokenType;
        private int expiresIn;
        private int extExpiresIn;
        private String accessToken;

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder expiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder extExpiresIn(int extExpiresIn) {
            this.extExpiresIn = extExpiresIn;
            return this;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public AzureAdTokenSuccessResponseJson build() {
            return new AzureAdTokenSuccessResponseJson(tokenType, expiresIn, extExpiresIn, accessToken);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}