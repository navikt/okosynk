package no.nav.okosynk.hentbatchoppgaver.parselinje;

public class UrMeldingFormat {
    private UrMeldingFormat() {
    }

    public static final int GJELDER_ID_START = 0;
    public static final int GJELDER_ID_SLUTT = 11;

    public static final int GJELDER_ID_TYPE_START = 11;
    public static final int GJELDER_ID_TYPE_SLUTT = 23;

    public static final int DATO_FOR_STATUS_START = 23;
    public static final int DATO_FOR_STATUS_SLUTT = 42;

    public static final int NYESTE_VENTESTATUS_START = 42;
    public static final int NYESTE_VENTESTATUS_SLUTT = 44;

    public static final int BRUKER_ID_START = 44;
    public static final int BRUKER_ID_SLUTT = 54;

    public static final int TOTALT_NETTO_BELOP_START = 54;
    public static final int TOTALT_NETTO_BELOP_SLUTT = 69;

    public static final int BEHANDLENDE_ENHET_START = 69;
    public static final int BEHANDLENDE_ENHET_SLUTT = 73;

    public static final int OPPDRAGS_KODE_START = 73;
    public static final int OPPDRAGS_KODE_SLUTT = 80;

    public static final int KILDE_START = 80;
    public static final int KILDE_SLUTT = 85;

    public static final int DATO_POSTERT_START = 85;
    public static final int DATO_POSTERT_SLUTT = 95;

    public static final int BILAGS_ID_START = 95;
    public static final int BILAGS_ID_SLUTT = 104;

    public static final int ARSAKS_TEKST_START = 104;
    public static final int ARSAKS_TEKST_SLUTT = 154;

    public static final int MOTTAKER_ID_START = 154;
    public static final int MOTTAKER_ID_SLUTT = 165;

}
