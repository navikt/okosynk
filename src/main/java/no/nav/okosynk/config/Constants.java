package no.nav.okosynk.config;

public class Constants {

  public enum BATCH_TYPE {
    OS(
        OS_BATCH_NAVN,
        OS_BATCH_BRUKER_KEY,
        OS_BATCH_BRUKER_DEFAULT_VALUE,
        OS_OPPGAVE_TYPE,
        OS_FTP_HOST_URL_KEY,
        OS_FTP_USER_KEY,
        OS_FTP_PASSWORD_KEY,
        OS_FTP_CHARSET_NAME_KEY,
        OS_MAPPING_RULES_PROPERTIES_FILENAME,
        OS_BATCH_BRUKER_PASSWORD_KEY,
        OS_ALERT_COLLECTOR_METRIC_NAME
    ),
    UR(
        UR_BATCH_NAVN,
        UR_BATCH_BRUKER_KEY,
        UR_BATCH_BRUKER_DEFAULT_VALUE,
        UR_OPPGAVE_TYPE,
        UR_FTP_HOST_URL_KEY,
        UR_FTP_USER_KEY,
        UR_FTP_PASSWORD_KEY,
        UR_FTP_CHARSET_NAME_KEY,
        UR_MAPPING_RULES_PROPERTIES_FILENAME,
        UR_BATCH_BRUKER_PASSWORD_KEY,
        UR_ALERT_COLLECTOR_METRIC_NAME
    );

    private final String name;
    private final String batchBrukerKey;
    private final String batchBrukerDefaultValue;
    private final String oppgaveType;
    private final String ftpHostUrlKey;
    private final String ftpUserKey;
    private final String ftpPasswordKey;
    private final String ftpCharsetNameKey;
    private final String mappingRulesPropertiesFileName;
    private final String batchBrukerPasswordKey;
    private final String alertCollectorMetricName;

    public String getConsumerStatisticsName() {
      return name() + " - " + getName();
    }

    public String getBatchBrukerPasswordKey() {
      return batchBrukerPasswordKey;
    }

    public String getName() {
      return name;
    }

    public String getAlertCollectorMetricName() {
      return this.alertCollectorMetricName;
    }

    public String getBatchBrukerKey() {
      return batchBrukerKey;
    }

    public String getBatchBrukerDefaultValue() {
      return batchBrukerDefaultValue;
    }

    public String getOppgaveType() {
      return oppgaveType;
    }

    public String getFtpHostUrlKey() {
      return ftpHostUrlKey;
    }

    public String getFtpUserKey() {
      return ftpUserKey;
    }

    public String getFtpPasswordKey() {
      return ftpPasswordKey;
    }

    public String getFtpCharsetNameKey() {
      return ftpCharsetNameKey;
    }

    public String getMappingRulesPropertiesFileName() {
      return mappingRulesPropertiesFileName;
    }

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
  }

