package no.nav.okosynk.hentbatchoppgaver.lagoppgave.model;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.Mappingregelverk;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.model.GjelderIdType;

public record OsMeldingFunksjonelleAggregeringsKriterier(
        String faggruppe,
        String gjelderId,
        GjelderIdType gjelderIdType,
        String ansvarligEnhetId) {

    public OsMeldingFunksjonelleAggregeringsKriterier(OsMelding osMelding) {
        this(
                osMelding.getFaggruppe(),

                osMelding.getGjelderId(),

                GjelderIdType.fra(osMelding.getGjelderId()),

                Mappingregelverk.finnRegel(osMelding.regelnøkkel()).map(MappingRegel::ansvarligEnhetId).orElse(null));
    }
}
