package no.nav.okosynk.consumer.oppgave;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.consumer.util.DatoKonverterer;
import no.nav.okosynk.domain.Oppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.*;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OppgaveV3ServiceImplTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String OPPRETTET_AV    = "Ident";
    private static final String OPPGAVETYPEKODE = "OKO_OS";
    private OppgaveV3 oppgaveV3;
    private IOppgaveConsumerGateway oppgaveGateway;

    @BeforeEach
    void setUp() {
        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        this.oppgaveV3      = mock(OppgaveV3.class);
        this.oppgaveGateway = new OppgaveConsumerV3ServiceImpl(okosynkConfiguration, oppgaveV3);
    }

    @Test
    void finn_oppgaver_returnerer_en_oppgave_hvis_oppgave_applikasjonen_returnerer_en_oppgave() {

        enteringTestHeaderLogger.debug(null);

        when(oppgaveV3.finnOppgaveListe(any(WSFinnOppgaveListeRequest.class))).thenReturn(new WSFinnOppgaveListeResponse()
                .withOppgaveListe(lagWsOppgaveliste())
                .withTotaltAntallTreff(1));

        final Collection<Oppgave> funneOppgaver = new ArrayList<>();
        final ConsumerStatistics consumerStatistics = oppgaveGateway.finnOppgaver(OPPRETTET_AV, funneOppgaver);

        assertNotNull(funneOppgaver);
        assertEquals(1, funneOppgaver.size(), "Oppgavelisten skal inneholde en oppgave");
    }

    @Test
    void finn_oppgaver_returnerer_tom_liste_hvis_oppgave_applikasjonen_ikke_returnerer_noen_oppgaver() {

        enteringTestHeaderLogger.debug(null);

        final WSFinnOppgaveListeResponse wsFinnOppgaveListeResponse =
            new WSFinnOppgaveListeResponse()
            .withOppgaveListe(new ArrayList<>())
            .withTotaltAntallTreff(0);

        when(
            oppgaveV3
                .finnOppgaveListe(
                    any(WSFinnOppgaveListeRequest.class)
                )
        )
        .thenReturn(wsFinnOppgaveListeResponse)
        ;

        final Collection<Oppgave> funneOppgaver = new ArrayList<>();
        final ConsumerStatistics consumerStatistics =
            oppgaveGateway.finnOppgaver(OPPRETTET_AV, funneOppgaver);

        assertNotNull(funneOppgaver);
        assertTrue(funneOppgaver.isEmpty(), "Oppgavelisten skal være tom");
    }

    @Test
    void finnOppgaverKasterExceptionHvisTjenestekalletFeiler() {

        enteringTestHeaderLogger.debug(null);

        when(oppgaveV3.finnOppgaveListe(any(WSFinnOppgaveListeRequest.class))).thenThrow(new RuntimeException("Noe gikk galt"));

        final Collection<Oppgave> funneOppgaver = new ArrayList<>();
        assertThrows(Exception.class, () -> oppgaveGateway.finnOppgaver(OPPRETTET_AV, funneOppgaver));
    }

    private Collection<WSOppgave> lagWsOppgaveliste() {
        Collection<WSOppgave> oppgaveliste = new ArrayList<>();
        WSOppgave wsOppgave = new WSOppgave().withOppgaveId("1")
                .withGjelder(new WSBruker().withBrukerId("10108000398"))
                .withLest(false)
                .withVersjon(1)
                .withSporing(new WSSporing().withEndretInfo(new WSSporingsdetalj()
                        .withDato(DatoKonverterer.konverterLocalDateTimeTilXMLGregorianCalendar(
                                LocalDateTime.of(2000, 1, 1, 1, 0, 00)))));
        oppgaveliste.add(wsOppgave);
        return oppgaveliste;
    }
}
