package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;

public class UrMappingRegelRepository
    extends AbstractMappingRegelRepository<UrMelding> {

  public UrMappingRegelRepository() {
    super(Constants.BATCH_TYPE.UR);
  }

  @Override
  protected String createMappingRegelKey(final UrMelding urMelding) {

    return settSammenNokkel(urMelding.oppdragsKode, urMelding.getBehandlendeEnhet());
  }
}
