package no.nav.okosynk.consumer.oppgave;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class HttpEntityWithAllMethodsImplementedAndThrowing implements HttpEntity {
  @Override public boolean isRepeatable() {throw new UnsupportedOperationException("NYI");}
  @Override public boolean isChunked() {throw new UnsupportedOperationException("NYI");}
  @Override public long getContentLength() {throw new UnsupportedOperationException("NYI");}
  @Override public Header getContentType() {throw new UnsupportedOperationException("NYI");}
  @Override public Header getContentEncoding() {throw new UnsupportedOperationException("NYI");}
  @Override public InputStream getContent() throws IOException, UnsupportedOperationException {throw new UnsupportedOperationException("NYI");}
  @Override public void writeTo(OutputStream outputStream) throws IOException {throw new UnsupportedOperationException("NYI");}
  @Override public boolean isStreaming() {throw new UnsupportedOperationException("NYI");}
  @Override public void consumeContent() throws IOException {throw new UnsupportedOperationException("NYI");}
}
