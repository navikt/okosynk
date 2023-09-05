package no.nav.okosynk.model;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.AbstractOppgaveOppretter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

public class Oppgave {

    private static final String LOG_FIELD_SEPARATOR = ", ";
    public final LocalDate aktivFra;
    public final LocalDate aktivTil;
    public final LocalDateTime sistEndret;
    public final String aktoerId;
    public final String ansvarligEnhetId;
    public final String ansvarligSaksbehandlerIdent;
    public final String behandlingstema;
    public final String behandlingstype;
    public final String beskrivelse;
    public final String bnr;
    public final String fagomradeKode;
    public final String folkeregisterIdent;
    public final String mappeId;
    public final String oppgaveId;
    public final String oppgavetypeKode;
    public final String orgnr;
    public final String prioritetKode;
    public final String samhandlernr;
    public final boolean lest;
    public final int antallMeldinger;
    public final int versjon;

    private Oppgave(OppgaveBuilder oppgaveBuilder) {
        this.aktivFra = oppgaveBuilder.aktivFra;
        this.aktivTil = oppgaveBuilder.aktivTil;
        this.aktoerId = oppgaveBuilder.aktoerId;
        this.ansvarligEnhetId = oppgaveBuilder.ansvarligEnhetId;
        this.ansvarligSaksbehandlerIdent = oppgaveBuilder.saksbehandlerIdent;
        this.antallMeldinger = oppgaveBuilder.antallMeldinger;
        this.behandlingstema = oppgaveBuilder.behandlingstema;
        this.behandlingstype = oppgaveBuilder.behandlingstype;
        this.beskrivelse = oppgaveBuilder.beskrivelse;
        this.bnr = oppgaveBuilder.bnr;
        this.fagomradeKode = oppgaveBuilder.fagomradeKode;
        this.folkeregisterIdent = oppgaveBuilder.folkeregisterIdent;
        this.lest = oppgaveBuilder.lest;
        this.mappeId = oppgaveBuilder.mappeId;
        this.oppgaveId = oppgaveBuilder.oppgaveId;
        this.oppgavetypeKode = oppgaveBuilder.oppgavetypeKode;
        this.orgnr = oppgaveBuilder.orgnr;
        this.prioritetKode = oppgaveBuilder.prioritetKode;
        this.samhandlernr = oppgaveBuilder.samhandlernr;
        this.sistEndret = oppgaveBuilder.sistEndret;
        this.versjon = oppgaveBuilder.versjon;
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

    public static class OppgaveBuilder {

        private LocalDate aktivFra;
        private LocalDate aktivTil;
        private String aktoerId;
        private String ansvarligEnhetId;
        private int antallMeldinger;
        private String behandlingstema;
        private String behandlingstype;
        private String beskrivelse;
        private String bnr;
        private String fagomradeKode;
        private boolean lest;
        private String mappeId;
        private String folkeregisterIdent;
        private String oppgaveId;
        private String oppgavetypeKode;
        private String orgnr;
        private String prioritetKode;
        private String saksbehandlerIdent;
        private String samhandlernr;
        private LocalDateTime sistEndret;
        private int versjon;

        public OppgaveBuilder withOppgaveId(final String oppgaveId) {
            this.oppgaveId = oppgaveId;
            return this;
        }

        public OppgaveBuilder withAktoerId(final String aktoerId) {
            this.aktoerId = aktoerId;
            return this;
        }

        public OppgaveBuilder withFolkeregisterIdent(final String folkeregisterIdent) {
            this.folkeregisterIdent = folkeregisterIdent;
            return this;
        }

        public OppgaveBuilder withSamhandlernr(final String samhandlernr) {
            this.samhandlernr = samhandlernr;
            return this;
        }

        public OppgaveBuilder withOrgnr(final String orgnr) {
            this.orgnr = orgnr;
            return this;
        }

        public OppgaveBuilder withBnr(final String bnr) {
            this.bnr = bnr;
            return this;
        }

        public OppgaveBuilder withOppgavetypeKode(final String oppgavetypeKode) {
            this.oppgavetypeKode = oppgavetypeKode;
            return this;
        }

        public OppgaveBuilder withFagomradeKode(final String fagomradeKode) {
            this.fagomradeKode = fagomradeKode;
            return this;
        }

        public OppgaveBuilder withBehandlingstema(final String behandlingstema) {
            this.behandlingstema = behandlingstema;
            return this;
        }

        public OppgaveBuilder withBehandlingstype(final String behandlingstype) {
            this.behandlingstype = behandlingstype;
            return this;
        }

        public OppgaveBuilder withPrioritetKode(final String prioritetKode) {
            this.prioritetKode = prioritetKode;
            return this;
        }

        public OppgaveBuilder withBeskrivelse(final String beskrivelse) {
            this.beskrivelse = beskrivelse;
            return this;
        }

        public OppgaveBuilder withAktivFra(final LocalDate aktivFra) {
            this.aktivFra = aktivFra;
            return this;
        }

        public OppgaveBuilder withAktivTil(final LocalDate aktivTil) {
            this.aktivTil = aktivTil;
            return this;
        }

        public OppgaveBuilder withAnsvarligEnhetId(final String ansvarligEnhetId) {
            this.ansvarligEnhetId = ansvarligEnhetId;
            return this;
        }

        public OppgaveBuilder withLest(final boolean lest) {
            this.lest = lest;
            return this;
        }

        public OppgaveBuilder withVersjon(final int versjon) {
            this.versjon = versjon;
            return this;
        }

        public OppgaveBuilder withSistEndret(final LocalDateTime sistEndret) {
            this.sistEndret = sistEndret;
            return this;
        }

        public OppgaveBuilder withAntallMeldinger(final int antallMeldinger) {
            this.antallMeldinger = antallMeldinger;
            return this;
        }

        public OppgaveBuilder withMappeId(final String mappeId) {
            this.mappeId = mappeId;
            return this;
        }

        public OppgaveBuilder withAnsvarligSaksbehandlerIdent(final String saksbehandlerIdent) {
            this.saksbehandlerIdent = saksbehandlerIdent;
            return this;
        }

        public OppgaveBuilder withGjelderIdResultat(final AbstractOppgaveOppretter.GjelderIdResultat gjelderIdResultat) {
            switch (gjelderIdResultat.gjelderIdFelt()) {
                case BNR:
                    this.bnr = gjelderIdResultat.gjelderId();
                    break;
                case AKTORID:
                    this.aktoerId = gjelderIdResultat.gjelderId();
                    break;
                case SAMHANDLER:
                    this.samhandlernr = gjelderIdResultat.gjelderId();
                    break;
                case ORGANISASJON:
                    this.orgnr = gjelderIdResultat.gjelderId();
                    break;
                case FEIL:
                case INGEN_GJELDERID:
                    break;
            }
            return this;
        }

        public OppgaveBuilder withSameValuesAs(final Oppgave otherOppgave) {
            this.oppgaveId = otherOppgave.oppgaveId;
            this.folkeregisterIdent = otherOppgave.folkeregisterIdent;
            this.aktoerId = otherOppgave.aktoerId;
            this.samhandlernr = otherOppgave.samhandlernr;
            this.orgnr = otherOppgave.orgnr;
            this.bnr = otherOppgave.bnr;
            this.oppgavetypeKode = otherOppgave.oppgavetypeKode;
            this.fagomradeKode = otherOppgave.fagomradeKode;
            this.behandlingstema = otherOppgave.behandlingstema;
            this.behandlingstype = otherOppgave.behandlingstype;
            this.prioritetKode = otherOppgave.prioritetKode;
            this.beskrivelse = otherOppgave.beskrivelse;
            this.aktivFra = otherOppgave.aktivFra;
            this.aktivTil = otherOppgave.aktivTil;
            this.ansvarligEnhetId = otherOppgave.ansvarligEnhetId;
            this.lest = otherOppgave.lest;
            this.mappeId = otherOppgave.mappeId;
            this.saksbehandlerIdent = otherOppgave.ansvarligSaksbehandlerIdent;

            this.versjon = otherOppgave.versjon;
            this.sistEndret = otherOppgave.sistEndret;
            return this;
        }

        public Oppgave build() {
            return new Oppgave(this);
        }
    }
}
