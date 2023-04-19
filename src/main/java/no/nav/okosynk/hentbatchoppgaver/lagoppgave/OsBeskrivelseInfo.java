package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.max;
import static java.util.Collections.min;
import static no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding.FELTSEPARATOR;
import static no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding.formatAsNorwegianDate;

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

    public OsBeskrivelseInfo(OsMelding osMelding) {
        nyesteVentestatus = osMelding.nyesteVentestatus;
        hentNettoBelopSomStreng = osMelding.hentNettoBelopSomStreng();
        beregningsId = osMelding.beregningsId;
        beregningsDato = osMelding.beregningsDato;
        forsteFomIPeriode = osMelding.forsteFomIPeriode;
        sisteTomIPeriode = osMelding.sisteTomIPeriode;
        flaggFeilkonto = osMelding.flaggFeilkonto;
        datoForStatus = osMelding.datoForStatus;
        etteroppgjor = Optional.ofNullable(osMelding.etteroppgjor).orElse("");
        utbetalesTilId = osMelding.utbetalesTilId;
        brukerId = osMelding.brukerId;
    }

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
                min(List.of(this.forsteFomIPeriode, osBeskrivelseInfo.forsteFomIPeriode), Comparator.naturalOrder()),
                max(List.of(this.sisteTomIPeriode, osBeskrivelseInfo.sisteTomIPeriode), Comparator.naturalOrder()),
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
