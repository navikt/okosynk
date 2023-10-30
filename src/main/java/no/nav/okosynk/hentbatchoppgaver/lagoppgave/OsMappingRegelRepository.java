package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;

public class OsMappingRegelRepository
    extends AbstractMappingRegelRepository<OsMelding> {

  public OsMappingRegelRepository() {
    super(Constants.BATCH_TYPE.OS);
  }

  @Override
  protected String createMappingRegelKey(final OsMelding osMelding) {

    return settSammenNokkel(osMelding.getFaggruppe(), osMelding.getBehandlendeEnhet());
  }
}
