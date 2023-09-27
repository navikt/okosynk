package no.nav.okosynk;

import lombok.Getter;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.IMeldingLinjeFileReader;
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
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static no.nav.okosynk.hentbatchoppgaver.lesfrafil.FileReaderStatus.OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

public abstract class AbstractBatchTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBatchTest.class);

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private Batch<? extends AbstractMelding> batch;

    @Getter
    private final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

    @Test
    void when_batch_is_successfully_run_with_no_input_file_then_the_batch_status_should_be_set_to_warning_for_not_found_retries_exceeded()
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);
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
            NotFoundOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader = mock(IMeldingLinjeFileReader.class);
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
            NotFoundOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);
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
            NotFoundOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);
        doReturn(emptyList()).when(mockedUspesifikkMeldingLinjeReader).read();
        doReturn(true).when(mockedUspesifikkMeldingLinjeReader).removeInputData();
        doReturn(OK).when(mockedUspesifikkMeldingLinjeReader).getStatus();

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
        doReturn(OK).when(mockedUspesifikkMeldingLinjeReader).getStatus();

        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);

        Assertions.assertEquals(BatchStatus.READY, batch.getBatchStatus());
    }

    static Stream<Arguments> statusesForExceptions() {
        return Stream.of(
                arguments(Named.of("io", IoOkosynkIoException.class), BatchStatus.ENDED_WITH_ERROR_GENERAL),
                arguments(Named.of("not found", NotFoundOkosynkIoException.class), BatchStatus.ENDED_WITH_WARNING_INPUT_DATA_NOT_FOUND),
                arguments(Named.of("configure or initialize", ConfigureOrInitializeOkosynkIoException.class), BatchStatus.ENDED_WITH_ERROR_GENERAL),
                arguments(Named.of("authenticate", AuthenticationOkosynkIoException.class), BatchStatus.ENDED_WITH_ERROR_CONFIGURATION),
                arguments(Named.of("nullpointer", NullPointerException.class), BatchStatus.ENDED_WITH_ERROR_CONFIGURATION),
                arguments(Named.of("runtime", RuntimeException.class), BatchStatus.ENDED_WITH_ERROR_GENERAL)
        );
    }

    @ParameterizedTest
    @MethodSource("statusesForExceptions")
    void when_reading_input_data_fails_with_an_exception_then_the_batch_status_should_be_set_correspondingly(Class<? extends Exception> clazz, BatchStatus batchStatus)
            throws IoOkosynkIoException, AuthenticationOkosynkIoException, ConfigureOrInitializeOkosynkIoException, NotFoundOkosynkIoException {
        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader = mock(IMeldingLinjeFileReader.class);
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
            NotFoundOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);

        final List<String> inputDataLines = new ArrayList<>();
        for (
                int lineCounter = 1;
                lineCounter < Batch.UPPER_LIMIT_OF_OPPGAVER_RETRIEVED_FROM_BATCH_INPUT + 2;
                lineCounter++) {
            inputDataLines.add(RandomStringUtils.randomAlphanumeric(2));
        }

        doReturn(inputDataLines).when(mockedUspesifikkMeldingLinjeReader).read();
        doReturn(OK).when(mockedUspesifikkMeldingLinjeReader).getStatus();

        batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
        assertDoesNotThrow(() -> batch.run());
        verify(mockedUspesifikkMeldingLinjeReader).read();
        assertEquals(BatchStatus.ENDED_WITH_ERROR_TOO_MANY_INPUT_DATA_LINES, batch.getBatchStatus());
    }

    @Test
    void when_input_data_is_erroneous_then_the_batch_should_throw()
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);

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
            NotFoundOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);

        final List<String> inputDataLines = new ArrayList<>();

        inputDataLines.add(getValidLineOfInputData());
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
            NotFoundOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);

        final List<String> inputDataLines = new ArrayList<>();

        inputDataLines.add(getValidLineOfInputData());
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

    protected void setBatch(final Batch<? extends AbstractMelding> batch) {
        this.batch = batch;
    }

    protected abstract String getValidLineOfInputData();

    protected abstract Constants.BATCH_TYPE getBatchType();
}
