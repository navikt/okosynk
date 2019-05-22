package no.nav.okosynk.batch.ur;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import no.nav.okosynk.batch.Batch;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.domain.MeldingUnreadableException;
import no.nav.okosynk.domain.Oppgave;
import no.nav.okosynk.domain.ur.UrMapper;
import no.nav.okosynk.domain.ur.UrMelding;
import no.nav.okosynk.domain.ur.UrMeldingReader;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.LinjeUnreadableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrBatchBlackBoxTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final long EKSEKVERINGS_ID = 0;
    private static final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

    private Batch<UrMelding> batch;
    private String inputFilPathMock = "mypath";

    private IMeldingLinjeFileReader meldingReaderMock =
        mock(IMeldingLinjeFileReader.class);

    @BeforeEach
    void setUp() throws MeldingUnreadableException {

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        batch =
            new Batch<UrMelding>(
                okosynkConfiguration,
                Constants.BATCH_TYPE.UR,
                EKSEKVERINGS_ID,
                new OppgaveRestClient(okosynkConfiguration, Constants.BATCH_TYPE.UR),
                new UrMeldingReader(UrMelding::new),
                new UrMapper());
        batch.setMeldingLinjeReader(meldingReaderMock);
    }

//    @Test
//    void lagOppgaveAvUfiltrertMelding() throws LinjeUnreadableException {
//
//        enteringTestHeaderLogger.debug(null);
//
//        final String input = "01018012345PERSON      2017-06-21T09:28:2824MWB2960   00000000000" +
//                "790æ4819PEN    UR2302017-06-21001548316Manuell retur - fra bank                          01018012345";
//        when(meldingReaderMock.read()).thenReturn(lagLinjer(input));
//        final ArgumentCaptor<Collection<Oppgave>> captor =
//            ArgumentCaptor.forClass((Class)Collection.class);
//
//        batch.run();
//
//        verify(mockedOppgaveBehandlingGateway, times(1)).opprettOppgaver(any(), captor.capture());
//        final Collection<Oppgave> oppgaver = captor.getValue();
//        final Oppgave oppgave = oppgaver.stream().findFirst().orElse(null);
//        assertTrue(oppgave.beskrivelse.contains("79kr"));
//
//    }

    private List<String> lagLinjer(String input){
        final List<String> linjer = new ArrayList<>();
        linjer.add(input);
        return linjer;
    }
}
