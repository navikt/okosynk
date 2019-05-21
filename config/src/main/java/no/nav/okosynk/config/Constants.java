package no.nav.okosynk.config;

public class Constants {

    public enum BATCH_TYPE {
          OS(
                  OS_BATCH_NAVN
                , OS_BATCH_BRUKER_KEY
                , OS_BATCH_BRUKER_DEFAULT_VALUE
                , OS_BATCH_RUN_SYNCHRONOUSLY_TIMER_NAME
                , OS_OPPGAVE_TYPE
                , OS_FTP_HOST_URL_KEY
                , OS_FTP_USER_KEY
                , OS_FTP_PASSWORD_KEY
                , OS_FTP_CONNECTION_TIMEOUT_KEY
                , OS_FTP_CONNECTION_TIMEOUT_DEFAULT_VALUE_IN_MS
                , OS_FTP_CHARSET_NAME_KEY
                , OS_EXECUTION_ID_OFFSET
                , OS_MAPPING_RULES_PROPERTIES_FILENAME)
        , UR(
                  UR_BATCH_NAVN
                , UR_BATCH_BRUKER_KEY
                , UR_BATCH_BRUKER_DEFAULT_VALUE
                , UR_BATCH_RUN_SYNCHRONOUSLY_TIMER_NAME
                , UR_OPPGAVE_TYPE
                , UR_FTP_HOST_URL_KEY
                , UR_FTP_USER_KEY
                , UR_FTP_PASSWORD_KEY
                , UR_FTP_CONNECTION_TIMEOUT_KEY
                , UR_FTP_CONNECTION_TIMEOUT_DEFAULT_VALUE_IN_MS
                , UR_FTP_CHARSET_NAME_KEY
                , UR_EXECUTION_ID_OFFSET
                , UR_MAPPING_RULES_PROPERTIES_FILENAME)
        ;

        private final String name;
        private final String batchBrukerKey;
        private final String batchBrukerDefaultValue;
        private final String batchRunSynchronouslyTimerName;
        private final String oppgaveType;
        private final String ftpHostUrlKey;
        private final String ftpUserKey;
        private final String ftpPasswordKey;
        private final String ftpConnectionTimeoutKey;
        private final int    ftpConnectionTimeoutDefaultValueInMs;
        private final String ftpCharsetNameKey;
        private final long   executionIdOffset;
        private final String mappingRulesPropertiesFileName;

        public String getName() {
            return name;
        }

        public String getBatchBrukerKey() {
            return batchBrukerKey;
        }

        public String getBatchBrukerDefaultValue() {
            return batchBrukerDefaultValue;
        }

