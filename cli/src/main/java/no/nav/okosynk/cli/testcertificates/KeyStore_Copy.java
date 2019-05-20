package no.nav.okosynk.cli.testcertificates;

import java.util.Properties;

public class KeyStore_Copy extends PropertySetter_Copy {

    public KeyStore_Copy(
        final String filePath,
        final String password) {

        super(createKeyStoreProperties(filePath, password));
    }

    private static Properties createKeyStoreProperties(
        final String filePath,
        final String password) {

        Properties props = new Properties();
        props.setProperty("no.nav.modig.security.appcert.keystore", filePath);
        props.setProperty("no.nav.modig.security.appcert.password", password);
        return props;
    }
}
