package no.nav.okosynk.testutils;

import java.util.Random;

public class RandUt {

    public static String constructRandomAlphaNumString(final int length, final Random random) {

        final String allChars = "abcdefghijklmnopqrstuvwxyzæøåABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ1234567890";
        final StringBuffer randomAlphaNumStringBuffer = new StringBuffer();
        for (int ix = 0; ix < length; ix++) {
            randomAlphaNumStringBuffer.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        return randomAlphaNumStringBuffer.toString();
    }
}
