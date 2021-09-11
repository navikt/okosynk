package no.nav.okosynk.consumer.oppgave.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;
import java.util.Comparator;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class FinnOppgaveResponseJson extends AbstractOppgaveJson {

    private Collection<IdentJson> identer;

    public FinnOppgaveResponseJson() {
        super();
    }

    @JsonProperty("identer")
    public Collection<IdentJson> getIdenter() {
        return identer;
    }

    public void setIdenter(final Collection<IdentJson> identer) {
        this.identer = identer;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final FinnOppgaveResponseJson otherFinnOppgaveResponseJson = (FinnOppgaveResponseJson) other;

        final Object[] thisIdentArray =
                this.identer == null
                        ?
                        new IdentJson[0]
                        :
                        this.identer.stream().sorted(
                                        Comparator.comparing(a -> a.getGruppe().navn())
                                )
                                .toArray();

        final Object[] otherIdentArray =
                   otherFinnOppgaveResponseJson.getIdenter() == null
                        ?
                        new IdentJson[0]
                        :
                        otherFinnOppgaveResponseJson.getIdenter().stream().sorted(
                                        Comparator.comparing(a -> a.getGruppe().navn())
                                )
                                .toArray();

        return new EqualsBuilder()
                .appendSuper(super.equals(other))
                .append(thisIdentArray, otherIdentArray)
                .isEquals()
                ;
    }

    @Override
    public int hashCode() {

        final Object[] thisIdentArray =
                this.identer == null
                        ?
                        new IdentJson[0]
                        :
                        this.identer.stream().sorted(
                                        Comparator.comparing(a -> a.getGruppe().navn())
                                )
                                .toArray();
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(thisIdentArray)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("identer", this.identer)
                .toString();
    }
}