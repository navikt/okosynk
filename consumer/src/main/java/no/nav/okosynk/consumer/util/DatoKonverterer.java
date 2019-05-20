package no.nav.okosynk.consumer.util;

import javax.xml.datatype.*;
import java.time.*;
import java.util.GregorianCalendar;

public class DatoKonverterer {

    public static XMLGregorianCalendar konverterLocalDateTilXMLGregorianCalendar(LocalDate localDate) throws DatoKonverteringException {
        return konverterLocalDateTimeTilXMLGregorianCalendar(localDate.atStartOfDay());
    }

    public static XMLGregorianCalendar konverterLocalDateTimeTilXMLGregorianCalendar(LocalDateTime localDateTime) {
        GregorianCalendar gregorianCalendar = GregorianCalendar.from(ZonedDateTime.of(localDateTime, ZoneId.systemDefault()));

        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new DatoKonverteringException(e, "Feil ved instansiering av DatatypeFactory under konvertering av dato.");
        }
    }
}
