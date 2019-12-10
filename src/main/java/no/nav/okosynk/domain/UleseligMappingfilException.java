package no.nav.okosynk.domain;

class UleseligMappingfilException extends RuntimeException {

  UleseligMappingfilException(final Exception cause) {
    super(cause);
  }

  @Override
  public String toString() {
    return super.getCause().toString();
  }
}
