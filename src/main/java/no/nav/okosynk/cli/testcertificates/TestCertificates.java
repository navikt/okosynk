package no.nav.okosynk.cli.testcertificates;

import java.io.InputStream;

public final class TestCertificates {

  private TestCertificates() {
  }

  public static void setupKeyAndTrustStore() {

    final String keyStoreFileName = "keystore.t4.2018-06-07.jks";
    final String keyStorePassword = "X";
    final String trustStoreFileName = "truststore.t4.2018-06-07.jts";
    final String trustStorePassword = "X";

    setupTemporaryKeyStore(keyStoreFileName, keyStorePassword);
    setupTemporaryTrustStore(trustStoreFileName, trustStorePassword);
  }

  private static void setupTemporaryKeyStore(
      final String keyStoreResourceName,
      final String password) {

    final InputStream keyStore = TestCertificates.class.getClassLoader()
        .getResourceAsStream(keyStoreResourceName);
    setupTemporaryKeyStore(keyStore, password);
  }

  private static void setupTemporaryKeyStore(
      final InputStream keystore,
      final String password) {

    (new KeyStore(FileUtils.putInTempFile(keystore).getAbsolutePath(), password))
        .setOn(System.getProperties());
  }

  private static void setupTemporaryTrustStore(
      final String trustStoreResourceName,
      final String password) {

    final InputStream trustStore = TestCertificates.class.getClassLoader()
        .getResourceAsStream(trustStoreResourceName);
    setupTemporaryTrustStore(trustStore, password);
  }

  private static void setupTemporaryTrustStore(
      final InputStream trustStore,
      final String password) {

    (new TrustStore(FileUtils.putInTempFile(trustStore).getAbsolutePath(), password))
        .setOn(System.getProperties());
  }
}
