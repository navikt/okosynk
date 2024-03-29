package no.nav.okosynk.hentbatchoppgaver.parselinje;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;

public class UrMeldingBatchInputRecordBuilder
    extends AbstractMeldingBatchInputRecordBuilder<UrMeldingBatchInputRecordBuilder, UrMelding> {

    private enum SUB_FIELD_DEF {
        GJELDER_ID_TYPE(UrMeldingFormat.GJELDER_ID_TYPE_START, UrMeldingFormat.GJELDER_ID_TYPE_SLUTT),
        OPPDRAGS_KODE(UrMeldingFormat.OPPDRAGS_KODE_START, UrMeldingFormat.OPPDRAGS_KODE_SLUTT),
        DATO_POSTERT(UrMeldingFormat.DATO_POSTERT_START, UrMeldingFormat.DATO_POSTERT_SLUTT),
        KILDE(UrMeldingFormat.KILDE_START, UrMeldingFormat.KILDE_SLUTT),
        BILAGS_ID(UrMeldingFormat.BILAGS_ID_START, UrMeldingFormat.BILAGS_ID_SLUTT),
        ARSAKS_TEKST(UrMeldingFormat.ARSAKS_TEKST_START, UrMeldingFormat.ARSAKS_TEKST_SLUTT),
        MOTTAKER_ID(UrMeldingFormat.MOTTAKER_ID_START, UrMeldingFormat.MOTTAKER_ID_SLUTT)
        ;

        public static int getRecordLength() {
            return RECORD_LENGTH;
        }

        private static final int RECORD_LENGTH =
            Arrays
                .stream(SUB_FIELD_DEF.values())
                .mapToInt(SUB_FIELD_DEF::getEndPos)
                .max()
                .getAsInt();

        private final int startPos;

        public int getStartPos() {
            return startPos;
        }

        public int getEndPos() {
            return endPos;
        }

        private final int endPos;

        SUB_FIELD_DEF(final int startPos, final int endPos) {
            this.startPos = startPos;
            this.endPos = endPos;
        }
    }

    protected final Map<SUB_FIELD_DEF, String> subFields = new HashMap<>();

    public static UrMeldingBatchInputRecordBuilder newBuilder() {
        return new UrMeldingBatchInputRecordBuilder();
    }

    public UrMeldingBatchInputRecordBuilder withGjelderIdType(final String val) {
        subFields.put(SUB_FIELD_DEF.GJELDER_ID_TYPE, val);
        return this;
    }

    public UrMeldingBatchInputRecordBuilder withOppdragsKode(final String val) {
        subFields.put(SUB_FIELD_DEF.OPPDRAGS_KODE, val);
        return this;
    }

    public UrMeldingBatchInputRecordBuilder withDatoPostert(final String val) {
        subFields.put(SUB_FIELD_DEF.DATO_POSTERT, val);
        return this;
    }

    public UrMeldingBatchInputRecordBuilder withKilde(final String val) {
        subFields.put(SUB_FIELD_DEF.KILDE, val);
        return this;
    }

    public UrMeldingBatchInputRecordBuilder withBilagsId(final String val) {
        subFields.put(SUB_FIELD_DEF.BILAGS_ID, val);
        return this;
    }

    public UrMeldingBatchInputRecordBuilder withArsaksTekst(final String val) {
        subFields.put(SUB_FIELD_DEF.ARSAKS_TEKST, val);
        return this;
    }

    public UrMeldingBatchInputRecordBuilder withMottakerId(final String val) {
        subFields.put(SUB_FIELD_DEF.MOTTAKER_ID, val);
        return this;
    }

    @Override
    public String build() {

        final int totalRecordLength =
            Math.max(SUPER_FIELD_DEF.getUrRecordLength(), SUB_FIELD_DEF.getRecordLength());
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
                        final int startPos = superFieldDef.getStartPosInUr();
                        final int endPos =
                            Math.min(startPos + val.length(), superFieldDef.getEndPosInUr());
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
