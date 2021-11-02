package no.nav.okosynk.consumer.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import no.nav.okosynk.consumer.oppgave.json.IdentJson;

import java.util.Collection;

@Getter(AccessLevel.PUBLIC)
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostPdlHentIdenterHentIdenterResponseJson {
    @JsonProperty("identer")
    private final Collection<IdentJson> identer;
}
