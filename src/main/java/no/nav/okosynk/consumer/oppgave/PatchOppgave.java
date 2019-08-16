package no.nav.okosynk.consumer.oppgave;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PatchOppgave {
    private final Long id;
    private final Integer versjon;
    private final String beskrivelse;

    public PatchOppgave(Integer versjon, Long id, String beskrivelse) {
        this.versjon = versjon;
        this.id = id;
        this.beskrivelse = beskrivelse;
    }

    public Long getId() {
        return id;
    }

    public Integer getVersjon() {
        return versjon;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("versjon", versjon)
                .append("beskrivelse", "*****")
                .toString();
    }
}

