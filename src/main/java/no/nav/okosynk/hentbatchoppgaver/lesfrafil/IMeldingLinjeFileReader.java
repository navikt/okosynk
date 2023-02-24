package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.*;

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

    Constants.BATCH_TYPE getBatchType();
}
