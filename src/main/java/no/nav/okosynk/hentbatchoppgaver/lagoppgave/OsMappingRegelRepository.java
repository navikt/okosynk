package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;

public class OsMappingRegelRepository extends AbstractMappingRegelRepository {

    public OsMappingRegelRepository() {
        super(Constants.BATCH_TYPE.OS);
    }

}
