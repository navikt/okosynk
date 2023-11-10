package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.model.OsMeldingTestGenerator;
import no.nav.okosynk.model.GjelderIdType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OsMeldingParserTest {

    private static final String OS_MELDING = "10108000398024544313 2009-08-072009-09-26RETUK231B3502009-08-012009-08-31000000004100æ 8020         KORTTID 10108000398            ";

    @ParameterizedTest(name = "gjelderId = {0}")
    @MethodSource("getOsMeldingAndExpected")
    void parseGjelderId(String gjelderId, GjelderIdType _gjelderIdType, String osmelding) {
        assertEquals(gjelderId, OsMeldingParser.parseGjelderId(osmelding));
    }

    private static Stream<Arguments> getOsMeldingAndExpected() {
        return OsMeldingTestGenerator.osMeldingAndExpectedProvider();
    }

    @Test
    void parseDatoForStatus() {

        assertEquals(LocalDate.of(2009, 9, 26), OsMeldingParser.parseDatoForStatus(OS_MELDING));
    }

    @Test
    void parseNyesteVentestatus() {

        assertEquals("RETU", OsMeldingParser.parseNyesteVentestatus(OS_MELDING));
    }

    @Test
    void parseBrukerId() {

        assertEquals("K231B350", OsMeldingParser.parseBrukerId(OS_MELDING));
    }

    @Test
    void parseTotaltNettoBelop() {
        assertEquals(410.0, OsMeldingParser.parseTotaltNettoBelop(OS_MELDING));
    }

    @Test
    void parseBehandlendeEnhet() {
        assertEquals("8020", OsMeldingParser.parseBehandlendeEnhet(OS_MELDING));
    }

    @Test
    void parseBeregningsId() {

        assertEquals("024544313", OsMeldingParser.parseBeregningsId(OS_MELDING));
    }

    @Test
    void parseBeregningsDato() {

        assertEquals(LocalDate.of(2009, 8, 7), OsMeldingParser.parseBeregningsDato(OS_MELDING));
    }

    @Test
    void parseForsteFomIPeriode() {

        assertEquals(LocalDate.of(2009, 8, 1), OsMeldingParser.parseForsteFomIPeriode(OS_MELDING));
    }

    @Test
    void parseSisteTomIPeriode() {

        assertEquals(LocalDate.of(2009, 8, 31), OsMeldingParser.parseSisteTomIPeriode(OS_MELDING));
    }

    @Test
    void parseFlaggFeilkonto() {

        assertEquals("", OsMeldingParser.parseFlaggFeilkonto(OS_MELDING));
    }

    @Test
    void parseFaggruppe() {

        assertEquals("KORTTID", OsMeldingParser.parseFaggruppe(OS_MELDING));
    }

    @Test
    void parseUtbetalesTilId() {

        assertEquals("10108000398", OsMeldingParser.parseUtbetalesTilId(OS_MELDING));
    }

    @Test
    void parseEtteroppgjor() {
        assertEquals("", OsMeldingParser.parseEtteroppgjor(OS_MELDING));
    }

}
