package no.nav.okosynk.consumer.hjelper;

import no.nav.okosynk.config.IOkosynkConfiguration;

public class ConsumerHjelper {

    private static final String SHOULD_SIMULATE_ERROR_PROPERTY_KEY_SUFFIX = ".simulate.error";

    static String mocketTekst      = "<b>MOCKET</b> ";
    static String ikkeMocketTekst  = "";
    static String avbruddTekst     = "<b>AVBRUDD+</b>";
    static String ikkeAvbruddTekst = "";

    public static String getMockStatus(
        final IOkosynkConfiguration okosynkConfiguration,
        final String                mockPropertyKey) {

        final boolean tillatMock =
            okosynkConfiguration.getBoolean(no.nav.okosynk.config.Constants.TILLAT_MOCK_PROPERTY_KEY, false);
        final boolean withMock   =
            okosynkConfiguration.getBoolean(mockPropertyKey, false);

        return (tillatMock && withMock) ? mocketTekst : ikkeMocketTekst;
    }

    public static String getAvbruddStatus(
        final IOkosynkConfiguration okosynkConfiguration,
        final String                mockPropertyKey) {

        final boolean tillatMock          =
            okosynkConfiguration.getBoolean(no.nav.okosynk.config.Constants.TILLAT_MOCK_PROPERTY_KEY, false);
        final boolean withMock            =
            okosynkConfiguration.getBoolean(mockPropertyKey, false);
        final boolean shouldSimulateError =
            okosynkConfiguration.getBoolean(
                mockPropertyKey + SHOULD_SIMULATE_ERROR_PROPERTY_KEY_SUFFIX,
                true);

        return (tillatMock && withMock && shouldSimulateError) ? avbruddTekst : ikkeAvbruddTekst;
    }
}
