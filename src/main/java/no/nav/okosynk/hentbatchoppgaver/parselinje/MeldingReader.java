package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;

import java.util.List;
import java.util.function.Function;

public record MeldingReader<T extends AbstractMelding>(Function<String, T> spesifikkMeldingCreator) implements IMeldingReader<T> {
    @Override
    public List<T> opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(final List<String> linjerMedUspesifikkeMeldinger) {
        return linjerMedUspesifikkeMeldinger.stream().map(spesifikkMeldingCreator()).toList();
    }
}
