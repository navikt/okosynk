package no.nav.okosynk.domain.os;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.domain.AbstractMappingRegelRepository;

public class OsMappingRegelRepository
    extends AbstractMappingRegelRepository<OsMelding> {

  OsMappingRegelRepository() {
    super(Constants.BATCH_TYPE.OS);
  }

  @Override
  protected String createMappingRegelKey(final OsMelding osMelding) {

    return settSammenNokkel(osMelding.faggruppe, osMelding.behandlendeEnhet);
  }
}