  // =========================================================================
  private static final String OS_BATCH_NAVN = "bokosynk001";
  private static final String OS_BATCH_BRUKER_KEY = "SRVBOKOSYNK001_USERNAME";
  private static final String OS_BATCH_BRUKER_PASSWORD_KEY = "SRVBOKOSYNK001_PASSWORD";
  private static final String OS_BATCH_BRUKER_DEFAULT_VALUE = "srvbokosynk001";
  private static final String OS_OPPGAVE_TYPE = "OKO_OS";
  private static final String OS_FTP_HOST_URL_KEY = "OSFTPBASEURL_URL";
  private static final String OS_FTP_USER_KEY = "OSFTPCREDENTIALS_USERNAME";
  private static final String OS_FTP_PASSWORD_KEY = "OSFTPCREDENTIALS_PASSWORD";
  private static final String OS_FTP_CHARSET_NAME_KEY = "OS_FTP_CHARSET";
  private static final String OS_MAPPING_RULES_PROPERTIES_FILENAME = "os_mapping_regler.properties";
  private static final String OS_ALERT_COLLECTOR_METRIC_NAME = "okosynk_os_batch_alert";
  // -------------------------------------------------------------------------
  private static final String UR_BATCH_NAVN = "bokosynk002";
  private static final String UR_BATCH_BRUKER_KEY = "SRVBOKOSYNK002_USERNAME";
  private static final String UR_BATCH_BRUKER_PASSWORD_KEY = "SRVBOKOSYNK002_PASSWORD";
  private static final String UR_BATCH_BRUKER_DEFAULT_VALUE = "srvbokosynk002";
  private static final String UR_OPPGAVE_TYPE = "OKO_UR";
  private static final String UR_FTP_HOST_URL_KEY = "URFTPBASEURL_URL";
  private static final String UR_FTP_USER_KEY = "URFTPCREDENTIALS_USERNAME";
  private static final String UR_FTP_PASSWORD_KEY = "URFTPCREDENTIALS_PASSWORD";
  private static final String UR_FTP_CHARSET_NAME_KEY = "UR_FTP_CHARSET";
  private static final String UR_MAPPING_RULES_PROPERTIES_FILENAME = "ur_mapping_regler.properties";
  private static final String UR_ALERT_COLLECTOR_METRIC_NAME = "okosynk_ur_batch_alert";
  // =========================================================================
  static final String TILLAT_MOCK_PROPERTY_KEY = "TILLATMOCK";
  static final String TILLAT_MOCK_PROPERTY_EXT_KEY = "tillatmock";
  static final String SRVBOKOSYNK_USERNAME_KEY = "SRVBOKOSYNK_USERNAME";
  static final String SRVBOKOSYNK_PASSWORD_EXT_KEY = "no.nav.modig.security.systemuser.username";
  static final String SRVBOKOSYNK_PASSWORD_KEY = "SRVBOKOSYNK_PASSWORD";
  static final String SYSTEM_USER_PASSWORD_EXT_KEY = "no.nav.modig.security.systemuser.password";
  static final String DISABLE_METRICS_REPORT_KEY = "DISABLE_METRICS_REPORT";
  static final String DISABLE_METRICS_REPORT_EXT_KEY = "disable.metrics.report";
  public static final int FTP_HOST_PORT_DEFAULT_VALUE = 20;
  public static final String BATCH_RETRY_WAIT_TIME_IN_MS_KEY = "batch.retry.wait.time.in.ms";
  public static final String BATCH_MAX_NUMBER_OF_TRIES_KEY = "batch.max.number.of.tries";

  public enum FTP_PROTOCOL {
    FTP, SFTP
  }

  public static final FTP_PROTOCOL FTP_PROTOCOL_DEFAULT_VALUE = FTP_PROTOCOL.SFTP;

  public static final String SRVOKOSYNK_KEYSTORE_KEY = "SRVOKOSYNK_KEYSTORE";
  public static final String SRVOKOSYNK_KEYSTORE_EXT_KEY = "no.nav.modig.security.appcert.keystore";
  public static final String SRVOKOSYNK_KEYSTORE_DEFAULT_VALUE = "\"keystore.jks\"";
  public static final String SRVOKOSYNK_PASSWORD_KEY = "SRVOKOSYNK_PASSWORD";
  public static final String SRVOKOSYNK_PASSWORD_EXT_KEY = "no.nav.modig.security.appcert.password";
  public static final String SRVOKOSYNK_PASSWORD_DEFAULT_VALUE = "X";
  public static final String NAV_TRUSTSTORE_PATH_KEY = "NAV_TRUSTSTORE_PATH";
  public static final String NAV_TRUSTSTORE_PATH_EXT_KEY = "javax.net.ssl.trustStore";
  public static final String NAV_TRUSTSTORE_PASSWORD_KEY = "NAV_TRUSTSTORE_PASSWORD";
  public static final String NAV_TRUSTSTORE_PASSWORD_EXT_KEY = "javax.net.ssl.trustStorePassword";
  public static final String REST_STS_URL_KEY = "REST_STS_URL";
  public static final String REST_AKTOER_REGISTER_URL_KEY = "AKTOERREGISTER_URL";
  public static final String OPPGAVE_URL_KEY = "OPPGAVE_URL";
  public static final String PUSH_GATEWAY_ENDPOINT_NAME_AND_PORT_KEY = "PUSH_GATEWAY_ADDRESS";
  public static final String X_CORRELATION_ID_HEADER_KEY = "X-Correlation-ID";

  // ==========================================================================================================================
  // ==========================================================================================================================
}
