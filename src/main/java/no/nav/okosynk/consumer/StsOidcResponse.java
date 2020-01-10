package no.nav.okosynk.consumer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StsOidcResponse {

  public StsOidcResponse() {
  }

  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("token_type")
  private String tokenType;
  @JsonProperty("expires_in")
  private Integer expiresIn;

  public String getAccessToken() {
    return this.accessToken;
  }

  public void setAccessToken(final String accessToken) {
    this.accessToken = accessToken;
  }

  public String getTokenType() {
    return this.tokenType;
  }

  public void setTokenType(final String tokenType) {
    this.tokenType = tokenType;
  }

  public Integer getExpiresIn() {
    return this.expiresIn;
  }

  public void setExpiresIn(final Integer expiresIn) {
    this.expiresIn = expiresIn;
  }
}
