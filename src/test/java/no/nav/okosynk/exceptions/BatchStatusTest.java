package no.nav.okosynk.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BatchStatusTest {

    private static final Logger logger = LoggerFactory.getLogger(BatchStatusTest.class);

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    void a_batchStatus_should_have_the_expected_properties() {

        enteringTestHeaderLogger.debug(null);

        final Map<BatchStatus, Pair<Boolean, Boolean>> expectedBatchStatusMap =
            new HashMap<>() {{
              put(BatchStatus.READY, new Pair<>(false, true));
              put(BatchStatus.STARTED, new Pair<>(false, true));
              put(BatchStatus.ENDED_WITH_OK, new Pair<>(false, false));
              put(BatchStatus.ENDED_WITH_WARNING_BATCH_INPUT_DATA_COULD_NOT_BE_DELETED_AFTER_OK_RUN, new Pair<>(false, true));
              put(BatchStatus.ENDED_WITH_WARNING_INPUT_DATA_NOT_FOUND, new Pair<>(true, false));
              put(BatchStatus.ENDED_WITH_ERROR_GENERAL, new Pair<>(true, true));
              put(BatchStatus.ENDED_WITH_ERROR_INPUT_DATA, new Pair<>(false, true));
              put(BatchStatus.ENDED_WITH_ERROR_TOO_MANY_INPUT_DATA_LINES, new Pair<>(false, true));
              put(BatchStatus.ENDED_WITH_ERROR_CONFIGURATION, new Pair<>(false, true));
            }};

        for (final BatchStatus actualBatchStatus : BatchStatus.values()) {
            logger.info("About to test: {}...", actualBatchStatus);
            assertEquals(
                expectedBatchStatusMap.get(actualBatchStatus).getValue0(),
                actualBatchStatus.failedButRerunningMaySucceed(),
                "BatchStatus " + actualBatchStatus + " has an unexpected failedButRerunningMaySucceed value."
            );
          assertEquals(
              expectedBatchStatusMap.get(actualBatchStatus).getValue1(),
              actualBatchStatus.shouldAlert(),
              "BatchStatus " + actualBatchStatus + " has an unexpected shouldAlert value."
          );
        }
    }
}
