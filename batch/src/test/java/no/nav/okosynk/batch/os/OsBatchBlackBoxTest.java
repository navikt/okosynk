package no.nav.okosynk.batch.os;

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
import no.nav.okosynk.domain.os.OsMapper;
import no.nav.okosynk.domain.os.OsMelding;
import no.nav.okosynk.domain.os.OsMeldingReader;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.LinjeUnreadableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsBatchBlackBoxTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final long EKSEKVERINGS_ID = 0;

    private static final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

    private Batch<OsMelding> batch;

    private IMeldingLinjeFileReader meldingReaderMock =
        mock(IMeldingLinjeFileReader.class);

    @BeforeEach
    void setUp() throws MeldingUnreadableException {

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        batch =
            new Batch<OsMelding>(
                okosynkConfiguration,
                Constants.BATCH_TYPE.OS,
                EKSEKVERINGS_ID,
                new OppgaveRestClient(okosynkConfiguration),
                new OsMeldingReader(OsMelding::new),
                new OsMapper());
        batch.setMeldingLinjeReader(meldingReaderMock);
    }

//    @Test
//    void lagOppgaveAvUfiltrertMelding() throws LinjeUnreadableException {
//
//        enteringTestHeaderLogger.debug(null);
//
//        final String input = "10108000398012345678 2015-07-212015-07-22AVVED133832 2015-07-012015-07-310000000" +
//                "19400æ 8020         BA      10108000398            ";
//        when(meldingReaderMock.read()).thenReturn(lagLinjer(input));
//        final ArgumentCaptor<Collection<Oppgave>> captor =
//            ArgumentCaptor.forClass((Class)Collection.class);
//
//        batch.run();
//
//        verify(mockedOppgaveBehandlingGateway, times(1)).opprettOppgaver(any(), captor.capture());
//        final Collection<Oppgave> oppgaver = captor.getValue();
//        final Oppgave oppgave = oppgaver.stream().findFirst().orElse(null);
//        Assertions.assertTrue(oppgave.beskrivelse.contains("1940kr"));
//
//    }

    private List<String> lagLinjer(String input){
        final List<String> linjer = new ArrayList<>();
        linjer.add(input);
        return linjer;
    }
}
