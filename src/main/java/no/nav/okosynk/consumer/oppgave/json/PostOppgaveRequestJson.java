package no.nav.okosynk.consumer.oppgave.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class PostOppgaveRequestJson extends AbstractOppgaveJson {

    @JsonAlias("navPersonIdent")
    private String npidOrFolkeregisterIdent;

    public PostOppgaveRequestJson() {
        super();
    }

    @JsonProperty("npidOrFolkeregisterIdent")
    public String getNpidOrFolkeregisterIdent() {
        return this.npidOrFolkeregisterIdent;
    }

    public void setNpidOrFolkeregisterIdent(final String npidOrFolkeregisterIdent) {
        this.npidOrFolkeregisterIdent = npidOrFolkeregisterIdent;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final PostOppgaveRequestJson oppgaveJson = (PostOppgaveRequestJson) other;

        return new EqualsBuilder()
                .appendSuper(super.equals(other))
                .append(this.npidOrFolkeregisterIdent, oppgaveJson.npidOrFolkeregisterIdent)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(39, 41)
                .appendSuper(super.hashCode())
                .append(npidOrFolkeregisterIdent)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("folkeregisterIdent", this.npidOrFolkeregisterIdent == null ? null : this.npidOrFolkeregisterIdent.length() > 5 ? this.npidOrFolkeregisterIdent.substring(0, 6) + "*****" : "*** ugyldig lengde *****")
                .toString();
    }
}
