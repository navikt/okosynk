package no.nav.okosynk.consumer.aktoer;

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

    public void setIdenter(List<AktoerIdentEntry> identer) {
        this.identer = identer;
    }

    public String getFeilmelding() {
        return feilmelding;
    }

    public void setFeilmelding(String feilmelding) {
        this.feilmelding = feilmelding;
    }
}