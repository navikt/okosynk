package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

import java.util.Collection;
import java.util.Optional;

public class PdlPersonIdentCollection {

    private final Collection<PdlPersonIdent> pdlPersonIdenter;

    private PdlPersonIdentCollection(Builder builder) {
        this.pdlPersonIdenter = builder.pdlPersonIdenter;
    }

    public Optional<PdlPersonIdent> extractGjeldendeAktorIdPdlPersonIdent() {
        return this.pdlPersonIdenter
                .stream()
                .filter(personIdent -> PdlGruppe.AKTORID.equals(personIdent.getGruppe()))
                .findAny();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Collection<PdlPersonIdent> pdlPersonIdenter;

        public Builder withPdlPersonIdenter(Collection<PdlPersonIdent> pdlPersonIdenter) {
            this.pdlPersonIdenter = pdlPersonIdenter;
            return this;
        }

        public Builder pdlPersonIdent(PdlPersonIdent pdlPersonIdent) {
            this.pdlPersonIdenter.add(pdlPersonIdent);
            return this;
        }

        public PdlPersonIdentCollection build() {
            return new PdlPersonIdentCollection(this);
        }
    }
}