package no.nav.okosynk.consumer.oppgavebehandling;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.domain.Oppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppgaveBehandlingV3ServiceImplTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private OppgavebehandlingV3       oppgavebehandlingV3;
    private IOppgaveBehandlingConsumerGateway oppgaveBehandlingGateway;
    private IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

    @BeforeEach
    void setUp() {
        oppgavebehandlingV3      = mock(OppgavebehandlingV3.class);
        oppgaveBehandlingGateway = new OppgaveBehandlingConsumerV3ServiceImpl(this.okosynkConfiguration, oppgavebehandlingV3);
    }

    @Test
    void opprettOppgaverKallerOppgavebehandlingV3() {

        enteringTestHeaderLogger.debug(null);

        when(oppgavebehandlingV3.opprettOppgaveBolk(any(WSOpprettOppgaveBolkRequest.class))).thenReturn(
                new WSOpprettOppgaveBolkResponse().withOppgaveIdListe(new ArrayList<>()));

        oppgaveBehandlingGateway.opprettOppgaver(this.okosynkConfiguration, lagOppgaveliste(1));

        verify(oppgavebehandlingV3).opprettOppgaveBolk(any(WSOpprettOppgaveBolkRequest.class));

    }

    @Test
    void opprett_oppgaver_kaster_ikke_exception_hvis_tjenestekallet_feiler() {

        enteringTestHeaderLogger.debug(null);

        when(oppgavebehandlingV3.opprettOppgaveBolk(any(WSOpprettOppgaveBolkRequest.class))).thenThrow(new RuntimeException("Noe gikk galt"));

        assertDoesNotThrow(
            () ->
                oppgaveBehandlingGateway
                    .opprettOppgaver(
                        this.okosynkConfiguration,
                        lagOppgaveliste(1)
                    )
        );
    }

    @Test
    void opprettOppgaverGjorIngenKallForTomListe() {

        enteringTestHeaderLogger.debug(null);

        oppgaveBehandlingGateway.opprettOppgaver(this.okosynkConfiguration, lagOppgaveliste(0));

        verify(oppgavebehandlingV3, never()).opprettOppgaveBolk(any(WSOpprettOppgaveBolkRequest.class));
    }

    @Test
    void opprettOppgaveGjorFlereKallForOppdeltListe() {
        when(oppgavebehandlingV3.opprettOppgaveBolk(any(WSOpprettOppgaveBolkRequest.class))).thenReturn(
                new WSOpprettOppgaveBolkResponse().withOppgaveIdListe(new ArrayList<>()));

        oppgaveBehandlingGateway
            .opprettOppgaver(
                this.okosynkConfiguration,
                lagOppgaveliste(getMaxAntallOppgaverForOpprettingPerRequest(this.okosynkConfiguration) + 1));

        verify(oppgavebehandlingV3, times(2)).opprettOppgaveBolk(any(WSOpprettOppgaveBolkRequest.class));
    }

    private int getMaxAntallOppgaverForOpprettingPerRequest(
        final IOkosynkConfiguration okosynkConfiguration) {

        final int maxAntallOppgaverForOpprettingPerRequest =
            parseInt(
                okosynkConfiguration
                    .getString(
                        Constants
                            .CONSUMER_TYPE.OPPGAVE_BEHANDLING
                            .getBulkSizeMaxForCreate()
                        , Constants
                            .CONSUMER_TYPE.OPPGAVE_BEHANDLING
                            .getBulkSizeDefaultForCreate()
                    )
            );

        return maxAntallOppgaverForOpprettingPerRequest;
    }

    @Test
    void oppdaterOppgaverKallerOppgavebehandlingV3() {

        enteringTestHeaderLogger.debug(null);

        when(oppgavebehandlingV3.lagreOppgaveBolk(any(WSLagreOppgaveBolkRequest.class))).thenReturn(
                new WSLagreOppgaveBolkResponse().withTransaksjonOk(true));

        oppgaveBehandlingGateway.oppdaterOppgaver(this.okosynkConfiguration, lagOppgaveliste(1));

        verify(oppgavebehandlingV3).lagreOppgaveBolk(any(WSLagreOppgaveBolkRequest.class));

    }

    @Test
    void oppdater_oppgaver_kaster_ikke_exception_for_feilet_request() {

        enteringTestHeaderLogger.debug(null);

        when(oppgavebehandlingV3.lagreOppgaveBolk(any(WSLagreOppgaveBolkRequest.class))).thenReturn(opprettFeilresponsForLagreOppgaveOperasjon());

        assertDoesNotThrow(() ->
            oppgaveBehandlingGateway.oppdaterOppgaver(this.okosynkConfiguration, lagOppgaveliste(1))
        );
    }

    private WSLagreOppgaveBolkResponse opprettFeilresponsForLagreOppgaveOperasjon() {
        return new WSLagreOppgaveBolkResponse().withTransaksjonOk(false);
    }

    @Test
    void oppdater_oppgaver_kaster_ikke_exception_hvis_tjenestekallet_feiler() {

        enteringTestHeaderLogger.debug(null);

        when(oppgavebehandlingV3.lagreOppgaveBolk(any(WSLagreOppgaveBolkRequest.class))).thenThrow(new RuntimeException("Noe gikk galt"));

        assertDoesNotThrow(
            () ->
                oppgaveBehandlingGateway
                    .oppdaterOppgaver(this.okosynkConfiguration, lagOppgaveliste(1))
        );
    }

    @Test
    void oppdatereOppgaverGjorIngenKallForTomListe() {

        enteringTestHeaderLogger.debug(null);

        oppgaveBehandlingGateway.oppdaterOppgaver(this.okosynkConfiguration, lagOppgaveliste(0));

        verify(oppgavebehandlingV3, never()).lagreOppgaveBolk(any(WSLagreOppgaveBolkRequest.class));
    }

    @Test
    void oppdaterOppgaveGjorFlereKallForOppdeltListe() {

        enteringTestHeaderLogger.debug(null);

        when(oppgavebehandlingV3.lagreOppgaveBolk(any(WSLagreOppgaveBolkRequest.class))).thenReturn(
                new WSLagreOppgaveBolkResponse().withTransaksjonOk(true));

        oppgaveBehandlingGateway
            .oppdaterOppgaver(
                this.okosynkConfiguration,
                lagOppgaveliste(getMaxAntallOppgaverForOppdateringPerRequest(this.okosynkConfiguration) + 1));

        verify(oppgavebehandlingV3, times(2)).lagreOppgaveBolk(any(WSLagreOppgaveBolkRequest.class));
    }

    @Test
    void ferdigstillOppgaverKallerOppgavebehandlingV3() {

        enteringTestHeaderLogger.debug(null);

        when(oppgavebehandlingV3.ferdigstillOppgaveBolk(any(WSFerdigstillOppgaveBolkRequest.class))).thenReturn(
                new WSFerdigstillOppgaveBolkResponse().withTransaksjonOk(true));

        oppgaveBehandlingGateway.ferdigstillOppgaver(lagOppgaveliste(1));

        verify(oppgavebehandlingV3).ferdigstillOppgaveBolk(any(WSFerdigstillOppgaveBolkRequest.class));

    }

    @Test
    void ferdigstill_oppgaver_kaster_ikke_exception_for_feilet_request() {

        enteringTestHeaderLogger.debug(null);

        when(oppgavebehandlingV3.ferdigstillOppgaveBolk(any(WSFerdigstillOppgaveBolkRequest.class))).thenReturn(opprettFeilresponsForFerdigstillOppgaveOperasjon());

        assertDoesNotThrow(
            () ->
                oppgaveBehandlingGateway
                    .ferdigstillOppgaver(lagOppgaveliste(1))
        );
    }

    private WSFerdigstillOppgaveBolkResponse opprettFeilresponsForFerdigstillOppgaveOperasjon() {
        return new WSFerdigstillOppgaveBolkResponse().withTransaksjonOk(false);
    }

    @Test
    void ferdigstill_oppgaver_kaster_ikke_exception_hvis_tjenestekallet_feiler() {

        enteringTestHeaderLogger.debug(null);

        when(oppgavebehandlingV3
                .ferdigstillOppgaveBolk(any(WSFerdigstillOppgaveBolkRequest.class)))
            .thenThrow(new RuntimeException("Noe gikk galt"));

        assertDoesNotThrow(() -> oppgaveBehandlingGateway.ferdigstillOppgaver(lagOppgaveliste(1)));
    }

    @Test
    void ferdigstillOppgaverGjorIngenKallForTomListe() {

        enteringTestHeaderLogger.debug(null);

        oppgaveBehandlingGateway.ferdigstillOppgaver(lagOppgaveliste(0));

        verify(oppgavebehandlingV3, never()).ferdigstillOppgaveBolk(any(WSFerdigstillOppgaveBolkRequest.class));
    }

    @Test
    void ferdigstillOppgaveGjorFlereKallForOppdeltListe() {

        enteringTestHeaderLogger.debug(null);

        when(oppgavebehandlingV3.ferdigstillOppgaveBolk(any(WSFerdigstillOppgaveBolkRequest.class))).thenReturn(
                new WSFerdigstillOppgaveBolkResponse().withTransaksjonOk(true));

        oppgaveBehandlingGateway
            .ferdigstillOppgaver(
                lagOppgaveliste(
                    OppgaveBehandlingConsumerV3ServiceImpl.MAX_ANTALL_OPPGAVER_FOR_FERDIGSTILLING_PER_REQUEST + 1));

        verify(oppgavebehandlingV3, times(2)).ferdigstillOppgaveBolk(any(WSFerdigstillOppgaveBolkRequest.class));
    }

    private Collection<Oppgave> lagOppgaveliste(int antallOppgaver) {
        Collection<Oppgave> oppgaveliste = new ArrayList<>();

        final Oppgave oppgave = lagOppgave();
        for (int i = 0; i < antallOppgaver; i++) {
            oppgaveliste.add(oppgave);
        }

        return oppgaveliste;
    }

    private Oppgave lagOppgave() {
        return new Oppgave(new Oppgave.OppgaveBuilder()
                .withOppgaveId("1")
                .withBrukerId("10108000398")
                .withBrukertypeKode("PERSON")
                .withOppgavetypeKode("OKO_UR")
                .withFagomradeKode("OKO").withPrioritetKode("LAV").withUnderkategoriKode("INNT")
                .withBeskrivelse("Blabla")
                .withAktivFra(LocalDate.of(2000, 1, 1))
                .withAktivTil(LocalDate.of(2000, 2, 1))
                .withAnsvarligEnhetId("2990")
                .withLest(true));
    }

    private int getMaxAntallOppgaverForOppdateringPerRequest(
        final IOkosynkConfiguration okosynkConfiguration) {

        final int maxAntallOppgaverForOpprettingPerRequest =
            parseInt(
                okosynkConfiguration
                    .getString(
                        Constants
                            .CONSUMER_TYPE.OPPGAVE_BEHANDLING
                            .getBulkSizeMaxForUpdate()
                        , Constants
                            .CONSUMER_TYPE.OPPGAVE_BEHANDLING
                            .getBulkSizeDefaultForUpdate()
                    )
            );

        return maxAntallOppgaverForOpprettingPerRequest;
    }
}
