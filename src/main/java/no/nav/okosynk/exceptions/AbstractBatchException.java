package no.nav.okosynk.exceptions;

public abstract class AbstractBatchException extends Exception {

    protected AbstractBatchException(final Throwable cause) {
        super(cause);
    }

    protected AbstractBatchException(final String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        return super.getMessage()
            + (
                (getCause() == null)
                ?
                ""
                :
                (
                    super.getMessage().equals(getCause().toString())
                    ?
                    ""
                    :
                    ", cause: " + getCause().toString()
                )
            );
    }
}