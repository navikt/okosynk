package no.nav.okosynk.synkroniserer.consumer.oppgave.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.nav.okosynk.synkroniserer.consumer.oppgave.LocalDateTimeSerializer;
import no.nav.okosynk.synkroniserer.consumer.oppgave.OppgaveStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

public abstract class AbstractOppgaveJson {

    private Map<String, String> metadata;
    private String aktivDato;
    private String behandlesAvApplikasjon;
    private String behandlingstema;
    private String behandlingstype;
    private String beskrivelse;
    private String endretAv;
    private String endretAvEnhetsnr;
    private LocalDateTime endretTidspunkt;
    private LocalDateTime ferdigstiltTidspunkt;
    private String fristFerdigstillelse;
    private String id;
    private String journalpostId;
    private String journalpostkilde;
    private String mappeId;
    private String oppgavetype;
    private String opprettetAv;
    private String opprettetAvEnhetsnr;
    private LocalDateTime opprettetTidspunkt;
    private String prioritet;
    private String saksreferanse;
    private OppgaveStatus status;
    private String tema;
    private String temagruppe;
    private String tildeltEnhetsnr;
    private String tilordnetRessurs;
    private Integer versjon;

    private String aktoerId;
    private String bnr;
    private String orgnr;
    private String samhandlernr;

    @JsonIgnore
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @JsonIgnore
    @JsonProperty("endretAvEnhetsnr")
    public String getEndretAvEnhetsnr() {
        return endretAvEnhetsnr;
    }

    public void setEndretAvEnhetsnr(final String endretAvEnhetsnr) {
        this.endretAvEnhetsnr = endretAvEnhetsnr;
    }

    public String getOpprettetAvEnhetsnr() {
        return opprettetAvEnhetsnr;
    }

    public void setOpprettetAvEnhetsnr(final String opprettetAvEnhetsnr) {
        this.opprettetAvEnhetsnr = opprettetAvEnhetsnr;
    }

    public String getBnr() {
        return bnr;
    }

    public void setBnr(final String bnr) {
        this.bnr = bnr;
    }

    public String getSamhandlernr() {
        return samhandlernr;
    }

