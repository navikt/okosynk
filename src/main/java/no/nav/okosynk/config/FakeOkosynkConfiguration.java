package no.nav.okosynk.config;

import no.nav.okosynk.config.IOkosynkConfiguration;

public class FakeOkosynkConfiguration
    implements IOkosynkConfiguration {

    @Override
    public void setBoolean(final String key, final boolean defaultValue) {

        System.setProperty(
            key,
            Boolean.valueOf(defaultValue).toString());
    }

    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {

        return Boolean
            .parseBoolean(
                System.getProperty(key,
                    Boolean.valueOf(defaultValue).toString())
            );
    }

    @Override
    public String getRequiredString(final String key) {

        final String value = System.getProperty(key);
        assert (value != null) : "There is no system property set for the key " + key;
        return value;
    }

    @Override
    public void clearSystemProperty(final String key) {
        System.clearProperty(key);
    }

    @Override
    public void setSystemProperty(final String key, final String value) {

        System.setProperty(key, value);
    }

    @Override
    public String getString(final String key) {

        return System.getProperty(key);
    }

    @Override
    public String getString(final String key, final String defaulValue) {

        return System.getProperty(key, defaulValue);
    }
}
