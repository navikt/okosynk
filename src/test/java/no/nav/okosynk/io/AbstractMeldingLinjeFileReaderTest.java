package no.nav.okosynk.io;

import java.util.function.Function;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

    // =========================================================================
    @Test
    @DisplayName("Tests that fullyQualifiedInputFileName is not null when creating an instance of IMeldingLinjeFileReader")
    void testUspesifikkMeldingLinjeFtpReaderFullyQualifiedInputFileNameIsNull() {

        enteringTestHeaderLogger.debug(null);

        Assertions.assertThrows(
            NullPointerException.class,
            () -> getCreator().apply(null)
        );
    }

    @Test
    @DisplayName("Status set to not ok if fullyQualifiedInputFileName is empty")
    void statusSetToFeilIfFullyQualifiedInputFileNameIsEmpty() {

        enteringTestHeaderLogger.debug(null);

        final String[] fullyQualifiedInputFileNames = new String[]{"", " ", "  "};

        for (final String fullyQualifiedInputFileName : fullyQualifiedInputFileNames) {

            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> getCreator().apply(fullyQualifiedInputFileName)
            );
        }
    }

    // =========================================================================

    protected abstract Function  <String           , IMeldingLinjeFileReader> getCreator();
}
