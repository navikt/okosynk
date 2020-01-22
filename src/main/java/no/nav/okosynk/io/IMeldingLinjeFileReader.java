package no.nav.okosynk.io;

import java.util.List;

public interface IMeldingLinjeFileReader {

    enum Status {
    UNSET,
    ERROR,
    OK
    }

    List<String> read() throws OkosynkIoException;

    Status getStatus();
}
