package no.nav.okosynk.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstantsTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Test
    @DisplayName("Assert no.nav.okosynk.io.os batch user is as expected when the corresponding property is not set")
    public void testHentOsBatchbrukerWhenPropertyNotSet() {

        enteringTestHeaderLogger.debug(null);

        final String expectedUser = "srvbokosynk001";

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        okosynkConfiguration.clearSystemProperty(Constants.BATCH_TYPE.OS.getBatchBrukerKey());
        assertEquals(expectedUser, this.hentOsBatchBruker(okosynkConfiguration));
    }

    @Test
    @DisplayName("Assert no.nav.okosynk.io.os batch user is as expected when the corresponding property is set")
    public void testHentOsBatchbrukerWhenPropertySet() {

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final String expectedUser = "Residents";
        okosynkConfiguration.setSystemProperty(Constants.BATCH_TYPE.OS.getBatchBrukerKey(), expectedUser);

        assertEquals(expectedUser, this.hentOsBatchBruker(okosynkConfiguration));
    }

    @Test
    @DisplayName("Assert ur batch user is as expected when the corresponding property is not set")
    void testHentUrBatchbrukerWhenPropertyNotSet() {

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();

        final String expectedUser = "srvbokosynk002";

        okosynkConfiguration.clearSystemProperty(Constants.BATCH_TYPE.UR.getBatchBrukerKey());
        assertEquals(expectedUser, this.hentUrBatchBruker(okosynkConfiguration));
    }

    @Test
    @DisplayName("Assert ur batch user is as expected when the corresponding property is set")
    void testHentUrBatchbrukerWhenPropertySet() {

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration();
        final String expectedUser = "JohnCage";
        okosynkConfiguration.setSystemProperty(Constants.BATCH_TYPE.UR.getBatchBrukerKey(), expectedUser);

        assertEquals(expectedUser, this.hentUrBatchBruker(okosynkConfiguration));
    }

    private static String hentOsBatchBruker(final IOkosynkConfiguration okosynkConfiguration) {

        final String batchBruker =
            okosynkConfiguration
                .getString(
                    Constants.BATCH_TYPE.OS.getBatchBrukerKey(),
                    Constants.BATCH_TYPE.OS.getBatchBrukerDefaultValue()
                );

        return batchBruker;
    }



    public static String hentUrBatchBruker(final IOkosynkConfiguration okosynkConfiguration) {

        final String batchBruker =
            okosynkConfiguration
                .getString(
                    Constants.BATCH_TYPE.UR.getBatchBrukerKey(),
                    Constants.BATCH_TYPE.UR.getBatchBrukerDefaultValue()
                );

        return batchBruker;
    }
}
