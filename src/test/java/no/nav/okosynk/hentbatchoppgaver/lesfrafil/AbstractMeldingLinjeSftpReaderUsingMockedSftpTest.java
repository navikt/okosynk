package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.AuthenticationOkosynkIoException;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static no.nav.okosynk.metrics.TestSftpServer.FTP_TEST_SERVER_USER;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractMeldingLinjeSftpReaderUsingMockedSftpTest {

    @Test
    void when_connect_fails_then_a_correct_OkosynkIoException_should_be_thrown() {

        assertThrows(AuthenticationOkosynkIoException.class,
                () -> new TinyFtpReader(new FtpSettings(
                        new URI("dsfgdfg"),
                        FTP_TEST_SERVER_USER,
                        "enPrivatnøkkel",
                        StandardCharsets.ISO_8859_1,
                        "noskapin"
                )).read());
    }

    protected abstract Constants.BATCH_TYPE getBatchType();

}
