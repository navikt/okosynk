package no.nav.okosynk.consumer.oppgave;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Map;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class OppgaveDTO {

    private Integer id;
    private String aktoerId;
    private String orgnr;
    private String tema;
    private String journalpostId;
    private String saksreferanse;
    private String temagruppe;
    private String behandlingstema;
    private String oppgavetype;
    private String behandlingstype;
    private Date aktivDato;
    private Date fristFerdigstillelse;
    private String prioritet;
    private String tildeltEnhetsnr;
    private String endretAvEnhetsnr;
    private String opprettetAvEnhetsnr;
    private String tilordnetRessurs;
    private Integer mappeId;
    private OppgaveStatus status;
    private String opprettetAv;
    private String endretAv;
    private Date opprettetTidspunkt;
    private Date endretTidspunkt;
    private Date ferdigstiltTidspunkt;
    private String beskrivelse;
    private Integer versjon;
    private String journalpostkilde;
    private String behandlesAvApplikasjon;
    private String bnr;
    private String samhandlernr;
    Map<String, String> metadata;

    public OppgaveDTO() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEndretAvEnhetsnr() {
        return endretAvEnhetsnr;
    }

    public void setEndretAvEnhetsnr(String endretAvEnhetsnr) {
        this.endretAvEnhetsnr = endretAvEnhetsnr;
    }

    public String getOpprettetAvEnhetsnr() {
        return opprettetAvEnhetsnr;
    }

    public void setOpprettetAvEnhetsnr(String opprettetAvEnhetsnr) {
        this.opprettetAvEnhetsnr = opprettetAvEnhetsnr;
    }

    public String getBnr() {
        return bnr;
    }

    public void setBnr(String bnr) {
        this.bnr = bnr;
    }

    public String getSamhandlernr() {
        return samhandlernr;
    }

    public void setSamhandlernr(String samhandlernr) {
        this.samhandlernr = samhandlernr;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public void setAktoerId(String aktoerId) {
        this.aktoerId = aktoerId;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public String getJournalpostkilde() {
        return journalpostkilde;
    }

    public void setJournalpostkilde(String journalpostkilde) {
        this.journalpostkilde = journalpostkilde;
    }

    public String getBehandlesAvApplikasjon() {
        return behandlesAvApplikasjon;
    }

    public void setBehandlesAvApplikasjon(String behandlesAvApplikasjon) {
        this.behandlesAvApplikasjon = behandlesAvApplikasjon;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public void setJournalpostId(String journalpostId) {
        this.journalpostId = journalpostId;
    }

    public String getSaksreferanse() {
        return saksreferanse;
    }

    public void setSaksreferanse(String saksreferanse) {
        this.saksreferanse = saksreferanse;
    }

    public String getOrgnr() {
        return orgnr;
    }

    public void setOrgnr(String orgnr) {
        this.orgnr = orgnr;
    }

    public String getTemagruppe() {
        return temagruppe;
    }

    public void setTemagruppe(String temagruppe) {
        this.temagruppe = temagruppe;
    }

    public String getBehandlingstema() {
        return behandlingstema;
    }

    public void setBehandlingstema(String behandlingstema) {
        this.behandlingstema = behandlingstema;
    }

    public String getOppgavetype() {
        return oppgavetype;
    }

    public void setOppgavetype(String oppgavetype) {
        this.oppgavetype = oppgavetype;
    }

    public String getBehandlingstype() {
        return behandlingstype;
    }

    public void setBehandlingstype(String behandlingstype) {
        this.behandlingstype = behandlingstype;
    }

    public Date getAktivDato() {
        return aktivDato;
    }

    public void setAktivDato(Date aktivDato) {
        this.aktivDato = aktivDato;
    }

    public Date getFristFerdigstillelse() {
        return fristFerdigstillelse;
    }

    public void setFristFerdigstillelse(Date fristFerdigstillelse) {
        this.fristFerdigstillelse = fristFerdigstillelse;
    }

    public String getPrioritet() {
        return prioritet;
    }

    public void setPrioritet(String prioritet) {
        this.prioritet = prioritet;
    }

    public String getTildeltEnhetsnr() {
        return tildeltEnhetsnr;
    }

    public void setTildeltEnhetsnr(String tildeltEnhetsnr) {
        this.tildeltEnhetsnr = tildeltEnhetsnr;
    }

    public String getTilordnetRessurs() {
        return tilordnetRessurs;
    }

    public void setTilordnetRessurs(String tilordnetRessurs) {
        this.tilordnetRessurs = tilordnetRessurs;
    }

    public Integer getMappeId() {
        return mappeId;
    }

    public void setMappeId(Integer mappeId) {
        this.mappeId = mappeId;
    }

    public OppgaveStatus getStatus() {
        return status;
    }

    public void setStatus(OppgaveStatus status) {
        this.status = status;
    }

    public String getOpprettetAv() {
        return opprettetAv;
    }

    public void setOpprettetAv(String opprettetAv) {
        this.opprettetAv = opprettetAv;
    }

    public Date getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public void setOpprettetTidspunkt(Date opprettetTidspunkt) {
        this.opprettetTidspunkt = opprettetTidspunkt;
    }

    public Integer getVersjon() {
        return versjon;
    }

    public void setVersjon(Integer versjon) {
        this.versjon = versjon;
    }

    public String getEndretAv() {
        return endretAv;
    }

    public void setEndretAv(String endretAv) {
        this.endretAv = endretAv;
    }

    public Date getEndretTidspunkt() {
        return endretTidspunkt;
    }

    public void setEndretTidspunkt(Date endretTidspunkt) {
        this.endretTidspunkt = endretTidspunkt;
    }

    public Date getFerdigstiltTidspunkt() {
        return ferdigstiltTidspunkt;
    }

    public void setFerdigstiltTidspunkt(Date ferdigstiltTidspunkt) {
        this.ferdigstiltTidspunkt = ferdigstiltTidspunkt;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OppgaveDTO oppgaveDTO = (OppgaveDTO) o;

        return new EqualsBuilder()
                .append(id, oppgaveDTO.id)
                .append(tema, oppgaveDTO.tema)
                .append(aktoerId, oppgaveDTO.aktoerId)
                .append(orgnr, oppgaveDTO.orgnr)
                .append(bnr, oppgaveDTO.bnr)
                .append(samhandlernr, oppgaveDTO.samhandlernr)
                .append(journalpostId, oppgaveDTO.journalpostId)
                .append(saksreferanse, oppgaveDTO.saksreferanse)
                .append(temagruppe, oppgaveDTO.temagruppe)
                .append(behandlingstema, oppgaveDTO.behandlingstema)
                .append(oppgavetype, oppgaveDTO.oppgavetype)
                .append(behandlingstype, oppgaveDTO.behandlingstype)
                .append(aktivDato, oppgaveDTO.aktivDato)
                .append(fristFerdigstillelse, oppgaveDTO.fristFerdigstillelse)
                .append(prioritet, oppgaveDTO.prioritet)
                .append(tildeltEnhetsnr, oppgaveDTO.tildeltEnhetsnr)
                .append(opprettetAvEnhetsnr, oppgaveDTO.opprettetAvEnhetsnr)
                .append(endretAvEnhetsnr, oppgaveDTO.endretAvEnhetsnr)
                .append(tilordnetRessurs, oppgaveDTO.tilordnetRessurs)
                .append(mappeId, oppgaveDTO.mappeId)
                .append(status, oppgaveDTO.status)
                .append(versjon, oppgaveDTO.versjon)
                .append(opprettetAv, oppgaveDTO.opprettetAv)
                .append(endretAv, oppgaveDTO.endretAv)
                .append(opprettetTidspunkt, oppgaveDTO.opprettetTidspunkt)
                .append(endretTidspunkt, oppgaveDTO.endretTidspunkt)
                .append(beskrivelse, oppgaveDTO.getBeskrivelse())
                .append(journalpostkilde, oppgaveDTO.getJournalpostkilde())
                .append(behandlesAvApplikasjon, oppgaveDTO.getBehandlesAvApplikasjon())
                .append(metadata, oppgaveDTO.metadata)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("tema", tema)
                .append("aktoerId", aktoerId)
                .append("orgnr", orgnr)
                .append("bnr", bnr)
                .append("samhandlernr", samhandlernr)
                .append("journalpostId", journalpostId)
                .append("saksreferanse", saksreferanse)
                .append("temagruppe", temagruppe)
                .append("behandlingstema", behandlingstema)
                .append("oppgavetype", oppgavetype)
                .append("behandlingstype", behandlingstype)
                .append("aktivDato", aktivDato)
                .append("fristFerdigstillelse", fristFerdigstillelse)
                .append("prioritet", prioritet)
                .append("tildeltEnhetsnr", tildeltEnhetsnr)
                .append("opprettetAvEnhetsnr", opprettetAvEnhetsnr)
                .append("endretAvEnhetsnr", endretAvEnhetsnr)
                .append("tilordnetRessurs", tilordnetRessurs)
                .append("mappeId", mappeId)
                .append("status", status)
                .append("versjon", versjon)
                .append("opprettetAv", opprettetAv)
                .append("endretAv", endretAv)
                .append("opprettetTidspunkt", opprettetTidspunkt)
                .append("endretTidspunkt", endretTidspunkt)
                .append("beskrivelse", "******")
                .append("journalpostkilde", journalpostkilde)
                .append("behandlesAvApplikasjon", behandlesAvApplikasjon)
                .append("metadata", metadata)
                .toString();
    }

}
