package no.nav.okosynk.consumer.oppgave;

import java.util.List;

public class FinnOppgaveResponse {

    private int antallTreffTotalt;
    private List<OppgaveDto> oppgaver;

    public FinnOppgaveResponse() {
    }

    public List<OppgaveDto> getOppgaver() {
        return oppgaver;
    }

    public void setOppgaver(List<OppgaveDto> oppgaver) {
        this.oppgaver = oppgaver;
    }

    public int getAntallTreffTotalt() {
        return antallTreffTotalt;
    }

    public void setAntallTreffTotalt(int antallTreffTotalt) {
        this.antallTreffTotalt = antallTreffTotalt;
    }
}
