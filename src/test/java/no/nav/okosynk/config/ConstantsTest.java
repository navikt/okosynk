package no.nav.okosynk.config;

import no.nav.okosynk.config.Constants.BATCH_TYPE;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstantsTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private static String hentOsBatchBruker(final IOkosynkConfiguration okosynkConfiguration) {
        return okosynkConfiguration.getBatchBruker(Constants.BATCH_TYPE.OS);
    }

    private static String hentUrBatchBruker(final IOkosynkConfiguration okosynkConfiguration) {
        return okosynkConfiguration.getBatchBruker(Constants.BATCH_TYPE.UR);
    }

    @Test
    @DisplayName("Assert no.nav.okosynk.io.os batch user is as expected when the corresponding property is not set")
    public void testHentOsBatchbrukerWhenPropertyNotSet() {

        enteringTestHeaderLogger.debug(null);

        final String expectedUser = "srvbokosynk001";

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration(true, true);

        okosynkConfiguration.clearSystemProperty(Constants.BATCH_TYPE.OS.getBatchBrukerKey());
        assertEquals(expectedUser, this.hentOsBatchBruker(okosynkConfiguration));
    }

    @Test
    @DisplayName("Assert no.nav.okosynk.io.os batch user is as expected when the corresponding property is set")
    public void testHentOsBatchbrukerWhenPropertySet() {

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration(true, true);
        final String expectedUser = "Residents";
        okosynkConfiguration.setSystemProperty(Constants.BATCH_TYPE.OS.getBatchBrukerKey(), expectedUser);

        assertEquals(expectedUser, this.hentOsBatchBruker(okosynkConfiguration));
    }

    @Test
    @DisplayName("Assert ur batch user is as expected when the corresponding property is not set")
    void testHentUrBatchbrukerWhenPropertyNotSet() {

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration(true, true);

        final String expectedUser = "srvbokosynk002";

        okosynkConfiguration.clearSystemProperty(Constants.BATCH_TYPE.UR.getBatchBrukerKey());
        assertEquals(expectedUser, this.hentUrBatchBruker(okosynkConfiguration));
    }

    @Test
    @DisplayName("Assert ur batch user is as expected when the corresponding property is set")
    void testHentUrBatchbrukerWhenPropertySet() {

        enteringTestHeaderLogger.debug(null);

        final IOkosynkConfiguration okosynkConfiguration = new FakeOkosynkConfiguration(true, true);
        final String expectedUser = "JohnCage";
        okosynkConfiguration.setSystemProperty(Constants.BATCH_TYPE.UR.getBatchBrukerKey(), expectedUser);

        assertEquals(expectedUser, this.hentUrBatchBruker(okosynkConfiguration));
    }

    @Test
    void when_os_the_consumer_statistics_name_should_reflect_it() {

        enteringTestHeaderLogger.debug(null);

        when_x_the_consumer_statistics_name_should_reflect_it(BATCH_TYPE.OS);
    }

    @Test
    void when_ur_the_consumer_statistics_name_should_reflect_it() {

        enteringTestHeaderLogger.debug(null);

        when_x_the_consumer_statistics_name_should_reflect_it(BATCH_TYPE.UR);
    }

    private void when_x_the_consumer_statistics_name_should_reflect_it(final BATCH_TYPE batchType) {

        final String expectedConsumerStatisticsName =
                batchType.name() + " - " + batchType.getName();
        final String actualConsumerStatisticsName =
                batchType.getConsumerStatisticsName();

        System.out.println("actualConsumerStatisticsName: " + actualConsumerStatisticsName);

        assertEquals(expectedConsumerStatisticsName, actualConsumerStatisticsName);
    }
}