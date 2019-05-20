package no.nav.okosynk.batch;

public class BatchExecutionException extends RuntimeException {

    public final Exception cause;

    public BatchExecutionException(Exception cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return cause.toString();
    }
}
