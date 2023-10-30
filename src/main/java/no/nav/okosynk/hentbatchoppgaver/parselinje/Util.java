package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.parselinje.exceptions.IncorrectMeldingFormatException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
    private static final Map<Character, Integer> verdier = new HashMap<>();
    private static final List<Character> KODER_FOR_POSITIVT_FORTEGN = Arrays.asList('æ', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I');
    private static final List<Character> KODER_FOR_NEGATIVT_FORTEGN = Arrays.asList('å', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R');

    static {
        for (int verdi = 0; verdi <= 9; ++verdi) {
            verdier.put(KODER_FOR_POSITIVT_FORTEGN.get(verdi), verdi);
            verdier.put(KODER_FOR_NEGATIVT_FORTEGN.get(verdi), verdi);
        }
    }

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

    public static String formatAsNorwegianDate(final LocalDate dato) {
        return DateTimeFormatter.ofPattern("dd.MM.yy").format(dato);
    }

    private static boolean erPositiv(String input) {
        char sisteTegn = input.charAt(input.length() - 1);

        if (KODER_FOR_POSITIVT_FORTEGN.contains(sisteTegn)) {
            return true;
        } else if (KODER_FOR_NEGATIVT_FORTEGN.contains(sisteTegn)) {
            return false;
        } else {
            throw new NumberFormatException(String.format("Ugyldig input: %s%n Siste tegn angir ikke om beløpet er positivt eller negativt.", input));
        }
    }

    private static double trekkUtFraksjonsdel(String input) {
        int forsteDesimal = Integer.parseInt(String.valueOf(input.charAt(input.length() - 2)));
        char sisteTegn = input.charAt(input.length() - 1);

        if (!verdier.containsKey(sisteTegn)) {
            throw new NumberFormatException(String.format("Ugyldig input: %s%nSiste tegn angir ingen tallverdi.", input));
        }
        int andreDesimal = verdier.get(sisteTegn);

        return opprettFraksjonFraDesimaler(forsteDesimal, andreDesimal);
    }

    private static double opprettFraksjonFraDesimaler(int forsteDesimal, int andreDesimal) {
        return forsteDesimal / 10d + andreDesimal / 100d;
    }

    private static long trekkUtHeltallsdel(String input) {
        return Long.parseLong(input.substring(0, input.length() - 2));
    }

    public static double parseDouble(String input) {
        try {
            double verdi = trekkUtHeltallsdel(input) + trekkUtFraksjonsdel(input);
            return erPositiv(input) ? verdi : -verdi;
        } catch (NumberFormatException e) {
            throw new IncorrectMeldingFormatException(e);
        }
    }
}
