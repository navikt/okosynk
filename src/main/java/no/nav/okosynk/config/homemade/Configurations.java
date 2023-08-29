package no.nav.okosynk.config.homemade;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Configurations {
    public Configuration properties(File file) throws ConfigurationException {
        Map<String, String> propertyList = new HashMap<>();

        try (InputStream inputStream = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            Properties properties = new Properties();
            properties.load(reader);

            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                propertyList.put(key, value);
            }
            return new Configuration(propertyList);

        } catch (IOException e) {
            throw new ConfigurationException();
        }
    }
}
