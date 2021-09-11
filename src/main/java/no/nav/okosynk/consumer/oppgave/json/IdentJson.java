package no.nav.okosynk.consumer.oppgave.json;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class IdentJson {

    private String ident;

    private IdentGruppeV2 gruppe;

    public IdentJson() {
        //JaxRS
    }

    public IdentJson(final IdentGruppeV2 identgruppe, final String ident) {
        this.gruppe = identgruppe;
        this.ident = ident;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("ident", ident)
                .append("gruppe", gruppe)
                .toString();
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(final String ident) {
        this.ident = ident;
    }

    public IdentGruppeV2 getGruppe() {
        return gruppe;
    }

    public void setGruppe(final IdentGruppeV2 gruppe) {
        this.gruppe = gruppe;
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final IdentJson otherIdentJson = (IdentJson) other;

        return new EqualsBuilder()
                .append(this.ident, otherIdentJson.ident)
                .append(this.gruppe, otherIdentJson.gruppe)
                .isEquals()
                ;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.ident)
                .append(this.gruppe)
                .toHashCode();
    }
}