package no.nav.okosynk.consumer.oppgave.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class PostOppgaveRequestJson extends AbstractOppgaveJson {

    private String navPersonIdent;

    public PostOppgaveRequestJson() {
        super();
    }

    @JsonProperty("navPersonIdent")
    public String getNavPersonIdent() {
        return this.navPersonIdent;
    }

    public void setNavPersonIdent(final String navPersonIdent) {
        this.navPersonIdent = navPersonIdent;
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
                .append(this.navPersonIdent, oppgaveJson.navPersonIdent)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(39, 41)
                .appendSuper(super.hashCode())
                .append(navPersonIdent)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("navPersonIdent", this.navPersonIdent)
                .toString();
    }
}
