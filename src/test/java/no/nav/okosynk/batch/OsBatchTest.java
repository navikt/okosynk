package no.nav.okosynk.batch;

import static org.mockito.Mockito.mock;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.domain.os.OsMapper;
import no.nav.okosynk.domain.os.OsMelding;
import no.nav.okosynk.domain.MeldingUnreadableException;
import no.nav.okosynk.domain.os.OsMeldingReader;
import no.nav.okosynk.io.OkosynkIoException;
import org.junit.jupiter.api.BeforeEach;

class OsBatchTest extends AbstractBatchTest<OsMelding> {

    OsBatchTest() {
        super(Constants.BATCH_TYPE.OS);
    }

    // =========================================================================
    @BeforeEach
    void setUp() throws MeldingUnreadableException, OkosynkIoException {
        this.setMeldingReader(mock(OsMeldingReader.class));
        this.setMeldingMapper(mock(OsMapper.class));

        //this.setInputFilePath();
        this.setBatch(
            new Batch<>(
                this.getOkosynkConfiguration(),
                Constants.BATCH_TYPE.OS,
                getEksekveringsId(),
                new OsMeldingReader(OsMelding::new),
                new OsMapper(mock(AktoerRestClient.class))
            )
        );
        this.commonPostSetUp();
    }
    // =========================================================================
}