        public String getBatchRunSynchronouslyTimerName() {
            return batchRunSynchronouslyTimerName;
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

        public String getFtpConnectionTimeoutKey() {
            return ftpConnectionTimeoutKey;
        }

        public int getFtpConnectionTimeoutDefaultValueInMs() {
            return ftpConnectionTimeoutDefaultValueInMs;
        }

        public String getFtpCharsetNameKey() {
            return ftpCharsetNameKey;
        }

        public long getExecutionIdOffset() {
            return executionIdOffset;
        }

        public String getMappingRulesPropertiesFileName() {
            return mappingRulesPropertiesFileName;
        }

        BATCH_TYPE(
              final String name
            , final String batchBrukerKey
            , final String batchBrukerDefaultValue
            , final String batchRunSynchronouslyTimerName
            , final String oppgaveType
            , final String ftpHostUrlKey
            , final String ftpUserKey
            , final String ftpPasswordKey
            , final String ftpConnectionTimeoutKey
            , final int    ftpConnectionTimeoutDefaultValueInMs
            , final String ftpCharsetNameKey
            , final long   executionIdOffset
            , final String mappingRulesPropertiesFileName
        ) {
            this.name                                 = name;
            this.batchBrukerKey                       = batchBrukerKey;
            this.batchBrukerDefaultValue              = batchBrukerDefaultValue;
            this.batchRunSynchronouslyTimerName       = batchRunSynchronouslyTimerName;
            this.oppgaveType                          = oppgaveType;
            this.ftpHostUrlKey                        = ftpHostUrlKey;
            this.ftpUserKey                           = ftpUserKey;
            this.ftpPasswordKey                       = ftpPasswordKey;
            this.ftpConnectionTimeoutKey              = ftpConnectionTimeoutKey;
            this.ftpConnectionTimeoutDefaultValueInMs = ftpConnectionTimeoutDefaultValueInMs;
            this.ftpCharsetNameKey                    = ftpCharsetNameKey;
            this.executionIdOffset                    = executionIdOffset;
            this.mappingRulesPropertiesFileName       = mappingRulesPropertiesFileName;
        }
    }
    // =========================================================================
    private static final String OS_BATCH_NAVN                                 = "bokosynk001";
    private static final String OS_BATCH_BRUKER_KEY                           = "SRVBOKOSYNK001_USERNAME";
    private static final String OS_BATCH_BRUKER_DEFAULT_VALUE                 = "srvbokosynk001";
    private static final String OS_BATCH_RUN_SYNCHRONOUSLY_TIMER_NAME         = "OsBatchRestControllerStartBatchSynchronously";
    private static final String OS_OPPGAVE_TYPE                               = "OKO_OS";
    private static final String OS_FTP_HOST_URL_KEY                           = "OSFTPBASEURL_URL"         ;
    private static final String OS_FTP_USER_KEY                               = "OSFTPCREDENTIALS_USERNAME";
    private static final String OS_FTP_PASSWORD_KEY                           = "OSFTPCREDENTIALS_PASSWORD";
    private static final String OS_FTP_CONNECTION_TIMEOUT_KEY                 = "OS_FTP_CONNECT_TIMEOUT_IN_MS";
    private static final int    OS_FTP_CONNECTION_TIMEOUT_DEFAULT_VALUE_IN_MS = 20000;
    private static final String OS_FTP_CHARSET_NAME_KEY                       = "OS_FTP_CHARSET";
    private static final long   OS_EXECUTION_ID_OFFSET                        = 0L;
    private static final String OS_MAPPING_RULES_PROPERTIES_FILENAME          = "os_mapping_regler.properties";
    // -------------------------------------------------------------------------
    private static final String UR_BATCH_NAVN                                 = "bokosynk002";
    private static final String UR_BATCH_BRUKER_KEY                           = "SRVBOKOSYNK002_USERNAME";
    private static final String UR_BATCH_BRUKER_DEFAULT_VALUE                 = "srvbokosynk002";
    private static final String UR_BATCH_RUN_SYNCHRONOUSLY_TIMER_NAME         = "UrBatchRestControllerStartBatchSynchronously";
    private static final String UR_OPPGAVE_TYPE                               = "OKO_UR";
    private static final String UR_FTP_HOST_URL_KEY                           = "URFTPBASEURL_URL"         ;
    private static final String UR_FTP_USER_KEY                               = "URFTPCREDENTIALS_USERNAME";
    private static final String UR_FTP_PASSWORD_KEY                           = "URFTPCREDENTIALS_PASSWORD";
    private static final String UR_FTP_CONNECTION_TIMEOUT_KEY                 = "UR_FTP_CONNECT_TIMEOUT_IN_MS";
    private static final int    UR_FTP_CONNECTION_TIMEOUT_DEFAULT_VALUE_IN_MS = 20000;
    private static final String UR_FTP_CHARSET_NAME_KEY                       = "UR_FTP_CHARSET";
    private static final long   UR_EXECUTION_ID_OFFSET                        = Long.MAX_VALUE/2;
    private static final String UR_MAPPING_RULES_PROPERTIES_FILENAME          = "ur_mapping_regler.properties";
    // =========================================================================
    // =========================================================================

    public enum CONSUMER_TYPE {
          OPPGAVE(
              OPPGAVE_NAME
            , OPPGAVE_WITHMOCK_KEY
            , VIRKSOMHET_OPPGAVE_V3_ENDPOINTURL_KEY
            , OPPGAVE_TJENESTEBESKRIVELSE
            , OPPGAVE_TIMEOUT
            , null
            , null
            , null
            , null

          )

