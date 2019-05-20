package no.nav.okosynk.consumer.oppgave;

import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.*;

public class OppgaveMock implements OppgaveV3 {

    @Override
    public WSHentOppgaveResponse hentOppgave(WSHentOppgaveRequest hentOppgaveRequest) throws HentOppgaveOppgaveIkkeFunnet {
        return null;
    }

    @Override
    public WSFinnOppgaveListeResponse finnOppgaveListe(WSFinnOppgaveListeRequest finnOppgaveListeRequest) {
        return null;
    }

    @Override
    public WSFinnFerdigstiltOppgaveListeResponse finnFerdigstiltOppgaveListe(WSFinnFerdigstiltOppgaveListeRequest finnFerdigstiltOppgaveListeRequest) {
        return null;
    }

    @Override
    public WSFinnFeilregistrertOppgaveListeResponse finnFeilregistrertOppgaveListe(WSFinnFeilregistrertOppgaveListeRequest finnFeilregistrertOppgaveListeRequest) {
        return null;
    }

    @Override
    public WSFinnMappeListeResponse finnMappeListe(WSFinnMappeListeRequest finnMappeListeRequest) {
        return null;
    }

    @Override
    public void ping() {

    }
}