    public void setSamhandlernr(final String samhandlernr) {
        this.samhandlernr = samhandlernr;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public void setAktoerId(final String aktoerId) {
        this.aktoerId = aktoerId;
    }

    public String getBeskrivelse() {
        return this.beskrivelse;
    }

    public void setBeskrivelse(final String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public String getJournalpostkilde() {
        return this.journalpostkilde;
    }

    public void setJournalpostkilde(final String journalpostkilde) {
        this.journalpostkilde = journalpostkilde;
    }

    public String getBehandlesAvApplikasjon() {
        return this.behandlesAvApplikasjon;
    }

    public void setBehandlesAvApplikasjon(final String behandlesAvApplikasjon) {
        this.behandlesAvApplikasjon = behandlesAvApplikasjon;
    }

    public String getTema() {
        return this.tema;
    }

    public void setTema(final String tema) {
        this.tema = tema;
    }

    public String getJournalpostId() {
        return this.journalpostId;
    }

    public void setJournalpostId(final String journalpostId) {
        this.journalpostId = journalpostId;
    }

    public String getSaksreferanse() {
        return saksreferanse;
    }

    public void setSaksreferanse(final String saksreferanse) {
        this.saksreferanse = saksreferanse;
    }

    public String getOrgnr() {
        return orgnr;
    }

    public void setOrgnr(final String orgnr) {
        this.orgnr = orgnr;
    }

    public String getTemagruppe() {
        return this.temagruppe;
    }

    public void setTemagruppe(final String temagruppe) {
        this.temagruppe = temagruppe;
    }

    public String getBehandlingstema() {
        return this.behandlingstema;
    }

    public void setBehandlingstema(final String behandlingstema) {
        this.behandlingstema = behandlingstema;
    }

    public String getOppgavetype() {
        return this.oppgavetype;
    }

    public void setOppgavetype(final String oppgavetype) {
        this.oppgavetype = oppgavetype;
    }

    public String getBehandlingstype() {
        return this.behandlingstype;
    }

    public void setBehandlingstype(final String behandlingstype) {
        this.behandlingstype = behandlingstype;
    }

    public String getAktivDato() {
        return this.aktivDato;
    }

    public void setAktivDato(final String aktivDato) {
        this.aktivDato = aktivDato;
    }

    public String getFristFerdigstillelse() {
        return this.fristFerdigstillelse;
    }

    public void setFristFerdigstillelse(final String fristFerdigstillelse) {
        this.fristFerdigstillelse = fristFerdigstillelse;
    }

    public String getPrioritet() {
        return this.prioritet;
    }

    public void setPrioritet(final String prioritet) {
        this.prioritet = prioritet;
    }

    public String getTildeltEnhetsnr() {
        return this.tildeltEnhetsnr;
    }

    public void setTildeltEnhetsnr(final String tildeltEnhetsnr) {
        this.tildeltEnhetsnr = tildeltEnhetsnr;
    }

    public String getTilordnetRessurs() {
        return this.tilordnetRessurs;
    }

    public void setTilordnetRessurs(final String tilordnetRessurs) {
        this.tilordnetRessurs = tilordnetRessurs;
    }

    public String getMappeId() {
        return this.mappeId;
    }

    public void setMappeId(final String mappeId) {
        this.mappeId = mappeId;
    }

    @JsonIgnore
    @JsonProperty("status")
    public OppgaveStatus getStatus() {
        return this.status;
    }

    public void setStatus(final OppgaveStatus status) {
        this.status = status;
    }

    @JsonIgnore
    @JsonProperty("opprettetAv")
    public String getOpprettetAv() {
        return this.opprettetAv;
    }

    public void setOpprettetAv(final String opprettetAv) {
        this.opprettetAv = opprettetAv;
    }

    @JsonIgnore
    @JsonProperty("opprettetTidspunkt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getOpprettetTidspunkt() {
        return this.opprettetTidspunkt;
    }

    public void setOpprettetTidspunkt(final String opprettetTidspunkt) {
        this.opprettetTidspunkt = opprettetTidspunkt == null ? null : ZonedDateTime.parse(opprettetTidspunkt).toLocalDateTime();
    }

    @JsonIgnore
    @JsonProperty("versjon")
    public Integer getVersjon() {
        return this.versjon;
    }

    public void setVersjon(final Integer versjon) {
        this.versjon = versjon;
    }

    @JsonIgnore
    @JsonProperty("endretAv")
    public String getEndretAv() {
        return this.endretAv;
    }

    public void setEndretAv(final String endretAv) {
        this.endretAv = endretAv;
    }

    @JsonIgnore
    @JsonProperty("endretTidspunkt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getEndretTidspunkt() {
        return this.endretTidspunkt;
    }

    public void setEndretTidspunkt(final String endretTidspunkt) {
        this.endretTidspunkt = endretTidspunkt == null ? null : ZonedDateTime.parse(endretTidspunkt).toLocalDateTime();
    }

    @JsonIgnore
    @JsonProperty("ferdigstiltTidspunkt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getFerdigstiltTidspunkt() {
        return this.ferdigstiltTidspunkt;
    }

    public void setFerdigstiltTidspunkt(final String ferdigstiltTidspunkt) {
        this.ferdigstiltTidspunkt =
                ferdigstiltTidspunkt == null ? null : ZonedDateTime.parse(ferdigstiltTidspunkt).toLocalDateTime();
    }

    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    public void setMetadata(final Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AbstractOppgaveJson oppgaveJson = (AbstractOppgaveJson) o;

        return new EqualsBuilder()
                .append(this.id, oppgaveJson.id)
                .append(this.tema, oppgaveJson.tema)

                .append(this.aktoerId, oppgaveJson.aktoerId)
                .append(this.orgnr, oppgaveJson.orgnr)
                .append(this.bnr, oppgaveJson.bnr)
                .append(this.samhandlernr, oppgaveJson.samhandlernr)

                .append(this.journalpostId, oppgaveJson.journalpostId)
                .append(this.saksreferanse, oppgaveJson.saksreferanse)
                .append(this.temagruppe, oppgaveJson.temagruppe)
                .append(this.behandlingstema, oppgaveJson.behandlingstema)
                .append(this.oppgavetype, oppgaveJson.oppgavetype)
                .append(this.behandlingstype, oppgaveJson.behandlingstype)
                .append(this.aktivDato, oppgaveJson.aktivDato)
                .append(this.fristFerdigstillelse, oppgaveJson.fristFerdigstillelse)
                .append(this.prioritet, oppgaveJson.prioritet)
                .append(this.tildeltEnhetsnr, oppgaveJson.tildeltEnhetsnr)
                .append(this.opprettetAvEnhetsnr, oppgaveJson.opprettetAvEnhetsnr)
                .append(this.endretAvEnhetsnr, oppgaveJson.endretAvEnhetsnr)
                .append(this.tilordnetRessurs, oppgaveJson.tilordnetRessurs)
                .append(this.mappeId, oppgaveJson.mappeId)
                .append(this.status, oppgaveJson.status)
                .append(this.versjon, oppgaveJson.versjon)
                .append(this.opprettetAv, oppgaveJson.opprettetAv)
                .append(this.endretAv, oppgaveJson.endretAv)
                .append(this.opprettetTidspunkt, oppgaveJson.opprettetTidspunkt)
                .append(this.endretTidspunkt, oppgaveJson.endretTidspunkt)
                .append(this.beskrivelse, oppgaveJson.getBeskrivelse())
                .append(this.journalpostkilde, oppgaveJson.getJournalpostkilde())
                .append(this.behandlesAvApplikasjon, oppgaveJson.getBehandlesAvApplikasjon())
                .append(this.metadata, oppgaveJson.metadata)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(101, 103)
                .append(id)
                .append(tema)
                .append(aktoerId)
                .append(orgnr)
                .append(bnr)
                .append(samhandlernr)
                .append(journalpostId)
                .append(saksreferanse)
                .append(temagruppe)
                .append(behandlingstema)
                .append(oppgavetype)
                .append(behandlingstype)
                .append(aktivDato)
                .append(fristFerdigstillelse)
                .append(prioritet)
                .append(tildeltEnhetsnr)
                .append(opprettetAvEnhetsnr)
                .append(endretAvEnhetsnr)
                .append(tilordnetRessurs)
                .append(mappeId)
                .append(status)
                .append(versjon)
                .append(opprettetAv)
                .append(endretAv)
                .append(opprettetTidspunkt)
                .append(endretTidspunkt)
                .append(beskrivelse)
                .append(journalpostkilde)
                .append(behandlesAvApplikasjon)
                .append(metadata)
                .toHashCode();
    }
}