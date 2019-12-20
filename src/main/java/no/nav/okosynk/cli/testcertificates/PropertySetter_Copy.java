package no.nav.okosynk.cli.testcertificates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Properties;

class PropertySetter_Copy {

  private static final Logger LOG = LoggerFactory.getLogger(PropertySetter_Copy.class);

  private final Properties propertiesToSet;

  public PropertySetter_Copy(
      final Properties propertiesToSet) {

    this.propertiesToSet = propertiesToSet;
  }

  final void setOn(
      final Properties properties) {

    String propName;
    for (Iterator i$ = this.propertiesToSet.stringPropertyNames().iterator(); i$.hasNext();
        properties.setProperty(propName, this.propertiesToSet.getProperty(propName))) {
      propName = (String) i$.next();
      if (properties.containsKey(propName)) {
        LOG.warn("Overwriting {} = {} to {} = {}",
            new Object[]{propName, properties.getProperty(propName), propName,
                this.propertiesToSet.getProperty(propName)});
      } else {
        LOG.info("Setting {} = {}", propName, this.propertiesToSet.getProperty(propName));
      }
    }

  }
}
