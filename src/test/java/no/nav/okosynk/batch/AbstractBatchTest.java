package no.nav.okosynk.batch;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.IMeldingReader;
import no.nav.okosynk.io.AuthenticationOkosynkIoException;
import no.nav.okosynk.io.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.io.EncodingOkosynkIoException;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.IMeldingLinjeFileReader.Status;
import no.nav.okosynk.io.IoOkosynkIoException;
import no.nav.okosynk.io.NotFoundOkosynkIoException;
import org.apache.commons.lang3.RandomStringUtils;
import org.javatuples.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBatchTest<SPESIFIKKMELDINGTYPE extends AbstractMelding> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBatchTest.class);

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private Batch<? extends AbstractMelding> batch;

    private IMeldingReader<SPESIFIKKMELDINGTYPE> mockedMeldingReader;

    private IMeldingMapper<SPESIFIKKMELDINGTYPE> mockedMeldingMapper;

    private final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration(true, true);

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
                            this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);
                            Assertions.assertEquals(BatchStatus.ENDED_WITH_ERROR_GENERAL, batch.getBatchStatus());
                        }
                );
    }

    @Test
    void when_reading_input_data_fails_with_an_exception_then_the_batch_status_should_be_set_correspondingly() {

        enteringTestHeaderLogger.debug(null);

        final IMeldingLinjeFileReader mockedUspesifikkMeldingLinjeReader =
                mock(IMeldingLinjeFileReader.class);
        this.batch.setUspesifikkMeldingLinjeReader(mockedUspesifikkMeldingLinjeReader);

        final Collection<Pair<Class<? extends Exception>, BatchStatus>> exceptionsAndBatchStatuses =
                new ArrayList() {{
                    add(new Pair<>(IoOkosynkIoException.class, BatchStatus.ENDED_WITH_ERROR_GENERAL));
                    add(new Pair<>(NotFoundOkosynkIoException.class, BatchStatus.ENDED_WITH_WARNING_INPUT_DATA_NOT_FOUND));
                    add(new Pair<>(ConfigureOrInitializeOkosynkIoException.class, BatchStatus.ENDED_WITH_ERROR_GENERAL));
                    add(new Pair<>(AuthenticationOkosynkIoException.class, BatchStatus.ENDED_WITH_ERROR_CONFIGURATION));;
                    add(new Pair<>(EncodingOkosynkIoException.class, BatchStatus.ENDED_WITH_ERROR_CONFIGURATION));;
                    add(new Pair<>(NullPointerException.class, BatchStatus.ENDED_WITH_ERROR_CONFIGURATION));
                    add(new Pair<>(RuntimeException.class, BatchStatus.ENDED_WITH_ERROR_GENERAL));
                }};
        exceptionsAndBatchStatuses
                .stream()
                .forEach(
                        (final Pair<Class<? extends Exception>, BatchStatus> exceptionAndBatchStatus) -> {
                            try {
                                doThrow(exceptionAndBatchStatus.getValue0()).when(mockedUspesifikkMeldingLinjeReader).read();
                                assertDoesNotThrow(() -> this.batch.run());
                                logger.info("Exception {} should result in: ", exceptionAndBatchStatus.getValue0());
                                assertEquals(exceptionAndBatchStatus.getValue1(), this.batch.getBatchStatus());
                            } catch (AssertionFailedError e) {
                                throw e;
                            } catch (Throwable e) {
                                fail("A highly unexpected exception received");
                            }
                        }
                );
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

        assertThrows(NullPointerException.class, () -> {
            this.batch.setUspesifikkMeldingLinjeReader(null);
        });
    }

    @Test
    void when_setting_the_synkroniserer_to_null_then_a_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(NullPointerException.class, () -> {
            this.batch.setOppgaveSynkroniserer(null);
        });
    }

    protected void setMockedMeldingReader(final IMeldingReader<SPESIFIKKMELDINGTYPE> mockedMeldingReader) {
        this.mockedMeldingReader = mockedMeldingReader;
    }

    protected void setMockedMeldingMapper(final IMeldingMapper<SPESIFIKKMELDINGTYPE> mockedMeldingMapper) {
        this.mockedMeldingMapper = mockedMeldingMapper;
    }

    protected void setBatch(final Batch batch) {
        this.batch = batch;
    }

    protected abstract String getValidLineOfInputData();
}