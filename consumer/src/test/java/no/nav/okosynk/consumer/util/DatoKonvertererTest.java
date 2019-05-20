package no.nav.okosynk.consumer.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatoKonvertererTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final int YEAR = 2003;
    private static final int MONTH = 7;
    private static final int DAY = 29;

    @Test
    void konverterLocalDateTilXMLGregorianCalendar() throws DatatypeConfigurationException {

        enteringTestHeaderLogger.debug(null);

        LocalDate inputDato = LocalDate.of(YEAR, MONTH, DAY);

        XMLGregorianCalendar actual = DatoKonverterer.konverterLocalDateTilXMLGregorianCalendar(inputDato);

        GregorianCalendar gregorianCalendar = GregorianCalendar.from(inputDato.atStartOfDay(ZoneId.systemDefault()));
        XMLGregorianCalendar expected = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);

        assertEquals(expected, actual);
    }
}
