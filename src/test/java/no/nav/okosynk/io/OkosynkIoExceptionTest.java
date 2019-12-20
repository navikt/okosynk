package no.nav.okosynk.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.okosynk.io.OkosynkIoException.ErrorCode;
import org.junit.jupiter.api.Test;

public class OkosynkIoExceptionTest {

  @Test
  void when_crated_with_an_error_code_then_the_same_error_code_should_be_returned() {

    final ErrorCode expectedErrorCode = ErrorCode.IO;
    final Throwable expectedCause = null;
    final OkosynkIoException okosynkIoException = new OkosynkIoException(ErrorCode.IO, expectedCause);

    assertEquals(expectedErrorCode, okosynkIoException.getErrorCode());
    assertEquals(expectedCause, okosynkIoException.getCause());
  }

  @Test
  void when_crated_with_an_error_code_then_the_same_error_code_should_be_included_in_the_string_representation() {

    final ErrorCode expectedErrorCode = ErrorCode.IO;
    final OkosynkIoException okosynkIoException = new OkosynkIoException(ErrorCode.IO, "");

    assertTrue(okosynkIoException.toString().contains(expectedErrorCode.toString()));
  }
}
