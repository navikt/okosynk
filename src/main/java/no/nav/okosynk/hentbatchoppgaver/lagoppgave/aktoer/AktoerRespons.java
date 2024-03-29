package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

import lombok.Getter;

@Getter
public class AktoerRespons {

  private final String feilmelding;
  private final String aktoerId;

  private AktoerRespons(final String aktoerId, final String feilmelding) {
    this.aktoerId = aktoerId;
    this.feilmelding = feilmelding;
  }

  public static AktoerRespons feil(final String feilmelding) {
    return new AktoerRespons(null, feilmelding);
  }

  public static AktoerRespons ok(final String aktoerId) {
    return new AktoerRespons(aktoerId, null);
  }

  public boolean isOk() {
    return feilmelding == null;
  }
}
