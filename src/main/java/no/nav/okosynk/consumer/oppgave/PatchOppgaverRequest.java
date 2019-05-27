package no.nav.okosynk.consumer.oppgave;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class PatchOppgaverRequest {
    private final Long mappeId;
    private final String tilordnetRessurs;
    private final String tildeltEnhetsnr;
    private String endretAvEnhetsnr;
    private final String behandlingstema;
    private final String behandlingstype;
    private final List<PatchOppgave> oppgaver;

    public PatchOppgaverRequest(Builder builder) {
        this.mappeId = builder.mappeId;
        this.tilordnetRessurs = builder.tilordnetRessurs;
        this.endretAvEnhetsnr = builder.endretAvEnhetsnr;
        this.tildeltEnhetsnr = builder.tildeltEnhetsnr;
        this.behandlingstema = builder.behandlingstema;
        this.behandlingstype = builder.behandlingstype;
        this.oppgaver = builder.oppgaver;
    }

    public Long getMappeId() {
        return mappeId;
    }

    public String getTilordnetRessurs() {
        return tilordnetRessurs;
    }

    public String getTildeltEnhetsnr() {
        return tildeltEnhetsnr;
    }

    public String getBehandlingstema() {
        return behandlingstema;
    }

    public String getBehandlingstype() {
        return behandlingstype;
    }

    public String getEndretAvEnhetsnr() {
        return endretAvEnhetsnr;
    }

    public List<PatchOppgave> getOppgaver() {
        return oppgaver;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("mappeId", mappeId)
                .append("tilordnetRessurs", tilordnetRessurs)
                .append("tildeltEnhetsnr", tildeltEnhetsnr)
                .append("endretAvEnhetsnr", endretAvEnhetsnr)
                .append("behandlingstema", behandlingstema)
                .append("behandlingstype", behandlingstype)
                .append("oppgaver", oppgaver)
                .toString();
    }

    public static final class Builder {
        Long mappeId;
        String tilordnetRessurs;
        String tildeltEnhetsnr;
        String behandlingstema;
        String endretAvEnhetsnr;
        String behandlingstype;
        List<PatchOppgave> oppgaver;

        public Builder withMappeId(Long mappeId) {
            this.mappeId = mappeId;
            return this;
        }

        public Builder withEndretAvEnhetsnr(String endretAvEnhetsnr) {
            this.endretAvEnhetsnr = endretAvEnhetsnr;
            return this;
        }

        public Builder withTilordnetRessurs(String tilordnetRessurs) {
            this.tilordnetRessurs = tilordnetRessurs;
            return this;
        }

        public Builder withTildeltEnhetsnr(String tildeltEnhetsnr) {
            this.tildeltEnhetsnr = tildeltEnhetsnr;
            return this;
        }

        public Builder withBehandlingstema(String behandlingstema) {
            this.behandlingstema = behandlingstema;
            return this;
        }

        public Builder withBehandlingstype(String behandlingstype) {
            this.behandlingstype = behandlingstype;
            return this;
        }

        public Builder withOppgaver(List<PatchOppgave> oppgaver) {
            this.oppgaver = oppgaver;
            return this;
        }

        public PatchOppgaverRequest build() {
            return new PatchOppgaverRequest(this);
        }
    }
}