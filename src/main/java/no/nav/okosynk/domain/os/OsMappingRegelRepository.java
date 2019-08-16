package no.nav.okosynk.domain.os;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.domain.AbstractMappingRegelRepository;

public class OsMappingRegelRepository
    extends AbstractMappingRegelRepository<OsMelding> {

    public OsMappingRegelRepository() {
        super(Constants.BATCH_TYPE.OS);
    }

    @Override
    protected String createMappingRegelKey(final OsMelding melding) {

        final String mappingRegelKey = settSammenNokkel(melding.faggruppe, melding.behandlendeEnhet);

        return mappingRegelKey;
    }
}
