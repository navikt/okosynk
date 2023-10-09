package no.nav.okosynk.hentbatchoppgaver.model;

import no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingParser;
import no.nav.okosynk.model.GjelderIdType;

import java.time.LocalDate;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class OsMelding extends AbstractMelding {

    static final String ORGANISASJON_PREFIKS = "00";
    private static final String FLAGG_FEILKONTO_DEFAULT = " ";

    public final String faggruppe;
    public final String beregningsId;
    public final LocalDate beregningsDato;
    public final LocalDate forsteFomIPeriode;
    public final LocalDate sisteTomIPeriode;
    public final String flaggFeilkonto;
    public final String utbetalesTilId;
    public final String etteroppgjor;

    public OsMelding(final String osMelding) {

        super(osMelding, new OsMeldingParser());

        final OsMeldingParser parser = (OsMeldingParser)getParser();

        this.faggruppe = parser.parseFaggruppe(osMelding);
        this.beregningsId = parser.parseBeregningsId(osMelding);
        this.beregningsDato = parser.parseBeregningsDato(osMelding);
        this.forsteFomIPeriode = parser.parseForsteFomIPeriode(osMelding);
        this.sisteTomIPeriode = parser.parseSisteTomIPeriode(osMelding);
        final String flaggFeilkontoParsed = parser.parseFlaggFeilkonto(osMelding);
        this.flaggFeilkonto = isEmpty(flaggFeilkontoParsed) ? FLAGG_FEILKONTO_DEFAULT : flaggFeilkontoParsed;
        this.utbetalesTilId = parser.parseUtbetalesTilId(osMelding);
        this.etteroppgjor = parser.parseEtteroppgjor(osMelding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), super.behandlendeEnhet, this.beregningsId, this.beregningsDato, this.faggruppe, GjelderIdType.fra(this.gjelderId));
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (!super.equals(other)) {
            return false;
        }

        if (!(other instanceof OsMelding otherAsOsMelding)) {
            return false;
        }

        final AbstractMelding otherAsAbstractMelding = (AbstractMelding)other;

        return
            super.behandlendeEnhet.equals(otherAsAbstractMelding.behandlendeEnhet)
            &&
            this.beregningsId.equals(otherAsOsMelding.beregningsId)
            &&
            this.beregningsDato.equals(otherAsOsMelding.beregningsDato)
            &&
            this.faggruppe.equals(otherAsOsMelding.faggruppe)
            &&
            GjelderIdType.fra(this.gjelderId) == GjelderIdType.fra(otherAsAbstractMelding.gjelderId);
    }

    @Override
    public String toString() {

        return super.toString() + FIELD_SEPARATOR +
           "faggruppe            : " + faggruppe + FIELD_SEPARATOR +
           "beregningsId         : " + beregningsId + FIELD_SEPARATOR +
           "beregningsDato       : " + beregningsDato + FIELD_SEPARATOR +
           "forsteFomIPeriode    : " + forsteFomIPeriode + FIELD_SEPARATOR +
           "sisteTomIPeriode     : " + sisteTomIPeriode + FIELD_SEPARATOR +
           "flaggFeilkonto       : " + flaggFeilkonto + FIELD_SEPARATOR +
           "utbetalesTilId       : " + utbetalesTilId + FIELD_SEPARATOR +
           "etteroppgjor         : " + etteroppgjor;
    }
}
