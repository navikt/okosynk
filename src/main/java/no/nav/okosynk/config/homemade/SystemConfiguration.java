package no.nav.okosynk.config.homemade;

import java.util.HashMap;
import java.util.Map;

public class SystemConfiguration extends Configuration {
    public SystemConfiguration() {
        super(getSystemProperties());
        PRIORITY = 1;
    }

    private static Map<String, String> getSystemProperties() {
        HashMap<String, String> map = new HashMap<>();
        System.getProperties().stringPropertyNames().forEach(key ->
                map.put(key, System.getProperty(key))
        );
        return map;
    }
}
