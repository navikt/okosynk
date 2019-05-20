package no.nav.okosynk.domain;

import no.nav.okosynk.domain.os.OsMelding;

import java.util.Objects;

public class MappingRegel {

    public final String underkategoriKode;
    public final String ansvarligEnhetId;

    public MappingRegel(String underkategoriKode, String ansvarligEnhetId) {

        this.underkategoriKode = underkategoriKode;
        this.ansvarligEnhetId = ansvarligEnhetId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.underkategoriKode, this.ansvarligEnhetId);
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (!(other instanceof MappingRegel)) {
            return false;
        }

        final MappingRegel otherAsMappingRegel = (MappingRegel)other;

        return
                this.underkategoriKode.equals(otherAsMappingRegel.underkategoriKode)
                &&
                this.ansvarligEnhetId.equals(otherAsMappingRegel.ansvarligEnhetId)
            ;
    }
}
