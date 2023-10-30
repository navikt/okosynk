package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.model.Melding;
import no.nav.okosynk.model.Oppgave;

import java.util.List;

public interface IMeldingMapper<T extends Melding> {
    List<Oppgave> lagOppgaver(List<T> meldinger);
}
