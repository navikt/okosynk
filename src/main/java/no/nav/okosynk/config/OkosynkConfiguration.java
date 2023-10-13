package no.nav.okosynk.config;

import no.nav.okosynk.config.homemade.*;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static no.nav.okosynk.config.Constants.*;

public class OkosynkConfiguration {

    public static final String AZURE_APP_CLIENT_ID_KEY = "AZURE_APP_CLIENT_ID";
    public static final String PDL_URL_KEY = "PDL_URL";
    private static final Logger logger = LoggerFactory.getLogger(OkosynkConfiguration.class);
    private static OkosynkConfiguration singleton = null;
    private final CompositeConfiguration compositeConfigurationForFirstPriority = new CompositeConfiguration();
    private final CompositeConfiguration compositeConfigurationForSecondPriority = new CompositeConfiguration();
    private final SystemConfiguration systemConfiguration;

    private OkosynkConfiguration(
            final String applicationPropertiesFileName) {

        this.systemConfiguration = new SystemConfiguration();
        this.compositeConfigurationForSecondPriority.addConfiguration(systemConfiguration);

        this.compositeConfigurationForFirstPriority.addConfiguration(new EnvironmentConfiguration());
        addVaultProperties(compositeConfigurationForFirstPriority);
        this.compositeConfigurationForSecondPriority.addConfiguration(new EnvironmentConfiguration());

        try {
            this.compositeConfigurationForSecondPriority
                    .addConfiguration(
                            new Configurations()
                                    .properties(new File(applicationPropertiesFileName))
                    );
            logger.info("Konfigurasjon lastet fra {}", applicationPropertiesFileName);
        } catch (ConfigurationException e) {
            logger.info("Fant ikke {}", applicationPropertiesFileName);
        }

        final String pomPropertiesFileName = "properties-from-pom.properties";
        try {
            this.compositeConfigurationForSecondPriority
                    .addConfiguration(
                            new Configurations()
                                    .properties(new File(pomPropertiesFileName))
                    );
            logger.info("Konfigurasjon lastet fra {}", pomPropertiesFileName);
        } catch (ConfigurationException e) {
            logger.info("Fant ikke {}", pomPropertiesFileName);
        }

        copySomePropertiesToSystemPropertiesToSatisfyExternalLibraries();

        logger.info("Konfigurasjon lastet fra system- og miljøvariabler");
    }

    public static void createAndReplaceSingletonInstance(
            final String applicationPropertiesFileName) {

        if (applicationPropertiesFileName == null) {
            final String msg = "The parameter applicationPropertiesFileName is null";
            logger.error(msg);
            throw new NullPointerException(msg);
        }

        OkosynkConfiguration.singleton =
                new OkosynkConfiguration(applicationPropertiesFileName);
    }

    public static OkosynkConfiguration getSingletonInstance() {
        return OkosynkConfiguration.singleton;
    }

    public String getString(final String key) {
        return (containsEnvironmentVariableCaseSensitively(convertToFirstPriorityKey(key))) ? compositeConfigurationForFirstPriority.getString(convertToFirstPriorityKey(key))
                : compositeConfigurationForSecondPriority.getString(key);
    }

    public String getString(final String key, final String defaultValue) {
        return (containsEnvironmentVariableCaseSensitively(convertToFirstPriorityKey(key))) ? compositeConfigurationForFirstPriority.getString(convertToFirstPriorityKey(key))
                : compositeConfigurationForSecondPriority.getString(key, defaultValue);
    }

    public String getRequiredString(final String key) {
        if (containsEnvironmentVariableCaseSensitively(convertToFirstPriorityKey(key))) {
            return compositeConfigurationForFirstPriority.getString(convertToFirstPriorityKey(key));
        } else {
            checkRequired(key);
            return compositeConfigurationForSecondPriority.getString(key);
        }
    }

    public boolean getBoolean(final String key, final boolean defaultValue) {
        return (containsEnvironmentVariableCaseSensitively(convertToFirstPriorityKey(key))) ? compositeConfigurationForFirstPriority.getBoolean(convertToFirstPriorityKey(key))
                : compositeConfigurationForSecondPriority.getBoolean(key, defaultValue);
    }

    public int getRequiredInt(final String key) {
        if (containsEnvironmentVariableCaseSensitively(convertToFirstPriorityKey(key))) {
            return compositeConfigurationForFirstPriority.getInt(convertToFirstPriorityKey(key));
        } else {
            checkRequired(key);
            return compositeConfigurationForSecondPriority.getInt(key);
        }
    }

    public void setSystemProperty(final String key, final String value) {
        this.systemConfiguration.setProperty(key, value);
    }

    public void clearSystemProperty(final String key) {
        this.systemConfiguration.clearProperty(key);
    }

    private void checkRequired(final String key) {
        Validate.validState(compositeConfigurationForSecondPriority.containsKey(key),
                "Fant ikke konfigurasjonsnøkkel %s", key);
    }

    public String getNavTrustStorePath() {
        return getRequiredString(NAV_TRUSTSTORE_PATH_KEY);
    }

    public String getNavTrustStorePassword() {
        return getRequiredString(NAV_TRUSTSTORE_PASSWORD_KEY);
    }

