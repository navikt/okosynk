package no.nav.okosynk.domain.os;

public class OsMeldingFunksjonelleAggregeringsKriterier {

    public final String faggruppe;
    public final String gjelderId;
    public final String gjelderIdType;
    public final String ansvarligEnhetId;

    private final OsMappingRegelRepository osMappingRegelRepository;

    public OsMeldingFunksjonelleAggregeringsKriterier(OsMelding osMelding) {
        this.osMappingRegelRepository = new OsMappingRegelRepository();
        this.gjelderId = osMelding.gjelderId;
        this.gjelderIdType = osMelding.utledGjelderIdType();
        this.ansvarligEnhetId = this.osMappingRegelRepository.finnRegel(osMelding).get().ansvarligEnhetId;
        this.faggruppe = osMelding.faggruppe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OsMeldingFunksjonelleAggregeringsKriterier that = (OsMeldingFunksjonelleAggregeringsKriterier) o;
        if (!gjelderId.equals(that.gjelderId)) return false;
        if (!gjelderIdType.equals(that.gjelderIdType)) return false;
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
