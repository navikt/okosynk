package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.model.GjelderIdType;

public record OsMeldingFunksjonelleAggregeringsKriterier(
        String faggruppe,
        String gjelderId,
        GjelderIdType gjelderIdType,
        String ansvarligEnhetId) {

    public OsMeldingFunksjonelleAggregeringsKriterier(OsMelding osMelding) {
        this(osMelding.getFaggruppe(),
                osMelding.getGjelderId(),
                GjelderIdType.fra(osMelding.getGjelderId()),
                new Mappingregelverk(Constants.BATCH_TYPE.OS.getMappingRulesPropertiesFileName()).finnRegel(osMelding.regelnøkkel()).map(t -> t.ansvarligEnhetId).orElse(null));
    }
}
