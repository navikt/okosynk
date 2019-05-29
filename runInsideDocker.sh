#!/bin/sh
# This is the start script that will be run from inside the Docker container, to start the job.
if ! echo "${NAV_TRUSTSTORE_PASSWORD}" | keytool -list -keystore ${NAV_TRUSTSTORE_PATH} > /dev/null;
then
    echo Truststore is corrupt, or bad password
    exit 1
fi
java \
-Doppgave.endpoint.url="${SERVICEGATEWAY_URL}" \
-Doppgavebehandling.endpoint.url="${SERVICEGATEWAY_URL}" \
-Dno.nav.modig.security.appcert.keystore="${SRVOKOSYNK_KEYSTORE}" \
-Dno.nav.modig.security.appcert.password="${SRVOKOSYNK_PASSWORD}" \
-Ddisable.metrics.report=true \
-jar "${JAR_FILE:-cli/target/okosynk.jar}"