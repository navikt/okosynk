package no.nav.okosynk;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.IMeldingLinjeFileReader;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.IMeldingLinjeFileReader.Status;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.*;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.synkroniserer.OppgaveSynkroniserer;
import no.nav.okosynk.synkroniserer.consumer.ConsumerStatistics;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

public abstract class AbstractBatchTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBatchTest.class);

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private Batch<? extends AbstractMelding> batch;

    private final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

    public IOkosynkConfiguration getOkosynkConfiguration() {
        return this.okosynkConfiguration;
    }

    @Test
    void when_batch_is_successfully_run_with_no_input_file_then_the_batch_status_should_be_set_to_warning_for_not_found_retries_exceeded()
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException,
            EncodingOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);
        doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();
        doThrow(new NotFoundOkosynkIoException(null, null)).when(mockedUspesifikkMeldingLinjeReader).read();

        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);

        assertDoesNotThrow(() -> batch.run());
        assertEquals(BatchStatus.ENDED_WITH_WARNING_INPUT_DATA_NOT_FOUND, batch.getBatchStatus());
    }

    @Test
    void when_batch_is_unsuccessfully_run_with_io_error_then_the_batch_status_should_be_set_to_warning_for_not_found_retries_exceeded()
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException, EncodingOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);
        doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();
        doThrow(new IoOkosynkIoException(null, null)).when(mockedUspesifikkMeldingLinjeReader).read();

        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);

        assertDoesNotThrow(() -> batch.run());
        assertEquals(BatchStatus.ENDED_WITH_ERROR_GENERAL, batch.getBatchStatus());
    }

    @Test
    void when_batch_is_unsuccessfully_run_with_unforeseen_error_then_the_batch_status_should_be_set_to_warning_for_not_found_retries_exceeded()
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException, EncodingOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);
        doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();
        doThrow(new RuntimeException()).when(mockedUspesifikkMeldingLinjeReader).read();

        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);

        assertDoesNotThrow(() -> batch.run());
        assertEquals(BatchStatus.ENDED_WITH_ERROR_GENERAL, batch.getBatchStatus());
    }

    @Test
    void when_batch_is_successfully_run_with_zero_input_lines_then_the_batch_status_should_be_set_to_ok()
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException, EncodingOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);
        doReturn(emptyList()).when(mockedUspesifikkMeldingLinjeReader).read();
        doReturn(true).when(mockedUspesifikkMeldingLinjeReader).removeInputData();
        doReturn(IMeldingLinjeFileReader.Status.OK).when(mockedUspesifikkMeldingLinjeReader).getStatus();
        doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();

        final OppgaveSynkroniserer mockedOppgaveSynkroniserer = mock(OppgaveSynkroniserer.class);
        doReturn(ConsumerStatistics.zero(this.batch.getBatchType())).when(mockedOppgaveSynkroniserer).synkroniser(anyCollection());

        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
        this.batch.setOppgaveSynkroniserer(mockedOppgaveSynkroniserer);

        assertDoesNotThrow(() -> batch.run());
        assertEquals(BatchStatus.ENDED_WITH_OK, batch.getBatchStatus());
        verify(mockedUspesifikkMeldingLinjeReader).read();
    }

    @Test
    void when_batch_set_with_an_ok_initialized_uspesifikkMeldingLinjeReader_then_the_batch_status_should_be_set_to_ready() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);
        doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();
        doReturn(IMeldingLinjeFileReader.Status.OK).when(mockedUspesifikkMeldingLinjeReader).getStatus();

        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);

        Assertions.assertEquals(BatchStatus.READY, batch.getBatchStatus());
    }

    @Test
    void when_batch_set_with_anything_but_ok_initialized_uspesifikkMeldingLinjeReader_then_then_the_batch_status_should_be_set_to_general_error() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);

        Arrays
                .stream(Status.values())
                .filter((final IMeldingLinjeFileReader.Status status) -> !IMeldingLinjeFileReader.Status.OK.equals(status))
                .forEach(
                        (final IMeldingLinjeFileReader.Status status) -> {
                            doReturn(status).when(mockedUspesifikkMeldingLinjeReader).getStatus();
                            doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();
                            this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
                            Assertions.assertEquals(BatchStatus.ENDED_WITH_ERROR_GENERAL, batch.getBatchStatus());
                        }
                );
    }

    static Stream<Arguments> statusesForExceptions() {
        return Stream.of(
                arguments(Named.of("io", IoOkosynkIoException.class), BatchStatus.ENDED_WITH_ERROR_GENERAL),
                arguments(Named.of("not found", NotFoundOkosynkIoException.class), BatchStatus.ENDED_WITH_WARNING_INPUT_DATA_NOT_FOUND),
                arguments(Named.of("configure or initialize", ConfigureOrInitializeOkosynkIoException.class), BatchStatus.ENDED_WITH_ERROR_GENERAL),
                arguments(Named.of("authenticate", AuthenticationOkosynkIoException.class), BatchStatus.ENDED_WITH_ERROR_CONFIGURATION),
                arguments(Named.of("encoding", EncodingOkosynkIoException.class), BatchStatus.ENDED_WITH_ERROR_CONFIGURATION),
                arguments(Named.of("nullpointer", NullPointerException.class), BatchStatus.ENDED_WITH_ERROR_CONFIGURATION),
                arguments(Named.of("runtime", RuntimeException.class), BatchStatus.ENDED_WITH_ERROR_GENERAL)
        );
    }

    @ParameterizedTest
    @MethodSource("statusesForExceptions")
    void when_reading_input_data_fails_with_an_exception_then_the_batch_status_should_be_set_correspondingly(Class<? extends Exception> clazz, BatchStatus batchStatus)
            throws IoOkosynkIoException, AuthenticationOkosynkIoException, ConfigureOrInitializeOkosynkIoException, EncodingOkosynkIoException, NotFoundOkosynkIoException {
        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader = mock(IMeldingLinjeFileReader.class);
        doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();
        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
        doThrow(clazz).when(mockedUspesifikkMeldingLinjeReader).read();
        assertDoesNotThrow(() -> this.batch.run());
        logger.info("Exception {} should result in: ", clazz);
        assertEquals(batchStatus, this.batch.getBatchStatus());
    }

    @Test
    void when_input_data_contains_too_many_lines_then_the_batch_should_throw()
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException, EncodingOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);
        doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();

        final List<String> inputDataLines = new ArrayList<>();
        for (
                int lineCounter = 1;
                lineCounter < Batch.UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT + 2;
                lineCounter++) {
            inputDataLines.add(RandomStringUtils.randomAlphanumeric(2));
        }

        doReturn(inputDataLines).when(mockedUspesifikkMeldingLinjeReader).read();
        doReturn(IMeldingLinjeFileReader.Status.OK).when(mockedUspesifikkMeldingLinjeReader).getStatus();

        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
        assertDoesNotThrow(() -> batch.run());
        verify(mockedUspesifikkMeldingLinjeReader).read();
        assertEquals(BatchStatus.ENDED_WITH_ERROR_TOO_MANY_INPUT_DATA_LINES, batch.getBatchStatus());
    }

    @Test
    void when_input_data_is_erroneous_then_the_batch_should_throw()
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException, EncodingOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);
        doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();

        final List<String> inputDataLines = new ArrayList<>();
        inputDataLines.add("lknlknklnklnlkn");
        doReturn(inputDataLines).when(mockedUspesifikkMeldingLinjeReader).read();

        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
        assertDoesNotThrow(() -> batch.run());
        verify(mockedUspesifikkMeldingLinjeReader).read();
        assertEquals(BatchStatus.ENDED_WITH_ERROR_INPUT_DATA, batch.getBatchStatus());
    }

    @Test
    void when_input_data_is_ok_and_the_input_data_is_successfully_removed_then_the_batch_should_return_ok()
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException, EncodingOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);

        final List<String> inputDataLines = new ArrayList<>();

        inputDataLines.add(getValidLineOfInputData());
        doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();
        doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();
        doReturn(inputDataLines).when(mockedUspesifikkMeldingLinjeReader).read();
        doReturn(true).when(mockedUspesifikkMeldingLinjeReader).removeInputData();

        final OppgaveSynkroniserer mockedOppgaveSynkroniserer = mock(OppgaveSynkroniserer.class);
        doReturn(ConsumerStatistics.zero(this.batch.getBatchType().getConsumerStatisticsName()))
                .when(mockedOppgaveSynkroniserer).synkroniser(anyCollection());

        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
        this.batch.setOppgaveSynkroniserer(mockedOppgaveSynkroniserer);

        assertDoesNotThrow(() -> batch.run());
        verify(mockedUspesifikkMeldingLinjeReader).read();
        assertEquals(BatchStatus.ENDED_WITH_OK, batch.getBatchStatus());
    }

    @Test
    void when_input_data_is_ok_and_the_input_data_is_unsuccessfully_removed_then_the_batch_should_return_warning()
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException,
            EncodingOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);

        final List<String> inputDataLines = new ArrayList<>();

        inputDataLines.add(getValidLineOfInputData());
        doReturn(getBatchType()).when(mockedUspesifikkMeldingLinjeReader).getBatchType();
        doReturn(inputDataLines).when(mockedUspesifikkMeldingLinjeReader).read();
        doReturn(false).when(mockedUspesifikkMeldingLinjeReader).removeInputData();

        final OppgaveSynkroniserer mockedOppgaveSynkroniserer = mock(OppgaveSynkroniserer.class);
        doReturn(ConsumerStatistics.zero(this.batch.getBatchType().getConsumerStatisticsName()))
                .when(mockedOppgaveSynkroniserer).synkroniser(anyCollection());

        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
        this.batch.setOppgaveSynkroniserer(mockedOppgaveSynkroniserer);

        assertDoesNotThrow(() -> batch.run());
        verify(mockedUspesifikkMeldingLinjeReader).read();
        assertEquals(BatchStatus.ENDED_WITH_WARNING_BATCH_INPUT_DATA_COULD_NOT_BE_DELETED_AFTER_OK_RUN, batch.getBatchStatus());
    }

    @Test
    void when_setting_the_melding_linje_reader_to_null_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(NullPointerException.class, () -> this.batch.setUspesifikkMeldingLinjeReader(null));
    }

    @Test
    void when_setting_the_synkroniserer_to_null_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(NullPointerException.class, () -> this.batch.setOppgaveSynkroniserer(null));
    }

    protected void setBatch(final Batch<? extends AbstractMelding> batch) {
        this.batch = batch;
    }

    protected abstract String getValidLineOfInputData();

    protected abstract Constants.BATCH_TYPE getBatchType();
}
