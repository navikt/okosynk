package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.ConfigureOrInitializeOkosynkIoException;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static no.nav.okosynk.metrics.TestSftpServer.FTP_TEST_SERVER_PASSWORD;
import static no.nav.okosynk.metrics.TestSftpServer.FTP_TEST_SERVER_USER;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractMeldingLinjeSftpReaderUsingMockedSftpTest {

    @Test
    void when_connect_fails_then_a_correct_OkosynkIoException_should_be_thrown() throws URISyntaxException {

        assertThrows(ConfigureOrInitializeOkosynkIoException.class, new MeldingLinjeSftpReader(new FtpSettings(
                new URI("dsfgdfg"),
                FTP_TEST_SERVER_USER,
                FTP_TEST_SERVER_PASSWORD,
                "ISO8859_1"
        ),
                getBatchType())::read);
    }

    protected abstract Constants.BATCH_TYPE getBatchType();

}
