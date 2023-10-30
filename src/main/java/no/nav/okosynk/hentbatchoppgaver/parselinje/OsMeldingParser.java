package no.nav.okosynk.hentbatchoppgaver.parselinje;

import java.time.LocalDate;

public class OsMeldingParser {

    private OsMeldingParser() {
    }

    public static String parseGjelderId(String osMelding) {
        final String gjelderId = Util.trimmedSubstring(osMelding, OsMeldingFormat.GJELDER_ID_START,
                OsMeldingFormat.GJELDER_ID_SLUTT);
        final int firstIndexAfter00 = 2;
        return gjelderId.startsWith("00") ? gjelderId.substring(firstIndexAfter00) : gjelderId;
    }

    public static String parseNyesteVentestatus(String osMelding) {
        return Util.trimmedSubstring(osMelding, OsMeldingFormat.NYESTE_VENTESTATUS_START,
                OsMeldingFormat.NYESTE_VENTESTATUS_SLUTT);
    }

    public static String parseBrukerId(String osMelding) {
        return Util.trimmedSubstring(osMelding, OsMeldingFormat.BRUKER_ID_START,
                OsMeldingFormat.BRUKER_ID_SLUTT);
    }

    public static double parseTotaltNettoBelop(String osMelding) {
        return Util.parseDouble(
                Util.trimmedSubstring(osMelding, OsMeldingFormat.TOTALT_NETTO_BELOP_START,
                        OsMeldingFormat.TOTALT_NETTO_BELOP_SLUTT));
    }

    public static String parseBehandlendeEnhet(String osMelding) {
        return Util.trimmedSubstring(osMelding, OsMeldingFormat.BEHANDLENDE_ENHET_START,
                OsMeldingFormat.BEHANDLENDE_ENHET_SLUTT);
    }

    public static LocalDate parseDatoForStatus(String osMelding) {
        return Util.parseDatoUtenKlokkeslett(
                Util.trimmedSubstring(osMelding, OsMeldingFormat.DATO_FOR_STATUS_START,
                        OsMeldingFormat.DATO_FOR_STATUS_SLUTT));
    }

    public static String parseBeregningsId(String osMelding) {
        return Util.trimmedSubstring(osMelding, OsMeldingFormat.BEREGNINGS_ID_START,
                OsMeldingFormat.BEREGNINGS_ID_SLUTT);
    }

    public static LocalDate parseBeregningsDato(String osMelding) {
        return Util.parseDatoUtenKlokkeslett(
                Util.trimmedSubstring(osMelding, OsMeldingFormat.BEREGNINGS_DATO_START,
                        OsMeldingFormat.BEREGNINGS_DATO_SLUTT));
    }

    public static LocalDate parseForsteFomIPeriode(String osMelding) {
        return Util.parseDatoUtenKlokkeslett(
                Util.trimmedSubstring(osMelding, OsMeldingFormat.FORSTE_FOM_I_PERIODE_START,
                        OsMeldingFormat.FORSTE_FOM_I_PERIODE_SLUTT));
    }

    public static LocalDate parseSisteTomIPeriode(String osMelding) {
        return Util.parseDatoUtenKlokkeslett(
                Util.trimmedSubstring(osMelding, OsMeldingFormat.SISTE_TOM_I_PERIODE_START,
                        OsMeldingFormat.SISTE_TOM_I_PERIODE_SLUTT));
    }

    public static String parseFlaggFeilkonto(String osMelding) {
        return Util.trimmedSubstring(osMelding, OsMeldingFormat.FLAGG_FEILKONTO_START,
                OsMeldingFormat.FLAGG_FEILKONTO_SLUTT);
    }

    public static String parseFaggruppe(String osMelding) {
        return Util.trimmedSubstring(osMelding, OsMeldingFormat.FAGGRUPPE_START,
                OsMeldingFormat.FAGGRUPPE_SLUTT);
    }

    public static String parseUtbetalesTilId(String osMelding) {
        return Util.trimmedSubstring(osMelding, OsMeldingFormat.UTBETALES_TIL_ID_START,
                OsMeldingFormat.UTBETALES_TIL_ID_SLUTT);
    }

    public static String parseEtteroppgjor(String osMelding) {
        return Util.trimmedSubstring(osMelding, OsMeldingFormat.ETTEROPPGJOR_START,
                OsMeldingFormat.ETTEROPPGJOR_SLUTT);
    }
}
