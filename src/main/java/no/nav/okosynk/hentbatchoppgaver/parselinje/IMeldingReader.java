package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.MeldingUnreadableException;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;

import java.util.List;

public interface IMeldingReader<T extends AbstractMelding> {

    List<T> opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(final List<String> linjerMedUspesifikkeMeldinger)
            throws MeldingUnreadableException;
}
