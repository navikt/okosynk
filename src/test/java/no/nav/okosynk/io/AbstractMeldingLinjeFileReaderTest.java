package no.nav.okosynk.io;

import java.util.function.Function;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractMeldingLinjeFileReaderTest {

    // =========================================================================
    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");
    // =========================================================================
    protected static final String FILNAVN_EXISTING_UR_OR_OS_INPUT_FIL =
        AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp.FILNAVN_EXISTING_UR_OR_OS_INPUT_FIL;
    // =========================================================================

    public IOkosynkConfiguration getOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    private final IOkosynkConfiguration okosynkConfiguration =
        new FakeOkosynkConfiguration();

    public static String getInputFileName() {
        return INPUT_FILE_NAME;
    }

    public static void setInputFileName(String inputFileName) {
        INPUT_FILE_NAME = inputFileName;
    }

    private static String INPUT_FILE_NAME;

    @BeforeEach
    void beforeEach() {
        getOkosynkConfiguration().setSystemProperty(
            Constants.FILE_READER_MAX_NUMBER_OF_READ_TRIES_KEY,
            "2");
        getOkosynkConfiguration().setSystemProperty(
            Constants.FILE_READER_RETRY_WAIT_TIME_IN_MILLISECONDS_KEY,
            "1000");
    }

    // =========================================================================
    @Test
    void when_number_of_retries_is_not_set_then_an_exception_should_upon_initiation_of_MeldingLinjeFileReader() {

        enteringTestHeaderLogger.debug(null);

        getOkosynkConfiguration().clearSystemProperty(
            Constants.FILE_READER_MAX_NUMBER_OF_READ_TRIES_KEY);
        final String fullyQualifiedInputFileName = "someNonEmptyFileName";

        Assertions.assertThrows(
            IllegalStateException.class,
            () -> getMeldingLinjeFileReaderCreator().apply(fullyQualifiedInputFileName)
        );
    }

    @Test
    void when_retry_wait_time_is_not_set_then_an_exception_should_upon_initiation_of_MeldingLinjeFileReader() {

        enteringTestHeaderLogger.debug(null);

        getOkosynkConfiguration().clearSystemProperty(
            Constants.FILE_READER_RETRY_WAIT_TIME_IN_MILLISECONDS_KEY);

        final String fullyQualifiedInputFileName = "someNonEmptyFileName";

        Assertions.assertThrows(
            IllegalStateException.class,
            () -> getMeldingLinjeFileReaderCreator().apply(fullyQualifiedInputFileName)
        );
    }

    @Test
    void when_fully_qualified_input_file_name_is_null_then_an_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        Assertions.assertThrows(
            NullPointerException.class,
            () -> getMeldingLinjeFileReaderCreator().apply(null)
        );
    }

    @Test
    void when_fully_qualified_input_file_name_is_empty_then_an_npe_should_be_thrown() {

        enteringTestHeaderLogger.debug(null);

        final String[] fullyQualifiedInputFileNames = new String[]{"", " ", "  "};

        for (final String fullyQualifiedInputFileName : fullyQualifiedInputFileNames) {

            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> getMeldingLinjeFileReaderCreator().apply(fullyQualifiedInputFileName)
            );
        }
    }

    // =========================================================================

    protected abstract Function  <String, IMeldingLinjeFileReader> getMeldingLinjeFileReaderCreator();
}
