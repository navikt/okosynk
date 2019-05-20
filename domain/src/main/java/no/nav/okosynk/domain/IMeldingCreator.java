package no.nav.okosynk.domain;

import java.util.function.Function;

public interface IMeldingCreator<MELDINGSTYPE extends AbstractMelding>
    extends Function<String, MELDINGSTYPE> {

    @Override
    MELDINGSTYPE apply(String s);
}
