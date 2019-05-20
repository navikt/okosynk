package no.nav.okosynk.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public abstract class MeldingParser {

    private static final DateTimeFormatter DATO_FORMAT_UTEN_KLOKKESLETT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATO_FORMAT_MED_KLOKKESLETT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public abstract String parseGjelderId(String melding);

    public abstract LocalDate parseDatoForStatus(String melding);

    public abstract String parseNyesteVentestatus(String melding);

    public abstract String parseBrukerId(String melding);

    public abstract double parseTotaltNettoBelop(String melding);

    public abstract String parseBehandlendeEnhet(String melding);

    public String trimmedSubstring(String s, int start, int end) {
        try {
            return s.substring(start, end).trim();
        } catch (StringIndexOutOfBoundsException e) {
            throw new IncorrectMeldingFormatException(e);
        }
    }

    public LocalDateTime parseDatoMedKlokkeslett(String datoMedKlokkeslett) {
        try {
            return LocalDateTime.parse(datoMedKlokkeslett, DATO_FORMAT_MED_KLOKKESLETT);
        } catch (DateTimeParseException e) {
            throw new IncorrectMeldingFormatException(e);
        }
    }

    public LocalDate parseDatoUtenKlokkeslett(String datoUtenKlokkeslett) {
        try {
            return LocalDate.parse(datoUtenKlokkeslett, DATO_FORMAT_UTEN_KLOKKESLETT);
        } catch (DateTimeParseException e) {
            throw new IncorrectMeldingFormatException(e);
        }
    }
}
