package no.nav.okosynk.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
                put(BatchStatus.STARTET, -1);
                put(BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL, 0);
                put(BatchStatus.FEIL, 8);
                put(BatchStatus.STOPPET, 10);
                put(BatchStatus.READY, 100);
                put(BatchStatus.FULLFORT_MED_UVENTEDE_FEIL, 371);
            }};

        for (final BatchStatus batchStatus : BatchStatus.values()) {
            assertEquals(
                batchStatusMap.get(batchStatus).intValue(),
                batchStatus.getStatusCode(),
                "BatchStatus " + batchStatus + " has anunexpected status code."
            );
        }
    }
}
