package no.nav.okosynk.cli.testcertificates;

import java.util.Properties;

final class KeyStore extends PropertySetter {

    KeyStore(
            final String filePath,
            final String password) {

        super(createKeyStoreProperties(filePath, password));
    }

    private static Properties createKeyStoreProperties(
            final String filePath,
            final String password) {

        final Properties props = new Properties();
        //props.setProperty(Constants.SRVOKOSYNK_KEYSTORE_EXT_KEY, filePath);
        //props.setProperty(Constants.SRVOKOSYNK_PASSWORD_EXT_KEY, password);

        return props;
    }
}
