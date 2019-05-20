package no.nav.okosynk.config;

public interface IOkosynkConfiguration {

    void    setBoolean(final String key, final boolean value);
    boolean getBoolean(final String key, final boolean defaultValue);
    String  getRequiredString(final String key);

    /**
     * Used when the reader of the property does
     * explicitly rely on the system property
     * as such not having been cleared.
     * E.g. typically: For external libraries over which we do not have control
     * this method should be used.
     * @param key
     */
    void    clearSystemProperty(final String key);

    /**
     * Used when the reader of the property does
     * explicitly rely on the system property
     * as such having been set.
     * E.g. typically: For external libraries over which we do not have control
     * this method should be used.
     * @param key
     * @param value
     */
    void    setSystemProperty(final String key, final String value);

    String  getString(final String key);
    String  getString(final String key, final String defaulValue);
}
