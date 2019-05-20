package no.nav.okosynk.domain.os;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import no.nav.okosynk.domain.MappingRegel;
import no.nav.okosynk.domain.os.OsMappingRegelRepository;
import no.nav.okosynk.domain.os.OsMelding;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OsMappingRegelRepositoryTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private OsMappingRegelRepository osMappingRegelRepository;
    private OsMelding osMeldingUtenMappingRegel;
    private OsMelding osMeldingSomSkalBliTilOppgave;

    private static final String OS_MELDING_UTEN_MAPPING_REGEL = "01010012345000000001 2009-11-062009-11-30AVVEAAA1234 2009-11-012009-11-30000000015000æ 8019         HELSEREF01010012345            ";
    private static final String OS_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL = "02029912345000000002 2008-10-102008-10-10NEG A123B1232008-11-012008-11-30000000001600å 4151         GS      02029912345            ";
    @BeforeEach
    void setUp() {
        osMappingRegelRepository = new OsMappingRegelRepository();
        osMeldingUtenMappingRegel = new OsMelding(OS_MELDING_UTEN_MAPPING_REGEL);
        osMeldingSomSkalBliTilOppgave = new OsMelding(OS_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL);
    }

    @Test
    @DisplayName("Riktig mappingregel skal bli funnet for en OS-melding som skal mappes til oppgave")
    void riktigMapping() {

        enteringTestHeaderLogger.debug(null);

        Optional<MappingRegel> osMappingRegel = osMappingRegelRepository.finnRegel(osMeldingSomSkalBliTilOppgave);

        assertTrue(osMappingRegel.isPresent(), "Mapping mangler");
        assertAll("Mapping skal ha riktige verdier",
                () -> assertEquals("GS", osMappingRegel.get().underkategoriKode),
                () -> assertEquals("4151", osMappingRegel.get().ansvarligEnhetId)
        );
    }

    @Test
    @DisplayName("Ingen mappingregel skal bli funnet for en OS-melding som ikke skal mappes til oppgave")
    void mappingFinnesIkke() {

        enteringTestHeaderLogger.debug(null);

        Optional<MappingRegel> osMappingRegel = osMappingRegelRepository.finnRegel(osMeldingUtenMappingRegel);

        assertFalse(osMappingRegel.isPresent(), "Mapping ble funnet for oppgave som ikke skal mappes");
    }

}
