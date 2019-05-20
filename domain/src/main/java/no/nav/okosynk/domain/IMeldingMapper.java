package no.nav.okosynk.domain;

import java.util.List;

public interface IMeldingMapper<MELDINGSTYPE extends AbstractMelding> {
    List<Oppgave> lagOppgaver(List<MELDINGSTYPE> meldinger);
}
