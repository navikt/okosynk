package no.nav.okosynk.consumer.aktoer;

class AktoerIdentEntry {

  private String ident;
  private String identgruppe;
  private boolean gjeldende;

  public AktoerIdentEntry() {
    //JaxRS
  }

  public String getIdent() {
    return ident;
  }

  public void setIdent(String ident) {
    this.ident = ident;
  }

  public String getIdentgruppe() {
    return identgruppe;
  }

  public void setIdentgruppe(String identgruppe) {
    this.identgruppe = identgruppe;
  }

  public boolean isGjeldende() {
    return gjeldende;
  }

  public void setGjeldende(boolean gjeldende) {
    this.gjeldende = gjeldende;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null) {
      return false;
    }
    if (!other.getClass().equals(this.getClass())) {
      return false;
    }
    if (this.getIdent() == null) {
      return false;
    }
    if (((AktoerIdentEntry) other).getIdent() == null) {
      return false;
    }
    if (other == this) {
      return true;
    }

    return (this.getIdent().equals(((AktoerIdentEntry) other).getIdent()));
  }
}
