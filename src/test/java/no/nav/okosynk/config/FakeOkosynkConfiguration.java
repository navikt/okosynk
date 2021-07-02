package no.nav.okosynk.config;

public class FakeOkosynkConfiguration
        extends AbstractOkosynkConfiguration {

    public FakeOkosynkConfiguration() {
        this(true, true);
    }

    public FakeOkosynkConfiguration(boolean shouldRunOsCommandLineOverride, boolean shouldRunUrCommandLineOverride) {
        super(shouldRunOsCommandLineOverride, shouldRunUrCommandLineOverride);
    }

    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {

        return Boolean
                .parseBoolean(
                        System.getProperty(key,
                                Boolean.valueOf(defaultValue).toString())
                );
    }

    @Override
    public int getRequiredInt(final String key) {
        final String value = System.getProperty(key);
        if (value == null) {
            throw new IllegalStateException(String.format("There is no system property set for the key " + key));
        }
        return Integer.valueOf(value);
    }

    @Override
    public String getRequiredString(final String key) {

        final String value = System.getProperty(key);
        if (value == null) {
            throw new IllegalStateException(String.format("There is no system property set for the key " + key));
        }
        return value;
    }

    @Override
    public void clearSystemProperty(final String key) {
        System.clearProperty(key);
    }

    @Override
    public void setSystemProperty(final String key, final String value) {
        System.setProperty(key, value);
    }

    @Override
    public String getString(final String key) {
        return System.getProperty(key);
    }

    @Override
    public String getString(final String key, final String defaulValue) {
        return System.getProperty(key, defaulValue);
    }

    @Override
    public String getNavTrustStorePath() {
        return getRequiredString(Constants.NAV_TRUSTSTORE_PATH_KEY);
    }

    @Override
    public String getNavTrustStorePassword() {
        return getRequiredString(Constants.NAV_TRUSTSTORE_PASSWORD_KEY);
    }

    @Override
    public String getPrometheusAddress(final String defaultPrometheusAddress) {
        return getString(Constants.PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY, defaultPrometheusAddress);
    }

    @Override
    public String getBatchBruker(final Constants.BATCH_TYPE batchType) {
        return getString(batchType.getBatchBrukerKey(), batchType.getBatchBrukerDefaultValue());
    }

    @Override
    public String getBatchBrukerPassword(final Constants.BATCH_TYPE batchType) {
        return getString(batchType.getBatchBrukerPasswordKey());
    }
}