package no.nav.okosynk;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.OsMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class OsBatchTest extends AbstractBatchTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    @BeforeEach
    void beforeEach() {

        super.setBatch(
                new Batch<>(
                        this.getOkosynkConfiguration(),
                        Constants.BATCH_TYPE.OS,
                        new MeldingReader<>(OsMelding::new),
                        new OsMapper(mock(IAktoerClient.class))
                )
        );
    }

    @Test
    void when_batch_is_created_with_null_configuration_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        MeldingReader<OsMelding> osMeldingMeldingReader = new MeldingReader<>(OsMelding::new);
        OsMapper osMapper = new OsMapper(mock(IAktoerClient.class));
        assertThrows(NullPointerException.class,
                () ->
                        new Batch<>(null, Constants.BATCH_TYPE.OS, osMeldingMeldingReader, osMapper)
        );
    }

    @Test
    void when_batch_is_created_with_null_batch_type_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        MeldingReader<OsMelding> osMeldingMeldingReader = new MeldingReader<>(OsMelding::new);
        IOkosynkConfiguration okosynkConfiguration = this.getOkosynkConfiguration();
        OsMapper osMapper = new OsMapper(mock(IAktoerClient.class));
        assertThrows(NullPointerException.class, () ->
                new Batch<>(okosynkConfiguration, null, osMeldingMeldingReader, osMapper)
        );
    }

    @Test
    void when_batch_is_created_with_null_MeldingReader_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        IOkosynkConfiguration okosynkConfiguration = this.getOkosynkConfiguration();
        OsMapper osMapper = new OsMapper(mock(IAktoerClient.class));
        assertThrows(NullPointerException.class,
                () -> new Batch<>(okosynkConfiguration, Constants.BATCH_TYPE.OS, null, osMapper));
    }

    @Test
    void when_batch_is_created_with_null_Mapper_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        IOkosynkConfiguration okosynkConfiguration = this.getOkosynkConfiguration();
        MeldingReader<OsMelding> osMeldingMeldingReader = new MeldingReader<>(OsMelding::new);

        assertThrows(
                NullPointerException.class,
                () ->
                        new Batch<>(
                                okosynkConfiguration,
                                Constants.BATCH_TYPE.OS,
                                osMeldingMeldingReader,
                                null
                        )
        );
    }

    @Override
    protected String getValidLineOfInputData() {
        return "01010112345366572769 2019-12-232019-12-23AVVED128555 2019-12-012019-12-31000000001040A 4819         PENPOST 01010112345                                                                                           ";
    }

    @Override
    protected Constants.BATCH_TYPE getBatchType() {
        return Constants.BATCH_TYPE.OS;
    }
}
