package no.nav.okosynk.cli.testcertificates;

import java.io.InputStream;

public final class TestCertificates_Copy {

  private TestCertificates_Copy() {
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

    InputStream keyStore = TestCertificates_Copy.class.getClassLoader()
        .getResourceAsStream(keyStoreResourceName);
    setupTemporaryKeyStore(keyStore, password);
  }

  private static void setupTemporaryKeyStore(
      final InputStream keystore,
      final String password) {

    (new KeyStore_Copy(FileUtils_Copy.putInTempFile(keystore).getAbsolutePath(), password))
        .setOn(System.getProperties());
  }

  private static void setupTemporaryTrustStore(
      final String trustStoreResourceName,
      final String password) {

    InputStream trustStore = TestCertificates_Copy.class.getClassLoader()
        .getResourceAsStream(trustStoreResourceName);
    setupTemporaryTrustStore(trustStore, password);
  }

  private static void setupTemporaryTrustStore(
      final InputStream trustStore,
      final String password) {

    (new TrustStore_Copy(FileUtils_Copy.putInTempFile(trustStore).getAbsolutePath(), password))
        .setOn(System.getProperties());
  }
}
