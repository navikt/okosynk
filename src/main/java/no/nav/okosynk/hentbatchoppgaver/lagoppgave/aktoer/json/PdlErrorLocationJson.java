package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PdlErrorLocationJson {
    @JsonProperty("line")
    private final String line;
    @JsonProperty("column")
    private final String column;

    public String getLine() {
        return line;
    }

    public String getColumn() {
        return column;
    }

    public PdlErrorLocationJson(String line, String column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "PdlErrorLocationJson{" +
                "line='" + line + '\'' +
                ", column='" + column + '\'' +
                '}';
    }
}
