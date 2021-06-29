package no.nav.okosynk.config;

import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.javatuples.Quintet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class OkosynkConfiguration
        extends AbstractOkosynkConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OkosynkConfiguration.class);
    private static IOkosynkConfiguration singleton = null;
    private final CompositeConfiguration compositeConfigurationForFirstPriority = new CompositeConfiguration();
    private final CompositeConfiguration compositeConfigurationForSecondPriority = new CompositeConfiguration();
    private final SystemConfiguration systemConfiguration;

    private OkosynkConfiguration(final String applicationPropertiesFileName) {

        final SystemConfiguration systemConfiguration = new SystemConfiguration();
        this.compositeConfigurationForSecondPriority.addConfiguration(systemConfiguration);
        this.systemConfiguration = systemConfiguration;

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

    /**
     * NB! If this method is called more than once,
     * and the subsequent calls have a different
     * applicationPropertiesFileName parameter from the first call,
     * that parameter will be neglected and the instance created
     * upon the first call will be returned.
     *
     * @param applicationPropertiesFileName
     * @return
     */
    public static IOkosynkConfiguration getInstance(final String applicationPropertiesFileName) {

        if (applicationPropertiesFileName == null) {
            final String msg = "The parameter applicationPropertiesFileName is null";
            logger.error(msg);
            throw new NullPointerException(msg);
        }

        if (OkosynkConfiguration.singleton == null) {
            OkosynkConfiguration.singleton = new OkosynkConfiguration(applicationPropertiesFileName);
        }

        return OkosynkConfiguration.singleton;
    }

    @Override
    public String getString(final String key) {

        final String firstPriorityKey = convertToFirstPriorityKey(key);
        final String secondPriorityKey = key;
        final String value;

        if (containsEnvironmentVariableCaseSensitively(firstPriorityKey)) {
            value = compositeConfigurationForFirstPriority.getString(firstPriorityKey);
        } else {
            value = compositeConfigurationForSecondPriority.getString(secondPriorityKey);
        }

        return value;
    }

    @Override
    public String getString(final String key, final String defaultValue) {

        final String firstPriorityKey = convertToFirstPriorityKey(key);
        final String secondPriorityKey = key;
        final String value;
        if (containsEnvironmentVariableCaseSensitively(firstPriorityKey)) {
            value = compositeConfigurationForFirstPriority.getString(firstPriorityKey);
        } else {
            value = compositeConfigurationForSecondPriority.getString(secondPriorityKey, defaultValue);
        }

        return value;
    }

    @Override
    public String getRequiredString(final String key) {

        final String firstPriorityKey = convertToFirstPriorityKey(key);
        final String secondPriorityKey = key;
        final String value;
        if (containsEnvironmentVariableCaseSensitively(firstPriorityKey)) {
            value = compositeConfigurationForFirstPriority.getString(firstPriorityKey);
        } else {
            checkRequired(secondPriorityKey);
            value = compositeConfigurationForSecondPriority.getString(secondPriorityKey);
        }

        return value;
    }

    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {

        final String firstPriorityKey = convertToFirstPriorityKey(key);
        final String secondPriorityKey = key;
        final boolean value;
        if (containsEnvironmentVariableCaseSensitively(firstPriorityKey)) {
            value = compositeConfigurationForFirstPriority.getBoolean(firstPriorityKey);
        } else {
            value =
                    compositeConfigurationForSecondPriority.getBoolean(secondPriorityKey, defaultValue);
        }

        return value;
    }

    @Override
    public int getRequiredInt(final String key) {

        final String firstPriorityKey = convertToFirstPriorityKey(key);
        final String secondPriorityKey = key;
        final int value;
        if (containsEnvironmentVariableCaseSensitively(firstPriorityKey)) {
            value = compositeConfigurationForFirstPriority.getInt(firstPriorityKey);
        } else {
            checkRequired(secondPriorityKey);
            value = compositeConfigurationForSecondPriority.getInt(secondPriorityKey);
        }
        return value;
    }

    @Override
    public void setSystemProperty(final String key, final String value) {
        this.systemConfiguration.setProperty(key, value);
    }

    @Override
    public void clearSystemProperty(final String key) {
        this.systemConfiguration.clearProperty(key);
    }

    private void checkRequired(final String key) {
        Validate.validState(compositeConfigurationForSecondPriority.containsKey(key),
                "Fant ikke konfigurasjonsnøkkel %s", key);
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

    private void copySomePropertiesToSystemPropertiesToSatisfyExternalLibraries() {

        // Since the used libraries do
        // not know of any IOkosynkConfiguration,
        // they must have some system properties explicitly set:
        final List<Quintet<String, String, Boolean, String, String>> propertyInfos =
                new ArrayList<Quintet<String, String, Boolean, String, String>>() {{
                    add(new Quintet<>(Constants.DISABLE_METRICS_REPORT_KEY,
                            Constants.DISABLE_METRICS_REPORT_EXT_KEY, false, null, null));
                    add(new Quintet<>(Constants.TILLAT_MOCK_PROPERTY_KEY,
                            Constants.TILLAT_MOCK_PROPERTY_EXT_KEY, false, null, null));
                }};

        propertyInfos
                .stream()
                .forEach(
                        (propertyInfo) -> {
                            final String okosynkKey = propertyInfo.getValue0();
                            final String externalKey = propertyInfo.getValue1();
                            final boolean mandatory = propertyInfo.getValue2();
                            final String reportValuePlaceHolder = propertyInfo.getValue3();
                            final String defaultValue = propertyInfo.getValue4();
                            final String tempValue;
                            if (mandatory) {
                                tempValue = this.getRequiredString(okosynkKey);
                            } else {
                                tempValue = this.getString(okosynkKey);
                            }

                            final String value = (tempValue == null) ? defaultValue : tempValue;

                            if ((value != null) && (this.systemConfiguration.getProperty(externalKey) == null)) {

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

        final Iterator<String> keyIterator = compositeConfigurationForFirstPriority.getKeys();
        boolean containsEnvironmentVariableCaseSensitively = false;
        while (keyIterator.hasNext()) {
            final String environmentVariable = keyIterator.next();
            if (environmentVariable.equals(environmentVariableToFind)) {
                containsEnvironmentVariableCaseSensitively = true;
                break;
            }
        }
        return containsEnvironmentVariableCaseSensitively;
    }

    /**
     * In a NAIS environment, system properties of the form x.y are all converted to corresponding
     * environment variables X_Y.
     *
     * @param originalKey
     * @return
     */
    private String convertToFirstPriorityKey(final String originalKey) {
        final String convertedKey = originalKey.toUpperCase().replace('.', '_');
        return convertedKey;
    }

    private void addVaultProperties(final CompositeConfiguration compositeConfiguration) {
        final Configuration baseConfig = new BaseConfiguration();
        Stream.of(
                new ImmutablePair<>("SRVBOKOSYNK001_USERNAME", "/secrets/serviceuser/okosynk/srvbokosynk001/username"),
                new ImmutablePair<>("SRVBOKOSYNK001_PASSWORD", "/secrets/serviceuser/okosynk/srvbokosynk001/password"),
                new ImmutablePair<>("SRVBOKOSYNK002_USERNAME", "/secrets/serviceuser/okosynk/srvbokosynk002/username"),
                new ImmutablePair<>("SRVBOKOSYNK002_PASSWORD", "/secrets/serviceuser/okosynk/srvbokosynk002/password"),
                new ImmutablePair<>("OSFTPCREDENTIALS_USERNAME", "/secrets/serviceuser/okosynk/srvokosynksftp/username"),
                new ImmutablePair<>("OSFTPCREDENTIALS_PASSWORD", "/secrets/serviceuser/okosynk/srvokosynksftp/password"),
                new ImmutablePair<>("URFTPCREDENTIALS_USERNAME", "/secrets/serviceuser/okosynk/srvokosynksftp/username"),
                new ImmutablePair<>("URFTPCREDENTIALS_PASSWORD", "/secrets/serviceuser/okosynk/srvokosynksftp/password")
        ).forEach(pair -> addVaultProperty(baseConfig, pair.left, pair.right));
        compositeConfiguration.addConfiguration(baseConfig);
    }

    private void addVaultProperty(
            final Configuration baseConfig,
            final String propertyKey,
            final String fileName) {
        logger.info("About to add property {}, reading from file {} ", propertyKey, fileName);
        final String propertyValue = readStringFromFile(fileName);
        baseConfig.addProperty(propertyKey, propertyValue);
        logger.info("Property {} now contains the value: {}", propertyKey, (propertyValue == null ? null : propertyKey.contains("PASSWORD") ? "***<something>***" : propertyValue));
    }

    private String readStringFromFile(final String fileName) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (NoSuchFileException e) {
            logger.error("The file " + fileName + " does not exist");
        } catch (Throwable e) {
            logger.error("The file " + fileName + " could not be read", e);
        }
        return content;
    }
}