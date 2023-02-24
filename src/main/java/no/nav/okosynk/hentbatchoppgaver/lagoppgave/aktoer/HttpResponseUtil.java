package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

public class HttpResponseUtil {
  private HttpResponseUtil() {
  }

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
    } catch (Exception e) {
      entityStringRepresentationBody = "Nothing";
    }

    return System.lineSeparator()
       + "entityStringRepresentation: " + System.lineSeparator()
       + entityStringRepresentationBody;
  }
}