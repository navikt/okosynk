package no.nav.okosynk.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.okosynk.io.OkosynkIoException.ErrorCode;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkosynkIoExceptionTest {
  private static final Logger enteringTestHeaderLogger =
      LoggerFactory.getLogger("EnteringTestHeader");

  @Test
  void when_crated_with_an_error_code_then_the_same_error_code_should_be_returned() {

    enteringTestHeaderLogger.debug(null);

    final ErrorCode expectedErrorCode = ErrorCode.IO;
    final Throwable expectedCause = null;
    final OkosynkIoException okosynkIoException = new OkosynkIoException(ErrorCode.IO, expectedCause);

    assertEquals(expectedErrorCode, okosynkIoException.getErrorCode());
    assertEquals(expectedCause, okosynkIoException.getCause());
  }

  @Test
  void when_crated_with_an_error_code_then_the_same_error_code_should_be_included_in_the_string_representation() {

    enteringTestHeaderLogger.debug(null);

    final ErrorCode expectedErrorCode = ErrorCode.IO;
    final OkosynkIoException okosynkIoException = new OkosynkIoException(ErrorCode.IO, "");

    assertTrue(okosynkIoException.toString().contains(expectedErrorCode.toString()));
  }
}
