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

//    public void setProperty(String key, String value) {
//        System.setProperty(alteredKey(key), value);
//    }
//
//    public void clearProperty(String key) {
//        System.clearProperty(alteredKey(key));
//    }
//
//    public String getString(String key) {
//        return System.getProperty(alteredKey(key));
//    }
//
//
//    private String alteredKey(String key) {
//        return key;
////        return key.toUpperCase().replace('.', '_');
//    }
}
