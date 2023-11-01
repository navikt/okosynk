package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.Mappingregelverk;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.MappingregelverkTest;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import org.junit.jupiter.api.BeforeEach;

class OsMappingRegelRepositoryTest extends MappingregelverkTest<OsMelding> {

    private static final String OS_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL = "02029912345000000002 2008-10-102008-10-10NEG A123B1232008-11-012008-11-30000000001600å 4151         GS      02029912345            ";
    private static final String OS_MELDING_UTEN_MAPPING_REGEL                        = "01010012345000000001 2009-11-062009-11-30AVVEAAA1234 2009-11-012009-11-30000000015000æ 8019         HELSEREF01010012345            ";
    private static final String OS_MELDING_UTEN_BEHANDLINGSTEMA                      = "01010012345000000001 2009-11-062009-11-30AVVEAAA1234 2009-11-012009-11-30000000015000æ 1234         ABCDEFGH01010012345            ";
    private static final String OS_MELDING_UTEN_ANSVARLIG_ENHET_ID                   = OS_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL.replace("4151", "9797");

    private static final String EXPECTED_BEHANDLINGSTEMA   = "ab0155";
    private static final String EXPECTED_BEHANDLINGSTYPE   = "";
    private static final String EXPECTED_ANSVARLIGENHET_ID = "4151";

    @BeforeEach
    void setUp() {

        super.mappingRegelRepository         = new Mappingregelverk(Constants.BATCH_TYPE.OS.getMappingRulesPropertiesFileName());
        super.meldingSomSkalBliTilOppgave    = new OsMelding(OS_MELDING_SOM_IKKE_GJELDER_TSS_OG_HAR_MAPPING_REGEL);
        super.meldingUtenMappingRegel        = new OsMelding(OS_MELDING_UTEN_MAPPING_REGEL);
        super.meldingWithoutBehandlingsTema  = new OsMelding(OS_MELDING_UTEN_BEHANDLINGSTEMA);
        super.meldingWithoutAnsvarligEnhetId = new OsMelding(OS_MELDING_UTEN_ANSVARLIG_ENHET_ID);

        super.expectedBehandlingstema        = EXPECTED_BEHANDLINGSTEMA;
        super.expectedBehandlingstype        = EXPECTED_BEHANDLINGSTYPE;
        super.expectedAnsvarligenhet_id      = EXPECTED_ANSVARLIGENHET_ID;
    }
}
