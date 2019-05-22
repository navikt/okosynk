package no.nav.okosynk.consumer.oppgave;

import java.util.List;

public class FinnOppgaveResponse {

    private int antallTreffTotalt;
    private List<OppgaveDTO> oppgaver;

    public FinnOppgaveResponse() {
    }

    public List<OppgaveDTO> getOppgaver() {
        return oppgaver;
    }

    public void setOppgaver(List<OppgaveDTO> oppgaver) {
        this.oppgaver = oppgaver;
    }

    public int getAntallTreffTotalt() {
        return antallTreffTotalt;
    }

    public void setAntallTreffTotalt(int antallTreffTotalt) {
        this.antallTreffTotalt = antallTreffTotalt;
    }
}
