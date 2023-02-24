package no.nav.okosynk;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.UrMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class UrBatchTest extends AbstractBatchTest<UrMelding> {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    @BeforeEach
    void beforeEach() {

        super.setMockedMeldingReader(mock(MeldingReader.class));
        super.setMockedMeldingMapper(mock(UrMapper.class));

        super.setBatch(
                new Batch<>(
                        this.getOkosynkConfiguration(),
                        Constants.BATCH_TYPE.UR,
                        new MeldingReader<>(UrMelding::new),
                        new UrMapper(mock(IAktoerClient.class), getOkosynkConfiguration())
                )
        );
    }

    @Test
    void when_batch_is_created_with_null_configuration_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        MeldingReader<UrMelding> urMeldingMeldingReader = new MeldingReader<>(UrMelding::new);
        UrMapper urMapper = new UrMapper(mock(IAktoerClient.class), getOkosynkConfiguration());
        assertThrows(NullPointerException.class,
                () ->
                        new Batch<>(
                                null,
                                Constants.BATCH_TYPE.UR,
                                urMeldingMeldingReader,
                                urMapper
                        )
        );
    }

    @Test
    void when_batch_is_created_with_null_batch_type_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        IOkosynkConfiguration okosynkConfiguration = this.getOkosynkConfiguration();
        MeldingReader<UrMelding> urMeldingMeldingReader = new MeldingReader<>(UrMelding::new);
        UrMapper urMapper = new UrMapper(mock(IAktoerClient.class), okosynkConfiguration);
        assertThrows(NullPointerException.class,
                () ->
                        new Batch<>(
                                okosynkConfiguration,
                                null,
                                urMeldingMeldingReader,
                                urMapper
                        )
        );
    }

    @Test
    void when_batch_is_created_with_null_MeldingReader_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        IOkosynkConfiguration okosynkConfiguration = this.getOkosynkConfiguration();
        UrMapper urMapper = new UrMapper(mock(IAktoerClient.class), okosynkConfiguration);
        assertThrows(NullPointerException.class,
                () ->
                        new Batch<>(okosynkConfiguration, Constants.BATCH_TYPE.UR, null, urMapper)
        );
    }

    @Test
    void when_batch_is_created_with_null_Mapper_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        MeldingReader<UrMelding> urMeldingMeldingReader = new MeldingReader<>(UrMelding::new);
        IOkosynkConfiguration okosynkConfiguration = this.getOkosynkConfiguration();
        assertThrows(
                NullPointerException.class,
                () ->
                        new Batch<>(okosynkConfiguration, Constants.BATCH_TYPE.UR, urMeldingMeldingReader, null)
        );
    }

    @Override
    protected String getValidLineOfInputData() {
        return "***REMOVED***PERSON      2020-01-21T12:38:3724GKA2960   00000000006860A8020GHBATCHUR2302020-01-21001618071Manuell retur - fra bank                          ***REMOVED***";
    }

    @Override
    protected Constants.BATCH_TYPE getBatchType() {
        return Constants.BATCH_TYPE.UR;
    }
}