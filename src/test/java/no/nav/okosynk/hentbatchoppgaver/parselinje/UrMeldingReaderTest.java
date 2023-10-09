package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.MeldingUnreadableException;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class UrMeldingReaderTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private static final String INPUT_STRENG_FOR_UR_MELDING_SOM_HAR_MAPPING_TIL_OPPGAVE =
            "10108000398PERSON      2011-01-28T18:25:5825          00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398";
    private static final String INPUT_STRENG_FOR_UR_MELDING_SOM_IKKE_HAR_MAPPING_TIL_OPPGAVE =
            "00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8020ANDRUTBUR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282";

    Function<String, UrMelding> urMeldingCreator = UrMelding::new;
    MeldingReader<UrMelding> urMeldingReader;

    @BeforeEach
    void setUp() {
        urMeldingReader = new MeldingReader<>(urMeldingCreator);
    }

    @Test
    void opprettUrMeldingerFraFilOppretterUrMeldinger() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        List<UrMelding> urMeldinger =
                urMeldingReader.opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(lagUrMeldinger());

        assertNotNull(urMeldinger);
        assertEquals(lagUrMeldinger().size(), urMeldinger.size());
    }

    @Test
    @DisplayName("opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger kaster MeldingUnreadableException hvis inputstream innehoder ugyldige meldinger")
    void inputStreamMedUgyldigeMeldingerKasterFeil() {

        enteringTestHeaderLogger.debug(null);

        List<String> ugyldigStream = lagStreamMedUgyldigMelding();

        assertThrows(MeldingUnreadableException.class, () -> urMeldingReader.opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(ugyldigStream));
    }

    // =========================================================================

    private List<String> lagStreamMedUgyldigMelding() {
        List<String> ugyldigMeldingListe = new ArrayList<>();
        ugyldigMeldingListe.add("UGYLDIG_MELDING");
        return ugyldigMeldingListe;
    }

    private List<String> lagUrMeldinger() {
        List<String> urMeldinger = new ArrayList<>();
        urMeldinger.add(INPUT_STRENG_FOR_UR_MELDING_SOM_HAR_MAPPING_TIL_OPPGAVE);
        urMeldinger.add(INPUT_STRENG_FOR_UR_MELDING_SOM_IKKE_HAR_MAPPING_TIL_OPPGAVE);
        return urMeldinger;

    }
}
