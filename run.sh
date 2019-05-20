#!/bin/sh
if ! echo "467792be15c4a8807681fd2d5c9c1748" | keytool -list -keystore truststore-q.jts > /dev/null;
then
    echo Truststore is corrupt, or bad password
    exit 1
fi
java \
-Doppgave.endpoint.url=https://service-gw-q3.preprod.local \
-Doppgavebehandling.endpoint.url=https://service-gw-q3.preprod.local \
-Dno.nav.modig.security.appcert.keystore=keystore-q.jks \
-Dno.nav.modig.security.appcert.password=X \
-Djavax.net.ssl.trustStore=truststore-q.jts \
-Djavax.net.ssl.trustStorePassword=X \
-Ddisable.metrics.report=true \
-jar cli/target/okosynk.jar -p cli/src/test/resources/environment-q3.properties.default
