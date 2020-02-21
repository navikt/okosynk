package no.nav.okosynk.cli.testcertificates;

import java.util.Properties;

final class TrustStore extends PropertySetter {

  TrustStore(
      final String truststoreFilePath,
      final String truststorePassword) {

    super(createTrustStoreProperties(truststoreFilePath, truststorePassword));
  }

  private static Properties createTrustStoreProperties(
      final String truststoreFilePath,
      final String truststorePassword) {

    Properties props = new Properties();
    props.setProperty("javax.net.ssl.trustStore", truststoreFilePath);
    props.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);

    return props;
  }
}
