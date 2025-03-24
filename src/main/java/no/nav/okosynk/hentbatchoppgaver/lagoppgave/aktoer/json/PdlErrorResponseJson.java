package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PdlErrorResponseJson {

    @JsonProperty(value = "errors", required = true)
    private final Collection<PdlErrorJson> errors;

    public PdlErrorResponseJson(Collection<PdlErrorJson> errors) {
        this.errors = errors;
    }

    public Collection<PdlErrorJson> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "PdlErrorResponseJson{" +
                "errors=" + errors +
                '}';
    }

    public static class Builder {
        private Collection<PdlErrorJson> errors;

        public Builder errors(Collection<PdlErrorJson> errors) {
            this.errors = errors;
            return this;
        }

        public PdlErrorResponseJson build() {
            return new PdlErrorResponseJson(errors);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}