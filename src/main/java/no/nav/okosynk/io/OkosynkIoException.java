package no.nav.okosynk.io;

public class OkosynkIoException extends Exception {

  public enum ErrorCode {
      CONFIGURE_OR_INITIALIZE, IO, NOT_FOUND, ENCODING, READ, AUTHENTICATION, NUMBER_OF_RETRIES_EXCEEDED
  }

  final ErrorCode errorCode;

  public ErrorCode getErrorCode() {
    return this.errorCode;
  }

  OkosynkIoException(final ErrorCode errorCode, final String message) {
    this(errorCode, message, null);
  }

  public OkosynkIoException(final ErrorCode errorCode, final Throwable cause) {
    this(errorCode, null, cause);
  }

  public OkosynkIoException(
      final ErrorCode errorCode,
      final String message,
      final Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

    @Override
    public String toString() {
        return
          "ErrorCode: " + System.lineSeparator()
        + getErrorCode().toString() + System.lineSeparator()
        + super.toString();
    }
}