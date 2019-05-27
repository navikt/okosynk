package no.nav.okosynk.batch;

import static org.mockito.Mockito.mock;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.domain.ur.UrMapper;
import no.nav.okosynk.domain.ur.UrMelding;
import no.nav.okosynk.domain.MeldingUnreadableException;
import no.nav.okosynk.domain.ur.UrMeldingReader;
import no.nav.okosynk.io.LinjeUnreadableException;
import org.junit.jupiter.api.BeforeEach;

public class UrBatchTest extends BatchTest<UrMelding> {

    // =========================================================================
    @BeforeEach
    void setUp() throws MeldingUnreadableException, LinjeUnreadableException {
        this.setMeldingReader(mock(UrMeldingReader.class));
        this.setMeldingMapper(mock(UrMapper.class));
        //this.setInputFilePath();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        this.setBatch(
            new Batch<UrMelding>(
                okosynkConfiguration,
                Constants.BATCH_TYPE.UR,
                getEksekveringsId(),
                new UrMeldingReader(UrMelding::new),
                new UrMapper(mock(AktoerRestClient.class))
            )
        );
        this.commonPostSetUp();
    }
    // =========================================================================
}
