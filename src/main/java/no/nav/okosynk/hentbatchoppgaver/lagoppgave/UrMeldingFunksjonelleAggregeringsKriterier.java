package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

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
                new UrMappingRegelRepository().finnRegel(urMelding).map(t -> t.ansvarligEnhetId).orElse(null),
                urMelding.oppdragsKode);
    }
}
