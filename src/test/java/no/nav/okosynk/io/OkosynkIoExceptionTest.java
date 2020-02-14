package no.nav.okosynk.io;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkosynkIoExceptionTest {

  private static final Logger enteringTestHeaderLogger =
      LoggerFactory.getLogger("EnteringTestHeader");

  @Test
  void when_x_then_y() {

    enteringTestHeaderLogger.debug(null);

    assertTrue(true);
  }
}
