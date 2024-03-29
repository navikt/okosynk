package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

import java.util.List;

public class AktoerIdent {

  private List<AktoerIdentEntry> identer;
  private String feilmelding;

  public AktoerIdent() {
    //JaxRs
  }

  public List<AktoerIdentEntry> getIdenter() {
    return identer;
  }

  public void setIdenter(final List<AktoerIdentEntry> identer) {
    this.identer = identer;
  }

  public String getFeilmelding() {
    return feilmelding;
  }

  public void setFeilmelding(final String feilmelding) {
    this.feilmelding = feilmelding;
  }
}