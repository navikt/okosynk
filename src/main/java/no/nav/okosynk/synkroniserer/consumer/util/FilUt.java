package no.nav.okosynk.synkroniserer.consumer.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FilUt {
    private FilUt() {
    }

    /**
     * @param path When the file is situated in <root>/src/main/resources/x/y/z.e, the path should be given as: "x/y/z.e"
     * @return
     */
    public static String readResourceFileToString(final String path) {

        final InputStream inputStream = FilUt.getFileFromResourceAsStream(path);
        String fileContentAsString;
        try (final Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            fileContentAsString = scanner.useDelimiter("\\A").next();
            fileContentAsString = fileContentAsString == null ? null : fileContentAsString.trim();
        }

        return fileContentAsString;
    }

    private static InputStream getFileFromResourceAsStream(final String fileName) {

        final ClassLoader classLoader = FilUt.class.getClassLoader();
        final InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + fileName);
        } else {
            return inputStream;
        }
    }
}
