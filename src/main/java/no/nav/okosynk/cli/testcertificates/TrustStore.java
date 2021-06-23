package no.nav.okosynk.cli.testcertificates;

import no.nav.okosynk.config.Constants;

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
        props.setProperty(Constants.NAV_TRUSTSTORE_PATH_EXT_KEY, truststoreFilePath);
        props.setProperty(Constants.NAV_TRUSTSTORE_PASSWORD_EXT_KEY, truststorePassword);

        return props;
    }
}
