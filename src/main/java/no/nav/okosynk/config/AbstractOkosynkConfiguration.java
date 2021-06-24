package no.nav.okosynk.config;

abstract class AbstractOkosynkConfiguration implements IOkosynkConfiguration {

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
}