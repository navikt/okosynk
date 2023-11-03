package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.parselinje.exceptions.IncorrectMeldingFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DesimaltallParserTest {
    @ParameterizedTest(name = "parsedResult of {1} = {0}")
    @MethodSource("getParsedAndFlatfilNumber")
    void parseGirRiktigResultat(double expected, String flatFilTall) {
        assertEquals(expected, Util.parseDouble(flatFilTall));
    }

    @Test
    void parseKasterMeldingFormatExceptionHvisSisteTegnErUgyldig() {

        assertThrows(IncorrectMeldingFormatException.class, () -> {
            String strengMedUgyldigSisteTegn = "0000000009009";
            Util.parseDouble(strengMedUgyldigSisteTegn);
        });

        assertThrows(IncorrectMeldingFormatException.class, () -> {
            String strengMedUgyldigSisteTegn = "00000010201x";
            Util.parseDouble(strengMedUgyldigSisteTegn);
        });
    }

    @Test
    void parseKasterMeldingFormatExceptionForStrengSomIkkeRepresentererEtBelop() {

        assertThrows(IncorrectMeldingFormatException.class, () -> {
            String strengSomIkkeRepresentererEtBelop = "abcdefg19660P";
            Util.parseDouble(strengSomIkkeRepresentererEtBelop);
        });
    }

    @Test
    void parseFungererForStorsteMuligeTall() {

        assertEquals(99999999999.99, Util.parseDouble("999999999999I"));
    }

    private static Stream<Arguments> getParsedAndFlatfilNumber() {
        return Stream.of(
                Arguments.of(-90.00, "000000000900å"),
                Arguments.of(-550.00, "000000005500å"),
                Arguments.of(0.00, "000000000000æ"),
                Arguments.of(110.00, "000000001100æ"),
                Arguments.of(56.75, "000000000567E"),
                Arguments.of(41498.91, "000000414989A"),
                Arguments.of(182298.00, "000001822980æ"),
                Arguments.of(150.75, "000000001507E"),
                Arguments.of(476.00, "000000004760æ"),
                Arguments.of(-4428.00, "000000044280å"),
                Arguments.of(8484.01, "000000084840A"),
                Arguments.of(2290.02, "000000022900B"),
                Arguments.of(2298.03, "000000022980C"),
                Arguments.of(693.04, "000000006930D"),
                Arguments.of(1966.05, "000000019660E"),
                Arguments.of(999.06, "000000009990F"),
                Arguments.of(11640.07, "000000116400G"),
                Arguments.of(2268.08, "000000022680H"),
                Arguments.of(2320.09, "000000023200I"),
                Arguments.of(-11283.01, "000000112830J"),
                Arguments.of(-1966.02, "000000019660K"),
                Arguments.of(-999.03, "000000009990L"),
                Arguments.of(-970.04, "000000009700M"),
                Arguments.of(-1269.05, "000000012690N"),
                Arguments.of(-11858.06, "000000118580O"),
                Arguments.of(-1966.07, "000000019660P"),
                Arguments.of(-6909.08, "000000069090Q"),
                Arguments.of(-999.09, "000000009990R")
        );
    }
}
