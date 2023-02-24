package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.Collection;

@Getter(AccessLevel.PUBLIC)
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class PdlErrorResponseJson {
    @JsonProperty(value = "errors", required = true)
    private final Collection<PdlErrorJson> errors;
}
