package no.nav.okosynk.io;

public class LinjeUnreadableException  extends Exception {

    public LinjeUnreadableException(final String message) {
    super(message);
}

    public LinjeUnreadableException(final Throwable cause) {
        super(cause);
    }

    public LinjeUnreadableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
