package no.nav.okosynk.hentbatchoppgaver.lagoppgave.model;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.Mappingregelverk;
import no.nav.okosynk.hentbatchoppgaver.model.Melding;
import no.nav.okosynk.model.GjelderIdType;

public record AggregeringsKriterier(
        String gjelderId,
        String gjelderIdType,
        String ansvarligEnhetId,
        String faggruppeEllerOppdragskode
) {
    public AggregeringsKriterier(Melding melding) {
        this(melding.getGjelderId(),

                GjelderIdType.fra(melding.getGjelderId()).toString(),

                Mappingregelverk.finnRegel(melding.ruleKey()).map(MappingRegel::ansvarligEnhetId).orElse(null),

                melding.faggruppeEllerOppdragskode());
    }
}
