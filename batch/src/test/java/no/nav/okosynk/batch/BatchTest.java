package no.nav.okosynk.batch;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.oppgave.IOppgaveConsumerGateway;
import no.nav.okosynk.consumer.oppgavebehandling.IOppgaveBehandlingConsumerGateway;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.IMeldingReader;
import no.nav.okosynk.domain.MeldingUnreadableException;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.LinjeUnreadableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BatchTest<SPESIFIKKMELDINGTYPE extends AbstractMelding> {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final long EKSEKVERINGS_ID = 0;

    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    private Batch batch;

    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    private IMeldingReader<SPESIFIKKMELDINGTYPE> meldingReader;

    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    private IMeldingMapper<SPESIFIKKMELDINGTYPE> meldingMapper;

    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    private String fsInputFilePathKey;

    @Getter(AccessLevel.PROTECTED)
    private final IOppgaveConsumerGateway mockedOppgaveGateway =
        mock(IOppgaveConsumerGateway.class);

    @Getter(AccessLevel.PROTECTED)
    private final IOppgaveBehandlingConsumerGateway mockedOppgaveBehandlingGateway =
        mock(IOppgaveBehandlingConsumerGateway.class);

    @Getter(AccessLevel.PRIVATE)
    private final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
        mock(IMeldingLinjeFileReader.class);

    @Getter(AccessLevel.PRIVATE)
    private final OppgaveSynkroniserer oppgaveSynkroniserer =
        mock(OppgaveSynkroniserer.class);

    @Getter(AccessLevel.PROTECTED)
    final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
    // =========================================================================
    protected static long getEksekveringsId() {
        return EKSEKVERINGS_ID;
    }
    // =========================================================================

    protected void commonPostSetUp() throws LinjeUnreadableException, MeldingUnreadableException {

        getBatch().setMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
        getBatch().setSpesifikkMeldingReader(meldingReader);
        getBatch().setSpesifikkMapper(meldingMapper);
        getBatch().setOppgaveSynkroniserer(oppgaveSynkroniserer);

        when(mockedUspesifikkMeldingLinjeReader.read())
            .thenReturn(emptyList());
        when(meldingReader.opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(anyCollection().stream()))
            .thenReturn(new ArrayList<>());
        when(meldingMapper.lagOppgaver(anyList()))
            .thenReturn(new ArrayList());
    }

    // =========================================================================

    @Test
    @DisplayName("Kaster IllegalStateException hvis filområde ikke er satt som system property")
    void batchStatusSetToFeilIfUspesifikkMeldingLinjeFileReaderIsNotOk() {

        enteringTestHeaderLogger.debug(null);

        getBatch().setMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
        when(
            mockedUspesifikkMeldingLinjeReader.getStatus()
        )
        .thenReturn(IMeldingLinjeFileReader.Status.ERROR);

        Assertions.assertEquals(BatchStatus.FEIL, getBatch().getStatus());
    }

    @Test
    @DisplayName("Når batchen har kjørt ferdig skal den ha status FULLFØRT")
    void runSetterStatusTilFullfortNarBatchErFerdig() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        getBatch().run();

        assertEquals(BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL, getBatch().getStatus());
    }

    @Test
    void runLeserFraFil() throws LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        getBatch().run();

        verify(mockedUspesifikkMeldingLinjeReader).read();
    }

    @Test
    void runOppretterUrMeldingerFraFil() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        getBatch().run();

        verify(meldingReader).opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(anyCollection().stream());
    }

    @Test
    void statusSettesTilFeilHvisLesingFraFilFeiler() throws LinjeUnreadableException {

        enteringTestHeaderLogger.debug(null);

        when(mockedUspesifikkMeldingLinjeReader.read())
            .thenThrow(new LinjeUnreadableException(new IOException("Noe gikk skeis")));

        getBatch().run();

        assertEquals(BatchStatus.FEIL, getBatch().getStatus());
    }

    @Test
    void runOppretterMeldingerFraFil() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        getBatch().run();

        verify(meldingReader).opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(anyCollection().stream());
    }

    @Test
    void statusSettesTilFeilHvisOpprettingAvMeldingFeiler() throws MeldingUnreadableException {

        enteringTestHeaderLogger.debug(null);

        when(meldingReader.opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(anyCollection().stream()))
            .thenThrow(new MeldingUnreadableException(new IOException("Noe gikk skeis")));

        getBatch().run();

        assertEquals(BatchStatus.FEIL, getBatch().getStatus());
    }

    @Test
    void runOppretterLokaleOppgaverFraMeldinger() {

        enteringTestHeaderLogger.debug(null);

        getBatch().run();

        verify(meldingMapper).lagOppgaver(anyList());
    }

    @Test
    void runSynkronisererOppgaverMotGsak() {

        enteringTestHeaderLogger.debug(null);

        getBatch().run();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        verify(oppgaveSynkroniserer).synkroniser(any(), anyCollection(), anyString());
    }

    @Test
    void stoppSetterStatusTilStoppet() {

        enteringTestHeaderLogger.debug(null);

        getBatch().stopp();

        assertEquals(BatchStatus.STOPPET, getBatch().getStatus());
    }

    @Test
    void setStatusOverskriverIkkeStatusDersomBatchErStoppet() {

        enteringTestHeaderLogger.debug(null);

        getBatch().stopp();
        getBatch().setStatus(BatchStatus.FEIL);

        assertEquals(BatchStatus.STOPPET, getBatch().getStatus());
    }

    @Test
    @DisplayName("Assert that a null pointer exception is thrown when trying to inject null for uspesifikkMeldingLinjeReader")
    void setUspesifikkMeldingLinjeReaderToNull() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(NullPointerException.class, () -> {
            getBatch().setMeldingLinjeReader(null);
        });
    }
}
