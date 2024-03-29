package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

class UrMappingRegelRepositoryTest extends MappingregelverkTest<UrMelding> {

    private static final String UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL = "10108000398PERSON      2011-02-01T06:11:4625          00000000033390æ8020UTPOST UR2302011-01-31343285958Kredit kontonummer ugyldig                        00963702833";
    private static final String UR_MELDING_UTEN_MAPPING_REGEL                        = "00837873282ORGANISASJON2011-02-01T06:11:4625          00000000304160æ8019ANDRUTBUR2302011-01-31343296727Feil bruk av KID/ugyldig KID                      00837873282";
    private static final String UR_MELDING_UTEN_BEHANDLINGSTEMA                      = UR_MELDING_UTEN_MAPPING_REGEL;
    private static final String UR_MELDING_UTEN_ANSVARLIG_ENHET_ID                   = UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL.replace("UTPOST", "INNOST");

    private static final String EXPECTED_BEHANDLINGSTEMA   = "";
    private static final String EXPECTED_BEHANDLINGSTYPE   = "ae0204";
    private static final String EXPECTED_ANSVARLIGENHET_ID = "4151";

    @BeforeEach
    void setUp() throws IOException {

        Mappingregelverk.init(Constants.BATCH_TYPE.UR.getMappingRulesPropertiesFileName());
        super.meldingSomSkalBliTilOppgave    = new UrMelding(UR_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL);
        super.meldingUtenMappingRegel        = new UrMelding(UR_MELDING_UTEN_MAPPING_REGEL);
        super.meldingWithoutBehandlingsTema  = new UrMelding(UR_MELDING_UTEN_BEHANDLINGSTEMA);
        super.meldingWithoutAnsvarligEnhetId = new UrMelding(UR_MELDING_UTEN_ANSVARLIG_ENHET_ID);

        super.expectedBehandlingstema        = EXPECTED_BEHANDLINGSTEMA;
        super.expectedBehandlingstype        = EXPECTED_BEHANDLINGSTYPE;
        super.expectedAnsvarligenhet_id      = EXPECTED_ANSVARLIGENHET_ID;
    }
}
