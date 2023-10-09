package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.MeldingUnreadableException;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class OsMeldingReaderTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private static final String INPUT_STRENG_FOR_OS_MELDING_MED_MAPPING_REGEL =
            "10108000398022828640 2009-07-042009-09-26RETUK231B3502009-05-012009-07-31000000012300æ 8020         INNT    10108000398            ";
    private static final String INPUT_STRENG_FOR_OS_MELDING_UTEN_MAPPING_REGEL =
            "06025800174029568753 2009-11-062009-11-30AVVEX123456 2009-11-012009-11-30000000072770æ 8020         HELSEREF06025800174            ";

    Function<String, OsMelding> osMeldingCreator = OsMelding::new;
    MeldingReader<OsMelding> osMeldingReader;

    @BeforeEach
    void setUp() {
        osMeldingReader = new MeldingReader<>(osMeldingCreator);
    }

    @Test
    void opprettOsMeldingerFraFilOppretterOsMeldinger() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        List<OsMelding> osMeldinger =
                osMeldingReader.opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(lagOsMeldinger());

        assertNotNull(osMeldinger);
        assertEquals(lagOsMeldinger().size(), osMeldinger.size());
    }

    @Test
    @DisplayName("opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger kaster MeldingUnreadableException hvis inputstream inneholder ugyldige meldinger")
    void inputStreamMedUgyldigeMeldingerKasterFeil() {

        enteringTestHeaderLogger.debug(null);

        List<String> ugyldigStream = lagStreamMedUgyldigMelding();

        assertThrows(MeldingUnreadableException.class, () -> osMeldingReader.opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(ugyldigStream));
    }

    @Test
    @DisplayName("Map inputstreng til OsMelding med osMeldingReader")
    void mapInputStrengTilOsMelding() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        String ufiltrertMelding = "00990000000251889728 2017-01-232017-01-24AVVEK231B2622016-09-012016-09-30000000000003E 8020         KTPOST  00994932691            ";
        List<OsMelding> osMeldinger = osMeldingReader.opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(lagOsMeldinger(ufiltrertMelding));
        assertNotNull(osMeldinger);
    }

    // =========================================================================

    private List<String> lagStreamMedUgyldigMelding() {
        List<String> ugyldigMeldingListe = new ArrayList<>();
        ugyldigMeldingListe.add("UGYLDIG_MELDING");
        return ugyldigMeldingListe;
    }

    private List<String> lagOsMeldinger() {
        List<String> osMeldinger = new ArrayList<>();
        osMeldinger.add(INPUT_STRENG_FOR_OS_MELDING_MED_MAPPING_REGEL);
        osMeldinger.add(INPUT_STRENG_FOR_OS_MELDING_UTEN_MAPPING_REGEL);
        return osMeldinger;
    }

    private List<String> lagOsMeldinger(final String input) {
        List<String> osMeldinger = new ArrayList<>();
        osMeldinger.add(input);
        return osMeldinger;

    }
}
