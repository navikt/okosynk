package no.nav.okosynk.batch;

public class BatchException extends Exception {

    public final Exception cause;

    public BatchException(final Exception cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return cause.toString();
    }
}
