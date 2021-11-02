package no.nav.okosynk.consumer.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter(AccessLevel.PUBLIC)
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostPdlHentIdenterDataResponseJson {
    @JsonProperty("hentIdenter")
    private final PostPdlHentIdenterHentIdenterResponseJson hentIdenter;
}
