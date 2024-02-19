package no.nav.okosynk.hentbatchoppgaver.parselinje;

import java.time.LocalDate;

public class UrMeldingParser {
    private UrMeldingParser() {
    }

    public static String parseGjelderId(String urMelding) {
        final String gjelderId = Util.trimmedSubstring(urMelding, UrMeldingFormat.GJELDER_ID_START,
                UrMeldingFormat.GJELDER_ID_SLUTT);
        final int firstIndexAfter00 = 2;
        return gjelderId.startsWith("00") ? gjelderId.substring(firstIndexAfter00) : gjelderId;
    }

    public static String parseNyesteVentestatus(String urMelding) {
        return Util.trimmedSubstring(urMelding, UrMeldingFormat.NYESTE_VENTESTATUS_START,
                UrMeldingFormat.NYESTE_VENTESTATUS_SLUTT);
    }

    public static String parseBrukerId(String urMelding) {
        return Util.trimmedSubstring(urMelding, UrMeldingFormat.BRUKER_ID_START,
                UrMeldingFormat.BRUKER_ID_SLUTT);
    }

    public static double parseTotaltNettoBelop(String urMelding) {
        return Util.parseDouble(
                Util.trimmedSubstring(urMelding, UrMeldingFormat.TOTALT_NETTO_BELOP_START,
                        UrMeldingFormat.TOTALT_NETTO_BELOP_SLUTT));
    }

    public static String parseBehandlendeEnhet(String urMelding) {
        return Util.trimmedSubstring(urMelding, UrMeldingFormat.BEHANDLENDE_ENHET_START,
                UrMeldingFormat.BEHANDLENDE_ENHET_SLUTT);
    }

    public static LocalDate parseDatoForStatus(String urMelding) {
        return Util.parseDatoMedKlokkeslett(
                Util.trimmedSubstring(urMelding, UrMeldingFormat.DATO_FOR_STATUS_START,
                        UrMeldingFormat.DATO_FOR_STATUS_SLUTT)).toLocalDate();
    }

    public static String parseGjelderIdType(String urMelding) {
        return Util.trimmedSubstring(urMelding, UrMeldingFormat.GJELDER_ID_TYPE_START,
                UrMeldingFormat.GJELDER_ID_TYPE_SLUTT);
    }

    public static String parseOppdragsKode(String urMelding) {
        return Util.trimmedSubstring(urMelding, UrMeldingFormat.OPPDRAGS_KODE_START,
                UrMeldingFormat.OPPDRAGS_KODE_SLUTT);
    }

    public static String parseKilde(String urMelding) {
        return Util.trimmedSubstring(urMelding, UrMeldingFormat.KILDE_START,
                UrMeldingFormat.KILDE_SLUTT);
    }

    public static LocalDate parseDatoPostert(String urMelding) {
        return Util.parseDatoUtenKlokkeslett(
                Util.trimmedSubstring(urMelding, UrMeldingFormat.DATO_POSTERT_START,
                        UrMeldingFormat.DATO_POSTERT_SLUTT));
    }

    public static String parseBilagsId(String urMelding) {
        return Util.trimmedSubstring(urMelding, UrMeldingFormat.BILAGS_ID_START,
                UrMeldingFormat.BILAGS_ID_SLUTT);
    }

    public static String parseArsaksTekst(String urMelding) {
        return Util.trimmedSubstring(urMelding, UrMeldingFormat.ARSAKS_TEKST_START,
                UrMeldingFormat.ARSAKS_TEKST_SLUTT);
    }

    public static String parseMottakerId(String urMelding) {
        return Util.trimmedSubstring(urMelding, UrMeldingFormat.MOTTAKER_ID_START,
                UrMeldingFormat.MOTTAKER_ID_SLUTT);
    }
}
