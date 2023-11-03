package no.nav.okosynk.hentbatchoppgaver.lagoppgave.model;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.Mappingregelverk;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import no.nav.okosynk.model.GjelderIdType;

public record UrMeldingFunksjonelleAggregeringsKriterier(
        String gjelderId,
        String gjelderIdType,
        String ansvarligEnhetId,
        String oppdragsKode
) {
    public UrMeldingFunksjonelleAggregeringsKriterier(UrMelding urMelding) {
        this(urMelding.getGjelderId(),

                GjelderIdType.fra(urMelding.getGjelderId()).toString(),

                Mappingregelverk.finnRegel(urMelding.regelnøkkel()).map(MappingRegel::ansvarligEnhetId).orElse(null),

                urMelding.oppdragsKode);
    }
}
