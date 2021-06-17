package no.nav.okosynk.consumer.aktoer;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

public class HttpResponseUtil {

  public static String createStringRepresentationOfResponseEntity(final CloseableHttpResponse response) {

    String entityStringRepresentationBody = "";
    try {
      final HttpEntity httpEntity = response.getEntity();
      final InputStream inputStream = httpEntity.getContent();
      final StringWriter writer = new StringWriter();
      IOUtils.copy(inputStream, writer, Charset.defaultCharset());
      final String entityStringRepresentationBodyTemp = writer.toString().trim();
      entityStringRepresentationBody =
          (entityStringRepresentationBodyTemp.isEmpty())
              ?
              "Nothing"
              :
              entityStringRepresentationBodyTemp;
    } catch (Throwable e) {
      entityStringRepresentationBody = "Nothing";
    }
    final String entityStringRepresentation =
        System.lineSeparator()
            + "entityStringRepresentation: " + System.lineSeparator()
            + entityStringRepresentationBody;

    return entityStringRepresentation;
  }
}