package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PdlErrorJson {
    @JsonProperty("message")
    private final String message;
    @JsonProperty("locations")
    private final Collection<PdlErrorLocationJson> locations;
    @JsonProperty("path")
    private final Collection<String> path;
    @JsonProperty("extensions")
    private final PdlErrorExtensionsJson extensions;

    public PdlErrorJson(String message, Collection<PdlErrorLocationJson> locations, Collection<String> path, PdlErrorExtensionsJson extensions) {
        this.message = message;
        this.locations = locations;
        this.path = path;
        this.extensions = extensions;
    }

    public String getMessage() {
        return message;
    }

    public Collection<PdlErrorLocationJson> getLocations() {
        return locations;
    }

    public Collection<String> getPath() {
        return path;
    }

    public PdlErrorExtensionsJson getExtensions() {
        return extensions;
    }
}
