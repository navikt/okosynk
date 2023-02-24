package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMeldingBatchInputRecordBuilder<
        MELDINGSBUILDERTYPE extends AbstractMeldingBatchInputRecordBuilder<MELDINGSBUILDERTYPE, MELDINGSTYPE>,
        MELDINGSTYPE        extends AbstractMelding
    > {

    public enum SUPER_FIELD_DEF {
        GJELDER_ID(OsMeldingFormat.GJELDER_ID_START, OsMeldingFormat.GJELDER_ID_SLUTT, UrMeldingFormat.GJELDER_ID_KOLONNE_START, UrMeldingFormat.GJELDER_ID_KOLONNE_SLUTT),
        BEHANDLENDE_ENHET(OsMeldingFormat.BEHANDLENDE_ENHET_START, OsMeldingFormat.BEHANDLENDE_ENHET_SLUTT, UrMeldingFormat.BEHANDLENDE_ENHET_KOLONNE_START, UrMeldingFormat.BEHANDLENDE_ENHET_KOLONNE_SLUTT),
        DATO_FOR_STATUS(OsMeldingFormat.DATO_FOR_STATUS_START, OsMeldingFormat.DATO_FOR_STATUS_SLUTT, UrMeldingFormat.DATO_FOR_STATUS_KOLONNE_START, UrMeldingFormat.DATO_FOR_STATUS_KOLONNE_SLUTT),
        NYESTE_VENTESTATUS(OsMeldingFormat.NYESTE_VENTESTATUS_START, OsMeldingFormat.NYESTE_VENTESTATUS_SLUTT, UrMeldingFormat.NYESTE_VENTESTATUS_KOLONNE_START, UrMeldingFormat.NYESTE_VENTESTATUS_KOLONNE_SLUTT),
        BRUKER_ID(OsMeldingFormat.BRUKER_ID_START, OsMeldingFormat.BRUKER_ID_SLUTT, UrMeldingFormat.BRUKER_ID_KOLONNE_START, UrMeldingFormat.BRUKER_ID_KOLONNE_SLUTT),
        TOTALT_NETTO_BELOP(OsMeldingFormat.TOTALT_NETTO_BELOP_START, OsMeldingFormat.TOTALT_NETTO_BELOP_SLUTT, UrMeldingFormat.TOTALT_NETTO_BELOP_KOLONNE_START, UrMeldingFormat.TOTALT_NETTO_BELOP_KOLONNE_SLUTT),
        ;

        public int getStartPosInOs() {
            return startPosInOs;
        }

        public int getEndPosInOs() {
            return endPosInOs;
        }

        public int getStartPosInUr() {
            return startPosInUr;
        }

        public int getEndPosInUr() {
            return endPosInUr;
        }

        public static int getOsRecordLength() {
            return OS_RECORD_LENGTH;
        }

        public static int getUrRecordLength() {
            return UR_RECORD_LENGTH;
        }

        private final int startPosInOs;
        private final int endPosInOs;
        private final int startPosInUr;
        private final int endPosInUr;

        private static final int OS_RECORD_LENGTH =
            Arrays
                .stream(SUPER_FIELD_DEF.values())
                .mapToInt(superFieldDef -> superFieldDef.getEndPosInOs())
                .max()
                .getAsInt();

        private static final int UR_RECORD_LENGTH =
            Arrays
                .stream(SUPER_FIELD_DEF.values())
                .mapToInt(superFieldDef -> superFieldDef.getEndPosInUr())
                .max()
                .getAsInt();

        private SUPER_FIELD_DEF(
            final int startPosInOs,
            final int endPosInOs,
            final int startPosInUr,
            final int endPosInUr) {

            this.startPosInOs = startPosInOs;
            this.endPosInOs = endPosInOs;
            this.startPosInUr = startPosInUr;
            this.endPosInUr = endPosInUr;
        }
    }

    protected final Map<SUPER_FIELD_DEF, String> superFields = new HashMap<>();

    protected abstract String build();

    public MELDINGSBUILDERTYPE withGjelderId(final String val) {
        superFields.put(SUPER_FIELD_DEF.GJELDER_ID, val);
        return (MELDINGSBUILDERTYPE)this;
    }

    public MELDINGSBUILDERTYPE withBehandlendeEnhet(final String val) {
        superFields.put(SUPER_FIELD_DEF.BEHANDLENDE_ENHET, val);
        return (MELDINGSBUILDERTYPE)this;
    }

    public MELDINGSBUILDERTYPE withDatoForStatus(final String val) {
        superFields.put(SUPER_FIELD_DEF.DATO_FOR_STATUS, val);
        return (MELDINGSBUILDERTYPE)this;
    }

    public MELDINGSBUILDERTYPE withNyesteVentestatus(final String val) {
        superFields.put(SUPER_FIELD_DEF.NYESTE_VENTESTATUS, val);
        return (MELDINGSBUILDERTYPE)this;
    }

    public MELDINGSBUILDERTYPE withBrukerId(final String val) {
        superFields.put(SUPER_FIELD_DEF.BRUKER_ID, val);
        return (MELDINGSBUILDERTYPE)this;
    }

    public MELDINGSBUILDERTYPE withTotaltNettoBelop(final String val) {
        superFields.put(SUPER_FIELD_DEF.TOTALT_NETTO_BELOP, val);
        return (MELDINGSBUILDERTYPE)this;
    }
}
