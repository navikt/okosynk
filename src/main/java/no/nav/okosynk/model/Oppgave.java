package no.nav.okosynk.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

public record Oppgave(LocalDate aktivFra, LocalDate aktivTil, String aktoerId, String ansvarligEnhetId,
                      String ansvarligSaksbehandlerIdent,
                      int antallMeldinger, String behandlingstema, String behandlingstype, String beskrivelse,
                      String bnr,
                      String fagomradeKode, String folkeregisterIdent, boolean lest, String mappeId, String oppgaveId,
                      String oppgavetypeKode, String orgnr, String prioritetKode, String samhandlernr,
                      LocalDateTime sistEndret,
                      int versjon) {

    private static final String LOG_FIELD_SEPARATOR = ", ";

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .aktivFra(this.aktivFra)
                .aktivTil(this.aktivTil)
                .aktoerId(this.aktoerId)
                .ansvarligEnhetId(this.ansvarligEnhetId)
                .ansvarligSaksbehandlerIdent(this.ansvarligSaksbehandlerIdent)
                .antallMeldinger(this.antallMeldinger)
                .behandlingstema(this.behandlingstema)
                .behandlingstype(this.behandlingstype)
                .beskrivelse(this.beskrivelse)
                .bnr(this.bnr)
                .fagomradeKode(this.fagomradeKode)
                .folkeregisterIdent(this.folkeregisterIdent)
                .lest(this.lest)
                .mappeId(this.mappeId)
                .oppgaveId(this.oppgaveId)
                .oppgavetypeKode(this.oppgavetypeKode)
                .orgnr(this.orgnr)
                .prioritetKode(this.prioritetKode)
                .samhandlernr(this.samhandlernr)
                .sistEndret(this.sistEndret)
                .versjon(this.versjon);
    }

    public static class Builder {
        private LocalDate aktivFra;
        private LocalDate aktivTil;
        private String aktoerId;
        private String ansvarligEnhetId;
        private String ansvarligSaksbehandlerIdent;
        private int antallMeldinger;
        private String behandlingstema;
        private String behandlingstype;
        private String beskrivelse;
        private String bnr;
        private String fagomradeKode;
        private String folkeregisterIdent;
        private boolean lest;
        private String mappeId;
        private String oppgaveId;
        private String oppgavetypeKode;
        private String orgnr;
        private String prioritetKode;
        private String samhandlernr;
        private LocalDateTime sistEndret;
        private int versjon;

        public Builder aktivFra(LocalDate aktivFra) {
            this.aktivFra = aktivFra;
            return this;
        }

        public Builder aktivTil(LocalDate aktivTil) {
            this.aktivTil = aktivTil;
            return this;
        }

        public Builder aktoerId(String aktoerId) {
            this.aktoerId = aktoerId;
            return this;
        }

        public Builder ansvarligEnhetId(String ansvarligEnhetId) {
            this.ansvarligEnhetId = ansvarligEnhetId;
            return this;
        }

        public Builder ansvarligSaksbehandlerIdent(String ansvarligSaksbehandlerIdent) {
            this.ansvarligSaksbehandlerIdent = ansvarligSaksbehandlerIdent;
            return this;
        }

        public Builder antallMeldinger(int antallMeldinger) {
            this.antallMeldinger = antallMeldinger;
            return this;
        }

        public Builder behandlingstema(String behandlingstema) {
            this.behandlingstema = behandlingstema;
            return this;
        }

        public Builder behandlingstype(String behandlingstype) {
            this.behandlingstype = behandlingstype;
            return this;
        }

        public Builder beskrivelse(String beskrivelse) {
            this.beskrivelse = beskrivelse;
            return this;
        }

        public Builder bnr(String bnr) {
            this.bnr = bnr;
            return this;
        }

        public Builder fagomradeKode(String fagomradeKode) {
            this.fagomradeKode = fagomradeKode;
            return this;
        }

        public Builder folkeregisterIdent(String folkeregisterIdent) {
            this.folkeregisterIdent = folkeregisterIdent;
            return this;
        }

        public Builder lest(boolean lest) {
            this.lest = lest;
            return this;
        }

        public Builder mappeId(String mappeId) {
            this.mappeId = mappeId;
            return this;
        }

        public Builder oppgaveId(String oppgaveId) {
            this.oppgaveId = oppgaveId;
            return this;
        }

        public Builder oppgavetypeKode(String oppgavetypeKode) {
            this.oppgavetypeKode = oppgavetypeKode;
            return this;
        }

        public Builder orgnr(String orgnr) {
            this.orgnr = orgnr;
            return this;
        }

        public Builder prioritetKode(String prioritetKode) {
            this.prioritetKode = prioritetKode;
            return this;
        }

        public Builder samhandlernr(String samhandlernr) {
            this.samhandlernr = samhandlernr;
            return this;
        }

        public Builder sistEndret(LocalDateTime sistEndret) {
            this.sistEndret = sistEndret;
            return this;
        }

        public Builder versjon(int versjon) {
            this.versjon = versjon;
            return this;
        }

        public Oppgave build() {
            return new Oppgave(aktivFra, aktivTil, aktoerId, ansvarligEnhetId, ansvarligSaksbehandlerIdent, antallMeldinger, behandlingstema, behandlingstype, beskrivelse, bnr, fagomradeKode, folkeregisterIdent, lest, mappeId, oppgaveId, oppgavetypeKode, orgnr, prioritetKode, samhandlernr, sistEndret, versjon);
        }
    }

    boolean bareEnFinnes(String... strings) {
        return Stream.of(strings).filter(Objects::nonNull).count() == 1L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Oppgave other = (Oppgave) o;
        return Objects.equals(this.behandlingstema, other.behandlingstema)
                && Objects.equals(this.behandlingstype, other.behandlingstype)
                && Objects.equals(this.ansvarligEnhetId, other.ansvarligEnhetId)
                && Objects.equals(this.bnr, other.bnr)
                && Objects.equals(this.orgnr, other.orgnr)
                && Objects.equals(this.samhandlernr, other.samhandlernr)
                && !bareEnFinnes(this.aktoerId, other.aktoerId, this.folkeregisterIdent, other.folkeregisterIdent)
                && (Objects.equals(this.aktoerId, other.aktoerId) && (this.folkeregisterIdent == null || other.folkeregisterIdent == null)
                || Objects.equals(this.folkeregisterIdent, other.folkeregisterIdent) && (this.aktoerId == null || other.aktoerId == null)
                || Objects.equals(this.aktoerId, other.aktoerId) && Objects.equals(this.folkeregisterIdent, other.folkeregisterIdent));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                behandlingstema,
                behandlingstype,
                ansvarligEnhetId,
                bnr,
                orgnr,
                samhandlernr
        );
    }

    @Override
    public String toString() {
        return "oppgaveId                  : " + this.oppgaveId + LOG_FIELD_SEPARATOR +
                "aktoerId                   : " + this.aktoerId + LOG_FIELD_SEPARATOR +
                "folkeregisterIdent         : " + this.folkeregisterIdent + LOG_FIELD_SEPARATOR +
                "samhandlernr               : " + this.samhandlernr + LOG_FIELD_SEPARATOR +
                "beskrivelse                : " + (this.beskrivelse == null ? "<null>" : beskrivelse.substring(0, Math.min(beskrivelse.length(), 30))) + "..." + LOG_FIELD_SEPARATOR +
                "orgnr                      : " + this.orgnr + LOG_FIELD_SEPARATOR +
                "bnr                        : " + this.bnr + LOG_FIELD_SEPARATOR +
                "oppgavetypeKode            : " + this.oppgavetypeKode + LOG_FIELD_SEPARATOR +
                "fagomradeKode              : " + this.fagomradeKode + LOG_FIELD_SEPARATOR +
                "behandlingstema            : " + this.behandlingstema + LOG_FIELD_SEPARATOR +
                "behandlingstype            : " + this.behandlingstype + LOG_FIELD_SEPARATOR +
                "prioritetKode              : " + this.prioritetKode + LOG_FIELD_SEPARATOR +
                "aktivFra                   : " + this.aktivFra + LOG_FIELD_SEPARATOR +
                "aktivTil                   : " + this.aktivTil + LOG_FIELD_SEPARATOR +
                "ansvarligEnhetId           : " + this.ansvarligEnhetId + LOG_FIELD_SEPARATOR +
                "lest                       : " + this.lest + LOG_FIELD_SEPARATOR +
                "versjon                    : " + this.versjon + LOG_FIELD_SEPARATOR +
                "sistEndret                 : " + this.sistEndret + LOG_FIELD_SEPARATOR +
                "antallMeldinger            : " + this.antallMeldinger + LOG_FIELD_SEPARATOR +
                "mappeId                    : " + this.mappeId + LOG_FIELD_SEPARATOR +
                "ansvarligSaksbehandlerIdent: " + this.ansvarligSaksbehandlerIdent;
    }
}