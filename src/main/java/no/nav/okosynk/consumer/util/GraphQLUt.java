package no.nav.okosynk.consumer.util;

public class GraphQLUt {

    public static String buildQueryWrappedInJson(
            final String resourceFileName,
            final String variablename,
            final String value) {

        final String query = FilUt.readResourceFileToString(resourceFileName);
        final String wrappedQuery =
                "{\n" +
                        "    \"query\": \"" + query + "\", \n" +
                        GraphQLUt.buildVariablesBlock(variablename, value) +
                        "}";
        return wrappedQuery;
    }

    private static String buildVariablesBlock(final String variableName, final String value) {
        return "    \"variables\": {\n" +
                "        \"" + variableName + "\": \"" + value + "\"\n" +
                "    }\n";
    }
}
