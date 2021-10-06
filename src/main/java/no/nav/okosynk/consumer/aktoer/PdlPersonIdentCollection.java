package no.nav.okosynk.consumer.aktoer;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Singular;

import java.util.Collection;
import java.util.Optional;

@Builder(setterPrefix = "with", access = AccessLevel.PUBLIC)
public class PdlPersonIdentCollection {

    @Singular("pdlPersonIdent")
    private final Collection<PdlPersonIdent> pdlPersonIdenter;

    public Optional<PdlPersonIdent> extractGjeldendeAktorIdPdlPersonIdent() {
        return
                this.pdlPersonIdenter
                        .stream()
                        .filter(personIdent -> PdlGruppe.AKTOERID.equals(personIdent.getGruppe()))
                        .findAny()
                ;
    }
}