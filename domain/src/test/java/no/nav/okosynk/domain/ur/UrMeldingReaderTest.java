package no.nav.okosynk.domain.ur;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import no.nav.okosynk.domain.ur.UrMelding;
import no.nav.okosynk.domain.ur.UrMeldingCreator;
import no.nav.okosynk.domain.MeldingUnreadableException;
import no.nav.okosynk.domain.ur.UrMeldingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UrMeldingReaderTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String INPUT_STRENG_FOR_UR_MELDING_SOM_HAR_MAPPING_TIL_OPPGAVE =
        "10108000398PERSON      2011-01-28T18:25:5825          00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398";
    private static final String INPUT_STRENG_FOR_UR_MELDING_SOM_IKKE_HAR_MAPPING_TIL_OPPGAVE =
        "00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8020ANDRUTBUR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282";

    UrMeldingCreator urMeldingCreator = UrMelding::new;
    UrMeldingReader urMeldingReader;

    @BeforeEach
    void setUp() {
        urMeldingReader = new UrMeldingReader(urMeldingCreator);
    }

    @Test
    void opprettUrMeldingerFraFilOppretterUrMeldinger() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        List<UrMelding> urMeldinger =
            urMeldingReader.opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(lagUrMeldinger());

        assertNotNull(urMeldinger);
        assertEquals(lagUrMeldinger().count(), urMeldinger.size());
    }

    @Test
    @DisplayName("opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger kaster MeldingUnreadableException hvis inputstream innehoder ugyldige meldinger")
    void inputStreamMedUgyldigeMeldingerKasterFeil() {

        enteringTestHeaderLogger.debug(null);

        Stream<String> ugyldigStream = lagStreamMedUgyldigMelding();

        assertThrows(MeldingUnreadableException.class, () -> urMeldingReader.opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(ugyldigStream));
    }

    // =========================================================================

    private Stream<String> lagStreamMedUgyldigMelding() {
        List<String> ugyldigMeldingListe = new ArrayList<>();
        ugyldigMeldingListe.add("UGYLDIG_MELDING");
        return ugyldigMeldingListe.stream();
    }

    private Stream<String> lagUrMeldinger() {
        List<String> urMeldinger = new ArrayList<>();
        urMeldinger.add(INPUT_STRENG_FOR_UR_MELDING_SOM_HAR_MAPPING_TIL_OPPGAVE);
        urMeldinger.add(INPUT_STRENG_FOR_UR_MELDING_SOM_IKKE_HAR_MAPPING_TIL_OPPGAVE);
        return urMeldinger.stream();

    }
}
