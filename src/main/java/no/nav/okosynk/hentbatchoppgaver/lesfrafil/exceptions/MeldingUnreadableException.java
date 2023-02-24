package no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions;

public class MeldingUnreadableException extends Exception {

    public MeldingUnreadableException(final String message) {
        super(message);
    }

    public MeldingUnreadableException(final Throwable cause) {
        super(cause);
    }

    public MeldingUnreadableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
