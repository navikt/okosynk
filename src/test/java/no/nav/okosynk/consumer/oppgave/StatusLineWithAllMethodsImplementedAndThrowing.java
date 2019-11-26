package no.nav.okosynk.consumer.oppgave;

import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;

public class StatusLineWithAllMethodsImplementedAndThrowing implements StatusLine {
  @Override public ProtocolVersion getProtocolVersion() {throw new UnsupportedOperationException("NYI");}
  @Override public int getStatusCode() {throw new UnsupportedOperationException("NYI");}
  @Override public String getReasonPhrase() {throw new UnsupportedOperationException("NYI");}
}
