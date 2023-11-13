package no.nav.okosynk.model;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

@Builder(toBuilder = true)
public record Oppgave(LocalDate aktivFra, LocalDate aktivTil, String aktoerId, String ansvarligEnhetId,
                      String ansvarligSaksbehandlerIdent,
                      int antallMeldinger, String behandlingstema, String behandlingstype, String beskrivelse,
                      String bnr,
                      String fagomradeKode, String folkeregisterIdent, boolean lest, String mappeId, String oppgaveId,
                      String oppgavetypeKode, String orgnr, String prioritetKode, String samhandlernr,
                      LocalDateTime sistEndret,
                      int versjon) {

    private static final String LOG_FIELD_SEPARATOR = ", ";
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
