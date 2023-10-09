package no.nav.okosynk.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

public abstract class AbstractOkosynkConfiguration implements IOkosynkConfiguration {

    public static final String AZURE_APP_CLIENT_ID_KEY = "AZURE_APP_CLIENT_ID";
    public static final String PDL_URL_KEY = "PDL_URL";

    protected AbstractOkosynkConfiguration() {
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
        return getRequiredString(Constants.SHOULD_RUN_OS_OR_UR_KEY).equals(batchType.name());
    }

    @Override
    public Collection<String> getOpprettetAvValuesForFinn(final Constants.BATCH_TYPE batchType) {
        return unmodifiableList(asList(getBatchBruker(batchType), getNaisAppName()));
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
            return azureAppTokenUrl.getProtocol() + "://"
                    + azureAppTokenUrl.getHost() + ":" + azureAppTokenUrl.getPort() + "/"
                    + getAzureAppTenantId() + "/oauth2/v2.0/token";
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Exception received when trying to calculate the azureAppTokenUrl");
        }
    }

    @Override
    public String getAzureAppClientId() {
        return getRequiredString(AZURE_APP_CLIENT_ID_KEY);
    }

    @Override
    public String getAzureAppClientSecret() {
        return getRequiredString("AZURE_APP_CLIENT_SECRET");
    }

    @Override
    public String getAzureAppScopes() {
        return Stream.of(getRequiredString("AZURE_APP_SCOPE_OPPGAVE")).collect(joining(" "));
    }

    @Override
    public String getSecureHttpProxyUrl() {
        return getString("HTTPS_PROXY");
    }

    @Override
    public String getPdlUrl() {
        return getRequiredString(PDL_URL_KEY);
    }
}
