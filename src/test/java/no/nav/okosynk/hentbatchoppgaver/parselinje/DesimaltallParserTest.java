package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.parselinje.exceptions.IncorrectMeldingFormatException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class DesimaltallParserTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    void parseGirRiktigResultat() throws ParseException {

        enteringTestHeaderLogger.debug(null);

        assertAll("parseGirRiktigResultat",
                () -> assertEquals(-90.00, DesimaltallParser.parse("000000000900å")),
                () -> assertEquals(-550.00, DesimaltallParser.parse("000000005500å")),
                () -> assertEquals(0.00, DesimaltallParser.parse("000000000000æ")),
                () -> assertEquals(110.00, DesimaltallParser.parse("000000001100æ")),
                () -> assertEquals(56.75, DesimaltallParser.parse("000000000567E")),
                () -> assertEquals(41498.91, DesimaltallParser.parse("000000414989A")),
                () -> assertEquals(182298.00, DesimaltallParser.parse("000001822980æ")),
                () -> assertEquals(150.75, DesimaltallParser.parse("000000001507E")),
                () -> assertEquals(476.00, DesimaltallParser.parse("000000004760æ")),
                () -> assertEquals(-4428.00, DesimaltallParser.parse("000000044280å")),
                () -> assertEquals(8484.01, DesimaltallParser.parse("000000084840A")),
                () -> assertEquals(2290.02, DesimaltallParser.parse("000000022900B")),
                () -> assertEquals(2298.03, DesimaltallParser.parse("000000022980C")),
                () -> assertEquals(693.04, DesimaltallParser.parse("000000006930D")),
                () -> assertEquals(1966.05, DesimaltallParser.parse("000000019660E")),
                () -> assertEquals(999.06, DesimaltallParser.parse("000000009990F")),
                () -> assertEquals(11640.07, DesimaltallParser.parse("000000116400G")),
                () -> assertEquals(2268.08, DesimaltallParser.parse("000000022680H")),
                () -> assertEquals(2320.09, DesimaltallParser.parse("000000023200I")),
                () -> assertEquals(-11283.01, DesimaltallParser.parse("000000112830J")),
                () -> assertEquals(-1966.02, DesimaltallParser.parse("000000019660K")),
                () -> assertEquals(-999.03, DesimaltallParser.parse("000000009990L")),
                () -> assertEquals(-970.04, DesimaltallParser.parse("000000009700M")),
                () -> assertEquals(-1269.05, DesimaltallParser.parse("000000012690N")),
                () -> assertEquals(-11858.06, DesimaltallParser.parse("000000118580O")),
                () -> assertEquals(-1966.07, DesimaltallParser.parse("000000019660P")),
                () -> assertEquals(-6909.08, DesimaltallParser.parse("000000069090Q")),
                () -> assertEquals(-999.09, DesimaltallParser.parse("000000009990R"))
        );
    }

    @Test
    void parseKasterMeldingFormatExceptionHvisSisteTegnErUgyldig() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(IncorrectMeldingFormatException.class, () -> {
            String strengMedUgyldigSisteTegn = "0000000009009";
            DesimaltallParser.parse(strengMedUgyldigSisteTegn);
        });

        assertThrows(IncorrectMeldingFormatException.class, () -> {
            String strengMedUgyldigSisteTegn = "00000010201x";
            DesimaltallParser.parse(strengMedUgyldigSisteTegn);
        });
    }

    @Test
    void parseKasterMeldingFormatExceptionForStrengSomIkkeRepresentererEtBelop() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(IncorrectMeldingFormatException.class, () -> {
            String strengSomIkkeRepresentererEtBelop = "abcdefg19660P";
            DesimaltallParser.parse(strengSomIkkeRepresentererEtBelop);
        });
    }

    @Test
    void parseFungererForStorsteMuligeTall() throws ParseException {

        enteringTestHeaderLogger.debug(null);

        assertEquals(99999999999.99, DesimaltallParser.parse("999999999999I"));
    }
}
