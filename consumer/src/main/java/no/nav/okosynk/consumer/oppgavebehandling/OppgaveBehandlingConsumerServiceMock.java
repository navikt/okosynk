package no.nav.okosynk.consumer.oppgavebehandling;

import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.*;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.*;

public class OppgaveBehandlingConsumerServiceMock implements OppgavebehandlingV3 {

    @Override
    public void feilregistrerOppgave(WSFeilregistrerOppgaveRequest feilregistrerOppgaveRequest) throws FeilregistrerOppgaveOppgaveIkkeFunnet, FeilregistrerOppgaveUlovligStatusOvergang {

    }

    @Override
    public void lagreOppgave(WSLagreOppgaveRequest lagreOppgaveRequest) throws LagreOppgaveOppgaveIkkeFunnet, LagreOppgaveOptimistiskLasing {

    }

    @Override
    public WSOpprettOppgaveResponse opprettOppgave(WSOpprettOppgaveRequest opprettOppgaveRequest) {
        return null;
    }

    @Override
    public WSOpprettOppgaveBolkResponse opprettOppgaveBolk(WSOpprettOppgaveBolkRequest opprettOppgaveBolkRequest) {
        return null;
    }

    @Override
    public WSFerdigstillOppgaveBolkResponse ferdigstillOppgaveBolk(WSFerdigstillOppgaveBolkRequest ferdigstillOppgaveBolkRequest) {
        return null;
    }

    @Override
    public WSOpprettMappeResponse opprettMappe(WSOpprettMappeRequest opprettMappeRequest) {
        return null;
    }

    @Override
    public void lagreMappe(WSLagreMappeRequest lagreMappeRequest) throws LagreMappeMappeIkkeFunnet {

    }

    @Override
    public void slettMappe(WSSlettMappeRequest slettMappeRequest) throws SlettMappeMappeIkkeFunnet, SlettMappeMappeIkkeTom {

    }

    @Override
    public WSLagreOppgaveBolkResponse lagreOppgaveBolk(WSLagreOppgaveBolkRequest lagreOppgaveBolkRequest) {
        return null;
    }

    @Override
    public void ping() {

    }
}
