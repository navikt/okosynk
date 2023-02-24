package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;

import java.util.function.Function;

public interface IMeldingCreator<T extends AbstractMelding>
    extends Function<String, T> {

    @Override
    T apply(String s);
}
