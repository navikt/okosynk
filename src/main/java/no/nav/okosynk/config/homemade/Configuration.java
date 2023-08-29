package no.nav.okosynk.config.homemade;

import java.util.*;
import java.util.stream.Stream;

public class Configuration {
    protected int PRIORITY = 3;
    final private Map<String, String> props;
    final private List<Configuration> configurations = new ArrayList<>();

    public Map<String, String> getOwnProps() {
        return props;
    }

    private Map<String, String> getAllProps() {
        HashMap<String, String> copy = new HashMap<>();

        Stream.concat(configurations.stream(), Stream.of(this))
                .sorted(Comparator.comparingInt((Configuration a) -> a.PRIORITY).reversed())
                .forEach(p -> copy.putAll(p.getOwnProps()));

        return copy;
    }

    public Configuration() {
        props = new HashMap<>();
    }

    public Configuration(Map<String, String> propertyList) {
        props = new HashMap<>(propertyList);
    }

    public void addConfiguration(Configuration config) {
        configurations.add(config);
    }

    public String getString(String key) {
        return getAllProps().get(key);
    }

    public String getString(String key, String defaultProp) {
        return getAllProps().getOrDefault(key, defaultProp);
    }


    public Boolean getBoolean(String key) {
        return convertToBoolean(getAllProps().get(key));
    }

    public Boolean getBoolean(String key, boolean defaultProp) {
        Boolean prop = getBoolean(key);
        if (Objects.isNull(prop)) return defaultProp;
        else return prop;
    }

    Boolean convertToBoolean(String s) {
        if ("true".equalsIgnoreCase(s)) return true;
        else if ("false".equalsIgnoreCase(s)) return false;
        else return null;
    }

    public Integer getInt(String key) {
        Map<String, String> allProps = getAllProps();

        if (allProps.containsKey(key) && Objects.isNull(allProps.get(key)))
            throw new IllegalStateException();

        return convertToInt(allProps.get(key));
    }

    Integer convertToInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new ConversionException();
        }
    }

    public void setProperty(String key, String value) {
        if (Objects.nonNull(key)) {
            props.put(key, value);
            if (Objects.nonNull(value)) System.setProperty(key, value);
        }
    }

    public void clearProperty(String key) {
        props.remove(key);
        System.clearProperty(key);
    }

    public boolean containsKey(String key) {
        return getAllProps().containsKey(key);
    }
}
