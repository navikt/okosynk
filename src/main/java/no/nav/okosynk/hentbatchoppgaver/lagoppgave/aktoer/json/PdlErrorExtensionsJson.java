package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PdlErrorExtensionsJson {
    private final String code;
    private final String classification;

    public PdlErrorExtensionsJson(String code, String classification) {
        this.code = code;
        this.classification = classification;
    }

    public String getCode() {
        return code;
    }

    public String getClassification() {
        return classification;
    }
}