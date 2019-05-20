package no.nav.okosynk.domain.os;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import no.nav.okosynk.domain.AbstractMeldingBatchInputRecordBuilder;

public class OsMeldingBatchInputRecordBuilder
    extends AbstractMeldingBatchInputRecordBuilder<OsMeldingBatchInputRecordBuilder, OsMelding> {

    public enum SUB_FIELD_DEF {
        BEREGNINGS_ID(OsMeldingFormat.BEREGNINGS_ID_START, OsMeldingFormat.BEREGNINGS_ID_SLUTT),
        BEREGNINGS_DATO(OsMeldingFormat.BEREGNINGS_DATO_START, OsMeldingFormat.BEREGNINGS_DATO_SLUTT),
        FAGGRUPPE(OsMeldingFormat.FAGGRUPPE_START, OsMeldingFormat.FAGGRUPPE_SLUTT),
        FORSTE_FOM_I_PERIODE(OsMeldingFormat.FORSTE_FOM_I_PERIODE_START, OsMeldingFormat.FORSTE_FOM_I_PERIODE_SLUTT),
        SISTE_TOM_I_PERIODE(OsMeldingFormat.SISTE_TOM_I_PERIODE_START, OsMeldingFormat.SISTE_TOM_I_PERIODE_SLUTT),
        FLAGG_FEIL_KONTO(OsMeldingFormat.FLAGG_FEILKONTO_START, OsMeldingFormat.FLAGG_FEILKONTO_SLUTT),
        UTBETALES_TIL_ID(OsMeldingFormat.UTBETALES_TIL_ID_START, OsMeldingFormat.UTBETALES_TIL_ID_SLUTT),
        ETTER_OPPGJOR(OsMeldingFormat.ETTEROPPGJOR_START, OsMeldingFormat.ETTEROPPGJOR_SLUTT)
        ;

        @Getter(AccessLevel.PUBLIC)
        private static final int RECORD_LENGTH =
            Arrays
                .stream(SUB_FIELD_DEF.values())
                .mapToInt(subFieldDef -> subFieldDef.getEndPos())
                .max()
                .getAsInt();
        ;

        @Getter(AccessLevel.PUBLIC)
        private final int startPos;

        @Getter(AccessLevel.PUBLIC)
        private final int endPos;

        private SUB_FIELD_DEF(final int startPos, final int endPos) {
            this.startPos = startPos;
            this.endPos = endPos;
        }
    }

    protected final Map<SUB_FIELD_DEF, String> subFields = new HashMap<>();

    public static OsMeldingBatchInputRecordBuilder newBuilder() {
        return new OsMeldingBatchInputRecordBuilder();
    }

    public OsMeldingBatchInputRecordBuilder withBeregningsId(final String val) {
        subFields.put(SUB_FIELD_DEF.BEREGNINGS_ID, val);
        return this;
    }

    public OsMeldingBatchInputRecordBuilder withBeregningsDato(final String val) {
        subFields.put(SUB_FIELD_DEF.BEREGNINGS_DATO, val);
        return this;
    }

    public OsMeldingBatchInputRecordBuilder withFaggruppe(final String val) {
        subFields.put(SUB_FIELD_DEF.FAGGRUPPE, val);
        return this;
    }

    public OsMeldingBatchInputRecordBuilder withForsteFomIPeriode(final String val) {
        subFields.put(SUB_FIELD_DEF.FORSTE_FOM_I_PERIODE, val);
        return this;
    }

    public OsMeldingBatchInputRecordBuilder withSisteTomIPeriode(final String val) {
        subFields.put(SUB_FIELD_DEF.SISTE_TOM_I_PERIODE, val);
        return this;
    }

    public OsMeldingBatchInputRecordBuilder withFlaggFeilkonto(final String val) {
        subFields.put(SUB_FIELD_DEF.FLAGG_FEIL_KONTO, val);
        return this;
    }

    public OsMeldingBatchInputRecordBuilder withUtbetalesTilId(final String val) {
        subFields.put(SUB_FIELD_DEF.UTBETALES_TIL_ID, val);
        return this;
    }

    public OsMeldingBatchInputRecordBuilder withEtteroppgjor(final String val) {
        subFields.put(SUB_FIELD_DEF.ETTER_OPPGJOR, val);
        return this;
    }

    @Override
    public String build() {

        final int totalRecordLength =
            Math.max(SUPER_FIELD_DEF.getUR_RECORD_LENGTH(), SUB_FIELD_DEF.getRECORD_LENGTH());
        final char[] pad = new char[totalRecordLength];
        Arrays.fill(pad, ' ');
        final StringBuffer stringBuffer = new StringBuffer(totalRecordLength);
        stringBuffer.append(pad);

        Arrays
            .stream(SUPER_FIELD_DEF.values())
            .forEach(
                superFieldDef
                ->
                {
                    final String val = superFields.get(superFieldDef);
                    if (val != null) {
                        final int startPos = superFieldDef.getStartPosInOs();
                        final int endPos =
                            Math.min(startPos + val.length(), superFieldDef.getEndPosInOs());
                        stringBuffer.replace(startPos, endPos, val.substring(0, endPos - startPos));
                    }
                }
            );

        Arrays
            .stream(SUB_FIELD_DEF.values())
            .forEach(
                subFieldDef
                ->
                {
                    final String val = subFields.get(subFieldDef);
                    if (val != null) {
                        final int startPos = subFieldDef.getStartPos();
                        final int endPos =
                            Math.min(startPos + val.length(), subFieldDef.getEndPos());
                        stringBuffer.replace(startPos, endPos, val.substring(0, endPos - startPos));
                    }
                }
            );

        return stringBuffer.toString();
    }
}
