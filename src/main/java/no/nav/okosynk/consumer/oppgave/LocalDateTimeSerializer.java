package no.nav.okosynk.consumer.oppgave;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

  public LocalDateTimeSerializer() {
    super(LocalDateTime.class);
  }

  @Override
  public void serialize(
      final LocalDateTime      value,
      final JsonGenerator      generator,
      final SerializerProvider provider) throws IOException {

    // final String formattedLocalDateTime = value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);

    // final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddTHH:mm:ss.SSSSSS Z");
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    final String formattedLocalDateTime = ZonedDateTime.of(value, ZoneId.systemDefault()).format(dateTimeFormatter);

    generator.writeString(formattedLocalDateTime);
  }
}