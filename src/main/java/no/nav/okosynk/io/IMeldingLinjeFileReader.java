package no.nav.okosynk.io;

import java.util.List;

public interface IMeldingLinjeFileReader {

    enum Status {
    UNSET,
    ERROR,
    OK
    }

    List<String> read()
        throws ConfigureOrInitializeOkosynkIoException,
        IoOkosynkIoException,
        NotFoundOkosynkIoException, AuthenticationOkosynkIoException, EncodingOkosynkIoException;

    /**
     * Never throws anything, because renaming is considered relatively harmless.
     *
     * @return <code>true</code> if OK, <code>false</code> otherwise.
     */
    boolean removeInputData();

    Status getStatus();
}
