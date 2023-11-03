package no.nav.okosynk.config;

import no.nav.okosynk.config.Constants.BATCH_TYPE;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ConstantsTest {

    @Test
    @DisplayName("Assert ur batch user has no default value")
    void testHentUrBatchbrukerWhenPropertyNotSet() {
        final OkosynkConfiguration okosynkConfiguration = mock(OkosynkConfiguration.class);

        okosynkConfiguration.clearSystemProperty(Constants.OPPGAVE_USERNAME);
        assertThat(okosynkConfiguration.getString(Constants.OPPGAVE_USERNAME)).isNull();
    }

    @Test
    void when_os_the_consumer_statistics_name_should_reflect_it() {

        when_x_the_consumer_statistics_name_should_reflect_it(BATCH_TYPE.OS);
    }

    @Test
    void when_ur_the_consumer_statistics_name_should_reflect_it() {

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
