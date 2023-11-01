package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.util.Collections.max;
import static java.util.Collections.min;
import static java.util.Comparator.naturalOrder;
import static java.util.List.of;
import static no.nav.okosynk.hentbatchoppgaver.model.Melding.FELTSEPARATOR;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.Util.formatAsNorwegianDate;

public class OsBeskrivelseInfo {

    private final String nyesteVentestatus;
    private final String hentNettoBelopSomStreng;
    private final String beregningsId;
    private final LocalDate beregningsDato;
    private final LocalDate forsteFomIPeriode;
    private final LocalDate sisteTomIPeriode;
    private final String flaggFeilkonto;
    private final LocalDate datoForStatus;
    private final String etteroppgjor;
    private final String utbetalesTilId;
    private final String brukerId;

    public OsBeskrivelseInfo(String nyesteVentestatus, String hentNettoBelopSomStreng, String beregningsId, LocalDate beregningsDato, LocalDate forsteFomIPeriode, LocalDate sisteTomIPeriode, String flaggFeilkonto, LocalDate datoForStatus, String etteroppgjor, String utbetalesTilId, String brukerId) {
        this.nyesteVentestatus = nyesteVentestatus;
        this.hentNettoBelopSomStreng = hentNettoBelopSomStreng;
        this.beregningsId = beregningsId;
        this.beregningsDato = beregningsDato;
        this.forsteFomIPeriode = forsteFomIPeriode;
        this.sisteTomIPeriode = sisteTomIPeriode;
        this.flaggFeilkonto = flaggFeilkonto;
        this.datoForStatus = datoForStatus;
        this.etteroppgjor = etteroppgjor;
        this.utbetalesTilId = utbetalesTilId;
        this.brukerId = brukerId;
    }

    public OsBeskrivelseInfo pluss(OsBeskrivelseInfo osBeskrivelseInfo) {
        return new OsBeskrivelseInfo(
                this.nyesteVentestatus,
                new BigDecimal(this.hentNettoBelopSomStreng)
                        .add(new BigDecimal(osBeskrivelseInfo.hentNettoBelopSomStreng)).toString(),
                this.beregningsId,
                this.beregningsDato,
                min(of(this.forsteFomIPeriode, osBeskrivelseInfo.forsteFomIPeriode), naturalOrder()),
                max(of(this.sisteTomIPeriode, osBeskrivelseInfo.sisteTomIPeriode), naturalOrder()),
                this.flaggFeilkonto,
                this.datoForStatus,
                this.etteroppgjor,
                this.utbetalesTilId,
                this.brukerId
        );
    }

    public String lagBeskrivelse() {
        return String.join(FELTSEPARATOR,
                nyesteVentestatus,
                hentNettoBelopSomStreng + "kr",
                "beregningsdato/id:" + formatAsNorwegianDate(beregningsDato) + "/" + beregningsId,
                "periode:" + formatAsNorwegianDate(forsteFomIPeriode) + "-" + formatAsNorwegianDate(sisteTomIPeriode),
                "feilkonto:" + flaggFeilkonto,
                "statusdato:" + formatAsNorwegianDate(datoForStatus),
                etteroppgjor == null ? "" : etteroppgjor,
                "UtbTil:" + utbetalesTilId,
                brukerId
        ).trim();
    }
}
