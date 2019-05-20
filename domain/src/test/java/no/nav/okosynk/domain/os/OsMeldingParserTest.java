package no.nav.okosynk.domain.os;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.stream.Stream;
import no.nav.okosynk.domain.IncorrectMeldingFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OsMeldingParserTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String OS_MELDING = "10108000398024544313 2009-08-072009-09-26RETUK231B3502009-08-012009-08-31000000004100æ 8020         KORTTID 10108000398            ";
    private static final OsMeldingParser OS_MELDING_PARSER = new OsMeldingParser();

    @ParameterizedTest(name = "gjelderId = {0}")
    @MethodSource("getOsMeldingAndExpected")
    void parseGjelderId(String gjelderId, String gjelderIdType, String osmelding ) {
        assertEquals(gjelderId, OS_MELDING_PARSER.parseGjelderId(osmelding));
    }

    private static Stream<Arguments> getOsMeldingAndExpected() {
        return OsMeldingTestGenerator.osMeldingAndExpectedProvider();
    }

    @Test
    void parseDatoForStatus() {

        enteringTestHeaderLogger.debug(null);

        assertEquals(LocalDate.of(2009, 9, 26), OS_MELDING_PARSER.parseDatoForStatus(OS_MELDING));
    }

    @Test
    void parseNyesteVentestatus() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("RETU", OS_MELDING_PARSER.parseNyesteVentestatus(OS_MELDING));
    }

    @Test
    void parseBrukerId() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("K231B350", OS_MELDING_PARSER.parseBrukerId(OS_MELDING));
    }

    @Test
    void parseTotaltNettoBelop() {

        enteringTestHeaderLogger.debug(null);

        assertEquals(410.0, OS_MELDING_PARSER.parseTotaltNettoBelop(OS_MELDING));
    }

    @Test
    void parseBehandlendeEnhet() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("8020", OS_MELDING_PARSER.parseBehandlendeEnhet(OS_MELDING));
    }

    @Test
    void parseBeregningsId() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("024544313", OS_MELDING_PARSER.parseBeregningsId(OS_MELDING));
    }

    @Test
    void parseBeregningsDato() {

        enteringTestHeaderLogger.debug(null);

        assertEquals(LocalDate.of(2009, 8, 7), OS_MELDING_PARSER.parseBeregningsDato(OS_MELDING));
    }

    @Test
    void parseForsteFomIPeriode() {

        enteringTestHeaderLogger.debug(null);

        assertEquals(LocalDate.of(2009, 8, 1), OS_MELDING_PARSER.parseForsteFomIPeriode(OS_MELDING));
    }

    @Test
    void parseSisteTomIPeriode() {
        assertEquals(LocalDate.of(2009, 8, 31), OS_MELDING_PARSER.parseSisteTomIPeriode(OS_MELDING));
    }

    @Test
    void parseFlaggFeilkonto() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("", OS_MELDING_PARSER.parseFlaggFeilkonto(OS_MELDING));
    }

    @Test
    void parseFaggruppe() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("KORTTID", OS_MELDING_PARSER.parseFaggruppe(OS_MELDING));
    }

    @Test
    void parseUtbetalesTilId() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("10108000398", OS_MELDING_PARSER.parseUtbetalesTilId(OS_MELDING));
    }

    @Test
    void parseEtteroppgjor() {
        assertEquals("", OS_MELDING_PARSER.parseEtteroppgjor(OS_MELDING));
    }

    @Test
    void trimmedSubstringKasterMeldingFormatExceptionForUgyldigSubstring() {

        enteringTestHeaderLogger.debug(null);

        String tomStreng = "";

        assertThrows(IncorrectMeldingFormatException.class, () -> OS_MELDING_PARSER.trimmedSubstring(tomStreng, 0, 1));
    }

    @Test
    void parseDatoMedKlokkeslettKasterMeldingFormatExceptionForUgyldigDato() {

        enteringTestHeaderLogger.debug(null);

        String ugyldigDato = "UGYLDIG DATO";

        assertThrows(IncorrectMeldingFormatException.class, () -> OS_MELDING_PARSER.parseDatoMedKlokkeslett(ugyldigDato));
    }

    @Test
    void parseDatoUtenKlokkeslettKasterMeldingFormatExceptionForUgyldigDato() {

        enteringTestHeaderLogger.debug(null);

        String ugyldigDato = "UGYLDIG DATO";

        assertThrows(IncorrectMeldingFormatException.class, () -> OS_MELDING_PARSER.parseDatoUtenKlokkeslett(ugyldigDato));
    }
}
