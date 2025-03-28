package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.model.Melding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMeldingBatchInputRecordBuilder<
        T extends AbstractMeldingBatchInputRecordBuilder<T, U>,
        U extends Melding
    > {

    public enum SUPER_FIELD_DEF {
        GJELDER_ID(OsMeldingFormat.GJELDER_ID_START, OsMeldingFormat.GJELDER_ID_SLUTT, UrMeldingFormat.GJELDER_ID_START, UrMeldingFormat.GJELDER_ID_SLUTT),
        BEHANDLENDE_ENHET(OsMeldingFormat.BEHANDLENDE_ENHET_START, OsMeldingFormat.BEHANDLENDE_ENHET_SLUTT, UrMeldingFormat.BEHANDLENDE_ENHET_START, UrMeldingFormat.BEHANDLENDE_ENHET_SLUTT),
        DATO_FOR_STATUS(OsMeldingFormat.DATO_FOR_STATUS_START, OsMeldingFormat.DATO_FOR_STATUS_SLUTT, UrMeldingFormat.DATO_FOR_STATUS_START, UrMeldingFormat.DATO_FOR_STATUS_SLUTT),
        NYESTE_VENTESTATUS(OsMeldingFormat.NYESTE_VENTESTATUS_START, OsMeldingFormat.NYESTE_VENTESTATUS_SLUTT, UrMeldingFormat.NYESTE_VENTESTATUS_START, UrMeldingFormat.NYESTE_VENTESTATUS_SLUTT),
        BRUKER_ID(OsMeldingFormat.BRUKER_ID_START, OsMeldingFormat.BRUKER_ID_SLUTT, UrMeldingFormat.BRUKER_ID_START, UrMeldingFormat.BRUKER_ID_SLUTT),
        TOTALT_NETTO_BELOP(OsMeldingFormat.TOTALT_NETTO_BELOP_START, OsMeldingFormat.TOTALT_NETTO_BELOP_SLUTT, UrMeldingFormat.TOTALT_NETTO_BELOP_START, UrMeldingFormat.TOTALT_NETTO_BELOP_SLUTT),
        ;

        public static int getUrRecordLength() {
            return UR_RECORD_LENGTH;
        }

        private final int startPosInOs;
        private final int endPosInOs;
        private final int startPosInUr;
        private final int endPosInUr;

        private static final int UR_RECORD_LENGTH =
            Arrays
                .stream(SUPER_FIELD_DEF.values())
                .mapToInt(SUPER_FIELD_DEF::getEndPosInUr)
                .max()
                .orElse(0);

        SUPER_FIELD_DEF(
                final int startPosInOs,
                final int endPosInOs,
                final int startPosInUr,
                final int endPosInUr) {

            this.startPosInOs = startPosInOs;
            this.endPosInOs = endPosInOs;
            this.startPosInUr = startPosInUr;
            this.endPosInUr = endPosInUr;
        }

        public int getStartPosInOs() {
            return startPosInOs;
        }

        public int getStartPosInUr() {
            return startPosInUr;
        }

        public int getEndPosInOs() {
            return endPosInOs;
        }

        public int getEndPosInUr() {
            return endPosInUr;
        }
    }

    protected final Map<SUPER_FIELD_DEF, String> superFields = new HashMap<>();

    protected abstract String build();

    public T withGjelderId(final String val) {
        superFields.put(SUPER_FIELD_DEF.GJELDER_ID, val);
        return (T)this;
    }

    public T withBehandlendeEnhet(final String val) {
        superFields.put(SUPER_FIELD_DEF.BEHANDLENDE_ENHET, val);
        return (T)this;
    }

    public T withDatoForStatus(final String val) {
        superFields.put(SUPER_FIELD_DEF.DATO_FOR_STATUS, val);
        return (T)this;
    }

    public T withNyesteVentestatus(final String val) {
        superFields.put(SUPER_FIELD_DEF.NYESTE_VENTESTATUS, val);
        return (T)this;
    }

    public T withBrukerId(final String val) {
        superFields.put(SUPER_FIELD_DEF.BRUKER_ID, val);
        return (T)this;
    }

    public T withTotaltNettoBelop(final String val) {
        superFields.put(SUPER_FIELD_DEF.TOTALT_NETTO_BELOP, val);
        return (T)this;
    }
}
