package no.nav.okosynk.synkroniserer.consumer.oppgave.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum IdentGruppeV2 {
    @JsonProperty("FOLKEREGISTERIDENT")
    FOLKEREGISTERIDENT("FOLKEREGISTERIDENT"),
    @JsonAlias("AKTORID")
    @JsonProperty("AKTOERID")
    AKTOERID("AKTOERID"),
    @JsonProperty("NPID")
    NPID("NPID");

    private final String navn;

    IdentGruppeV2() {this.navn = "";}

    IdentGruppeV2(final String navn) {
        this.navn = navn;
    }

    public String navn() {
        return this.navn;
    }
}
