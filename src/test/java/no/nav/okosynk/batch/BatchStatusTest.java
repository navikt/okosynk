package no.nav.okosynk.batch;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BatchStatusTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    @DisplayName("Test that BatchStatus has status codes as expected")
    void testStatusCode() {

        enteringTestHeaderLogger.debug(null);

        final Map<BatchStatus, Integer> batchStatusMap =
            new HashMap<BatchStatus, Integer>() {{
              put(BatchStatus.READY, 100);
              put(BatchStatus.STARTED, -1);
              put(BatchStatus.ENDED_WITH_OK, 0);
              put(BatchStatus.ENDED_WITH_WARNING_BATCH_INPUT_DATA_COULD_NOT_BE_DELETED_AFTER_OK_RUN, 1023);
              put(BatchStatus.ENDED_WITH_WARNING_NUMBER_OF_RETRIES_EXCEEDED_NOT_FOUND, 933);
              put(BatchStatus.ENDED_WITH_ERROR_GENERAL, 8);
              put(BatchStatus.ENDED_WITH_ERROR_NUMBER_OF_RETRIES_EXCEEDED_IO, 919);
            }};

        for (final BatchStatus batchStatus : BatchStatus.values()) {
            assertEquals(
                batchStatusMap.get(batchStatus).intValue(),
                batchStatus.getStatusCode(),
                "BatchStatus " + batchStatus + " has an unexpected status code."
            );
        }
    }
}
