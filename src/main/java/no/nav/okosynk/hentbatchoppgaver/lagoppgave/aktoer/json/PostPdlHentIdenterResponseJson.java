package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostPdlHentIdenterResponseJson {
    @JsonProperty("data")
    private final PostPdlHentIdenterDataResponseJson data;

    public PostPdlHentIdenterResponseJson() {this.data = PostPdlHentIdenterDataResponseJson.builder().build();}

    public PostPdlHentIdenterResponseJson(PostPdlHentIdenterDataResponseJson data) {
        this.data = data;
    }

    public PostPdlHentIdenterDataResponseJson getData() {
        return data;
    }

    public static class Builder {
        private PostPdlHentIdenterDataResponseJson data;

        public Builder data(PostPdlHentIdenterDataResponseJson data) {
            this.data = data;
            return this;
        }

        public PostPdlHentIdenterResponseJson build() {
            return new PostPdlHentIdenterResponseJson(data);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}