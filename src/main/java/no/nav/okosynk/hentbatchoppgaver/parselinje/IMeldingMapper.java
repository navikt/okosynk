package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.model.Oppgave;

import java.util.List;

public interface IMeldingMapper<T extends AbstractMelding> {
    List<Oppgave> lagOppgaver(List<T> meldinger);
}
