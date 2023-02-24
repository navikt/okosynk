package no.nav.okosynk.synkroniserer.consumer.oppgave.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FinnOppgaverResponseJson {

    private int antallTreffTotalt;
    private List<FinnOppgaveResponseJson> finnOppgaveResponseJsons;

    @JsonProperty("oppgaver")
    public List<FinnOppgaveResponseJson> getFinnOppgaveResponseJsons() {
        return finnOppgaveResponseJsons;
    }

    public void setFinnOppgaveResponseJsons(List<FinnOppgaveResponseJson> finnOppgaveResponseJsons) {
        this.finnOppgaveResponseJsons = finnOppgaveResponseJsons;
    }

    public int getAntallTreffTotalt() {
        return antallTreffTotalt;
    }

    public void setAntallTreffTotalt(int antallTreffTotalt) {
        this.antallTreffTotalt = antallTreffTotalt;
    }
}
