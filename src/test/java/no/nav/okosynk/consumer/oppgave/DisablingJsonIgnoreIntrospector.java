package no.nav.okosynk.consumer.oppgave;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

class DisablingJsonIgnoreIntrospector extends JacksonAnnotationIntrospector {

  @Override
  public boolean hasIgnoreMarker(final AnnotatedMember m) {
    return false;
  }
}