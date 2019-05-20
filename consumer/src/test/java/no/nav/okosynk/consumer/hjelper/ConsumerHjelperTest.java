package no.nav.okosynk.consumer.hjelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConsumerHjelperTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private IOkosynkConfiguration okosynkConfiguration =
        new FakeOkosynkConfiguration();

    @BeforeEach
    void setup() {
        this.okosynkConfiguration.clearSystemProperty(Constants.TILLAT_MOCK_PROPERTY_KEY);
        this.okosynkConfiguration.clearSystemProperty("tjeneste.mock");
        this.okosynkConfiguration.clearSystemProperty("tjeneste.simulate.error");
    }

    @Test
    void getMockStatusSkalReturnereIkkeMocketTekstDersomTillatmockErNull() throws Exception {

        enteringTestHeaderLogger.debug(null);

        assertEquals(ConsumerHjelper.getMockStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.ikkeMocketTekst);
    }

    @Test
    void getMockStatusSkalReturnereIkkeMocketTekstDersomTillatmockErFalse() throws Exception {

        enteringTestHeaderLogger.debug(null);

        okosynkConfiguration.setSystemProperty(Constants.TILLAT_MOCK_PROPERTY_KEY, "false");
        assertEquals(ConsumerHjelper.getMockStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.ikkeMocketTekst);
    }

    @Test
    void getMockStatusSkalReturnereIkkeMocketTekstDersomTillatmockErTrueOgMockkonfigurasjonForTjenestenErNull() throws Exception {

        enteringTestHeaderLogger.debug(null);

        okosynkConfiguration.setSystemProperty(Constants.TILLAT_MOCK_PROPERTY_KEY, "true");
        assertEquals(ConsumerHjelper.getMockStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.ikkeMocketTekst);
    }

    @Test
    void getMockStatusSkalReturnereIkkeMocketTekstDersomTillatmockErTrueOgTjenestenIkkeErMocket() throws Exception {

        enteringTestHeaderLogger.debug(null);

        okosynkConfiguration.setSystemProperty(Constants.TILLAT_MOCK_PROPERTY_KEY, "true");
        okosynkConfiguration.setSystemProperty("tjeneste.mock", "false");
        assertEquals(ConsumerHjelper.getMockStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.ikkeMocketTekst);
    }

    @Test
    void getMockStatusSkalReturnereMocketTekstDersomTillatmockErTrueOgTjenestenErMocket() throws Exception {
        okosynkConfiguration.setSystemProperty(Constants.TILLAT_MOCK_PROPERTY_KEY, "true");
        okosynkConfiguration.setSystemProperty("tjeneste.mock", "true");
        assertEquals(ConsumerHjelper.getMockStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.mocketTekst);
    }

    @Test
    void erAvbruddSkalReturnereIkkeAvbruddTekstDersomTillatmockErNull() throws Exception {

        enteringTestHeaderLogger.debug(null);

        assertEquals(ConsumerHjelper.getAvbruddStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.ikkeAvbruddTekst);
    }

    @Test
    void erAvbruddSkalReturnereIkkeAvbruddTekstDersomTillatmockErFalse() throws Exception {

        enteringTestHeaderLogger.debug(null);

        okosynkConfiguration.setSystemProperty(Constants.TILLAT_MOCK_PROPERTY_KEY, "false");
        assertEquals(ConsumerHjelper.getAvbruddStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.ikkeAvbruddTekst);
    }

    @Test
    void erAvbruddSkalReturnereIkkeAvbruddTekstDersomTillatmockErTrueOgMockkonfigurasjonForTjenestenErNull() throws Exception {

        enteringTestHeaderLogger.debug(null);

        okosynkConfiguration.setSystemProperty(Constants.TILLAT_MOCK_PROPERTY_KEY, "true");
        assertEquals(ConsumerHjelper.getAvbruddStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.ikkeAvbruddTekst);
    }

    @Test
    void erAvbruddSkalReturnereIkkeAvbruddTekstDersomTillatmockErTrueOgTjenestenIkkeErMocket() throws Exception {

        enteringTestHeaderLogger.debug(null);

        okosynkConfiguration.setSystemProperty(Constants.TILLAT_MOCK_PROPERTY_KEY, "true");
        okosynkConfiguration.setSystemProperty("tjeneste.mock", "false");
        assertEquals(ConsumerHjelper.getAvbruddStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.ikkeAvbruddTekst);
    }

    @Test
    void erAvbruddSkalReturnereIkkeAvbruddTekstDersomTillatmockErTrueOgAvbruddkonfigurasjonForTjenestenErNull() throws Exception {
        okosynkConfiguration.setSystemProperty(Constants.TILLAT_MOCK_PROPERTY_KEY, "true");
        assertEquals(ConsumerHjelper.getAvbruddStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.ikkeAvbruddTekst);
    }

    @Test
    void erAvbruddSkalReturnereIkkeAvbruddTekstDersomTillatmockErTrueOgTjenestenIkkeErAvbrudd() throws Exception {

        enteringTestHeaderLogger.debug(null);

        okosynkConfiguration.setSystemProperty(Constants.TILLAT_MOCK_PROPERTY_KEY, "true");
        okosynkConfiguration.setSystemProperty("tjeneste.mock", "false");
        okosynkConfiguration.setSystemProperty("tjeneste.mock.simulate.error", "false");

        assertEquals(ConsumerHjelper.getAvbruddStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.ikkeAvbruddTekst);
    }

    @Test
    void erAvbruddSkalReturnereAvbruddTekstDersomTillatmockErTrueOgTjenestenErMocket() throws Exception {

        enteringTestHeaderLogger.debug(null);

        okosynkConfiguration.setSystemProperty(Constants.TILLAT_MOCK_PROPERTY_KEY, "true");
        okosynkConfiguration.setSystemProperty("tjeneste.mock", "true");
        okosynkConfiguration.setSystemProperty("tjeneste.mock.simulate.error", "true");

        assertEquals(ConsumerHjelper.getAvbruddStatus(this.okosynkConfiguration, "tjeneste.mock"), ConsumerHjelper.avbruddTekst);
    }
}
