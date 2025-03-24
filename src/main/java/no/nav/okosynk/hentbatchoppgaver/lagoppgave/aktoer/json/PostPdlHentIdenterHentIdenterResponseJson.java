package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.okosynk.synkroniserer.consumer.oppgave.json.IdentJson;

import java.util.Collection;
import java.util.Collections;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostPdlHentIdenterHentIdenterResponseJson {
    @JsonProperty("identer")
    private final Collection<IdentJson> identer;

    public PostPdlHentIdenterHentIdenterResponseJson(){
        this.identer = Collections.emptySet();
    }

    public PostPdlHentIdenterHentIdenterResponseJson(Collection<IdentJson> identer) {
        this.identer = identer;
    }

    public Collection<IdentJson> getIdenter() {
        return identer;
    }

    public static class Builder {
        private Collection<IdentJson> identer;

        public Builder identer(Collection<IdentJson> identer) {
            this.identer = identer;
            return this;
        }

        public PostPdlHentIdenterHentIdenterResponseJson build() {
            return new PostPdlHentIdenterHentIdenterResponseJson(identer);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}