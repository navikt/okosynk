package no.nav.okosynk.domain;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;


import java.time.LocalDate;

import static java.time.ZoneOffset.UTC;

public abstract class AbstractMeldingTest {

    protected static String randomAlphanumeric(final int count, final Random random) {

        final String randomAlphanumeric =
            RandomStringUtils.random(count, 0, 0, true, true, (char[])null, random);

        return randomAlphanumeric;
    }

    protected static String randomNumeric(final int count, final Random random) {

        final String randomNumeric =
            RandomStringUtils.random(count, 0, 0, false, true, (char[])null, random);

        return randomNumeric;
    }

    protected static String randomLocalDateTime(final Random random) {

        final long epochSecond = Math.abs(random.nextLong() % 9999999999L);
        final int nanoOfSecond = Math.abs(random.nextInt() % 999999);
        final LocalDateTime localDateTime =
            LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, UTC);
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        return localDateTime.format(dateTimeFormatter);
    }

    protected static String randomLocalDate(final Random random) {

        final long epochDay = Math.abs(random.nextLong() % 99999L);
        final LocalDate localDate = LocalDate.ofEpochDay(epochDay);
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final String localDateStr = localDate.format(dateTimeFormatter);

        return localDateStr;
    }
}
