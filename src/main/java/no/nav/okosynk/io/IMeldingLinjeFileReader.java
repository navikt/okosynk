package no.nav.okosynk.io;

import java.util.List;

public interface IMeldingLinjeFileReader {

    public enum Status {
        UNSET,
        ERROR,
        OK
    }

    List<String> read() throws LinjeUnreadableException;

    Status getStatus();
}
