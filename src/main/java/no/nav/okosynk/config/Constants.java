package no.nav.okosynk.config;

import lombok.Getter;

public class Constants {

    public static final String AUTHORIZATION = "Authorization";
    public static final String HTTP_HEADER_NAV_CONSUMER_TOKEN_KEY = "Nav-Consumer-Token";
    public static final String HTTP_HEADER_NAV_CALL_ID_KEY = "Nav-Call-Id";
    public static final String HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY = "Content-Type";
    public static final String BATCH_MAX_NUMBER_OF_TRIES_KEY = "batch.max.number.of.tries";
    public static final String BATCH_RETRY_WAIT_TIME_IN_MS_KEY = "batch.retry.wait.time.in.ms";
    public static final String NAIS_APP_NAME_KEY = "NAIS_APP_NAME";
    public static final String NAV_TRUSTSTORE_PASSWORD_EXT_KEY = "javax.net.ssl.trustStorePassword";
    public static final String NAV_TRUSTSTORE_PASSWORD_KEY = "NAV_TRUSTSTORE_PASSWORD";
    public static final String NAV_TRUSTSTORE_PATH_EXT_KEY = "javax.net.ssl.trustStore";
    public static final String NAV_TRUSTSTORE_PATH_KEY = "NAV_TRUSTSTORE_PATH";
    public static final String OPPGAVE_URL_KEY = "OPPGAVE_URL";
    public static final String PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY = "PUSH_GATEWAY_ADDRESS";
    public static final String REST_STS_URL_KEY = "REST_STS_URL";
    public static final String X_CORRELATION_ID_HEADER_KEY = "X-Correlation-ID";
    public static final String SHOULD_RUN_OS_OR_UR_KEY = "SHOULD_RUN_OS_OR_UR";

    public static final String FTP_HOST_URL_KEY = "FTPBASEURL_URL";
    public static final String FTP_USERNAME = "FTPCREDENTIALS_USERNAME";
    public static final String FTP_PRIVATEKEY = "FTPCREDENTIALS_PRIVATE_KEY";
    public static final String FTP_HOSTKEY = "FTPCREDENTIALS_HOST_KEY";
    public static final String OPPGAVE_USERNAME = "OPPGAVE_USERNAME";
    public static final String OPPGAVE_PASSWORD = "OPPGAVE_PASSWORD";
    static final String DISABLE_METRICS_REPORT_EXT_KEY = "disable.metrics.report";
    static final String DISABLE_METRICS_REPORT_KEY = "DISABLE_METRICS_REPORT";
    static final String TILLAT_MOCK_PROPERTY_EXT_KEY = "tillatmock";
    static final String TILLAT_MOCK_PROPERTY_KEY = "TILLATMOCK";

    @Getter
    public enum BATCH_TYPE {
        OS(
                "bokosynk001",
                "OKO_OS",
                "os_mapping_regler.properties",
                "okosynk_os_batch_alert"
        ),
        UR(
                "bokosynk002",
                "OKO_UR",
                "ur_mapping_regler.properties",
                "okosynk_ur_batch_alert"
        );

        private final String name;
        private final String oppgaveType;
        private final String mappingRulesPropertiesFileName;
        private final String alertCollectorMetricName;

        BATCH_TYPE(
                final String name,
                final String oppgaveType,
                final String mappingRulesPropertiesFileName,
                final String alertCollectorMetricName
        ) {
            this.name = name;
            this.oppgaveType = oppgaveType;
            this.mappingRulesPropertiesFileName = mappingRulesPropertiesFileName;
            this.alertCollectorMetricName = alertCollectorMetricName;
        }

        public String getConsumerStatisticsName() {
            return name() + " - " + getName();
        }

    }
}
