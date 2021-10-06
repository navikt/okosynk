package no.nav.okosynk.consumer.aktoer;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class PdlPersonIdent {
    private final PdlGruppe gruppe;
    private final String ident;
}
