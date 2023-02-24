package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import java.util.Objects;

public class MappingRegel {

    public final String behandlingstema;
    public final String behandlingstype;
    public final String ansvarligEnhetId;

    public MappingRegel(
        final String behandlingstema,
        final String behandlingstype,
        final String ansvarligEnhetId) {
        this.behandlingstema = behandlingstema;
        this.behandlingstype = behandlingstype;
        this.ansvarligEnhetId = ansvarligEnhetId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.behandlingstema, this.behandlingstype, this.ansvarligEnhetId);
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

        return Objects.equals(behandlingstema, otherAsMappingRegel.behandlingstema) &&
                Objects.equals(behandlingstype, otherAsMappingRegel.behandlingstype) &&
                Objects.equals(ansvarligEnhetId, otherAsMappingRegel.ansvarligEnhetId);
    }
}
