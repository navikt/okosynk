package no.nav.okosynk.comm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Collections;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public AzureAdTokenErrorResponseJson() {
        this.error = "";
        this.errorDescription = "";
        this.errorCodes = Collections.emptyList();
        this.timestamp = "";
        this.traceId = "";
        this.correlationId = "";
        this.errorUri = "";
    }

    public AzureAdTokenErrorResponseJson(String error, String errorDescription, Collection<Integer> errorCodes, String timestamp, String traceId, String correlationId, String errorUri) {
        this.error = error;
        this.errorDescription = errorDescription;
        this.errorCodes = errorCodes;
        this.timestamp = timestamp;
        this.traceId = traceId;
        this.correlationId = correlationId;
        this.errorUri = errorUri;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public Collection<Integer> getErrorCodes() {
        return errorCodes;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getErrorUri() {
        return errorUri;
    }

    @Override
    public String toString() {
        return "AzureAdTokenErrorResponseJson{" +
                "error='" + error + '\'' +
                ", errorDescription='" + errorDescription + '\'' +
                ", errorCodes=" + errorCodes +
                ", timestamp='" + timestamp + '\'' +
                ", traceId='" + traceId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", errorUri='" + errorUri + '\'' +
                '}';
    }

    public static class Builder {
        private String error;
        private String errorDescription;
        private Collection<Integer> errorCodes;
        private String timestamp;
        private String traceId;
        private String correlationId;
        private String errorUri;

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder errorDescription(String errorDescription) {
            this.errorDescription = errorDescription;
            return this;
        }

        public Builder errorCodes(Collection<Integer> errorCodes) {
            this.errorCodes = errorCodes;
            return this;
        }

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder errorUri(String errorUri) {
            this.errorUri = errorUri;
            return this;
        }

        public AzureAdTokenErrorResponseJson build() {
            return new AzureAdTokenErrorResponseJson(error, errorDescription, errorCodes, timestamp, traceId, correlationId, errorUri);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}