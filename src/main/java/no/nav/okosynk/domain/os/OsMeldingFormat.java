package no.nav.okosynk.domain.os;

public class OsMeldingFormat {

    public static final int GJELDER_ID_START = 0;
    public static final int GJELDER_ID_SLUTT = 11;

    public static final int BEREGNINGS_ID_START = 11;
    public static final int BEREGNINGS_ID_SLUTT = 21;

    public static final int BEREGNINGS_DATO_START = 21;
    public static final int BEREGNINGS_DATO_SLUTT = 31;

    public static final int DATO_FOR_STATUS_START = 31;
    public static final int DATO_FOR_STATUS_SLUTT = 41;

    public static final int NYESTE_VENTESTATUS_START = 41;
    public static final int NYESTE_VENTESTATUS_SLUTT = 45;

    public static final int BRUKER_ID_START = 45;
    public static final int BRUKER_ID_SLUTT = 53;

    public static final int FORSTE_FOM_I_PERIODE_START = 53;
    public static final int FORSTE_FOM_I_PERIODE_SLUTT = 63;

    public static final int SISTE_TOM_I_PERIODE_START = 63;
    public static final int SISTE_TOM_I_PERIODE_SLUTT = 73;

    public static final int TOTALT_NETTO_BELOP_START = 73;
    public static final int TOTALT_NETTO_BELOP_SLUTT = 86;

    public static final int FLAGG_FEILKONTO_START = 86;
    public static final int FLAGG_FEILKONTO_SLUTT = 87;

    public static final int BEHANDLENDE_ENHET_START = 87;
    public static final int BEHANDLENDE_ENHET_SLUTT = 99;

    public static final int FAGGRUPPE_START = 99;
    public static final int FAGGRUPPE_SLUTT = 108;

    public static final int UTBETALES_TIL_ID_START = 108;
    public static final int UTBETALES_TIL_ID_SLUTT = 119;

    public static final int ETTEROPPGJOR_START = 119;
    public static final int ETTEROPPGJOR_SLUTT = 131;

    private OsMeldingFormat() {

    }
}
