package no.nav.okosynk.domain;

import java.util.List;
import java.util.stream.Stream;

public interface IMeldingReader<MELDINSGTYPE extends AbstractMelding> {

    List<MELDINSGTYPE> opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(final Stream<String> linjerMedUspesifikkeMeldinger)
        throws MeldingUnreadableException;
}
