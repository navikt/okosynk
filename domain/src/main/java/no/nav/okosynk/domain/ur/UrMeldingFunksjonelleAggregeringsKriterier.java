package no.nav.okosynk.domain.ur;

public class UrMeldingFunksjonelleAggregeringsKriterier {

    public final String gjelderId;
    public final String gjelderIdType;
    public final String ansvarligEnhetId;
    public final String oppdragsKode;

    private final UrMappingRegelRepository urMappingRegelRepository;

    public UrMeldingFunksjonelleAggregeringsKriterier(UrMelding urMelding) {
        this.urMappingRegelRepository = new UrMappingRegelRepository();
        this.gjelderId = urMelding.gjelderId;
        this.gjelderIdType = urMelding.utledGjelderIdType();
        this.ansvarligEnhetId = this.urMappingRegelRepository.finnRegel(urMelding).get().ansvarligEnhetId;
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
