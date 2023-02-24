package no.nav.okosynk.synkroniserer.consumer.oppgave.json;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class PostOppgaveRequestJson extends AbstractOppgaveJson {

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        return new EqualsBuilder()
                .appendSuper(super.equals(other))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(39, 41)
                .appendSuper(super.hashCode())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .toString();
    }
}
