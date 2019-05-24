package no.nav.okosynk.consumer.aktoer;

public class AktoerIdentEntry {
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
}
