package no.nav.okosynk.consumer.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.Collection;

@Jacksonized
@Getter
@ToString
@Builder
public class AzureAdTokenErrorResponseJson {
    @JsonProperty("error")
    private final String error;
    @JsonProperty("error_description")
    private final String errorDescription;
    @JsonProperty("error_codes")
    private final Collection<Integer> errorCodes;
    @JsonProperty("timestamp")
    private final String timestamp;
    @JsonProperty("trace_id")
    private final String traceId;
    @JsonProperty("correlation_id")
    private final String correlationId;
    @JsonProperty("error_uri")
    private final String errorUri;
}