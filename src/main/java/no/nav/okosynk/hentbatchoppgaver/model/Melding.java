package no.nav.okosynk.hentbatchoppgaver.model;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.BeskrivelseInfo;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Melding {
    protected static final String FIELD_SEPARATOR = System.lineSeparator();
    public static final String FORSTE_FELTSEPARATOR = ";;   ";
    public static final String FELTSEPARATOR = ";   ";
    final String behandlendeEnhet;
    final String brukerId; //Dette feltet er ikke det samme som Oppgave sin "brukerId"
    final LocalDate datoForStatus;
    final String gjelderId;
    final String nyesteVentestatus;
    final double totaltNettoBelop;

    protected Melding(String behandlendeEnhet,
                      String brukerId,
                      LocalDate datoForStatus,
                      String gjelderId,
                      String nyesteVentestatus,
                      double totaltNettoBelop) {
        this.behandlendeEnhet = behandlendeEnhet;
        this.brukerId = brukerId;
        this.datoForStatus = datoForStatus;
        this.gjelderId = gjelderId;
        this.nyesteVentestatus = nyesteVentestatus;
        this.totaltNettoBelop = totaltNettoBelop;
    }

    public String hentNettoBelopSomStreng() {
        final BigDecimal bd = BigDecimal.valueOf(this.totaltNettoBelop);
        return bd.toBigInteger().toString();
    }

    public abstract String ruleKey();

    public abstract String faggruppeEllerOppdragskode();

    public abstract LocalDate sammenligningsDato();

    public abstract BeskrivelseInfo beskrivelseInfo();
    public abstract Constants.BATCH_TYPE batchType();

    public String getGjelderId() {
        return gjelderId;
    }

    public String getNyesteVentestatus() {
        return nyesteVentestatus;
    }
}
