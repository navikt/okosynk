package no.nav.okosynk.batch;

import static org.mockito.Mockito.mock;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.domain.ur.UrMapper;
import no.nav.okosynk.domain.ur.UrMelding;
import no.nav.okosynk.domain.MeldingUnreadableException;
import no.nav.okosynk.domain.ur.UrMeldingReader;
import no.nav.okosynk.io.OkosynkIoException;
import org.junit.jupiter.api.BeforeEach;

class UrBatchTest extends AbstractBatchTest<UrMelding> {

    UrBatchTest() {
        super(Constants.BATCH_TYPE.UR);
    }

    // =========================================================================
    @BeforeEach
    void setUp() throws MeldingUnreadableException, OkosynkIoException {

        getOkosynkConfiguration().setSystemProperty(
            Constants.FILE_READER_MAX_NUMBER_OF_READ_TRIES_KEY,
            "2");
        getOkosynkConfiguration().setSystemProperty(
            Constants.FILE_READER_RETRY_WAIT_TIME_IN_MILLISECONDS_KEY,
            "1000");
        this.setMeldingReader(mock(UrMeldingReader.class));
        this.setMeldingMapper(mock(UrMapper.class));
        //this.setInputFilePath();

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        this.setBatch(
            new Batch<>(
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