package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.parselinje.exceptions.IncorrectMeldingFormatException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Util {
    private Util() {}
    private static final DateTimeFormatter DATO_FORMAT_UTEN_KLOKKESLETT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATO_FORMAT_MED_KLOKKESLETT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String trimmedSubstring(String s, int start, int end) {
        try {
            return s.substring(start, end).trim();
        } catch (StringIndexOutOfBoundsException e) {
            throw new IncorrectMeldingFormatException(e);
        }
    }

    public static LocalDateTime parseDatoMedKlokkeslett(String datoMedKlokkeslett) {
        try {
            return LocalDateTime.parse(datoMedKlokkeslett, DATO_FORMAT_MED_KLOKKESLETT);
        } catch (DateTimeParseException e) {
            throw new IncorrectMeldingFormatException(e);
        }
    }

    public static LocalDate parseDatoUtenKlokkeslett(String datoUtenKlokkeslett) {
        try {
            return LocalDate.parse(datoUtenKlokkeslett, DATO_FORMAT_UTEN_KLOKKESLETT);
        } catch (DateTimeParseException e) {
            throw new IncorrectMeldingFormatException(e);
        }
    }
}
