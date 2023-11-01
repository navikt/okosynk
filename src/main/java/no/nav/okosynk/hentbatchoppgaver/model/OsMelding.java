package no.nav.okosynk.hentbatchoppgaver.model;

import lombok.Getter;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.OsBeskrivelseInfo;
import no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseBehandlendeEnhet;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseBeregningsDato;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseBeregningsId;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseBrukerId;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseDatoForStatus;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseEtteroppgjor;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseFaggruppe;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseForsteFomIPeriode;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseGjelderId;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseNyesteVentestatus;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseSisteTomIPeriode;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseTotaltNettoBelop;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser.parseUtbetalesTilId;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class OsMelding extends Melding {

    static final String ORGANISASJON_PREFIKS = "00";
    private static final String FLAGG_FEILKONTO_DEFAULT = " ";

    @Getter
    final String faggruppe;
    final String beregningsId;
    @Getter
    final LocalDate beregningsDato;
    final LocalDate forsteFomIPeriode;
    final LocalDate sisteTomIPeriode;
    final String flaggFeilkonto;
    final String utbetalesTilId;
    final String etteroppgjor;

    public OsMelding(final String osMelding) {
        super(parseBehandlendeEnhet(osMelding),
                parseBrukerId(osMelding),
                parseDatoForStatus(osMelding),
                parseGjelderId(osMelding),
                parseNyesteVentestatus(osMelding),
                parseTotaltNettoBelop(osMelding));

        this.faggruppe = parseFaggruppe(osMelding);
        this.beregningsId = parseBeregningsId(osMelding);
        this.beregningsDato = parseBeregningsDato(osMelding);
        this.forsteFomIPeriode = parseForsteFomIPeriode(osMelding);
        this.sisteTomIPeriode = parseSisteTomIPeriode(osMelding);
        this.flaggFeilkonto = Optional.of(osMelding).map(OsMeldingParser::parseFlaggFeilkonto)
                .filter(f -> !isEmpty(f)).orElse(FLAGG_FEILKONTO_DEFAULT);
        this.utbetalesTilId = parseUtbetalesTilId(osMelding);
        this.etteroppgjor = parseEtteroppgjor(osMelding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.gjelderId, super.behandlendeEnhet, this.beregningsId, this.beregningsDato, this.faggruppe);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof OsMelding otherAsOsMelding)) {
            return false;
        }

        return this.getClass() == other.getClass()
                && this.gjelderId.equals(otherAsOsMelding.gjelderId)
                && this.behandlendeEnhet.equals(otherAsOsMelding.behandlendeEnhet)
                && this.beregningsId.equals(otherAsOsMelding.beregningsId)
                && this.beregningsDato.equals(otherAsOsMelding.beregningsDato)
                && this.faggruppe.equals(otherAsOsMelding.faggruppe);
    }

    @Override
    public String toString() {
        return String.join(FIELD_SEPARATOR, super.toString(),
                "faggruppe            : " + faggruppe,
                "beregningsId         : " + beregningsId,
                "beregningsDato       : " + beregningsDato,
                "forsteFomIPeriode    : " + forsteFomIPeriode,
                "sisteTomIPeriode     : " + sisteTomIPeriode,
                "flaggFeilkonto       : " + flaggFeilkonto,
                "utbetalesTilId       : " + utbetalesTilId,
                "etteroppgjor         : " + etteroppgjor);
    }

    public OsBeskrivelseInfo osBeskrivelseInfo() {
        return new OsBeskrivelseInfo(
                nyesteVentestatus,
                hentNettoBelopSomStreng(),
                beregningsId,
                beregningsDato,
                forsteFomIPeriode,
                sisteTomIPeriode,
                flaggFeilkonto,
                datoForStatus,
                Optional.ofNullable(etteroppgjor).orElse(""),
                utbetalesTilId,
                brukerId);
    }

    @Override
    public String regelnøkkel(){
        return String.join(",", faggruppe, behandlendeEnhet);
    }

}
