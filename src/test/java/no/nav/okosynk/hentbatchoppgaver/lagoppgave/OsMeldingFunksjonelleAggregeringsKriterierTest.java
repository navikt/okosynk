package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.AggregeringsKriterier;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.hentbatchoppgaver.model.OsMeldingTestGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class OsMeldingFunksjonelleAggregeringsKriterierTest {

    private AggregeringsKriterier osMeldingFunksjonelleAggregeringsKriterier;
    private OsMelding osMelding;

    @BeforeEach
    void setUp() {
        osMelding = new OsMelding(OsMeldingTestGenerator.OsMeldingForPerson.getMelding());
    }

    @Test
    @DisplayName("Når man sender inn to OsMeldingFunksjonelleAggregeringsKriterier-objekt med samme verdier til equals-metoden skal disse være like")
    void objekterMedLiktInneholdErLike() {

        osMeldingFunksjonelleAggregeringsKriterier = new AggregeringsKriterier(osMelding);

        assertEquals(osMeldingFunksjonelleAggregeringsKriterier, new AggregeringsKriterier(osMelding), "To objekter med likt innhold er ikke like");
    }

    @Test
    @DisplayName("Når man sender inn to OsMeldingFunksjonelleAggregeringsKriterier-objekt med ulik gjelderId til equals-metoden skal disse ikke være like")
    void objekterMedForskjelligGjelderIdErLike() {

        osMeldingFunksjonelleAggregeringsKriterier = new AggregeringsKriterier(osMelding);

        OsMelding annenOsMelding = new OsMelding(OsMeldingTestGenerator.withGjelderIdOrganiasjon());

        assertNotEquals(osMeldingFunksjonelleAggregeringsKriterier, new AggregeringsKriterier(annenOsMelding), "To objekter som skal være ulike er like");
    }
}
