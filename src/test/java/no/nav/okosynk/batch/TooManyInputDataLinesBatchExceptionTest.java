package no.nav.okosynk.batch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TooManyInputDataLinesBatchExceptionTest {

  private static final Logger enteringTestHeaderLogger =
      LoggerFactory.getLogger("EnteringTestHeader");

  @Test
  void when_instantiated_then_cause_should_always_be_null() {

    enteringTestHeaderLogger.debug(null);

    assertDoesNotThrow(() -> new TooManyInputDataLinesBatchException(1, 2));
    AbstractBatchException batchException = new TooManyInputDataLinesBatchException(1, 2);

    assertNull(batchException.getCause());
    assertTrue(batchException.toString().contains(" 1"));
    assertTrue(batchException.toString().contains(" 2"));
  }
}