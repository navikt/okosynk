package no.nav.okosynk.domain;

public class UleseligMappingfilException extends RuntimeException {

    private final Exception cause;

    public UleseligMappingfilException(final Exception cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return cause.toString();
    }
}
