package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class UrMeldingFunksjonelleAggregeringsKriterierTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private UrMeldingFunksjonelleAggregeringsKriterier urMeldingFunksjonelleAggregeringsKriterier;
    private UrMelding urMelding;

    @BeforeEach
    void setUp() {
        urMelding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());
    }

    @Test
    @DisplayName("Når man sender inn samme UrMeldingFunksjonelleAggregeringsKriterier-objekt til equals-metoden skal disse være like")
    void sammeObjektErLike() {

        enteringTestHeaderLogger.debug(null);

        urMeldingFunksjonelleAggregeringsKriterier = new UrMeldingFunksjonelleAggregeringsKriterier(urMelding);

        assertEquals(urMeldingFunksjonelleAggregeringsKriterier, urMeldingFunksjonelleAggregeringsKriterier, "Det samme objektet er ikke likt seg selv");
    }

    @Test
    @DisplayName("Når man sender inn to UrMeldingFunksjonelleAggregeringsKriterier-objekt med samme verdier til equals-metoden skal disse være like")
    void objekterMedLiktInneholdErLike() {

        enteringTestHeaderLogger.debug(null);

        urMeldingFunksjonelleAggregeringsKriterier = new UrMeldingFunksjonelleAggregeringsKriterier(urMelding);

        assertEquals(urMeldingFunksjonelleAggregeringsKriterier, new UrMeldingFunksjonelleAggregeringsKriterier(urMelding), "To objekter med likt innhold er ikke like");
    }

    @Test
    @DisplayName("Når man sender inn to UrMeldingFunksjonelleAggregeringsKriterier-objekt med ulik gjelderId til equals-metoden skal disse ikke være like")
    void objekterMedForskjelligGjelderIdErLike() {

        enteringTestHeaderLogger.debug(null);

        urMeldingFunksjonelleAggregeringsKriterier = new UrMeldingFunksjonelleAggregeringsKriterier(urMelding);

        UrMelding annenUrMelding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding("01234567890"));

        assertNotEquals(urMeldingFunksjonelleAggregeringsKriterier, new UrMeldingFunksjonelleAggregeringsKriterier(annenUrMelding), "To objekter som skal være ulike er like");
    }
}
