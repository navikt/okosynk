package no.nav.okosynk.synkroniserer.consumer.oppgave.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.nav.okosynk.synkroniserer.consumer.oppgave.LocalDateTimeSerializer;
import no.nav.okosynk.synkroniserer.consumer.oppgave.OppgaveStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class PostOppgaveRequestJson extends AbstractOppgaveJson {
    @Override
    @JsonIgnore
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getEndretTidspunkt() {
        return super.getEndretTidspunkt();
    }

    @Override
    @JsonIgnore
    public String getId() {
        return super.getId();
    }

    @Override
    @JsonIgnore
    public String getOpprettetAv() {
        return super.getOpprettetAv();
    }

    @Override
    @JsonIgnore
    public Integer getVersjon() {
        return super.getVersjon();
    }

    @Override
    @JsonIgnore
    public String getEndretAvEnhetsnr() {
        return super.getEndretAvEnhetsnr();
    }

    @Override
    @JsonIgnore
    public OppgaveStatus getStatus() {
        return super.getStatus();
    }

    @Override
    @JsonIgnore
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getOpprettetTidspunkt() {
        return super.getOpprettetTidspunkt();
    }

    @Override
    @JsonIgnore
    public String getEndretAv() {
        return super.getEndretAv();
    }

    @Override
    @JsonIgnore
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getFerdigstiltTidspunkt() {
        return super.getFerdigstiltTidspunkt();
    }

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