        , OPPGAVE_BEHANDLING(
              OPPGAVE_BEHANDLING_NAME
            , OPPGAVEBEHANDLING_WITHMOCK_KEY
            , VIRKSOMHET_OPPGAVEBEHANDLING_V3_ENDPOINTURL_KEY
            , OPPGAVE_BEHANDLING_TJENESTEBESKRIVELSE
            , OPPGAVE_BEHANDLING_TIMEOUT
            , OPPGAVEBEHANDLING_OPPRETTOPPGAVEBOLK_MAXANTALL_KEY
            , OPPGAVEBEHANDLING_OPPRETTOPPGAVEBOLK_MAXANTALL_DEFAULT_VALUE
            , OPPGAVEBEHANDLING_OPPDATEROPPGAVEBOLK_MAXANTALL_KEY
            , OPPGAVEBEHANDLING_OPPDATEROPPGAVEBOLK_MAXANTALL_DEFAULT_VALUE
        )
        ;

        private final String name;
        private final String mockKey;
        private final String endpointUrlKey;
        final String tjenestebeskrivelse;
        final int timeout;
        final String bulkSizeMaxForCreate;
        final String bulkSizeDefaultForCreate;
        final String bulkSizeMaxForUpdate;

        public String getName() {
            return name;
        }

        public String getMockKey() {
            return mockKey;
        }

        public String getEndpointUrlKey() {
            return endpointUrlKey;
        }

        public String getTjenestebeskrivelse() {
            return tjenestebeskrivelse;
        }

        public int getTimeout() {
            return timeout;
        }

        public String getBulkSizeMaxForCreate() {
            return bulkSizeMaxForCreate;
        }

        public String getBulkSizeDefaultForCreate() {
            return bulkSizeDefaultForCreate;
        }

        public String getBulkSizeMaxForUpdate() {
            return bulkSizeMaxForUpdate;
        }

        public String getBulkSizeDefaultForUpdate() {
            return bulkSizeDefaultForUpdate;
        }

        final String bulkSizeDefaultForUpdate;

        private CONSUMER_TYPE(
            final String name,
            final String mockKey,
            final String endpointUrlKey,
            final String tjenestebeskrivelse,
            final int    timeout,
            final String bulkSizeMaxForCreate,
            final String bulkSizeDefaultForCreate,
            final String bulkSizeMaxForUpdate,
            final String bulkSizeDefaultForUpdate

        ) {
            this.name                     = name;
            this.mockKey                  = mockKey;
            this.endpointUrlKey           = endpointUrlKey;
            this.tjenestebeskrivelse      = tjenestebeskrivelse;
            this.timeout                  = timeout;
            this.bulkSizeMaxForCreate     = bulkSizeMaxForCreate;
            this.bulkSizeDefaultForCreate = bulkSizeDefaultForCreate;
            this.bulkSizeMaxForUpdate     = bulkSizeMaxForUpdate;
            this.bulkSizeDefaultForUpdate = bulkSizeDefaultForUpdate;
        }
    }

