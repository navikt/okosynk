package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.UrMeldingTestGenerator;
import no.nav.okosynk.hentbatchoppgaver.parselinje.exceptions.IncorrectMeldingFormatException;
import no.nav.okosynk.model.GjelderIdType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrMeldingParserTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private static final String UR_MELDING = "10108000398PERSON      2011-01-28T18:25:5825          00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398";

    @ParameterizedTest(name = "gjelderId = {0}")
    @MethodSource("getUrMeldingAndExpected")
    void parseGjelderId(String gjelderId, GjelderIdType _gjelderIdType, String urMelding) {
        assertEquals(gjelderId, UrMeldingParser.parseGjelderId(urMelding));
    }

    private static Stream<Arguments> getUrMeldingAndExpected() {
        return UrMeldingTestGenerator.urMeldingAndExpectedProvider();
    }

    @Test
    void parseNyesteVentestatus() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("25", UrMeldingParser.parseNyesteVentestatus(UR_MELDING));
    }

    @Test
    void parseBrukerId() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("", UrMeldingParser.parseBrukerId(UR_MELDING));
    }

    @Test
    void parseTotaltNettoBelop() {

        enteringTestHeaderLogger.debug(null);

        assertEquals(1940.0, UrMeldingParser.parseTotaltNettoBelop(UR_MELDING));
    }

    @Test
    void parseBehandlendeEnhet() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("8020", UrMeldingParser.parseBehandlendeEnhet(UR_MELDING));
    }

    @Test
    void parseDatoForStatus() {

        enteringTestHeaderLogger.debug(null);

        assertEquals(LocalDate.of(2011, 1, 28), UrMeldingParser.parseDatoForStatus(UR_MELDING));
    }

    @Test
    void parseGjelderIdType() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("PERSON", UrMeldingParser.parseGjelderIdType(UR_MELDING));
    }

    @Test
    void parseOppdragsKode() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("INNT", UrMeldingParser.parseOppdragsKode(UR_MELDING));
    }

    @Test
    void parseKilde() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("UR230", UrMeldingParser.parseKilde(UR_MELDING));
    }

    @Test
    void parseDatoPostert() {

        enteringTestHeaderLogger.debug(null);

        assertEquals(LocalDate.of(2011, 1, 21), UrMeldingParser.parseDatoPostert(UR_MELDING));
    }

    @Test
    void parseBilagsId() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("342552558", UrMeldingParser.parseBilagsId(UR_MELDING));
    }

    @Test
    void parseArsaksTekst() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("Mottakers konto er oppgjort", UrMeldingParser.parseArsaksTekst(UR_MELDING));
    }

    @Test
    void parseMottakerId() {

        enteringTestHeaderLogger.debug(null);

        assertEquals("10108000398", UrMeldingParser.parseMottakerId(UR_MELDING));
    }

    @Test
    void trimmedSubstringKasterMeldingFormatExceptionForUgyldigSubstring() {

        enteringTestHeaderLogger.debug(null);

        String tomStreng = "";

        assertThrows(IncorrectMeldingFormatException.class, () -> Util.trimmedSubstring(tomStreng, 0, 1));
    }

    @Test
    void parseDatoMedKlokkeslettKasterMeldingFormatExceptionForUgyldigDato() {

        enteringTestHeaderLogger.debug(null);

        String ugyldigDato = "UGYLDIG DATO";

        assertThrows(IncorrectMeldingFormatException.class, () -> Util.parseDatoMedKlokkeslett(ugyldigDato));
    }

    @Test
    void parseDatoUtenKlokkeslettKasterMeldingFormatExceptionForUgyldigDato() {

        enteringTestHeaderLogger.debug(null);

        String ugyldigDato = "UGYLDIG DATO";

        assertThrows(IncorrectMeldingFormatException.class, () -> Util.parseDatoUtenKlokkeslett(ugyldigDato));
    }
}
