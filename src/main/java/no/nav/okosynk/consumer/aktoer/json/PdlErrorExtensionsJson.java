package no.nav.okosynk.consumer.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class PdlErrorExtensionsJson {
    private final String code;
    private final String classification;
}