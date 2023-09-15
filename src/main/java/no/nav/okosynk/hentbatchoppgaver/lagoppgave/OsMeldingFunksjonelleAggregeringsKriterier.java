package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.model.GjelderIdType;

public class OsMeldingFunksjonelleAggregeringsKriterier {

    public final String faggruppe;
    public final String gjelderId;
    public final GjelderIdType gjelderIdType;
    public final String ansvarligEnhetId;

    public OsMeldingFunksjonelleAggregeringsKriterier(OsMelding osMelding) {
        OsMappingRegelRepository osMappingRegelRepository = new OsMappingRegelRepository();
        this.gjelderId = osMelding.gjelderId;
        this.gjelderIdType = GjelderIdType.fra(osMelding.gjelderId);
        this.ansvarligEnhetId = osMappingRegelRepository.finnRegel(osMelding).map(t -> t.ansvarligEnhetId).orElse(null);
        this.faggruppe = osMelding.faggruppe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OsMeldingFunksjonelleAggregeringsKriterier that = (OsMeldingFunksjonelleAggregeringsKriterier) o;
        if (!gjelderId.equals(that.gjelderId)) return false;
        if (gjelderIdType != that.gjelderIdType) return false;
        if (!ansvarligEnhetId.equals(that.ansvarligEnhetId)) return false;
        return faggruppe.equals(that.faggruppe);
    }

    @Override
    public int hashCode() {
        int result = gjelderId.hashCode();
        result = 31 * result + gjelderIdType.hashCode();
        result = 31 * result + ansvarligEnhetId.hashCode();
        result = 31 * result + faggruppe.hashCode();
        return result;
    }
}
