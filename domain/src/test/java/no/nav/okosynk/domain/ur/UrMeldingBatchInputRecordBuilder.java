package no.nav.okosynk.domain.ur;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import no.nav.okosynk.domain.AbstractMeldingBatchInputRecordBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrMeldingBatchInputRecordBuilder
    extends AbstractMeldingBatchInputRecordBuilder<UrMeldingBatchInputRecordBuilder, UrMelding> {

    private enum SUB_FIELD_DEF {
        GJELDER_ID_TYPE(UrMeldingFormat.GJELDER_ID_TYPE_KOLONNE_START, UrMeldingFormat.GJELDER_ID_TYPE_KOLONNE_SLUTT),
        OPPDRAGS_KODE(UrMeldingFormat.OPPDRAGS_KODE_KOLONNE_START, UrMeldingFormat.OPPDRAGS_KODE_KOLONNE_SLUTT),
        DATO_POSTERT(UrMeldingFormat.DATO_POSTERT_KOLONNE_START, UrMeldingFormat.DATO_POSTERT_KOLONNE_SLUTT),
        KILDE(UrMeldingFormat.KILDE_KOLONNE_START, UrMeldingFormat.KILDE_KOLONNE_SLUTT),
        BILAGS_ID(UrMeldingFormat.BILAGS_ID_KOLONNE_START, UrMeldingFormat.BILAGS_ID_KOLONNE_SLUTT),
        ARSAKS_TEKST(UrMeldingFormat.ARSAKS_TEKST_KOLONNE_START, UrMeldingFormat.ARSAKS_TEKST_KOLONNE_SLUTT),
        MOTTAKER_ID(UrMeldingFormat.MOTTAKER_ID_KOLONNE_START, UrMeldingFormat.MOTTAKER_ID_KOLONNE_SLUTT)
        ;

        public static int getRecordLength() {
            return RECORD_LENGTH;
        }

        private static final int RECORD_LENGTH =
            Arrays
                .stream(SUB_FIELD_DEF.values())
                .mapToInt(subFieldDef -> subFieldDef.getEndPos())
                .max()
                .getAsInt();
            ;

        private final int startPos;

        public int getStartPos() {
            return startPos;
        }

        public int getEndPos() {
            return endPos;
        }

        private final int endPos;

        private SUB_FIELD_DEF(final int startPos, final int endPos) {
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
