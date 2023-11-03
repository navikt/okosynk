package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.UrMeldingFunksjonelleAggregeringsKriterier;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UrMeldingFunksjonelleAggregeringsKriterierTest {

    private UrMeldingFunksjonelleAggregeringsKriterier urMeldingFunksjonelleAggregeringsKriterier;
    private UrMelding urMelding;

    @BeforeEach
    void setUp() {
        urMelding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding());
    }

    @Test
    @DisplayName("Når man sender inn to UrMeldingFunksjonelleAggregeringsKriterier-objekt med samme verdier til equals-metoden skal disse være like")
    void objekterMedLiktInneholdErLike() {

        urMeldingFunksjonelleAggregeringsKriterier = new UrMeldingFunksjonelleAggregeringsKriterier(urMelding);

        assertEquals(urMeldingFunksjonelleAggregeringsKriterier, new UrMeldingFunksjonelleAggregeringsKriterier(urMelding), "To objekter med likt innhold er ikke like");
    }

    @Test
    @DisplayName("Når man sender inn to UrMeldingFunksjonelleAggregeringsKriterier-objekt med ulik gjelderId til equals-metoden skal disse ikke være like")
    void objekterMedForskjelligGjelderIdErLike() {

        urMeldingFunksjonelleAggregeringsKriterier = new UrMeldingFunksjonelleAggregeringsKriterier(urMelding);

        UrMelding annenUrMelding = new UrMelding(UrMeldingTestGenerator.EksempelMelding.getMelding("01234567890"));

        assertNotEquals(urMeldingFunksjonelleAggregeringsKriterier, new UrMeldingFunksjonelleAggregeringsKriterier(annenUrMelding), "To objekter som skal være ulike er like");
    }
}
