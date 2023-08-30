package no.nav.okosynk.config;

import java.util.Collection;

public interface IOkosynkConfiguration {

    boolean getBoolean(final String key, final boolean defaultValue);
    int getRequiredInt(final String key);
    String getString(final String key);
    String getString(final String key, final String defaulValue);
    String getRequiredString(final String key);
    void clearSystemProperty(final String key);
    void setSystemProperty(final String key, final String value);

    String getNavTrustStorePath();

    String getNavTrustStorePassword();

    String getPrometheusAddress(final String defaultPrometheusAddress);

    boolean shouldRun(final Constants.BATCH_TYPE batchType);

    /**
     * @return Verdien som oppgavefeltet opprettet_av blir utstyrt med for oppgaver opprettet av okosynk.
     */
    Collection<String> getOpprettetAvValuesForFinn(final Constants.BATCH_TYPE batchType);

    String getNaisAppName();

    String getBatchBruker(final Constants.BATCH_TYPE batchType);

    String getBatchBrukerPassword(final Constants.BATCH_TYPE batchType);

    String getAzureAppWellKnownUrl();

    String getAzureAppTenantId();

    String getFtpHostUrl(final Constants.BATCH_TYPE batchType);

    String getFtpUser(final Constants.BATCH_TYPE batchType);

    String getFtpPassword(final Constants.BATCH_TYPE batchType);

    String getFtpCharsetName(final Constants.BATCH_TYPE batchType, final String ftpCharsetNameDefault);

    String getAzureAppTokenUrl();

    String getAzureAppClientId();

    String getAzureAppClientSecret();

    String getAzureAppScopes();

    String getSecureHttpProxyUrl();

    String getPdlUrl();
}
