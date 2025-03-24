package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

public class PdlPersonIdent {
    private final PdlGruppe gruppe;
    private final String ident;

    private PdlPersonIdent(Builder builder) {
        this.gruppe = builder.gruppe;
        this.ident = builder.ident;
    }

    public PdlGruppe getGruppe() {
        return gruppe;
    }

    public String getIdent() {
        return ident;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PdlGruppe gruppe;
        private String ident;

        public Builder withGruppe(PdlGruppe gruppe) {
            this.gruppe = gruppe;
            return this;
        }

        public Builder withIdent(String ident) {
            this.ident = ident;
            return this;
        }

        public PdlPersonIdent build() {
            return new PdlPersonIdent(this);
        }
    }
}