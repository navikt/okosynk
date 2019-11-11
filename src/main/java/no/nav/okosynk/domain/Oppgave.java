package no.nav.okosynk.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Oppgave {

  private static final String LOG_FIELD_SEPARATOR = ", ";
  private static final String LOG_PARAGRAPH_SEPARATOR = ". - ";

  public final String oppgaveId;
  public final String aktoerId;
  public final String samhandlernr;
  public final String orgnr;
  public final String bnr;
  public final String oppgavetypeKode;
  public final String fagomradeKode;
  public final String behandlingstema;
  public final String behandlingstype;
  public final String prioritetKode;
  public final String beskrivelse;
  public final LocalDate aktivFra;
  public final LocalDate aktivTil;
  public final String ansvarligEnhetId;
  public final boolean lest;
  public final int versjon;
  public final LocalDateTime sistEndret;
  public final int antallMeldinger;
  public final String mappeId;
  public final String ansvarligSaksbehandlerIdent;

  public Oppgave(OppgaveBuilder oppgaveBuilder) {
    this.oppgaveId = oppgaveBuilder.oppgaveId;
    this.aktoerId = oppgaveBuilder.aktoerId;
    this.samhandlernr = oppgaveBuilder.samhandlernr;
    this.orgnr = oppgaveBuilder.orgnr;
    this.bnr = oppgaveBuilder.bnr;
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
  }

  public static class OppgaveBuilder {

    private String oppgaveId;
    private String aktoerId;
    private String samhandlernr;
    private String orgnr;
    private String bnr;
    private String oppgavetypeKode;
    private String fagomradeKode;
    private String behandlingstema;
    private String behandlingstype;
    private String prioritetKode;
    private String beskrivelse;
    private LocalDate aktivFra;
    private LocalDate aktivTil;
    private boolean lest;
    private String ansvarligEnhetId;
    private int versjon;
    private LocalDateTime sistEndret;
    private int antallMeldinger;
    private String mappeId;
    private String saksbehandlerIdent;

    public OppgaveBuilder withOppgaveId(String oppgaveId) {
      this.oppgaveId = oppgaveId;
      return this;
    }

    public OppgaveBuilder withAktoerId(String aktoerId) {
      this.aktoerId = aktoerId;
      return this;
    }

    public OppgaveBuilder withSamhandlernr(String samhandlernr) {
      this.samhandlernr = samhandlernr;
      return this;
    }

    public OppgaveBuilder withOrgnr(String orgnr) {
      this.orgnr = orgnr;
      return this;
    }

    public OppgaveBuilder withBnr(String bnr) {
      this.bnr = bnr;
      return this;
    }

    public OppgaveBuilder withOppgavetypeKode(String oppgavetypeKode) {
      this.oppgavetypeKode = oppgavetypeKode;
      return this;
    }

    public OppgaveBuilder withFagomradeKode(String fagomradeKode) {
      this.fagomradeKode = fagomradeKode;
      return this;
    }

    public OppgaveBuilder withBehandlingstema(String behandlingstema) {
      this.behandlingstema = behandlingstema;
      return this;
    }

    public OppgaveBuilder withBehandlingstype(String behandlingstype) {
      this.behandlingstype = behandlingstype;
      return this;
    }

    public OppgaveBuilder withPrioritetKode(String prioritetKode) {
      this.prioritetKode = prioritetKode;
      return this;
    }

    public OppgaveBuilder withBeskrivelse(String beskrivelse) {
      this.beskrivelse = beskrivelse;
      return this;
    }

    public OppgaveBuilder withAktivFra(LocalDate aktivFra) {
      this.aktivFra = aktivFra;
      return this;
    }

    public OppgaveBuilder withAktivTil(LocalDate aktivTil) {
      this.aktivTil = aktivTil;
      return this;
    }

    public OppgaveBuilder withAnsvarligEnhetId(String ansvarligEnhetId) {
      this.ansvarligEnhetId = ansvarligEnhetId;
      return this;
    }

    public OppgaveBuilder withLest(boolean lest) {
      this.lest = lest;
      return this;
    }

    public OppgaveBuilder withVersjon(int versjon) {
      this.versjon = versjon;
      return this;
    }

    public OppgaveBuilder withSistEndret(LocalDateTime sistEndret) {
      this.sistEndret = sistEndret;
      return this;
    }

    public OppgaveBuilder withAntallMeldinger(int antallMeldinger) {
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

    public OppgaveBuilder withSameValuesAs(final Oppgave oppgave) {
      this.oppgaveId = oppgave.oppgaveId;
      this.aktoerId = oppgave.aktoerId;
      this.samhandlernr = oppgave.samhandlernr;
      this.orgnr = oppgave.orgnr;
      this.bnr = oppgave.bnr;
      this.oppgavetypeKode = oppgave.oppgavetypeKode;
      this.fagomradeKode = oppgave.fagomradeKode;
      this.behandlingstema = oppgave.behandlingstema;
      this.behandlingstype = oppgave.behandlingstype;
      this.prioritetKode = oppgave.prioritetKode;
      this.beskrivelse = oppgave.beskrivelse;
      this.aktivFra = oppgave.aktivFra;
      this.aktivTil = oppgave.aktivTil;
      this.ansvarligEnhetId = oppgave.ansvarligEnhetId;
      this.lest = oppgave.lest;
      this.mappeId = oppgave.mappeId;
      this.saksbehandlerIdent = oppgave.ansvarligSaksbehandlerIdent;

      this.versjon = oppgave.versjon;
      this.sistEndret = oppgave.sistEndret;
      return this;
    }

    public Oppgave build() {

      String warnStr = "";

//            if (brukerId == null) {
//                warnStr += "An oppgave is about to be built with brukerId null" + LOG_PARAGRAPH_SEPARATOR;
//            }
//            if (behandlingstema == null && behandlingstype == null) {
//                warnStr += "An oppgave is about to be built with underkategoriKode null" + LOG_PARAGRAPH_SEPARATOR;
//            }
//            if (ansvarligEnhetId == null) {
//                warnStr += "An oppgave is about to be built with ansvarligEnhetId null" + LOG_PARAGRAPH_SEPARATOR;
//            }

      final Oppgave oppgave = new Oppgave(this);

      if (!warnStr.isEmpty()) {
        warnStr += "Oppgave: " + LOG_FIELD_SEPARATOR + oppgave.toString();
        logger.warn(warnStr);
      }

      return oppgave;
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(Oppgave.class);

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Oppgave oppgave = (Oppgave) o;
    return Objects.equals(aktoerId, oppgave.aktoerId)
        &&
        Objects.equals(samhandlernr, oppgave.samhandlernr)
        &&
        Objects.equals(orgnr, oppgave.orgnr)
        &&
        Objects.equals(bnr, oppgave.bnr)
        &&
        Objects.equals(behandlingstema, oppgave.behandlingstema)
        &&
        Objects.equals(behandlingstype, oppgave.behandlingstype)
        &&
        Objects.equals(ansvarligEnhetId, oppgave.ansvarligEnhetId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(aktoerId, samhandlernr, orgnr, bnr, behandlingstema, behandlingstype,
        ansvarligEnhetId);
  }

  @Override
  public String toString() {

    final StringBuffer strBuff =
        new StringBuffer()
            .append("oppgaveId                  : ").append(oppgaveId).append(LOG_FIELD_SEPARATOR)
            .append("aktoerId                   : ").append(aktoerId).append(LOG_FIELD_SEPARATOR)
            .append("samhandlernr               : ").append(samhandlernr)
            .append(LOG_FIELD_SEPARATOR)
            .append("orgnr                      : ").append(orgnr).append(LOG_FIELD_SEPARATOR)
            .append("bnr                        : ").append(bnr).append(LOG_FIELD_SEPARATOR)
            .append("oppgavetypeKode            : ").append(oppgavetypeKode)
            .append(LOG_FIELD_SEPARATOR)
            .append("fagomradeKode              : ").append(fagomradeKode)
            .append(LOG_FIELD_SEPARATOR)
            .append("behandlingstema            : ").append(behandlingstema)
            .append(LOG_FIELD_SEPARATOR)
            .append("behandlingstype            : ").append(behandlingstype)
            .append(LOG_FIELD_SEPARATOR)
            .append("prioritetKode              : ").append(prioritetKode)
            .append(LOG_FIELD_SEPARATOR)
            .append("aktivFra                   : ").append(aktivFra).append(LOG_FIELD_SEPARATOR)
            .append("aktivTil                   : ").append(aktivTil).append(LOG_FIELD_SEPARATOR)
            .append("ansvarligEnhetId           : ").append(ansvarligEnhetId)
            .append(LOG_FIELD_SEPARATOR)
            .append("lest                       : ").append(lest).append(LOG_FIELD_SEPARATOR)
            .append("versjon                    : ").append(versjon).append(LOG_FIELD_SEPARATOR)
            .append("sistEndret                 : ").append(sistEndret).append(LOG_FIELD_SEPARATOR)
            .append("antallMeldinger            : ").append(antallMeldinger)
            .append(LOG_FIELD_SEPARATOR)
            .append("mappeId                    : ").append(mappeId).append(LOG_FIELD_SEPARATOR)
            .append("ansvarligSaksbehandlerIdent: ").append(ansvarligSaksbehandlerIdent);

    final String str = strBuff.toString();

    return str;
  }
}
