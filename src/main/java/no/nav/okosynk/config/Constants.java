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

    static final String DISABLE_METRICS_REPORT_EXT_KEY = "disable.metrics.report";
    static final String DISABLE_METRICS_REPORT_KEY = "DISABLE_METRICS_REPORT";
    static final String TILLAT_MOCK_PROPERTY_EXT_KEY = "tillatmock";
    static final String TILLAT_MOCK_PROPERTY_KEY = "TILLATMOCK";

    public enum BATCH_TYPE {
        OS(
                "bokosynk001",
                "SRVBOKOSYNK001_USERNAME",
                "srvbokosynk001",
                "OKO_OS",
                "OSFTPBASEURL_URL",
                "FTPCREDENTIALS_USERNAME",
                "FTPCREDENTIALS_PASSWORD",
                "OS_FTP_CHARSET",
                "os_mapping_regler.properties",
                "SRVBOKOSYNK001_PASSWORD",
                "okosynk_os_batch_alert"
        ),
        UR(
                "bokosynk002",
                "SRVBOKOSYNK002_USERNAME",
                "srvbokosynk002",
                "OKO_UR",
                "URFTPBASEURL_URL",
                "FTPCREDENTIALS_USERNAME",
                "FTPCREDENTIALS_PASSWORD",
                "UR_FTP_CHARSET",
                "ur_mapping_regler.properties",
                "SRVBOKOSYNK002_PASSWORD",
                "okosynk_ur_batch_alert"
        );

        @Getter
        private final String name;
        private final String batchBrukerKey;
        private final String batchBrukerDefaultValue;
        @Getter
        private final String oppgaveType;
        @Getter
        private final String ftpHostUrlKey;
        @Getter
        private final String ftpUserKey;
        @Getter
        private final String ftpPasswordKey;
        @Getter
        private final String ftpCharsetNameKey;
        @Getter
        private final String mappingRulesPropertiesFileName;
        private final String batchBrukerPasswordKey;
        @Getter
        private final String alertCollectorMetricName;

        BATCH_TYPE(
                final String name,
                final String batchBrukerKey,
                final String batchBrukerDefaultValue,
                final String oppgaveType,
                final String ftpHostUrlKey,
                final String ftpUserKey,
                final String ftpPasswordKey,
                final String ftpCharsetNameKey,
                final String mappingRulesPropertiesFileName,
                final String batchBrukerPasswordKey,
                final String alertCollectorMetricName
        ) {
            this.name = name;
            this.batchBrukerKey = batchBrukerKey;
            this.batchBrukerDefaultValue = batchBrukerDefaultValue;
            this.oppgaveType = oppgaveType;
            this.ftpHostUrlKey = ftpHostUrlKey;
            this.ftpUserKey = ftpUserKey;
            this.ftpPasswordKey = ftpPasswordKey;
            this.ftpCharsetNameKey = ftpCharsetNameKey;
            this.mappingRulesPropertiesFileName = mappingRulesPropertiesFileName;
            this.batchBrukerPasswordKey = batchBrukerPasswordKey;
            this.alertCollectorMetricName = alertCollectorMetricName;
        }

        public String getConsumerStatisticsName() {
            return name() + " - " + getName();
        }

        String getBatchBrukerKey() {
            return batchBrukerKey;
        }

        String getBatchBrukerDefaultValue() {
            return batchBrukerDefaultValue;
        }

        String getBatchBrukerPasswordKey() {
            return batchBrukerPasswordKey;
        }
    }
}
