package no.nav.okosynk.consumer.oppgave;

import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class ErrorResponse {

  private String uuid;
  private String feilmelding;

  public ErrorResponse() {
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public void setFeilmelding(String feilmelding) {
    this.feilmelding = feilmelding;
  }

  public String getFeilmelding() {
    return feilmelding;
  }

  public String getUuid() {
    return uuid;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
        .append("uuid", uuid)
        .append("feilmelding", feilmelding)
        .toString();
  }
}
