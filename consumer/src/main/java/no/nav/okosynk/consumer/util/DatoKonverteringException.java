package no.nav.okosynk.consumer.util;

public class DatoKonverteringException extends RuntimeException {

    public DatoKonverteringException(Exception e, String msg) {
        super(msg, e);
    }
}
