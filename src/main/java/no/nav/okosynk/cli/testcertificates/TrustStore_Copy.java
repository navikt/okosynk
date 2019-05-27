package no.nav.okosynk.cli.testcertificates;

import java.util.Properties;

public class TrustStore_Copy extends PropertySetter_Copy {

    public TrustStore_Copy(
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
