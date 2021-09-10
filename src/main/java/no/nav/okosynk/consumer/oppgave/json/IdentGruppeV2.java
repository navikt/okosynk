package no.nav.okosynk.consumer.oppgave.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IdentGruppeV2 {
    @JsonProperty("FOLKEREGISTERIDENT")
    FOLKEREGISTERIDENT("FOLKEREGISTERIDENT"),
    @JsonProperty("AKTOERID")
    AKTOERID("AKTOERID"),
    @JsonProperty("NPID")
    NPID("NPID");

    private final String navn;

    IdentGruppeV2(final String navn) {
        this.navn = navn;
    }

    public String navn() {
        return this.navn;
    }
}
