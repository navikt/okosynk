package no.nav.okosynk.synkroniserer.consumer.util;


import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphQLUt {
    private GraphQLUt() {
    }

    public static String buildQueryWrappedInJson(
            final String resourceFileName,
            final String variablename,
            final String value) {

        final String query = FilUt.readResourceFileToString(resourceFileName);
        final Map<String, Object> keysWithValues = new HashMap<>();
        keysWithValues.put(variablename, value);

        return "{\n" +
               "    \"query\": \"" + query + "\", \n" +
               GraphQLUt.buildVariablesBlock(keysWithValues) +
               "}";
    }

    private static String buildVariablesBlock(final Map<String, Object> keysWithValues) {

        return "    \"variables\": {\n" +
               keysWithValues
                       .keySet()
                       .stream()
                       .map(
                               (final String key) -> {
                                   final Object value = keysWithValues.get(key);
                                   final String quoteChar = value instanceof String ? "\"" : "";
                                   return "        \"" + key + "\": " + quoteChar + keysWithValues.get(key).toString() + quoteChar;
                               }
                       )
                       .collect(Collectors.joining(", ")) +
               "    }\n";
    }
}
