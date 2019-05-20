package no.nav.okosynk.domain.ur;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.domain.AbstractMappingRegelRepository;

public class UrMappingRegelRepository
    extends AbstractMappingRegelRepository<UrMelding> {

    public UrMappingRegelRepository() {
        super(Constants.BATCH_TYPE.UR);
    }

    @Override
    protected String createMappingRegelKey(final UrMelding melding) {

        final String mappingRegelKey = settSammenNokkel(melding.oppdragsKode, melding.behandlendeEnhet);

        return mappingRegelKey;
    }
}
