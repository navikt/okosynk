package no.nav.okosynk.consumer;

public class STSOidcResponse {

  public STSOidcResponse() {

  }

  private String accessToken;
  private String tokenType;
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
