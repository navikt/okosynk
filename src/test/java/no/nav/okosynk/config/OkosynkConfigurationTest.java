package no.nav.okosynk.config;

import no.nav.okosynk.config.homemade.ConversionException;
import org.javatuples.Quintet;
import org.javatuples.Sextet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OkosynkConfigurationTest {

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

    @Test
    /*
     * To make the test pass...
     *  ...the following environment variables must be set:
     * TEST_ENV_001 TEST_ENV_001_VAL
     * test_env_002 TEST_ENV_002_VAL
     * ...the following environment variables must NOT be set:
     * TEST_ENV_003
     */
    public void testGetStringWithoutDefaultFirstPriorityKeyWithStringValue() {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final List<Quintet<String, String, String, String, String>> testData =
                new ArrayList<>() {{

                    add(new Quintet<>("test_env_001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("test_env_001", "TEST_ENV_001_VALx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST_ENV_001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST_ENV_001", "TEST_ENV_001_VALx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));

                    add(new Quintet<>("test_env_001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST_ENV_001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));

                    add(new Quintet<>("test.env.001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("test.env.001", "TEST_ENV_001_VALX", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST.ENV.001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST.ENV.001", "TEST_ENV_001_VALX", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));

                    add(new Quintet<>("test.env.001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST.ENV.001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));

                    add(new Quintet<>("test_env_002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Quintet<>("test_env_002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));
                    add(new Quintet<>("TEST_ENV_002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Quintet<>("TEST_ENV_002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));

                    add(new Quintet<>("test_env_002", null, "test_env_002", "test_env_002_val", "test_env_002_val"));
                    add(new Quintet<>("TEST_ENV_002", null, "test_env_002", "test_env_002_val", null));

                    add(new Quintet<>("test.env.002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Quintet<>("test.env.002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));
                    add(new Quintet<>("TEST.ENV.002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Quintet<>("TEST.ENV.002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));

                    add(new Quintet<>("test.env.002", null, "test_env_002", "test_env_002_val", null));
                    add(new Quintet<>("TEST.ENV.002", null, "test_env_002", "test_env_002_val", null));

                    add(new Quintet<>("test_env_003", "test_env_003_valx", null, null, "test_env_003_valx"));
                    add(new Quintet<>("test_env_003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"));
                    add(new Quintet<>("TEST_ENV_003", "test_env_003_valx", null, null, "test_env_003_valx"));
                    add(new Quintet<>("TEST_ENV_003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"));

                    add(new Quintet<>("test_env_003", null, null, null, null));
                    add(new Quintet<>("TEST_ENV_003", null, null, null, null));

                    add(new Quintet<>("test.env.003", "test_env_003_valx", null, null, "test_env_003_valx"));
                    add(new Quintet<>("test.env.003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"));
                    add(new Quintet<>("TEST.ENV.003", "test_env_003_valx", null, null, "test_env_003_valx"));
                    add(new Quintet<>("TEST.ENV.003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"));

                    add(new Quintet<>("test.env.003", null, null, null, null));
                    add(new Quintet<>("TEST.ENV.003", null, null, null, null));
                }};

        for (final Quintet<String, String, String, String, String> testDatum : testData) {

            final String secondPriorityKey = testDatum.getValue0();
            final String secondPriorityVal = testDatum.getValue1();
            final String firstPriorityKey = testDatum.getValue2();
            final String firstPriorityVal = testDatum.getValue3();
            final String expectedVal = testDatum.getValue4();

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
                    firstPriorityVal,
                    expectedVal
            );
            assertEquals(expectedVal, actualVal, msg);
        }
    }

    @Test
    /*
     * To make the test pass...
     *  ...the following environment variables must be set:
     * TEST_ENV_001 TEST_ENV_001_VAL
     * test_env_002 TEST_ENV_002_VAL
     * ...the following environment variables must NOT be set:
     * TEST_ENV_003
     */
    void testGetRequiredStringFirstPriorityKeyWithStringValue() {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final List<Quintet<String, String, String, String, String>> testData =
                new ArrayList<>() {{

                    add(new Quintet<>("test_env_001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("test_env_001", "TEST_ENV_001_VALx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST_ENV_001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST_ENV_001", "TEST_ENV_001_VALx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));

                    add(new Quintet<>("test_env_001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST_ENV_001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));

                    add(new Quintet<>("test.env.001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("test.env.001", "TEST_ENV_001_VALX", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST.ENV.001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST.ENV.001", "TEST_ENV_001_VALX", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));

                    add(new Quintet<>("test.env.001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Quintet<>("TEST.ENV.001", null, "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));

                    add(new Quintet<>("test_env_002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Quintet<>("test_env_002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));
                    add(new Quintet<>("TEST_ENV_002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Quintet<>("TEST_ENV_002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));

                    add(new Quintet<>("test_env_002", null, "test_env_002", "test_env_002_val", "test_env_002_val"));
                    add(new Quintet<>("TEST_ENV_002", null, "test_env_002", "test_env_002_val", null));

                    add(new Quintet<>("test.env.002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Quintet<>("test.env.002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));
                    add(new Quintet<>("TEST.ENV.002", "test_env_002_valx", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Quintet<>("TEST.ENV.002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));

                    add(new Quintet<>("test.env.002", null, "test_env_002", "test_env_002_val", null));
                    add(new Quintet<>("TEST.ENV.002", null, "test_env_002", "test_env_002_val", null));

                    add(new Quintet<>("test_env_003", "test_env_003_valx", null, null, "test_env_003_valx"));
                    add(new Quintet<>("test_env_003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"));
                    add(new Quintet<>("TEST_ENV_003", "test_env_003_valx", null, null, "test_env_003_valx"));
                    add(new Quintet<>("TEST_ENV_003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"));

                    add(new Quintet<>("test_env_003", null, null, null, null));
                    add(new Quintet<>("TEST_ENV_003", null, null, null, null));

                    add(new Quintet<>("test.env.003", "test_env_003_valx", null, null, "test_env_003_valx"));
                    add(new Quintet<>("test.env.003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"));
                    add(new Quintet<>("TEST.ENV.003", "test_env_003_valx", null, null, "test_env_003_valx"));
                    add(new Quintet<>("TEST.ENV.003", "TEST_ENV_003_VALx", null, null, "TEST_ENV_003_VALx"));

                    add(new Quintet<>("test.env.003", null, null, null, null));
                    add(new Quintet<>("TEST.ENV.003", null, null, null, null));
                }};

        for (final Quintet<String, String, String, String, String> testDatum : testData) {

            final String secondPriorityKey = testDatum.getValue0();
            final String secondPriorityVal = testDatum.getValue1();
            final String firstPriorityKey = testDatum.getValue2();
            final String firstPriorityVal = testDatum.getValue3();
            final String expectedVal = testDatum.getValue4();

            if (firstPriorityKey != null) {
                okosynkConfiguration.clearSystemProperty(firstPriorityKey);
            }

            okosynkConfiguration.clearSystemProperty(secondPriorityKey);
            if (secondPriorityVal != null) {
                okosynkConfiguration.setSystemProperty(secondPriorityKey, secondPriorityVal);
            }

            if (expectedVal == null) {
                assertThrows(IllegalStateException.class,
                        () -> okosynkConfiguration.getRequiredString(secondPriorityKey));
            } else {
                final String actualVal = okosynkConfiguration.getRequiredString(secondPriorityKey);

                final String msg = String.format(
                        System.lineSeparator()
                                + "secondPriorityKey: %s, " + System.lineSeparator()
                                + "secondPriorityVal: %s, " + System.lineSeparator()
                                + "firstPriorityKey : %s, " + System.lineSeparator()
                                + "firstPriorityVal : %s  " + System.lineSeparator(),
                        secondPriorityKey,
                        secondPriorityVal,
                        firstPriorityKey,
                        firstPriorityVal,
                        expectedVal
                );
                assertEquals(expectedVal, actualVal, msg);
            }
        }
    }

    @Test
    void testGetRequiredIntFirstPriorityKeyWithStringValue() {
        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final List<Quintet<String, String, String, String, Integer>> testData =
                new ArrayList<>() {{

                    add(new Quintet<>("test_env_001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", null));
                    add(new Quintet<>("test_env_001", "TEST_ENV_001_VALx", "TEST_ENV_001", "TEST_ENV_001_VAL", null));
                    add(new Quintet<>("TEST_ENV_001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", null));
                    add(new Quintet<>("TEST_ENV_001", "TEST_ENV_001_VALx", "TEST_ENV_001", "TEST_ENV_001_VAL", null));

                    add(new Quintet<>("test.env.001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", null));
                    add(new Quintet<>("test.env.001", "TEST_ENV_001_VALX", "TEST_ENV_001", "TEST_ENV_001_VAL", null));
                    add(new Quintet<>("TEST.ENV.001", "test_env_001_valx", "TEST_ENV_001", "TEST_ENV_001_VAL", null));
                    add(new Quintet<>("TEST.ENV.001", "TEST_ENV_001_VALX", "TEST_ENV_001", "TEST_ENV_001_VAL", null));

                    add(new Quintet<>("test.env.002", "test_env_002_valx", "test_env_002", "test_env_002_val", null));
                    add(new Quintet<>("test.env.002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", null));
                    add(new Quintet<>("TEST.ENV.002", "test_env_002_valx", "test_env_002", "test_env_002_val", null));
                    add(new Quintet<>("TEST.ENV.002", "TEST_ENV_002_VALx", "test_env_002", "test_env_002_val", null));

                    add(new Quintet<>("test.env.002", null, "test_env_002", "test_env_002_val", null));
                    add(new Quintet<>("TEST.ENV.002", null, "test_env_002", "test_env_002_val", null));

                    add(new Quintet<>("test_env_003", "test_env_003_valx", null, null, null));
                    add(new Quintet<>("test_env_003", "TEST_ENV_003_VALx", null, null, null));
                    add(new Quintet<>("TEST_ENV_003", "test_env_003_valx", null, null, null));
                    add(new Quintet<>("TEST_ENV_003", "TEST_ENV_003_VALx", null, null, null));

                    add(new Quintet<>("TEST_ENV_003", null, null, null, null));

                    add(new Quintet<>("test.env.003", "test_env_003_valx", null, null, null));
                    add(new Quintet<>("test.env.003", "TEST_ENV_003_VALx", null, null, null));
                    add(new Quintet<>("TEST.ENV.003", "test_env_003_valx", null, null, null));
                    add(new Quintet<>("TEST.ENV.003", "TEST_ENV_003_VALx", null, null, null));

                    add(new Quintet<>("test.env.003", null, null, null, null));
                    add(new Quintet<>("TEST.ENV.003", null, null, null, null));
                    add(new Quintet<>("TEST.ENV.003", "137", null, null, 137));

                    add(new Quintet<>("TEST_ENV_002", "143", "test_env_002", null, 143));
                    add(new Quintet<>("TEST_ENV_002", "143", "TEST_ENV_002", null, 143));
                    add(new Quintet<>("TEST_ENV_002", "143", "test.env.002", null, 143));
                    add(new Quintet<>("TEST.ENV.002", "TEST_ENV_002_VALx", "test_env_002", "143", null));
                    add(new Quintet<>("test.env.002", "143", "test.env.002", null, 143));
                    add(new Quintet<>("test.env.002", "143", "TEST_ENV_002", null, 143));
                    add(new Quintet<>("test.env.002", "143", "TEST_ENV_002", "143", 143));
                }};

        for (final Quintet<String, String, String, String, Integer> testDatum : testData) {

            final String secondPriorityKey = testDatum.getValue0();
            final String secondPriorityVal = testDatum.getValue1();
            final String firstPriorityKey = testDatum.getValue2();
            final String firstPriorityVal = testDatum.getValue3();
            final Integer expectedVal = testDatum.getValue4();

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

    @Test
    void testGetStringWithDefaultFirstPriorityKeyWithStringValue() {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final List<Sextet<String, String, String, String, String, String>> testData =
                new ArrayList<>() {{

                    add(new Sextet<>("test_env_001", "test_env_001_valx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("test_env_001", "TEST_ENV_001_VALx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("TEST_ENV_001", "test_env_001_valx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("TEST_ENV_001", "TEST_ENV_001_VALx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("test_env_001", null, "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("TEST_ENV_001", null, "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("test.env.001", "test_env_001_valx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("test.env.001", "TEST_ENV_001_VALX", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("TEST.ENV.001", "test_env_001_valx", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("TEST.ENV.001", "TEST_ENV_001_VALX", "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("test.env.001", null, "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("TEST.ENV.001", null, "defVal", "TEST_ENV_001", "TEST_ENV_001_VAL", "TEST_ENV_001_VAL"));
                    add(new Sextet<>("test_env_002", "test_env_002_valx", "defVal", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Sextet<>("test_env_002", "TEST_ENV_002_VALx", "defVal", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));
                    add(new Sextet<>("TEST_ENV_002", "test_env_002_valx", "defVal", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Sextet<>("TEST_ENV_002", "TEST_ENV_002_VALx", "defVal", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));
                    add(new Sextet<>("test_env_002", null, "defVal", "test_env_002", "test_env_002_val", "test_env_002_val"));
                    add(new Sextet<>("TEST_ENV_002", null, "defVal", "test_env_002", "test_env_002_val", "defVal"));
                    add(new Sextet<>("test.env.002", "test_env_002_valx", "defVal", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Sextet<>("test.env.002", "TEST_ENV_002_VALx", "defVal", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));
                    add(new Sextet<>("TEST.ENV.002", "test_env_002_valx", "defVal", "test_env_002", "test_env_002_val", "test_env_002_valx"));
                    add(new Sextet<>("TEST.ENV.002", "TEST_ENV_002_VALx", "defVal", "test_env_002", "test_env_002_val", "TEST_ENV_002_VALx"));
                    add(new Sextet<>("test.env.002", null, "defVal", "test_env_002", "test_env_002_val", "defVal"));
                    add(new Sextet<>("TEST.ENV.002", null, "defVal", "test_env_002", "test_env_002_val", "defVal"));
                    add(new Sextet<>("test_env_003", "test_env_003_valx", "defVal", null, null, "test_env_003_valx"));
                    add(new Sextet<>("test_env_003", "TEST_ENV_003_VALx", "defVal", null, null, "TEST_ENV_003_VALx"));
                    add(new Sextet<>("TEST_ENV_003", "test_env_003_valx", "defVal", null, null, "test_env_003_valx"));
                    add(new Sextet<>("TEST_ENV_003", "TEST_ENV_003_VALx", "defVal", null, null, "TEST_ENV_003_VALx"));
                    add(new Sextet<>("test_env_003", null, "defVal", null, null, "defVal"));
                    add(new Sextet<>("TEST_ENV_003", null, "defVal", null, null, "defVal"));
                    add(new Sextet<>("test.env.003", "test_env_003_valx", "defVal", null, null, "test_env_003_valx"));
                    add(new Sextet<>("test.env.003", "TEST_ENV_003_VALx", "defVal", null, null, "TEST_ENV_003_VALx"));
                    add(new Sextet<>("TEST.ENV.003", "test_env_003_valx", "defVal", null, null, "test_env_003_valx"));
                    add(new Sextet<>("TEST.ENV.003", "TEST_ENV_003_VALx", "defVal", null, null, "TEST_ENV_003_VALx"));
                    add(new Sextet<>("test.env.003", null, "defVal", null, null, "defVal"));
                    add(new Sextet<>("TEST.ENV.003", null, "defVal", null, null, "defVal"));
                }};

        for (final Sextet<String, String, String, String, String, String> testDatum : testData) {

            final String secondPriorityKey = testDatum.getValue0();
            final String secondPriorityVal = testDatum.getValue1();
            final String defVal = testDatum.getValue2();
            final String firstPriorityKey = testDatum.getValue3();
            final String firstPriorityVal = testDatum.getValue4();
            final String expectedVal = testDatum.getValue5();

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
    }

    @Test
    public void testGetBooleanFirstPriorityKeyWithStringValue() {

        enteringTestHeaderLogger.debug(null);

        OkosynkConfiguration.createAndReplaceSingletonInstance("Tullefil");
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        final List<Sextet<String, String, Boolean, String, String, Boolean>> testData =
                new ArrayList<>() {{

                    add(new Sextet<>("TEST_BOOL_ENV_001", "true", false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("TEST_BOOL_ENV_001", "false", false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("TEST_BOOL_ENV_001", null, false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("TEST_BOOL_ENV_001", null, false, "TEST_BOOL_ENV_001", "true", true));

                    add(new Sextet<>("TEST.BOOL.ENV.001", "true", false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("TEST.BOOL.ENV.001", "false", false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("TEST.BOOL.ENV.001", null, false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("TEST.BOOL.ENV.001", null, false, "TEST_BOOL_ENV_001", "true", true));

                    add(new Sextet<>("test_bool_env_001", "true", false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("test_bool_env_001", "false", false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("test_bool_env_001", null, false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("test_bool_env_001", null, false, "TEST_BOOL_ENV_001", "true", true));

                    add(new Sextet<>("test.bool.env.001", "true", false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("test.bool.env.001", "false", false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("test.bool.env.001", null, false, "TEST_BOOL_ENV_001", "true", true));
                    add(new Sextet<>("test.bool.env.001", null, false, "TEST_BOOL_ENV_001", "true", true));

                    add(new Sextet<>("TEST_BOOL_ENV_002", "true", false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("TEST_BOOL_ENV_002", "false", false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("TEST_BOOL_ENV_002", null, false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("TEST_BOOL_ENV_002", null, false, "TEST_BOOL_ENV_002", "false", false));

                    add(new Sextet<>("TEST.BOOL.ENV.002", "true", false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("TEST.BOOL.ENV.002", "false", false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("TEST.BOOL.ENV.002", null, false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("TEST.BOOL.ENV.002", null, false, "TEST_BOOL_ENV_002", "false", false));

                    add(new Sextet<>("test_bool_env_002", "true", false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("test_bool_env_002", "false", false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("test_bool_env_002", null, false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("test_bool_env_002", null, false, "TEST_BOOL_ENV_002", "false", false));

                    add(new Sextet<>("test.bool.env.002", "true", false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("test.bool.env.002", "false", false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("test.bool.env.002", null, false, "TEST_BOOL_ENV_002", "false", false));
                    add(new Sextet<>("test.bool.env.002", null, false, "TEST_BOOL_ENV_002", "false", false));

                    add(new Sextet<>("TEST_BOOL_ENV_003", "true", false, "test_bool_env_003", "true", true));
                    add(new Sextet<>("TEST_BOOL_ENV_003", "false", false, "test_bool_env_003", "true", false));
                    add(new Sextet<>("TEST_BOOL_ENV_003", null, true, "test_bool_env_003", "true", true));
                    add(new Sextet<>("TEST_BOOL_ENV_003", null, false, "test_bool_env_003", "true", false));

                    add(new Sextet<>("TEST.BOOL.ENV.003", "true", false, "test_bool_env_003", "true", true));
                    add(new Sextet<>("TEST.BOOL.ENV.003", "false", false, "test_bool_env_003", "true", false));
                    add(new Sextet<>("TEST.BOOL.ENV.003", null, false, "test_bool_env_003", "true", false));
                    add(new Sextet<>("TEST.BOOL.ENV.003", null, true, "test_bool_env_003", "true", true));

                    add(new Sextet<>("test_bool_env_003", "true", false, "test_bool_env_003", "true", true));
                    add(new Sextet<>("test_bool_env_003", "false", false, "test_bool_env_003", "true", false));
                    add(new Sextet<>("test_bool_env_003", null, true, "test_bool_env_003", "true", true));
                    add(new Sextet<>("test_bool_env_003", null, false, "test_bool_env_003", "true", true));

                    add(new Sextet<>("test.bool.env.003", "true", false, "test_bool_env_003", "true", true));
                    add(new Sextet<>("test.bool.env.003", "false", false, "test_bool_env_003", "true", false));
                    add(new Sextet<>("test.bool.env.003", null, true, "test_bool_env_003", "true", true));
                    add(new Sextet<>("test.bool.env.003", null, false, "test_bool_env_003", "true", false));

                    add(new Sextet<>("TEST_BOOL_ENV_004", "true", false, "test_bool_env_004", "false", true));
                    add(new Sextet<>("TEST_BOOL_ENV_004", "false", false, "test_bool_env_004", "false", false));
                    add(new Sextet<>("TEST_BOOL_ENV_004", null, true, "test_bool_env_004", "false", true));
                    add(new Sextet<>("TEST_BOOL_ENV_004", null, false, "test_bool_env_004", "false", false));

                    add(new Sextet<>("TEST.BOOL.ENV.004", "true", false, "test_bool_env_004", "false", true));
                    add(new Sextet<>("TEST.BOOL.ENV.004", "false", false, "test_bool_env_004", "false", false));
                    add(new Sextet<>("TEST.BOOL.ENV.004", null, false, "test_bool_env_004", "false", false));
                    add(new Sextet<>("TEST.BOOL.ENV.004", null, true, "test_bool_env_004", "false", true));

                    add(new Sextet<>("test_bool_env_004", "true", false, "test_bool_env_004", "false", true));
                    add(new Sextet<>("test_bool_env_004", "false", false, "test_bool_env_004", "false", false));
                    add(new Sextet<>("test_bool_env_004", null, true, "test_bool_env_004", "false", false));
                    add(new Sextet<>("test_bool_env_004", null, false, "test_bool_env_004", "false", false));

                    add(new Sextet<>("test.bool.env.004", "true", false, "test_bool_env_004", "false", true));
                    add(new Sextet<>("test.bool.env.004", "false", false, "test_bool_env_004", "false", false));
                    add(new Sextet<>("test.bool.env.004", null, true, "test_bool_env_004", "false", true));
                    add(new Sextet<>("test.bool.env.004", null, false, "test_bool_env_004", "false", false));

                    add(new Sextet<>("TEST_BOOL_ENV_005", "true", false, null, null, true));
                    add(new Sextet<>("TEST_BOOL_ENV_005", "false", false, null, null, false));
                    add(new Sextet<>("TEST_BOOL_ENV_005", null, true, null, null, true));
                    add(new Sextet<>("TEST_BOOL_ENV_005", null, false, null, null, false));

                    add(new Sextet<>("TEST.BOOL.ENV.005", "true", false, null, null, true));
                    add(new Sextet<>("TEST.BOOL.ENV.005", "false", false, null, null, false));
                    add(new Sextet<>("TEST.BOOL.ENV.005", null, false, null, null, false));
                    add(new Sextet<>("TEST.BOOL.ENV.005", null, true, null, null, true));

                    add(new Sextet<>("test_bool_env_005", "true", false, null, null, true));
                    add(new Sextet<>("test_bool_env_005", "false", false, null, null, false));
                    add(new Sextet<>("test_bool_env_005", null, true, null, null, true));
                    add(new Sextet<>("test_bool_env_005", null, false, null, null, false));

                    add(new Sextet<>("test.bool.env.005", "true", false, null, null, true));
                    add(new Sextet<>("test.bool.env.005", "false", false, null, null, false));
                    add(new Sextet<>("test.bool.env.005", null, true, null, null, true));
                    add(new Sextet<>("test.bool.env.005", null, false, null, null, false));

                    add(new Sextet<>("TEST_BOOL_ENV_006", "true", false, null, null, true));
                    add(new Sextet<>("TEST_BOOL_ENV_006", "false", false, null, null, false));
                    add(new Sextet<>("TEST_BOOL_ENV_006", null, true, null, null, true));
                    add(new Sextet<>("TEST_BOOL_ENV_006", null, false, null, null, false));

                    add(new Sextet<>("TEST.BOOL.ENV.006", "true", false, null, null, true));
                    add(new Sextet<>("TEST.BOOL.ENV.006", "false", false, null, null, false));
                    add(new Sextet<>("TEST.BOOL.ENV.006", null, false, null, null, false));
                    add(new Sextet<>("TEST.BOOL.ENV.006", null, true, null, null, true));

                    add(new Sextet<>("test_bool_env_006", "true", false, null, null, true));
                    add(new Sextet<>("test_bool_env_006", "false", false, null, null, false));
                    add(new Sextet<>("test_bool_env_006", null, true, null, null, true));
                    add(new Sextet<>("test_bool_env_006", null, false, null, null, false));

                    add(new Sextet<>("test.bool.env.006", "true", false, null, null, true));
                    add(new Sextet<>("test.bool.env.006", "false", false, null, null, false));
                    add(new Sextet<>("test.bool.env.006", null, true, null, null, true));
                    add(new Sextet<>("test.bool.env.006", null, false, null, null, false));
                }};

        for (final Sextet<String, String, Boolean, String, String, Boolean> testDatum : testData) {

            final String secondPriorityKey = testDatum.getValue0();
            final String secondPriorityVal = testDatum.getValue1();
            final Boolean defVal = testDatum.getValue2();
            final String firstPriorityKey = testDatum.getValue3();
            final String firstPriorityVal = testDatum.getValue4();
            final Boolean expectedVal = testDatum.getValue5();

            if (firstPriorityKey != null) {
                okosynkConfiguration.clearSystemProperty(firstPriorityKey);
            }

            okosynkConfiguration.clearSystemProperty(secondPriorityKey);
            if (secondPriorityVal != null) {
                okosynkConfiguration.setSystemProperty(secondPriorityKey, secondPriorityVal);
            }

            final Boolean actualVal = okosynkConfiguration.getBoolean(secondPriorityKey, defVal);

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
    }

    @Test
    public void testGetBooleanFirstPriorityKeyWithStringValuess() {

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

        final Boolean actualTestBoolEnv003 = okosynkConfiguration.getBoolean(secondPriorityKey, defVal);

        assertEquals(false, actualTestBoolEnv003);
    }

    @Test

    public void testGetBooleanFirstPriorityKeyWithStringValuefoff() {
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
