package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.hentbatchoppgaver.model.OsMeldingTestGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

        assertEquals(osMeldingFunksjonelleAggregeringsKriterier, osMeldingFunksjonelleAggregeringsKriterier, "Det samme objektet er ikke likt seg selv");
    }

    @Test
    @DisplayName("Når man sender inn to OsMeldingFunksjonelleAggregeringsKriterier-objekt med samme verdier til equals-metoden skal disse være like")
    void objekterMedLiktInneholdErLike() {

        enteringTestHeaderLogger.debug(null);

        osMeldingFunksjonelleAggregeringsKriterier = new OsMeldingFunksjonelleAggregeringsKriterier(osMelding);

        assertEquals(osMeldingFunksjonelleAggregeringsKriterier, new OsMeldingFunksjonelleAggregeringsKriterier(osMelding), "To objekter med likt innhold er ikke like");
    }

    @Test
    @DisplayName("Når man sender inn to OsMeldingFunksjonelleAggregeringsKriterier-objekt med ulik gjelderId til equals-metoden skal disse ikke være like")
    void objekterMedForskjelligGjelderIdErLike() {

        enteringTestHeaderLogger.debug(null);

        osMeldingFunksjonelleAggregeringsKriterier = new OsMeldingFunksjonelleAggregeringsKriterier(osMelding);

        OsMelding annenOsMelding = new OsMelding(OsMeldingTestGenerator.withGjelderIdOrganiasjon());

        assertNotEquals(osMeldingFunksjonelleAggregeringsKriterier, new OsMeldingFunksjonelleAggregeringsKriterier(annenOsMelding), "To objekter som skal være ulike er like");
    }
}
