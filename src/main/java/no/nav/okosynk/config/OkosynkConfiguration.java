package no.nav.okosynk.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.Validate;
import org.javatuples.Quintet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkosynkConfiguration
    implements IOkosynkConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OkosynkConfiguration.class);

    private final CompositeConfiguration compositeConfigurationForFirstPriority = new CompositeConfiguration();
    private final CompositeConfiguration compositeConfigurationForSecondPriority = new CompositeConfiguration();
    private final SystemConfiguration systemConfiguration;
    private static IOkosynkConfiguration instance = null;

    private OkosynkConfiguration(final String applicationPropertiesFileName) {

        final SystemConfiguration systemConfiguration = new SystemConfiguration();
        this.compositeConfigurationForSecondPriority.addConfiguration(systemConfiguration);
        this.systemConfiguration = systemConfiguration;

        this.compositeConfigurationForFirstPriority.addConfiguration(new EnvironmentConfiguration());
        this.compositeConfigurationForSecondPriority.addConfiguration(new EnvironmentConfiguration());
        try {
            this
                .compositeConfigurationForSecondPriority
                .addConfiguration(
                    new Configurations()
                        .properties(new File(applicationPropertiesFileName))
                );
            logger.info("Konfigurasjon lastet fra {}", applicationPropertiesFileName);
        } catch (ConfigurationException e) {
            logger.info("Fant ikke {}", applicationPropertiesFileName);
        }

        copySomePropertiesToSystemPropertiesToSatisfyExternalLibraries();

        logger.info("Konfigurasjon lastet fra system- og miljøvariabler");
    }

    // TODO: Not very beautiful to parametrize the applicationPropertiesFileName. What if the method is called with another applicationPropertiesFileName parameter?
    public static IOkosynkConfiguration getInstance(final String applicationPropertiesFileName) {

        if (applicationPropertiesFileName == null) {
            final String msg = "The parameter applicationPropertiesFileName is null";
            logger.error(msg);
            throw new NullPointerException(msg);
        }

        if (OkosynkConfiguration.instance == null) {
            OkosynkConfiguration.instance = new OkosynkConfiguration(applicationPropertiesFileName);
        }

        return OkosynkConfiguration.instance;
    }

    @Override
    public void setBoolean(final String key, final boolean value) {

        compositeConfigurationForSecondPriority.addProperty(key, value);
    }

    // --------- Getters BEGIN: ------------------------------------------------
    //
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

        final String firstPriorityKey  = convertToFirstPriorityKey(key);
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
    //
    // --------- Getters END ---------------------------------------------------

    private boolean containsEnvironmentVariableCaseSensitively(final String environmentVariableToFind) {

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

    @Override
    public void setSystemProperty(final String key, final String value) {
        this.systemConfiguration.setProperty(key, value);
    }

    @Override
    public void clearSystemProperty(final String key) {
        this.systemConfiguration.clearProperty(key);
    }

    private void checkRequired(final String key) {
        Validate.validState(compositeConfigurationForSecondPriority.containsKey(key), "Fant ikke konfigurasjonsnøkkel %s", key);
    }

    private void copySomePropertiesToSystemPropertiesToSatisfyExternalLibraries() {

        // Since the used libraries do
        // not know of any IOkosynkConfiguration,
        // they must have some system properties explicitly set:
        final List<Quintet<String, String, Boolean, String, String>> propertyInfos =
            new ArrayList<Quintet<String, String, Boolean, String, String>>() {{
                add(new Quintet<>(Constants.SRVBOKOSYNK_USERNAME_KEY  , Constants.SRVBOKOSYNK_PASSWORD_EXT_KEY  , true , null         , null));
                add(new Quintet<>(Constants.SRVBOKOSYNK_PASSWORD_KEY  , Constants.SYSTEM_USER_PASSWORD_EXT_KEY  , true , "***********", null));
                add(new Quintet<>(Constants.DISABLE_METRICS_REPORT_KEY, Constants.DISABLE_METRICS_REPORT_EXT_KEY, false, null         , null));
                add(new Quintet<>(Constants.TILLAT_MOCK_PROPERTY_KEY  , Constants.TILLAT_MOCK_PROPERTY_EXT_KEY  , false, null         , null));
            }};

        propertyInfos
            .stream()
            .forEach(
                (propertyInfo) ->
                {
                    final String  okosynkKey             = propertyInfo.getValue0();
                    final String  externalKey            = propertyInfo.getValue1();
                    final boolean mandatory              = propertyInfo.getValue2();
                    final String  reportValuePlaceHolder = propertyInfo.getValue3();
                    final String  defaultValue           = propertyInfo.getValue4();
                    final String  tempValue;
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

                        logger.debug(
                              "The property value {} "
                            + "is copied from some property source {} "
                            + "to the system property {}.", reportedValue, okosynkKey, externalKey);
                    }
                }
            );
    }

    /**
     * In a NAIS environment, system properties of the form x.y are
     * all converted to corresponding environment variables X_Y.
     * @param originalKey
     * @return
     */
    private String convertToFirstPriorityKey(final String originalKey) {
        final String convertedKey = originalKey.toUpperCase().replace('.', '_');

        return convertedKey;
    }
}
