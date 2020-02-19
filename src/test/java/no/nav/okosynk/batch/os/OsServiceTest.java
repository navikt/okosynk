package no.nav.okosynk.batch.os;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;

import no.nav.okosynk.batch.AbstractServiceTest;
import no.nav.okosynk.batch.Batch;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.os.OsMeldingReader;
import no.nav.okosynk.io.ConfigureOrInitializeOkosynkIoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OsServiceTest extends AbstractServiceTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String MOCK_OS_LINJE = "10108000398009543471 2008-10-102008-10-10NEG K231B2962008-11-012008-11-30000000008820æ 4151         GS      10108000398            ";

    @BeforeEach
    void setUp() {
        commonBeforeEach();
        setService(new OsService(getOkosynkConfiguration()));
    }

    @Test
    public void when_a_batch_is_created_it_should_not_be_null()
        throws ConfigureOrInitializeOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        System.setProperty(Constants.REST_STS_URL_KEY, "https://security-token-service.nais.adeo.no/rest/v1/sts/token");
        System.setProperty("OSFTPBASEURL_URL", "sftp://filmottak.adeo.no:22/home/srvokosynksftp/inbound/os.input");
        System.setProperty("OSFTPCREDENTIALS_USERNAME", "someShitUserIdNotBeingUsedByNeitherThisOrThat");
        System.setProperty("OSFTPCREDENTIALS_PASSWORD", "someShitPasswordNotBeingUsedByNeitherThisOrThat");

        final OsService mockedOsService = mock(OsService.class);
        when(mockedOsService.createAndConfigureBatch(any())).thenCallRealMethod();
        when(mockedOsService.getBatchType()).thenReturn(BATCH_TYPE.OS);
        when(mockedOsService.createMeldingReader()).thenReturn(mock(OsMeldingReader.class));
        when(mockedOsService.createMeldingMapper(any(AktoerRestClient.class))).thenReturn(mock(IMeldingMapper.class));

        final Batch<? extends AbstractMelding> osBatch =
            mockedOsService.createAndConfigureBatch(getOkosynkConfiguration());

        assertNotNull(osBatch);
    }

    /*
    private void setUpBatchFullfortMock(final IOkosynkConfiguration okosynkConfiguration) throws MeldingUnreadableException {

        this.service = Mockito.spy(new OsService(okosynkConfiguration));
        final Batch<OsMelding> batch = (Batch<OsMelding>) service.createAndConfigureBatch(this.okosynkConfiguration);
        batch.setUspesifikkMeldingLinjeReader(new MeldingLinjeFileReaderMock(MOCK_OS_LINJE));
        when(service.createAndConfigureBatch(this.okosynkConfiguration)).thenReturn((Batch)batch);
    }
     */
}