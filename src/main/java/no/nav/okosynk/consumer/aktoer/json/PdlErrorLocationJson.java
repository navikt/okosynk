package no.nav.okosynk.consumer.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter(AccessLevel.PUBLIC)
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class PdlErrorLocationJson {
    @JsonProperty("line")
    private final String line;
    @JsonProperty("column")
    private final String column;
}