    public String getPrometheusAddress(final String defaultPrometheusAddress) {
        return getString(PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY, defaultPrometheusAddress);
    }

    private void copySomePropertiesToSystemPropertiesToSatisfyExternalLibraries() {

        // Since the used libraries do
        // not know of any IOkosynkConfiguration,
        // they must have some system properties explicitly set:
        final List<Quintet<String, String, Boolean, String, String>> propertyInfos =
                asList(
                        new Quintet<>(DISABLE_METRICS_REPORT_KEY, DISABLE_METRICS_REPORT_EXT_KEY, false, null, null),
                        new Quintet<>(TILLAT_MOCK_PROPERTY_KEY, TILLAT_MOCK_PROPERTY_EXT_KEY, false, null, null)
                );

        propertyInfos.forEach(
                propertyInfo -> {
                    final String okosynkKey = propertyInfo._0();
                    final String externalKey = propertyInfo._1();
                    final boolean mandatory = propertyInfo._2();
                    final String reportValuePlaceHolder = propertyInfo._3();
                    final String defaultValue = propertyInfo._4();
                    final String tempValue;
                    if (mandatory) {
                        tempValue = this.getRequiredString(okosynkKey);
                    } else {
                        tempValue = this.getString(okosynkKey);
                    }

                    final String value = (tempValue == null) ? defaultValue : tempValue;

                    if ((value != null) && (this.systemConfiguration.getString(externalKey) == null)) {

                        this.systemConfiguration.setProperty(externalKey, value);
                        final String reportedValue =
                                (reportValuePlaceHolder == null)
                                        ?
                                        value
                                        :
                                        reportValuePlaceHolder;

                        logger.info(
                                "The property value {} "
                                        + "is copied from some property source {} "
                                        + "to the system property {}.", reportedValue, okosynkKey, externalKey);
                    }
                }
        );
    }

    private boolean containsEnvironmentVariableCaseSensitively(
            final String environmentVariableToFind) {
        return compositeConfigurationForFirstPriority.containsKey(environmentVariableToFind);
    }

    /**
     * In a NAIS environment, system properties of the form x.y are all converted to corresponding
     * environment variables X_Y.
     *
     * @param originalKey key in anycase
     * @return key in uppercase and with . replaced with _
     */
    private String convertToFirstPriorityKey(final String originalKey) {
        return originalKey.toUpperCase().replace('.', '_');
    }
    public String getNaisAppName() {
        return getRequiredString(Constants.NAIS_APP_NAME_KEY);
    }

    public String getAzureAppWellKnownUrl() {
        return getRequiredString("AZURE_APP_WELL_KNOWN_URL");
    }

    public String getAzureAppTenantId() {
        return getRequiredString("AZURE_APP_TENANT_ID");
    }

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

    public String getAzureAppClientId() {
        return getRequiredString(AZURE_APP_CLIENT_ID_KEY);
    }

    public String getAzureAppClientSecret() {
        return getRequiredString("AZURE_APP_CLIENT_SECRET");
    }

    public String getAzureAppScopes() {
        return Stream.of(getRequiredString("AZURE_APP_SCOPE_OPPGAVE")).collect(joining(" "));
    }

    public String getSecureHttpProxyUrl() {
        return getString("HTTPS_PROXY");
    }

    public String getPdlUrl() {
        return getRequiredString(PDL_URL_KEY);
    }

    private record Tuple2(String _1, String _2) {
    }

    private void addVaultProperties(final CompositeConfiguration compositeConfiguration) {
        final Configuration baseConfig = new BaseConfiguration();
        Stream.of(
                new Tuple2(OPPGAVE_USERNAME, "/secrets/oppgavecredentials/username"),
                new Tuple2(OPPGAVE_PASSWORD, "/secrets/oppgavecredentials/password"),
                new Tuple2(FTP_USERNAME, "/secrets/sftpcredentials/username"),
                new Tuple2(FTP_PRIVATEKEY, "/secrets/sftpcredentials/privateKey"),
                new Tuple2(FTP_HOSTKEY, "/secrets/sftpcredentials/hostKey")
        ).forEach(pair -> addVaultProperty(baseConfig, pair._1(), pair._2()));
        compositeConfiguration.addConfiguration(baseConfig);
    }

    private void addVaultProperty(
            final Configuration baseConfig,
            final String propertyKey,
            final String fileName) {
        logger.info("About to add property {}, reading from file {} ", propertyKey, fileName);
        final String propertyValue = readStringFromFile(fileName);
        baseConfig.setProperty(propertyKey, propertyValue);
        logger.info("Property {} now contains the value: {}", propertyKey,
                (propertyValue == null ? null :
                        Stream.of("PASSWORD", "PRIVATE_KEY", "HOST_KEY").anyMatch(propertyKey::contains) ? "***<something>***" : propertyValue));
    }

    private String readStringFromFile(final String fileName) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (NoSuchFileException e) {
            logger.error("The file {} does not exist", fileName);
        } catch (Exception e) {
            logger.error("The file could not be read", e);
        }
        return content;
    }

    public BATCH_TYPE getBatchType() {
        return getRequiredString(Constants.SHOULD_RUN_OS_OR_UR_KEY).equals(Constants.BATCH_TYPE.OS.name()) ? BATCH_TYPE.OS : BATCH_TYPE.UR;
    }

}
