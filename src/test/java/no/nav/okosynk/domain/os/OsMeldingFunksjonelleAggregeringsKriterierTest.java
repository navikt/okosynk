package no.nav.okosynk.domain.os;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.okosynk.domain.os.OsMelding;
import no.nav.okosynk.domain.os.OsMeldingTestGenerator;
import no.nav.okosynk.domain.os.OsMeldingFunksjonelleAggregeringsKriterier;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OsMeldingFunksjonelleAggregeringsKriterierTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private OsMeldingFunksjonelleAggregeringsKriterier osMeldingFunksjonelleAggregeringsKriterier;
    private OsMelding osMelding;

    @BeforeEach
    void setUp() {
        osMelding = new OsMelding(OsMeldingTestGenerator.OsMeldingForPerson.getMelding());
    }

    @Test
    @DisplayName("Når man sender inn samme OsMeldingFunksjonelleAggregeringsKriterier-objekt til equals-metoden skal disse være like")
    void sammeObjektErLike() {

        enteringTestHeaderLogger.debug(null);

        osMeldingFunksjonelleAggregeringsKriterier = new OsMeldingFunksjonelleAggregeringsKriterier(osMelding);

        assertTrue(osMeldingFunksjonelleAggregeringsKriterier.equals(osMeldingFunksjonelleAggregeringsKriterier), "Det samme objektet er ikke likt seg selv");
    }

    @Test
    @DisplayName("Når man sender inn to OsMeldingFunksjonelleAggregeringsKriterier-objekt med samme verdier til equals-metoden skal disse være like")
    void objekterMedLiktInneholdErLike() {

        enteringTestHeaderLogger.debug(null);

        osMeldingFunksjonelleAggregeringsKriterier = new OsMeldingFunksjonelleAggregeringsKriterier(osMelding);

        assertTrue(osMeldingFunksjonelleAggregeringsKriterier.equals(new OsMeldingFunksjonelleAggregeringsKriterier(osMelding)), "To objekter med likt innhold er ikke like");
    }

    @Test
    @DisplayName("Når man sender inn to OsMeldingFunksjonelleAggregeringsKriterier-objekt med ulik gjelderId til equals-metoden skal disse ikke være like")
    void objekterMedForskjelligGjelderIdErLike() {

        enteringTestHeaderLogger.debug(null);

        osMeldingFunksjonelleAggregeringsKriterier = new OsMeldingFunksjonelleAggregeringsKriterier(osMelding);

        OsMelding annenOsMelding = new OsMelding(OsMeldingTestGenerator.withGjelderIdOrganiasjon());

        assertFalse(osMeldingFunksjonelleAggregeringsKriterier.equals(new OsMeldingFunksjonelleAggregeringsKriterier(annenOsMelding)), "To objekter som skal være ulike er like");
    }
}
