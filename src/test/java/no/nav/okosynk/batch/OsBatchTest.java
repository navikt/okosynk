package no.nav.okosynk.batch;

import static org.mockito.Mockito.mock;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import no.nav.okosynk.domain.os.OsMapper;
import no.nav.okosynk.domain.os.OsMelding;
import no.nav.okosynk.domain.MeldingUnreadableException;
import no.nav.okosynk.domain.os.OsMeldingReader;
import no.nav.okosynk.io.LinjeUnreadableException;
import org.junit.jupiter.api.BeforeEach;

public class OsBatchTest extends BatchTest<OsMelding> {

    // =========================================================================
    @BeforeEach
    void setUp() throws MeldingUnreadableException, LinjeUnreadableException {
        this.setMeldingReader(mock(OsMeldingReader.class));
        this.setMeldingMapper(mock(OsMapper.class));
        //this.setInputFilePath();
        this.setBatch(
            new Batch<OsMelding>(
                this.getOkosynkConfiguration(),
                Constants.BATCH_TYPE.OS,
                getEksekveringsId(),
                new OsMeldingReader(OsMelding::new),
                new OsMapper(new AktoerRestClient(this.getOkosynkConfiguration(), Constants.BATCH_TYPE.OS))
            )
        );
        this.commonPostSetUp();
    }
    // =========================================================================
}