    // ==========================================================================================================================
    private static final String  OPPGAVE_NAME                                                  = "oppgave";
    private static final String  OPPGAVE_WITHMOCK_KEY                                          = "OPPGAVE_WITHMOCK";
    private static final String  VIRKSOMHET_OPPGAVE_V3_ENDPOINTURL_KEY                         = "VIRKSOMHET_OPPGAVE_V3_ENDPOINTURL";
    private static final String  OPPGAVE_TJENESTEBESKRIVELSE                                   = "OppgaveV3";
    private static final int     OPPGAVE_TIMEOUT                                               = 120000;
    // --------------------------------------------------------------------------------------------------------------------------
    private static final String  OPPGAVE_BEHANDLING_NAME                                       = "oppgavebehandling";
    private static final String  OPPGAVEBEHANDLING_WITHMOCK_KEY                                = "OPPGAVEBEHANDLING_WITHMOCK";
    private static final String  VIRKSOMHET_OPPGAVEBEHANDLING_V3_ENDPOINTURL_KEY               = "VIRKSOMHET_OPPGAVEBEHANDLING_V3_ENDPOINTURL";
    private static final String  OPPGAVE_BEHANDLING_TJENESTEBESKRIVELSE                        = "OppgavebehandlingV3";
    private static final int     OPPGAVE_BEHANDLING_TIMEOUT                                    = 120000;
    private static final String  OPPGAVEBEHANDLING_OPPRETTOPPGAVEBOLK_MAXANTALL_KEY            = "OPPGAVEBEHANDLING_OPPRETTOPPGAVEBOLK_MAXANTALL";
    private static final String  OPPGAVEBEHANDLING_OPPRETTOPPGAVEBOLK_MAXANTALL_DEFAULT_VALUE  = "50";
    private static final String  OPPGAVEBEHANDLING_OPPDATEROPPGAVEBOLK_MAXANTALL_KEY           = "OPPGAVEBEHANDLING_OPPDATEROPPGAVEBOLK_MAXANTALL";
    private static final String  OPPGAVEBEHANDLING_OPPDATEROPPGAVEBOLK_MAXANTALL_DEFAULT_VALUE = "50";
    // ==========================================================================================================================
    // ==========================================================================================================================
    public  static final String  OKOSYNK_SHOULD_RUN_DRY_KEY                                    = "OKOSYNK_SHOULD_RUN_DRY";
    public  static final boolean OKOSYNK_SHOULD_RUN_DRY_DEFAULT_VALUE                          = false;
    public  static final String  TILLAT_MOCK_PROPERTY_KEY                                      = "TILLATMOCK";
    public  static final String  TILLAT_MOCK_PROPERTY_EXT_KEY                                  = "tillatmock";
    public  static final String  SHOULD_USE_SOAP_KEY                                           = "SHOULD_USE_SOAP";
    public  static final String  SRVBOKOSYNK_USERNAME_KEY                                      = "SRVBOKOSYNK_USERNAME";
    public  static final String  SRVBOKOSYNK_PASSWORD_EXT_KEY                                  = "no.nav.modig.security.systemuser.username";
    public  static final String  SRVBOKOSYNK_PASSWORD_KEY                                      = "SRVBOKOSYNK_PASSWORD";
    public  static final String  SYSTEM_USER_PASSWORD_EXT_KEY                                  = "no.nav.modig.security.systemuser.password";
    public  static final String  DISABLE_METRICS_REPORT_KEY                                    = "DISABLE_METRICS_REPORT";
    public  static final String  DISABLE_METRICS_REPORT_EXT_KEY                                = "disable.metrics.report";
    public  static final int     FTP_HOST_PORT_DEFAULT_VALUE                                   = 20;
    public enum FTP_PROTOCOL {
        FTP, SFTP
    }
    public static final FTP_PROTOCOL FTP_PROTOCOL_DEFAULT_VALUE  = FTP_PROTOCOL.SFTP;

    public static final String SRVOKOSYNK_KEYSTORE_KEY           = "SRVOKOSYNK_KEYSTORE";
    public static final String SRVOKOSYNK_KEYSTORE_EXT_KEY       = "no.nav.modig.security.appcert.keystore";
    public static final String SRVOKOSYNK_KEYSTORE_DEFAULT_VALUE = "\"keystore.jks\"";
    public static final String SRVOKOSYNK_PASSWORD_KEY           = "SRVOKOSYNK_PASSWORD";
    public static final String SRVOKOSYNK_PASSWORD_EXT_KEY       = "no.nav.modig.security.appcert.password";
    public static final String SRVOKOSYNK_PASSWORD_DEFAULT_VALUE = "X";
    public static final String NAV_TRUSTSTORE_PATH_KEY           = "NAV_TRUSTSTORE_PATH";
    public static final String NAV_TRUSTSTORE_PATH_EXT_KEY       = "javax.net.ssl.trustStore";
    public static final String NAV_TRUSTSTORE_PASSWORD_KEY       = "NAV_TRUSTSTORE_PASSWORD";
    public static final String NAV_TRUSTSTORE_PASSWORD_EXT_KEY   = "javax.net.ssl.trustStorePassword";
    // ==========================================================================================================================
    // ==========================================================================================================================
}
