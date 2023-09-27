package no.nav.okosynk.config;

import no.nav.okosynk.config.homemade.ConversionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class OkosynkConfigurationTest {
    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private static final Logger logger = LoggerFactory.getLogger(OkosynkConfigurationTest.class);

    @Test
    @DisplayName("Test that the OkosynkConfiguration cannot be instantiated with a null applicationPropertiesFileName parameter")
    void creationOfOkosynkConfigurationWithNullParameter() {

        enteringTestHeaderLogger.debug(null);

        assertThrows(NullPointerException.class,
                () -> OkosynkConfiguration.createAndReplaceSingletonInstance(null));
    }

    @Test
    void setSystemPropertyBySystemGetProperty() {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final String expectedKey = "xyzXYZ";
        final String expectedValue = "TullOgTøysx";
        okosynkConfiguration.setSystemProperty(expectedKey, expectedValue);
        final String actualValue = System.getProperty(expectedKey);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void setSystemPropertyByOkosynkGetProperty() {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final String expectedKey = "xyzXYZ";
        final String expectedValue = "TullOgTøysx";
        okosynkConfiguration.setSystemProperty(expectedKey, expectedValue);
        final String actualValue = okosynkConfiguration.getString(expectedKey);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void clear_system_property_by_system_get_property() {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final String expectedKey = "xyzXYZ";
        final String expectedValue = "TullOgTøysx";
        okosynkConfiguration.setSystemProperty(expectedKey, expectedValue);
        String actualValue = System.getProperty(expectedKey);
        assertEquals(expectedValue, actualValue);

        okosynkConfiguration.clearSystemProperty(expectedKey);
        actualValue = System.getProperty(expectedKey);
        assertNull(actualValue);
    }

    @Test
    void testClearSystemPropertyByOkosynkGetProperty() {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final String expectedKey = "xyzXYZ";
        final String expectedValue = "TullOgTøysx";
        okosynkConfiguration.setSystemProperty(expectedKey, expectedValue);
        String actualValue = okosynkConfiguration.getString(expectedKey);
        assertEquals(expectedValue, actualValue);

        okosynkConfiguration.clearSystemProperty(expectedKey);
        actualValue = okosynkConfiguration.getString(expectedKey);
        assertNull(actualValue);
    }

    static Stream<Arguments> foos() {
        return Stream.of(
                arguments("test_env_001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test_env_001", "TEST_ENV_001_VALx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST_ENV_001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST_ENV_001", "TEST_ENV_001_VALx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test_env_001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST_ENV_001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test.env.001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test.env.001", "TEST_ENV_001_VALX", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST.ENV.001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST.ENV.001", "TEST_ENV_001_VALX", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test.env.001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST.ENV.001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test_env_002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"),
                arguments("test_env_002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"),
                arguments("TEST_ENV_002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"),
                arguments("TEST_ENV_002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"),
                arguments("test_env_002", null, "test_env_002", "test_env_002_val", "test_env_002_val"),
                arguments("TEST_ENV_002", null, "test_env_002", "test_env_002_val", null),
                arguments("test.env.002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"),
                arguments("test.env.002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"),
                arguments("TEST.ENV.002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"),
                arguments("TEST.ENV.002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"),
                arguments("test.env.002", null, "test_env_002", "test_env_002_val", null),
                arguments("TEST.ENV.002", null, "test_env_002", "test_env_002_val", null),
                arguments("test_env_003", "test_env_003_valx", null, null, "test_env_003_valx"),
                arguments("test_env_003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"),
                arguments("TEST_ENV_003", "test_env_003_valx", null, null, "test_env_003_valx"),
                arguments("TEST_ENV_003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"),
                arguments("test_env_003", null, null, null, null),
                arguments("TEST_ENV_003", null, null, null, null),
                arguments("test.env.003", "test_env_003_valx", null, null, "test_env_003_valx"),
                arguments("test.env.003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"),
                arguments("TEST.ENV.003", "test_env_003_valx", null, null, "test_env_003_valx"),
                arguments("TEST.ENV.003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"),
                arguments("test.env.003", null, null, null, null),
                arguments("TEST.ENV.003", null, null, null, null));
    }

    @ParameterizedTest
    @MethodSource("foos")
    void testGetStringWithoutDefaultFirstPriorityKeyWithStringValueee(
            final String secondPriorityKey,
            final String secondPriorityVal,
            final String firstPriorityKey,
            final String firstPriorityVal,
            final String expectedVal
    ) {
        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        if (firstPriorityKey != null) {
            okosynkConfiguration.clearSystemProperty(firstPriorityKey);
        }

        okosynkConfiguration.clearSystemProperty(secondPriorityKey);
        if (secondPriorityVal != null) {
            okosynkConfiguration.setSystemProperty(secondPriorityKey, secondPriorityVal);
        }

        final String actualVal = okosynkConfiguration.getString(secondPriorityKey);

        final String msg = String.format(
                System.lineSeparator()
                        + "secondPriorityKey: %s, " + System.lineSeparator()
                        + "secondPriorityVal: %s, " + System.lineSeparator()
                        + "firstPriorityKey : %s, " + System.lineSeparator()
                        + "firstPriorityVal : %s  " + System.lineSeparator(),
                secondPriorityKey,
                secondPriorityVal,
                firstPriorityKey,
                firstPriorityVal
        );
        Assertions.assertEquals(expectedVal, actualVal, msg);
    }

    static Stream<Arguments> bars() {
        return Stream.of(
                arguments("test_env_001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", null),
                arguments("test_env_001", "TEST_ENV_001_VALx", "TEST_ENV_001", "TEST_ENV_001_VAL", null),
                arguments("TEST_ENV_001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", null),
                arguments("TEST_ENV_001", "TEST_ENV_001_VALx", "TEST_ENV_001", "TEST_ENV_001_VAL", null),
                arguments("test.env.001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", null),
                arguments("test.env.001", "TEST_ENV_001_VALX", "TEST_ENV_001", "TEST_ENV_001_VAL", null),
                arguments("TEST.ENV.001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", null),
                arguments("TEST.ENV.001", "TEST_ENV_001_VALX", "TEST_ENV_001", "TEST_ENV_001_VAL", null),
                arguments("test.env.002", "test_env_002_valx", "test_env_002", "test_env_002_val", null),
                arguments("test.env.002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", null),
                arguments("TEST.ENV.002", "test_env_002_valx", "test_env_002", "test_env_002_val", null),
                arguments("TEST.ENV.002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", null),
                arguments("test.env.002", null, "test_env_002", "test_env_002_val", null),
                arguments("TEST.ENV.002", null, "test_env_002", "test_env_002_val", null),
                arguments("test_env_003", "test_env_003_valx", null, null, null),
                arguments("test_env_003", "TEST_ENV_003_VALx", null, null, null),
                arguments("TEST_ENV_003", "test_env_003_valx", null, null, null),
                arguments("TEST_ENV_003", "TEST_ENV_003_VALx", null, null, null),
                arguments("TEST_ENV_003", null, null, null, null),
                arguments("test.env.003", "test_env_003_valx", null, null, null),
                arguments("test.env.003", "TEST_ENV_003_VALx", null, null, null),
                arguments("TEST.ENV.003", "test_env_003_valx", null, null, null),
                arguments("TEST.ENV.003", "TEST_ENV_003_VALx", null, null, null),
                arguments("test.env.003", null, null, null, null),
                arguments("TEST.ENV.003", null, null, null, null),
                arguments("TEST.ENV.003", "137", null, null, 137),
                arguments("TEST_ENV_002", "143", "test_env_002", null, 143),
                arguments("TEST_ENV_002", "143", "TEST_ENV_002", null, 143),
                arguments("TEST_ENV_002", "143", "test.env.002", null, 143),
                arguments("TEST.ENV.002", "TEST_ENV_002_VALx", "test_env_002", "143", null),
                arguments("test.env.002", "143", "test.env.002", null, 143),
                arguments("test.env.002", "143", "TEST_ENV_002", null, 143),
                arguments("test.env.002", "143", "TEST_ENV_002", "143", 143));
    }


    @ParameterizedTest
    @MethodSource("bars")
    void testGetRequiredIntFirstPriorityKeyWithStringValue(
            final String secondPriorityKey,
            final String secondPriorityVal,
            final String firstPriorityKey,
            final String firstPriorityVal,
            final Integer expectedVal
    ) {
        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final String msg =
                String.format(
                        System.lineSeparator()
                                + "secondPriorityKey: %s, " + System.lineSeparator()
                                + "secondPriorityVal: %s, " + System.lineSeparator()
                                + "firstPriorityKey : %s, " + System.lineSeparator()
                                + "firstPriorityVal : %s  " + System.lineSeparator()
                                + "expectedVal      : %s  " + System.lineSeparator(),
                        secondPriorityKey,
                        secondPriorityVal,
                        firstPriorityKey,
                        firstPriorityVal,
                        expectedVal
                );
        logger.info("About to test: " + System.lineSeparator() + msg);

        if (firstPriorityKey != null) {
            okosynkConfiguration.clearSystemProperty(firstPriorityKey);
        }

        okosynkConfiguration.clearSystemProperty(secondPriorityKey);
        if (secondPriorityVal != null) {
            okosynkConfiguration.setSystemProperty(secondPriorityKey, secondPriorityVal);
        }

        if (expectedVal == null) {
            final Class<? extends Exception> expectedExceptionClass;
            if (secondPriorityVal == null) {
                expectedExceptionClass = IllegalStateException.class;
            } else {
                expectedExceptionClass = ConversionException.class;
            }
            assertThrows(expectedExceptionClass, () -> okosynkConfiguration.getRequiredInt(secondPriorityKey));
        } else {
            final int actualVal = okosynkConfiguration.getRequiredInt(secondPriorityKey);
            assertEquals(expectedVal, actualVal, msg);
        }
    }

    @Test
    void testGetRequiredIntFirstPriorityKeyWithStringValuex() {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final String secondPriorityKey = "test_env_001";
        final String secondPriorityVal = "test_env_001_valx";
        final String firstPriorityKey = "TEST_ENV_001";

        okosynkConfiguration.clearSystemProperty(firstPriorityKey);
        okosynkConfiguration.clearSystemProperty(secondPriorityKey);
        okosynkConfiguration.setSystemProperty(secondPriorityKey, secondPriorityVal);
        assertThrows(RuntimeException.class, () -> okosynkConfiguration.getRequiredInt(secondPriorityKey));
    }


    static Stream<Arguments> foobars() {
        return Stream.of(
                arguments("test_env_001", "test_env_001_valx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test_env_001", "TEST_ENV_001_VALx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST_ENV_001", "test_env_001_valx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST_ENV_001", "TEST_ENV_001_VALx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test_env_001", null, "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST_ENV_001", null, "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test.env.001", "test_env_001_valx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test.env.001", "TEST_ENV_001_VALX", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST.ENV.001", "test_env_001_valx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST.ENV.001", "TEST_ENV_001_VALX", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test.env.001", null, "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("TEST.ENV.001", null, "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"),
                arguments("test_env_002", "test_env_002_valx", "defVal", "test_env_002", "test_env_002_val", "test_env_002_valx"),
                arguments("test_env_002", "TEST_ENV_002_VALx", "defVal", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"),
                arguments("TEST_ENV_002", "test_env_002_valx", "defVal", "test_env_002", "test_env_002_val", "test_env_002_valx"),
                arguments("TEST_ENV_002", "TEST_ENV_002_VALx", "defVal", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"),
                arguments("test_env_002", null, "defVal", "test_env_002", "test_env_002_val", "test_env_002_val"),
                arguments("TEST_ENV_002", null, "defVal", "test_env_002", "test_env_002_val", "defVal"),
                arguments("test.env.002", "test_env_002_valx", "defVal", "test_env_002", "test_env_002_val", "test_env_002_valx"),
                arguments("test.env.002", "TEST_ENV_002_VALx", "defVal", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"),
                arguments("TEST.ENV.002", "test_env_002_valx", "defVal", "test_env_002", "test_env_002_val", "test_env_002_valx"),
                arguments("TEST.ENV.002", "TEST_ENV_002_VALx", "defVal", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"),
                arguments("test.env.002", null, "defVal", "test_env_002", "test_env_002_val", "defVal"),
                arguments("TEST.ENV.002", null, "defVal", "test_env_002", "test_env_002_val", "defVal"),
                arguments("test_env_003", "test_env_003_valx", "defVal", null, null, "test_env_003_valx"),
                arguments("test_env_003", "TEST_ENV_003_VALx", "defVal", null, null, "TEST_ENV_003_VALx"),
                arguments("TEST_ENV_003", "test_env_003_valx", "defVal", null, null, "test_env_003_valx"),
                arguments("TEST_ENV_003", "TEST_ENV_003_VALx", "defVal", null, null, "TEST_ENV_003_VALx"),
                arguments("test_env_003", null, "defVal", null, null, "defVal"),
                arguments("TEST_ENV_003", null, "defVal", null, null, "defVal"),
                arguments("test.env.003", "test_env_003_valx", "defVal", null, null, "test_env_003_valx"),
                arguments("test.env.003", "TEST_ENV_003_VALx", "defVal", null, null, "TEST_ENV_003_VALx"),
                arguments("TEST.ENV.003", "test_env_003_valx", "defVal", null, null, "test_env_003_valx"),
                arguments("TEST.ENV.003", "TEST_ENV_003_VALx", "defVal", null, null, "TEST_ENV_003_VALx"),
                arguments("test.env.003", null, "defVal", null, null, "defVal"),
                arguments("TEST.ENV.003", null, "defVal", null, null, "defVal"));
    }

    @ParameterizedTest
    @MethodSource("foobars")
    void testGetStringWithDefaultFirstPriorityKeyWithStringValue(
            final String secondPriorityKey,
            final String secondPriorityVal,
            final String defVal,
            final String firstPriorityKey,
            final String firstPriorityVal,
            final String expectedVal
    ) {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        if (firstPriorityKey != null) {
            okosynkConfiguration.clearSystemProperty(firstPriorityKey);
        }

        okosynkConfiguration.clearSystemProperty(secondPriorityKey);
        if (secondPriorityVal != null) {
            okosynkConfiguration.setSystemProperty(secondPriorityKey, secondPriorityVal);
        }

        final String actualVal = okosynkConfiguration.getString(secondPriorityKey, defVal);

        final String msg = String.format(
                System.lineSeparator()
                        + "secondPriorityKey: %s, " + System.lineSeparator()
                        + "secondPriorityVal: %s, " + System.lineSeparator()
                        + "defVal           : %s, " + System.lineSeparator()
                        + "firstPriorityKey : %s, " + System.lineSeparator()
                        + "firstPriorityVal : %s  " + System.lineSeparator(),
                secondPriorityKey,
                secondPriorityVal,
                defVal,
                firstPriorityKey,
                firstPriorityVal,
                expectedVal
        );
        assertEquals(expectedVal, actualVal, msg);
    }

    static Stream<Arguments> barfoos() {
        return Stream.of(
                arguments("TEST_BOOL_ENV_001", "true", false, "TEST_BOOL_ENV_001", "true", true),
                arguments("TEST_BOOL_ENV_001", "false", false, "TEST_BOOL_ENV_001", "true", true),
                arguments("TEST_BOOL_ENV_001", null, false, "TEST_BOOL_ENV_001", "true", true),
                arguments("TEST_BOOL_ENV_001", null, false, "TEST_BOOL_ENV_001", "true", true),
                arguments("TEST.BOOL.ENV.001", "true", false, "TEST_BOOL_ENV_001", "true", true),
                arguments("TEST.BOOL.ENV.001", "false", false, "TEST_BOOL_ENV_001", "true", true),
                arguments("TEST.BOOL.ENV.001", null, false, "TEST_BOOL_ENV_001", "true", true),
                arguments("TEST.BOOL.ENV.001", null, false, "TEST_BOOL_ENV_001", "true", true),
                arguments("test_bool_env_001", "true", false, "TEST_BOOL_ENV_001", "true", true),
                arguments("test_bool_env_001", "false", false, "TEST_BOOL_ENV_001", "true", true),
                arguments("test_bool_env_001", null, false, "TEST_BOOL_ENV_001", "true", true),
                arguments("test_bool_env_001", null, false, "TEST_BOOL_ENV_001", "true", true),
                arguments("test.bool.env.001", "true", false, "TEST_BOOL_ENV_001", "true", true),
                arguments("test.bool.env.001", "false", false, "TEST_BOOL_ENV_001", "true", true),
                arguments("test.bool.env.001", null, false, "TEST_BOOL_ENV_001", "true", true),
                arguments("test.bool.env.001", null, false, "TEST_BOOL_ENV_001", "true", true),
                arguments("TEST_BOOL_ENV_002", "true", false, "TEST_BOOL_ENV_002", "false", false),
                arguments("TEST_BOOL_ENV_002", "false", false, "TEST_BOOL_ENV_002", "false", false),
                arguments("TEST_BOOL_ENV_002", null, false, "TEST_BOOL_ENV_002", "false", false),
                arguments("TEST_BOOL_ENV_002", null, false, "TEST_BOOL_ENV_002", "false", false),
                arguments("TEST.BOOL.ENV.002", "true", false, "TEST_BOOL_ENV_002", "false", false),
                arguments("TEST.BOOL.ENV.002", "false", false, "TEST_BOOL_ENV_002", "false", false),
                arguments("TEST.BOOL.ENV.002", null, false, "TEST_BOOL_ENV_002", "false", false),
                arguments("TEST.BOOL.ENV.002", null, false, "TEST_BOOL_ENV_002", "false", false),
                arguments("test_bool_env_002", "true", false, "TEST_BOOL_ENV_002", "false", false),
                arguments("test_bool_env_002", "false", false, "TEST_BOOL_ENV_002", "false", false),
                arguments("test_bool_env_002", null, false, "TEST_BOOL_ENV_002", "false", false),
                arguments("test_bool_env_002", null, false, "TEST_BOOL_ENV_002", "false", false),
                arguments("test.bool.env.002", "true", false, "TEST_BOOL_ENV_002", "false", false),
                arguments("test.bool.env.002", "false", false, "TEST_BOOL_ENV_002", "false", false),
                arguments("test.bool.env.002", null, false, "TEST_BOOL_ENV_002", "false", false),
                arguments("test.bool.env.002", null, false, "TEST_BOOL_ENV_002", "false", false),
                arguments("TEST_BOOL_ENV_003", "true", false, "test_bool_env_003", "true", true),
                arguments("TEST_BOOL_ENV_003", "false", false, "test_bool_env_003", "true", false),
                arguments("TEST_BOOL_ENV_003", null, true, "test_bool_env_003", "true", true),
                arguments("TEST_BOOL_ENV_003", null, false, "test_bool_env_003", "true", false),
                arguments("TEST.BOOL.ENV.003", "true", false, "test_bool_env_003", "true", true),
                arguments("TEST.BOOL.ENV.003", "false", false, "test_bool_env_003", "true", false),
                arguments("TEST.BOOL.ENV.003", null, false, "test_bool_env_003", "true", false),
                arguments("TEST.BOOL.ENV.003", null, true, "test_bool_env_003", "true", true),
                arguments("test_bool_env_003", "true", false, "test_bool_env_003", "true", true),
                arguments("test_bool_env_003", "false", false, "test_bool_env_003", "true", false),
                arguments("test_bool_env_003", null, true, "test_bool_env_003", "true", true),
                arguments("test_bool_env_003", null, false, "test_bool_env_003", "true", true),
                arguments("test.bool.env.003", "true", false, "test_bool_env_003", "true", true),
                arguments("test.bool.env.003", "false", false, "test_bool_env_003", "true", false),
                arguments("test.bool.env.003", null, true, "test_bool_env_003", "true", true),
                arguments("test.bool.env.003", null, false, "test_bool_env_003", "true", false),
                arguments("TEST_BOOL_ENV_004", "true", false, "test_bool_env_004", "false", true),
                arguments("TEST_BOOL_ENV_004", "false", false, "test_bool_env_004", "false", false),
                arguments("TEST_BOOL_ENV_004", null, true, "test_bool_env_004", "false", true),
                arguments("TEST_BOOL_ENV_004", null, false, "test_bool_env_004", "false", false),
                arguments("TEST.BOOL.ENV.004", "true", false, "test_bool_env_004", "false", true),
                arguments("TEST.BOOL.ENV.004", "false", false, "test_bool_env_004", "false", false),
                arguments("TEST.BOOL.ENV.004", null, false, "test_bool_env_004", "false", false),
                arguments("TEST.BOOL.ENV.004", null, true, "test_bool_env_004", "false", true),
                arguments("test_bool_env_004", "true", false, "test_bool_env_004", "false", true),
                arguments("test_bool_env_004", "false", false, "test_bool_env_004", "false", false),
                arguments("test_bool_env_004", null, true, "test_bool_env_004", "false", false),
                arguments("test_bool_env_004", null, false, "test_bool_env_004", "false", false),
                arguments("test.bool.env.004", "true", false, "test_bool_env_004", "false", true),
                arguments("test.bool.env.004", "false", false, "test_bool_env_004", "false", false),
                arguments("test.bool.env.004", null, true, "test_bool_env_004", "false", true),
                arguments("test.bool.env.004", null, false, "test_bool_env_004", "false", false),
                arguments("TEST_BOOL_ENV_005", "true", false, null, null, true),
                arguments("TEST_BOOL_ENV_005", "false", false, null, null, false),
                arguments("TEST_BOOL_ENV_005", null, true, null, null, true),
                arguments("TEST_BOOL_ENV_005", null, false, null, null, false),
                arguments("TEST.BOOL.ENV.005", "true", false, null, null, true),
                arguments("TEST.BOOL.ENV.005", "false", false, null, null, false),
                arguments("TEST.BOOL.ENV.005", null, false, null, null, false),
                arguments("TEST.BOOL.ENV.005", null, true, null, null, true),
                arguments("test_bool_env_005", "true", false, null, null, true),
                arguments("test_bool_env_005", "false", false, null, null, false),
                arguments("test_bool_env_005", null, true, null, null, true),
                arguments("test_bool_env_005", null, false, null, null, false),
                arguments("test.bool.env.005", "true", false, null, null, true),
                arguments("test.bool.env.005", "false", false, null, null, false),
                arguments("test.bool.env.005", null, true, null, null, true),
                arguments("test.bool.env.005", null, false, null, null, false),
                arguments("TEST_BOOL_ENV_006", "true", false, null, null, true),
                arguments("TEST_BOOL_ENV_006", "false", false, null, null, false),
                arguments("TEST_BOOL_ENV_006", null, true, null, null, true),
                arguments("TEST_BOOL_ENV_006", null, false, null, null, false),
                arguments("TEST.BOOL.ENV.006", "true", false, null, null, true),
                arguments("TEST.BOOL.ENV.006", "false", false, null, null, false),
                arguments("TEST.BOOL.ENV.006", null, false, null, null, false),
                arguments("TEST.BOOL.ENV.006", null, true, null, null, true),
                arguments("test_bool_env_006", "true", false, null, null, true),
                arguments("test_bool_env_006", "false", false, null, null, false),
                arguments("test_bool_env_006", null, true, null, null, true),
                arguments("test_bool_env_006", null, false, null, null, false),
                arguments("test.bool.env.006", "true", false, null, null, true),
                arguments("test.bool.env.006", "false", false, null, null, false),
                arguments("test.bool.env.006", null, true, null, null, true),
                arguments("test.bool.env.006", null, false, null, null, false));
    }

    @ParameterizedTest
    @MethodSource("barfoos")
    void testGetBooleanFirstPriorityKeyWithStringValue(
            final String secondPriorityKey,
            final String secondPriorityVal,
            final Boolean defVal,
            final String firstPriorityKey,
            final String firstPriorityVal,
            final Boolean expectedVal
    ) {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();
        if (firstPriorityKey != null) {
            okosynkConfiguration.clearSystemProperty(firstPriorityKey);
        }

        okosynkConfiguration.clearSystemProperty(secondPriorityKey);
        if (secondPriorityVal != null) {
            okosynkConfiguration.setSystemProperty(secondPriorityKey, secondPriorityVal);
        }

        final Boolean actualVal = okosynkConfiguration.getBoolean(secondPriorityKey, defVal);

        final String msg = String.format("""
                secondPriorityKey: %s
                secondPriorityVal: %s
                defVal           : %s
                firstPriorityKey : %s
                firstPriorityVal : %s
                expected         : %s
                """,
                secondPriorityKey,
                secondPriorityVal,
                defVal,
                firstPriorityKey,
                firstPriorityVal,
                expectedVal
        );

        assertEquals(expectedVal, actualVal, msg);
    }

    @Test
    void testGetBooleanFirstPriorityKeyWithStringValuess() {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();
        final String secondPriorityKey = "test_bool_env_003";
        final String secondPriorityVal = "false";
        final boolean defVal = false;
        final String firstPriorityKey = "test_bool_env_003";

        okosynkConfiguration.clearSystemProperty(firstPriorityKey);
        okosynkConfiguration.clearSystemProperty(secondPriorityKey);
        okosynkConfiguration.setSystemProperty(secondPriorityKey, secondPriorityVal);

        assertFalse(okosynkConfiguration.getBoolean(secondPriorityKey, defVal));
    }

    @Test
    void testGetBooleanFirstPriorityKeyWithStringValuefoff() {
        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final String secondPriorityKey = "TEST_BOOL_ENV_001";
        final String secondPriorityVal = "true";
        final boolean defVal = false;
        final String firstPriorityKey = "TEST_BOOL_ENV_001";
        final Boolean expectedVal = true;

        okosynkConfiguration.clearSystemProperty(firstPriorityKey);
        okosynkConfiguration.clearSystemProperty(secondPriorityKey);
        okosynkConfiguration.setSystemProperty(secondPriorityKey, secondPriorityVal);

        final Boolean actualVal = okosynkConfiguration.getBoolean(secondPriorityKey, defVal);

        assertEquals(expectedVal, actualVal);
    }
}
