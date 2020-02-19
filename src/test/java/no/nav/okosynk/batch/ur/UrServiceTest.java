package no.nav.okosynk.batch.ur;

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
import no.nav.okosynk.domain.ur.UrMeldingReader;
import no.nav.okosynk.io.ConfigureOrInitializeOkosynkIoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UrServiceTest extends AbstractServiceTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String MOCK_UR_LINJE = "00003187051ORGANISASJON2004-01-19T06:22:4309          00000000084840æ0318KREDREFUR2302004-01-15134553997MOTTATT FRA FORSYSTEM                             00003187051";

    @BeforeEach
    void setUp() {
        commonBeforeEach();
        setService(new UrService(getOkosynkConfiguration()));
    }

    @Test
    public void when_a_batch_is_created_it_should_not_be_null()
        throws ConfigureOrInitializeOkosynkIoException {

        enteringTestHeaderLogger.debug(null);

        System.setProperty(Constants.REST_STS_URL_KEY, "https://security-token-service.nais.adeo.no/rest/v1/sts/token");
        System.setProperty("URFTPBASEURL_URL", "sftp://filmottak.adeo.no:22/home/srvokosynksftp/inbound/ur.input");
        System.setProperty("URFTPCREDENTIALS_USERNAME", "someShitUserIdNotBeingUsedByNeitherThisOrThat");
        System.setProperty("URFTPCREDENTIALS_PASSWORD", "someShitPasswordNotBeingUsedByNeitherThisOrThat");

        final UrService mockedUrService = mock(UrService.class);
        when(mockedUrService.createAndConfigureBatch(any())).thenCallRealMethod();
        when(mockedUrService.getBatchType()).thenReturn(BATCH_TYPE.UR);
        when(mockedUrService.createMeldingReader()).thenReturn(mock(UrMeldingReader.class));
        when(mockedUrService.createMeldingMapper(any(AktoerRestClient.class))).thenReturn(mock(IMeldingMapper.class));

        final Batch<? extends AbstractMelding> urBatch =
            mockedUrService.createAndConfigureBatch(getOkosynkConfiguration());

        assertNotNull(urBatch);
    }

    /*
    private void setUpBatchFullfortMock(final IOkosynkConfiguration okosynkConfiguration) throws MeldingUnreadableException {

        getService() = Mockito.spy(new UrService(okosynkConfiguration));
        final Batch<UrMelding> batch = getService().createAndConfigureBatch(getOkosynkConfiguration());
        batch.setUspesifikkMeldingLinjeReader(new MeldingLinjeFileReaderMock(MOCK_UR_LINJE));
        when(getService().createAndConfigureBatch(getOkosynkConfiguration())).thenReturn((Batch)batch);
    }
     */
}