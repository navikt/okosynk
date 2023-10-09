package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.AuthenticationOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.IoOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.NotFoundOkosynkIoException;

import java.util.List;

public interface IMeldingLinjeFileReader {

    List<String> read()
            throws ConfigureOrInitializeOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException,
            AuthenticationOkosynkIoException;

    boolean removeInputData();

    FileReaderStatus getStatus();
}
