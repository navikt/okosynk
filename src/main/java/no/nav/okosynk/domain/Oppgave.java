package no.nav.okosynk.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Oppgave {

    private static final String LOG_FIELD_SEPARATOR = ", ";
    private static final String LOG_PARAGRAPH_SEPARATOR = ". - ";
    private static final Logger logger = LoggerFactory.getLogger(Oppgave.class);
    public final LocalDate aktivFra;
    public final LocalDate aktivTil;
    public final int antallMeldinger;
    public final LocalDateTime sistEndret;
    public final String ansvarligEnhetId;
    public final String ansvarligSaksbehandlerIdent;
    public final String behandlingstema;
    public final String behandlingstype;
    public final String beskrivelse;
    public final String fagomradeKode;
    public final boolean lest;
    public final String mappeId;
    public final String oppgaveId;
    public final String oppgavetypeKode;
    public final String prioritetKode;
    public final int versjon;

    public final String aktoerId;
    public final String navPersonIdent;
    public final String bnr;
    public final String orgnr;
    public final String samhandlernr;

    private Oppgave(OppgaveBuilder oppgaveBuilder) {
        this.oppgaveId = oppgaveBuilder.oppgaveId;
        this.oppgavetypeKode = oppgaveBuilder.oppgavetypeKode;
        this.fagomradeKode = oppgaveBuilder.fagomradeKode;
        this.behandlingstema = oppgaveBuilder.behandlingstema;
        this.behandlingstype = oppgaveBuilder.behandlingstype;
        this.prioritetKode = oppgaveBuilder.prioritetKode;
        this.beskrivelse = oppgaveBuilder.beskrivelse;
        this.aktivFra = oppgaveBuilder.aktivFra;
        this.aktivTil = oppgaveBuilder.aktivTil;
        this.ansvarligEnhetId = oppgaveBuilder.ansvarligEnhetId;
        this.lest = oppgaveBuilder.lest;
        this.versjon = oppgaveBuilder.versjon;
        this.sistEndret = oppgaveBuilder.sistEndret;
        this.antallMeldinger = oppgaveBuilder.antallMeldinger;
        this.mappeId = oppgaveBuilder.mappeId;
        this.ansvarligSaksbehandlerIdent = oppgaveBuilder.saksbehandlerIdent;

        this.aktoerId = oppgaveBuilder.aktoerId;
        this.navPersonIdent = oppgaveBuilder.navPersonIdent;
        this.orgnr = oppgaveBuilder.orgnr;
        this.samhandlernr = oppgaveBuilder.samhandlernr;
        this.bnr = oppgaveBuilder.bnr;
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
                &&
                Objects.equals(this.behandlingstype, other.behandlingstype)
                &&
                Objects.equals(this.ansvarligEnhetId, other.ansvarligEnhetId)
                &&
                (
                        (
                                this.aktoerId == null && other.aktoerId == null
                                &&
                                Objects.equals(this.navPersonIdent, other.navPersonIdent)
                        )
                        ||
                        (
                                Objects.equals(this.aktoerId, other.aktoerId)
                                &&
                                this.navPersonIdent == null && other.navPersonIdent == null
                        )
                        ||
                        (

                                !(this.aktoerId == null && other.aktoerId == null)
                                &&
                                !(this.navPersonIdent == null && other.navPersonIdent == null)
                                &&
                                (
                                        Objects.equals(this.aktoerId, other.aktoerId)
                                        ||
                                        Objects.equals(this.navPersonIdent, other.navPersonIdent)
                                )
                        )
                )
                &&
                Objects.equals(this.bnr, other.bnr)
                &&
                Objects.equals(this.orgnr, other.orgnr)
                &&
                Objects.equals(this.samhandlernr, other.samhandlernr)
                ;
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

        final StringBuffer strBuff =
                new StringBuffer()
                        .append("oppgaveId                  : ").append(this.oppgaveId).append(LOG_FIELD_SEPARATOR)
                        .append("aktoerId                   : ").append(this.aktoerId).append(LOG_FIELD_SEPARATOR)
                        .append("navPersonIdent             : ").append(this.navPersonIdent).append(LOG_FIELD_SEPARATOR)
                        .append("samhandlernr               : ").append(this.samhandlernr).append(LOG_FIELD_SEPARATOR)
                        .append("beskrivelse                : ").append(this.beskrivelse == null ? "<null>" : beskrivelse.substring(0, Math.min(beskrivelse.length(), 30))).append("...").append(LOG_FIELD_SEPARATOR)
                        .append("orgnr                      : ").append(this.orgnr).append(LOG_FIELD_SEPARATOR)
                        .append("bnr                        : ").append(this.bnr).append(LOG_FIELD_SEPARATOR)
                        .append("oppgavetypeKode            : ").append(this.oppgavetypeKode).append(LOG_FIELD_SEPARATOR)
                        .append("fagomradeKode              : ").append(this.fagomradeKode).append(LOG_FIELD_SEPARATOR)
                        .append("behandlingstema            : ").append(this.behandlingstema).append(LOG_FIELD_SEPARATOR)
                        .append("behandlingstype            : ").append(this.behandlingstype).append(LOG_FIELD_SEPARATOR)
                        .append("prioritetKode              : ").append(this.prioritetKode).append(LOG_FIELD_SEPARATOR)
                        .append("aktivFra                   : ").append(this.aktivFra).append(LOG_FIELD_SEPARATOR)
                        .append("aktivTil                   : ").append(this.aktivTil).append(LOG_FIELD_SEPARATOR)
                        .append("ansvarligEnhetId           : ").append(this.ansvarligEnhetId).append(LOG_FIELD_SEPARATOR)
                        .append("lest                       : ").append(this.lest).append(LOG_FIELD_SEPARATOR)
                        .append("versjon                    : ").append(this.versjon).append(LOG_FIELD_SEPARATOR)
                        .append("sistEndret                 : ").append(this.sistEndret).append(LOG_FIELD_SEPARATOR)
                        .append("antallMeldinger            : ").append(this.antallMeldinger).append(LOG_FIELD_SEPARATOR)
                        .append("mappeId                    : ").append(this.mappeId).append(LOG_FIELD_SEPARATOR)
                        .append("ansvarligSaksbehandlerIdent: ").append(this.ansvarligSaksbehandlerIdent);

        final String str = strBuff.toString();

        return str;
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
        private String navPersonIdent;
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

        public OppgaveBuilder withNavPersonIdent(final String navPersonIdent) {
            this.navPersonIdent = navPersonIdent;
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

        public OppgaveBuilder withSameValuesAs(final Oppgave otherOppgave) {
            this.oppgaveId = otherOppgave.oppgaveId;
            this.navPersonIdent = otherOppgave.navPersonIdent;
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

            final Oppgave oppgave = new Oppgave(this);

            return oppgave;
        }
    }
}
