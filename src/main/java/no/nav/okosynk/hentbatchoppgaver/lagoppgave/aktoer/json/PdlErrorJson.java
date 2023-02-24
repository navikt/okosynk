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
public class PdlErrorJson {
    @JsonProperty("message")
    private final String message;
    @JsonProperty("locations")
    private final Collection<PdlErrorLocationJson> locations;
    @JsonProperty("path")
    private final Collection<String> path;
    @JsonProperty("extensions")
    private final PdlErrorExtensionsJson extensions;
}
