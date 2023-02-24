package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.MeldingUnreadableException;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;

import java.util.List;
import java.util.stream.Stream;

public interface IMeldingReader<T extends AbstractMelding> {

    List<T> opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(final Stream<String> linjerMedUspesifikkeMeldinger)
        throws MeldingUnreadableException;
}
