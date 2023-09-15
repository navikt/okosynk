package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import no.nav.okosynk.model.GjelderIdType;

public class UrMeldingFunksjonelleAggregeringsKriterier {

    public final String gjelderId;
    public final String gjelderIdType;
    public final String ansvarligEnhetId;
    public final String oppdragsKode;

    public UrMeldingFunksjonelleAggregeringsKriterier(UrMelding urMelding) {
        UrMappingRegelRepository urMappingRegelRepository = new UrMappingRegelRepository();
        this.gjelderId = urMelding.gjelderId;
        this.gjelderIdType = GjelderIdType.fra(urMelding.gjelderId).toString();
        this.ansvarligEnhetId = urMappingRegelRepository.finnRegel(urMelding).map(t -> t.ansvarligEnhetId).orElse(null);
        this.oppdragsKode = urMelding.oppdragsKode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrMeldingFunksjonelleAggregeringsKriterier that = (UrMeldingFunksjonelleAggregeringsKriterier) o;
        if (!gjelderId.equals(that.gjelderId)) return false;
        if (!gjelderIdType.equals(that.gjelderIdType)) return false;
        if (!ansvarligEnhetId.equals(that.ansvarligEnhetId)) return false;
        return oppdragsKode.equals(that.oppdragsKode);
    }

    @Override
    public int hashCode() {
        int result = gjelderId.hashCode();
        result = 31 * result + gjelderIdType.hashCode();
        result = 31 * result + ansvarligEnhetId.hashCode();
        result = 31 * result + oppdragsKode.hashCode();
        return result;
    }
}
