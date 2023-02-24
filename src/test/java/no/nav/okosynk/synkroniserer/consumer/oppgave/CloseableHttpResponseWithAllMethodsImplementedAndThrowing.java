package no.nav.okosynk.synkroniserer.consumer.oppgave;

import java.io.IOException;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.params.HttpParams;

public class CloseableHttpResponseWithAllMethodsImplementedAndThrowing implements CloseableHttpResponse {

  @Override public ProtocolVersion getProtocolVersion() {throw new UnsupportedOperationException("NYI");}
  @Override public boolean containsHeader(String s) {throw new UnsupportedOperationException("NYI");}
  @Override public Header[] getHeaders(String s) {throw new UnsupportedOperationException("NYI");}
  @Override public Header getFirstHeader(String s) {throw new UnsupportedOperationException("NYI");}
  @Override public Header getLastHeader(String s) {throw new UnsupportedOperationException("NYI");}
  @Override public Header[] getAllHeaders() {throw new UnsupportedOperationException("NYI");}
  @Override public void addHeader(Header header) {throw new UnsupportedOperationException("NYI");}
  @Override public void addHeader(String s, String s1) {throw new UnsupportedOperationException("NYI");}
  @Override public void setHeader(Header header) {throw new UnsupportedOperationException("NYI");}
  @Override public void setHeader(String s, String s1) {throw new UnsupportedOperationException("NYI");}
  @Override public void setHeaders(Header[] headers) {throw new UnsupportedOperationException("NYI");}
  @Override public void removeHeader(Header header) {throw new UnsupportedOperationException("NYI");}
  @Override public void removeHeaders(String s) {throw new UnsupportedOperationException("NYI");}
  @Override public HeaderIterator headerIterator() {throw new UnsupportedOperationException("NYI");}
  @Override public HeaderIterator headerIterator(String s) {throw new UnsupportedOperationException("NYI");}
  @Override public HttpParams getParams() {throw new UnsupportedOperationException("NYI");}
  @Override public void setParams(HttpParams httpParams) {throw new UnsupportedOperationException("NYI");}
  @Override public StatusLine getStatusLine() {throw new UnsupportedOperationException("NYI");}
  @Override public void setStatusLine(StatusLine statusLine) {throw new UnsupportedOperationException("NYI");}
  @Override public void setStatusLine(ProtocolVersion protocolVersion, int i) {throw new UnsupportedOperationException("NYI");}
  @Override public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {throw new UnsupportedOperationException("NYI");}
  @Override public void setStatusCode(int i) throws IllegalStateException {throw new UnsupportedOperationException("NYI");}
  @Override public void setReasonPhrase(String s) throws IllegalStateException {throw new UnsupportedOperationException("NYI");}
  @Override public HttpEntity getEntity() {throw new UnsupportedOperationException("NYI");}
  @Override public void setEntity(HttpEntity httpEntity) {throw new UnsupportedOperationException("NYI");}
  @Override public Locale getLocale() {throw new UnsupportedOperationException("NYI");}
  @Override public void setLocale(Locale locale) {throw new UnsupportedOperationException("NYI");}
  @Override public void close() throws IOException {throw new UnsupportedOperationException("NYI");}
}