package no.nav.okosynk;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.UrMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.nav.okosynk.config.Constants.OPPGAVE_URL_KEY;
import static no.nav.okosynk.config.Constants.OPPGAVE_USERNAME;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UrBatchTest extends AbstractBatchTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    @BeforeEach
    void beforeEach() throws ConfigureOrInitializeOkosynkIoException {
        OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);
        when(okosynkConfiguration.getString(OPPGAVE_URL_KEY)).thenReturn("https://oppgave.nais.adeo.no/api/v1/oppgaver");
        when(okosynkConfiguration.getString(OPPGAVE_USERNAME)).thenReturn("Executor");
        when(okosynkConfiguration.getNaisAppName()).thenReturn("okosynkur");
        when(okosynkConfiguration.getString(OPPGAVE_URL_KEY)).thenReturn("http://www.oppgave.no");
        super.setBatch(
                new Batch<>(
                        okosynkConfiguration,
                        Constants.BATCH_TYPE.UR,
                        new MeldingReader<>(UrMelding::new),
                        new UrMapper(mock(IAktoerClient.class))
                )
        );
    }

    @Test
    void when_batch_is_created_with_null_configuration_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        MeldingReader<UrMelding> urMeldingMeldingReader = new MeldingReader<>(UrMelding::new);
        UrMapper urMapper = new UrMapper(mock(IAktoerClient.class));
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

        OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);

        MeldingReader<UrMelding> urMeldingMeldingReader = new MeldingReader<>(UrMelding::new);
        UrMapper urMapper = new UrMapper(mock(IAktoerClient.class));
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

        OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);

        UrMapper urMapper = new UrMapper(mock(IAktoerClient.class));
        assertThrows(NullPointerException.class,
                () ->
                        new Batch<>(okosynkConfiguration, Constants.BATCH_TYPE.UR, null, urMapper)
        );
    }

    @Test
    void when_batch_is_created_with_null_Mapper_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        MeldingReader<UrMelding> urMeldingMeldingReader = new MeldingReader<>(UrMelding::new);
        OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);

        assertThrows(
                NullPointerException.class,
                () ->
                        new Batch<>(okosynkConfiguration, Constants.BATCH_TYPE.UR, urMeldingMeldingReader, null)
        );
    }

    @Override
    protected String getValidLineOfInputData() {
        return "01010112345PERSON      2020-01-21T12:38:3724GKA2960   00000000006860A8020GHBATCHUR2302020-01-21001618071Manuell retur - fra bank                          01010112345";
    }

    @Override
    protected Constants.BATCH_TYPE getBatchType() {
        return Constants.BATCH_TYPE.UR;
    }
}
