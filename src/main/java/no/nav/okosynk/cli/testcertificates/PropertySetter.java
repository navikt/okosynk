package no.nav.okosynk.cli.testcertificates;

import java.util.Iterator;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PropertySetter {

  private static final Logger LOG = LoggerFactory.getLogger(PropertySetter.class);

  private final Properties propertiesToSet;

  public PropertySetter(
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
