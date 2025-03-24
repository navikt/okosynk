package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostPdlHentIdenterDataResponseJson {
    @JsonProperty("hentIdenter")
    private final PostPdlHentIdenterHentIdenterResponseJson hentIdenter;

    public PostPdlHentIdenterDataResponseJson() {this.hentIdenter = PostPdlHentIdenterHentIdenterResponseJson.builder().build();}

    public PostPdlHentIdenterDataResponseJson(PostPdlHentIdenterHentIdenterResponseJson hentIdenter) {
        this.hentIdenter = hentIdenter;
    }

    public PostPdlHentIdenterHentIdenterResponseJson getHentIdenter() {
        return hentIdenter;
    }

    public static class Builder {
        private PostPdlHentIdenterHentIdenterResponseJson hentIdenter;

        public Builder hentIdenter(PostPdlHentIdenterHentIdenterResponseJson hentIdenter) {
            this.hentIdenter = hentIdenter;
            return this;
        }

        public PostPdlHentIdenterDataResponseJson build() {
            return new PostPdlHentIdenterDataResponseJson(hentIdenter);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}