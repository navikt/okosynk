package no.nav.okosynk;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class OsServiceTest extends AbstractServiceTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    @BeforeEach
    void setUp() {
        commonBeforeEach();
        setService(new OsService(getOkosynkConfiguration()));
    }

    @Test
    void when_a_batch_is_created_it_should_not_be_null()
            throws URISyntaxException {

        enteringTestHeaderLogger.debug(null);

        System.setProperty(Constants.REST_STS_URL_KEY, "https://security-token-service.nais.adeo.no/rest/v1/sts/token");
        System.setProperty("OSFTPBASEURL_URL", "sftp://filmottak.adeo.no:22/home/srvokosynksftp/inbound/os.testset_001.input");
        System.setProperty("OSFTPCREDENTIALS_USERNAME", "someShitUserIdNotBeingUsedByNeitherThisOrThat");
        System.setProperty("OSFTPCREDENTIALS_PASSWORD", "someShitPasswordNotBeingUsedByNeitherThisOrThat");

        final OsService mockedOsService = mock(OsService.class);
        when(mockedOsService.createAndConfigureBatch(any())).thenCallRealMethod();
        when(mockedOsService.getBatchType()).thenReturn(BATCH_TYPE.OS);
        when(mockedOsService.createMeldingReader()).thenReturn(mock(MeldingReader.class));
        when(mockedOsService.createMeldingMapper(any())).thenReturn(mock(IMeldingMapper.class));

        final Batch<? extends AbstractMelding> osBatch =
                mockedOsService.createAndConfigureBatch(getOkosynkConfiguration());

        assertNotNull(osBatch);
    }
}
