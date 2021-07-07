package no.nav.okosynk.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class AbstractOkosynkConfiguration implements IOkosynkConfiguration {

    public AbstractOkosynkConfiguration() {
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
        return getBoolean(batchType.getShouldRunKey(), true);
    }

    @Override
    public Collection<String> getOpprettetAvValuesForFinn(final Constants.BATCH_TYPE batchType) {
        final Collection<String> opprettetAvValuesForFinn =
                new ArrayList<String>() {{
                    add(getBatchBruker(batchType));
                    add(getNaisAppName());
                }};
        return Collections.unmodifiableCollection(opprettetAvValuesForFinn);
    }

    @Override
    public boolean shouldAuthenticateUsingAzureADAgainstOppgave() {
        return getBoolean(Constants.SHOULD_AUTHENTICATE_USING_AZURE_AD_AGAINST_OPPGAVE_KEY, false);
    }

    @Override
    public String getNaisAppName() {
        return getRequiredString(Constants.NAIS_APP_NAME_KEY);
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
    public String getAzureAppTenantId() {
        return getRequiredString("AZURE_APP_TENANT_ID");
    }

    @Override
    public String getAzureAppTokenUrl() {
        final String azureAppWellKnownUrl = getAzureAppWellKnownUrl();
        try {
            final URL azureAppTokenUrl = new URL(azureAppWellKnownUrl);
            final String azureAppTokenUrlString = azureAppTokenUrl.getProtocol() + "://" + azureAppTokenUrl.getHost() + ":" + azureAppTokenUrl.getPort() + "/" + getAzureAppTenantId() + "/oauth2/v2.0/token";
            return azureAppTokenUrlString;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Exception received when trying to calculate the azureAppTokenUrl");
        }
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