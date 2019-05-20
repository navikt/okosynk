package no.nav.okosynk.domain.ur;

import java.util.Optional;

import no.nav.okosynk.domain.MappingRegel;
import no.nav.okosynk.domain.ur.UrMappingRegelRepository;
import no.nav.okosynk.domain.ur.UrMelding;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class UrMappingRegelRepositoryTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING = "10108000398PERSON      2011-02-01T06:11:4625          00000000033390æ8020UTPOST UR2302011-01-31343285958Kredit kontonummer ugyldig                        00963702833";
    private static final String UR_MELDING_UTEN_MAPPING_REGEL = "00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8019ANDRUTBUR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282";

    private UrMappingRegelRepository urMappingRegelRepository;
    private UrMelding urMeldingSomSkalBliTilOppgave;
    private UrMelding urMeldingUtenMappingRegel;

    @BeforeEach
    void setUp() {
        urMappingRegelRepository = new UrMappingRegelRepository();
        urMeldingSomSkalBliTilOppgave = new UrMelding(UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING);
        urMeldingUtenMappingRegel = new UrMelding(UR_MELDING_UTEN_MAPPING_REGEL);
    }

    @Test
    @DisplayName("Riktig mapping skal bli funnet for en UR-melding som skal mappes til oppgave")
    void riktigMapping() {

        enteringTestHeaderLogger.debug(null);

        Optional<MappingRegel> mapping = urMappingRegelRepository.finnRegel(urMeldingSomSkalBliTilOppgave);

        assertTrue(mapping.isPresent(), "Mapping mangler");
        assertAll("Mapping skal ha riktige verdier",
                () -> assertEquals("UTPOST", mapping.get().underkategoriKode),
                () -> assertEquals("4151", mapping.get().ansvarligEnhetId)
        );
    }

    @Test
    @DisplayName("Ingen mapping skal bli funnet for en UR-melding som ikke skal mappes til oppgave")
    void mappingFinnesIkke() {

        enteringTestHeaderLogger.debug(null);

        Optional<MappingRegel> mapping = urMappingRegelRepository.finnRegel(urMeldingUtenMappingRegel);

        assertFalse(mapping.isPresent(), "Mapping ble funnet for oppgave som ikke skal mappes");
    }

}
