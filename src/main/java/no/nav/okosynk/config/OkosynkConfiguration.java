package no.nav.okosynk.config;

import no.nav.okosynk.config.homemade.*;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import no.nav.okosynk.config.homemade.Quintet;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class OkosynkConfiguration
        extends AbstractOkosynkConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OkosynkConfiguration.class);
    private static IOkosynkConfiguration singleton = null;
    private final CompositeConfiguration compositeConfigurationForFirstPriority = new CompositeConfiguration();
    private final CompositeConfiguration compositeConfigurationForSecondPriority = new CompositeConfiguration();
    private final SystemConfiguration systemConfiguration;

    private OkosynkConfiguration(
            final String applicationPropertiesFileName) {

        super();

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

    public static IOkosynkConfiguration getSingletonInstance() {
        return OkosynkConfiguration.singleton;
    }

    @Override
    public String getString(final String key) {
        return (containsEnvironmentVariableCaseSensitively(convertToFirstPriorityKey(key))) ? compositeConfigurationForFirstPriority.getString(convertToFirstPriorityKey(key))
                : compositeConfigurationForSecondPriority.getString(key);
    }

    @Override
    public String getString(final String key, final String defaultValue) {
        return (containsEnvironmentVariableCaseSensitively(convertToFirstPriorityKey(key))) ? compositeConfigurationForFirstPriority.getString(convertToFirstPriorityKey(key))
                : compositeConfigurationForSecondPriority.getString(key, defaultValue);
    }

    @Override
    public String getRequiredString(final String key) {
        if (containsEnvironmentVariableCaseSensitively(convertToFirstPriorityKey(key))) {
            return compositeConfigurationForFirstPriority.getString(convertToFirstPriorityKey(key));
        } else {
            checkRequired(key);
            return compositeConfigurationForSecondPriority.getString(key);
        }
    }

    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {
        return (containsEnvironmentVariableCaseSensitively(convertToFirstPriorityKey(key))) ? compositeConfigurationForFirstPriority.getBoolean(convertToFirstPriorityKey(key))
                : compositeConfigurationForSecondPriority.getBoolean(key, defaultValue);
    }

    @Override
    public int getRequiredInt(final String key) {
        if (containsEnvironmentVariableCaseSensitively(convertToFirstPriorityKey(key))) {
            return compositeConfigurationForFirstPriority.getInt(convertToFirstPriorityKey(key));
        } else {
            checkRequired(key);
            return compositeConfigurationForSecondPriority.getInt(key);
        }
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
                new ArrayList<>() {{
                    add(new Quintet<>(Constants.DISABLE_METRICS_REPORT_KEY,
                            Constants.DISABLE_METRICS_REPORT_EXT_KEY, false, null, null));
                    add(new Quintet<>(Constants.TILLAT_MOCK_PROPERTY_KEY,
                            Constants.TILLAT_MOCK_PROPERTY_EXT_KEY, false, null, null));
                }};

        propertyInfos.forEach(
                (propertyInfo) -> {
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

    private record Tuple2(String _1, String _2){}
    private void addVaultProperties(final CompositeConfiguration compositeConfiguration) {
        final Configuration baseConfig = new BaseConfiguration();
        Stream.of(
                new Tuple2("SRVBOKOSYNK001_USERNAME", "/secrets/serviceuser/okosynk/srvbokosynk001/username"),
                new Tuple2("SRVBOKOSYNK001_PASSWORD", "/secrets/serviceuser/okosynk/srvbokosynk001/password"),
                new Tuple2("SRVBOKOSYNK002_USERNAME", "/secrets/serviceuser/okosynk/srvbokosynk002/username"),
                new Tuple2("SRVBOKOSYNK002_PASSWORD", "/secrets/serviceuser/okosynk/srvbokosynk002/password"),
                new Tuple2("OSFTPCREDENTIALS_USERNAME", "/secrets/serviceuser/okosynk/srvokosynksftp/username"),
                new Tuple2("OSFTPCREDENTIALS_PASSWORD", "/secrets/serviceuser/okosynk/srvokosynksftp/password"),
                new Tuple2("URFTPCREDENTIALS_USERNAME", "/secrets/serviceuser/okosynk/srvokosynksftp/username"),
                new Tuple2("URFTPCREDENTIALS_PASSWORD", "/secrets/serviceuser/okosynk/srvokosynksftp/password")
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
