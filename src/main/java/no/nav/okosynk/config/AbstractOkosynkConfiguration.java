package no.nav.okosynk.config;

import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class AbstractOkosynkConfiguration implements IOkosynkConfiguration {

    // If false, overrides, othewise not
    private boolean shouldRunOsCommandLineOverride;
    // If false, overrides, othewise not
    private boolean shouldRunUrCommandLineOverride;

    public AbstractOkosynkConfiguration(boolean shouldRunOsCommandLineOverride, boolean shouldRunUrCommandLineOverride) {
        this.shouldRunOsCommandLineOverride = shouldRunOsCommandLineOverride;
        this.shouldRunUrCommandLineOverride = shouldRunUrCommandLineOverride;
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
    public boolean shouldRun(final Constants.BATCH_TYPE batchType) {

        final boolean shouldRunTemp = getBoolean(batchType.getShouldRunKey(), true);
        final boolean shouldRun;
        if (Constants.BATCH_TYPE.OS.equals(batchType)) {
            if (!shouldRunOsCommandLineOverride) {
                shouldRun = false;
            } else  {
                shouldRun = shouldRunTemp;
            }
        } else {
            if (!shouldRunUrCommandLineOverride) {
                shouldRun = false;
            } else  {
                shouldRun = shouldRunTemp;
            }
        }
        return shouldRun;
    }

    @Override
    public String getBatchBruker(final Constants.BATCH_TYPE batchType) {
        return getString(batchType.getBatchBrukerKey(), batchType.getBatchBrukerDefaultValue());
    }

    @Override
    public String getBatchBrukerPassword(final Constants.BATCH_TYPE batchType) {
        return getString(batchType.getBatchBrukerPasswordKey());
    }

    @Override
    public String getFtpHostUrl(final Constants.BATCH_TYPE batchType) {
        return getString(batchType.getFtpHostUrlKey());
    }

    @Override
    public String getFtpUser(final Constants.BATCH_TYPE batchType) {
        return getString(batchType.getFtpUserKey());
    }

    @Override
    public String getFtpPassword(final Constants.BATCH_TYPE batchType) {
        return getString(batchType.getFtpPasswordKey());
    }

    @Override
    public String getFtpCharsetName(final Constants.BATCH_TYPE batchType, final String ftpCharsetNameDefault) {
        return getString(batchType.getFtpCharsetNameKey(), ftpCharsetNameDefault);
    }

    @Override
    public String getAzureAppWellKnownUrl() {
        return getRequiredString("AZURE_APP_WELL_KNOWN_URL");
    }

    @Override
    public String getAzureAppClientId() {
        return getRequiredString("AZURE_APP_CLIENT_ID");
    }

    @Override
    public String getAzureAppClientSecret() {
        return getRequiredString("AZURE_APP_CLIENT_SECRET");
    }

    @Override
    public String getAzureAppScopes() {
        return Stream.of(getRequiredString("AZURE_APP_SCOPE_OPPGAVE")).collect(Collectors.joining(" "));
    }

    @Override
    public String getSecureHttpProxyUrl() {
        return getString("HTTPS_PROXY");
    }
}