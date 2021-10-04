package no.nav.okosynk.consumer.oppgave.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class PostOppgaveRequestJson extends AbstractOppgaveJson {

    @JsonAlias("navPersonIdent")
    @JsonProperty("folkeregisterIdent")
    private String folkeregisterIdent;

    public PostOppgaveRequestJson() {
        super();
    }

    @JsonAlias("navPersonIdent")
    @JsonProperty("folkeregisterIdent")
    public String getFolkeregisterIdent() {
        return this.folkeregisterIdent;
    }

    public void setFolkeregisterIdent(final String folkeregisterIdent) {
        this.folkeregisterIdent = folkeregisterIdent;
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
                .append(this.folkeregisterIdent, oppgaveJson.folkeregisterIdent)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(39, 41)
                .appendSuper(super.hashCode())
                .append(folkeregisterIdent)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("folkeregisterIdent", this.folkeregisterIdent == null ? null : this.folkeregisterIdent.length() > 5 ? this.folkeregisterIdent.substring(0, 6) + "*****" : "*** ugyldig lengde *****")
                .toString();
    }
}
