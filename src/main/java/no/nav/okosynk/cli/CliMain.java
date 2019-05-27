package no.nav.okosynk.cli;

import no.nav.okosynk.batch.BatchStatus;
import no.nav.okosynk.cli.os.OsBatchService;
import no.nav.okosynk.cli.testcertificates.TestCertificates_Copy;
import no.nav.okosynk.cli.ur.UrBatchService;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.OkosynkConfiguration;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CliMain {

    private enum ExitCode {
        OK, // MUST BE ORDINAL 0!
        ERROR
    }

    private static final Logger logger                                            = LoggerFactory.getLogger(CliMain.class);

    private static final String CLI_PROGRAM_NAME                                  = "java -jar okosynk.jar";

    private static final String CLI_APPLICATION_PROPERTIES_FILENAME_KEY           = "p";
    private static final String CLI_APPLICATION_PROPERTIES_FILENAME_LONG_KEY      = "propFile";
    private static final String CLI_APPLICATION_PROPERTIES_FILENAME_DESCRIPTION   = "The name of a file from which the application should read properties";
    private static final String CLI_APPLICATION_PROPERTIES_FILENAME_ARG_NAME      = "applicationPropertiesFileName";
    private static final String CLI_APPLICATION_PROPERTIES_FILENAME_DEFAULT_VALUE = "okosynk.properties";

    private static final String CLI_HELP_KEY                                      = "h";
    private static final String CLI_HELP_LONG_KEY                                 = "help";
    private static final String CLI_HELP_DESCRIPTION                              = "Print this message";

    private static final String CLI_START_FTP_SERVER_KEY                          = "f";
    private static final String CLI_START_FTP_SERVER_LONG_KEY                     = "ftp";
    private static final String CLI_START_FTP_SERVER_DESCRIPTION                  = "Start a local (s)ftp server for testing purposes";

    private static final String CLI_OS_ONLY_KEY                                   = "o";
    private static final String CLI_OS_ONLY_LONG_KEY                              = "onlyOs";
    private static final String CLI_OS_ONLY_DESCRIPTION                           = "Only run no.nav.okosynk.io.os batch";

    private static final String CLI_UR_ONLY_KEY                                   = "u";
    private static final String CLI_UR_ONLY_LONG_KEY                              = "onlyUr";
    private static final String CLI_UR_ONLY_DESCRIPTION                           = "Only run ur batch";

    private IOkosynkConfiguration getOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    private final IOkosynkConfiguration okosynkConfiguration;

    private IStartableAndStoppable getFtpServerTestStarter() {
        return ftpServerTestStarter;
    }

    private void setFtpServerTestStarter(IStartableAndStoppable ftpServerTestStarter) {
        this.ftpServerTestStarter = ftpServerTestStarter;
    }

    private IStartableAndStoppable ftpServerTestStarter = null;

    public CliMain(final String applicationPropertiesFileName) {

        // TODO: This instance of IOkosynkConfiguration should maybe be injected around.
        final IOkosynkConfiguration okosynkConfiguration =
            getOkosynkConfiguration(applicationPropertiesFileName);
        this.okosynkConfiguration = okosynkConfiguration;
        setUpCertificates();
    }

    public static void main(String[] args) throws Exception {

        ExitCode exitCode = ExitCode.OK;
        final CommandLine commandLine = treatCommandLineArgs(args);
        if (!commandLineContainsHelpOptions(commandLine)) {

            CliMain cliMain = null;
            try {
                final String applicationPropertiesFileName;
                if (commandLineContainsPropertyFileName(commandLine)) {
                    applicationPropertiesFileName = commandLine.getOptionValue(CLI_APPLICATION_PROPERTIES_FILENAME_KEY);
                } else {
                    applicationPropertiesFileName = CLI_APPLICATION_PROPERTIES_FILENAME_DEFAULT_VALUE;
                }

                logger.info("The following properties file will be used: {}", applicationPropertiesFileName);

                cliMain = new CliMain(applicationPropertiesFileName);
                if (shouldStartFtpServer(commandLine)) {
                    cliMain.startFtpServer();
                }
                final BatchStatus batchStatus =
                    cliMain.runBatches(shouldOnlyRunOs(commandLine), shouldOnlyRunUr(commandLine));

                exitCode = (BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL.equals(batchStatus)) ? ExitCode.OK : ExitCode.ERROR;

                logger.info("okosynk has finished with the BatchStatus {}", batchStatus);

            } finally {
                if ((cliMain != null) && shouldStartFtpServer(commandLine)) {
                    cliMain.stopFtpServer();
                }
            }
        }

        final int exitNumber = exitCode.ordinal();
        logger.info("Exiting with exit code {} ({})", exitNumber, exitCode);
        System.exit(exitCode.ordinal());
    }

    private static boolean commandLineContainsHelpOptions(final CommandLine commandLine) {
        return commandLine.hasOption(CLI_HELP_KEY);
    }

    private static boolean commandLineContainsPropertyFileName(final CommandLine commandLine) {
        return commandLine.hasOption(CLI_APPLICATION_PROPERTIES_FILENAME_KEY);
    }

    private static boolean shouldStartFtpServer(final CommandLine commandLine) {
        return commandLine.hasOption(CLI_START_FTP_SERVER_KEY);
    }

    private static boolean shouldOnlyRunOs(final CommandLine commandLine) {
        return commandLine.hasOption(CLI_OS_ONLY_KEY);
    }

    private static boolean shouldOnlyRunUr(final CommandLine commandLine) {
        return commandLine.hasOption(CLI_UR_ONLY_KEY);
    }

    private static CommandLine treatCommandLineArgs(final String[] args) throws ParseException {

        final Option helpOption =
            Option
                .builder(CLI_HELP_KEY)
                .longOpt(CLI_HELP_LONG_KEY)
                .desc(CLI_HELP_DESCRIPTION)
                .required(false)
                .hasArg(false)
                .build();

        final Option ftpOption =
            Option
                .builder(CLI_START_FTP_SERVER_KEY)
                .longOpt(CLI_START_FTP_SERVER_LONG_KEY)
                .desc(CLI_START_FTP_SERVER_DESCRIPTION)
                .required(false)
                .hasArg(false)
                .build();

        final Option osOption =
            Option
                .builder(CLI_OS_ONLY_KEY)
                .longOpt(CLI_OS_ONLY_LONG_KEY)
                .desc(CLI_OS_ONLY_DESCRIPTION)
                .required(false)
                .hasArg(false)
                .build();

        final Option urOption =
            Option
                .builder(CLI_UR_ONLY_KEY)
                .longOpt(CLI_UR_ONLY_LONG_KEY)
                .desc(CLI_UR_ONLY_DESCRIPTION)
                .required(false)
                .hasArg(false)
                .build();

        final Option applicationPropertiesFileNameOption =
            Option
                .builder(CLI_APPLICATION_PROPERTIES_FILENAME_KEY)
                .longOpt(CLI_APPLICATION_PROPERTIES_FILENAME_LONG_KEY)
                .desc(CLI_APPLICATION_PROPERTIES_FILENAME_DESCRIPTION)
                .required(false)
                .hasArg(true)
                .argName(CLI_APPLICATION_PROPERTIES_FILENAME_ARG_NAME)
                .valueSeparator(';')
                .type(String.class)
                .build();

        final Options options = new Options();
        options.addOption(helpOption);
        options.addOption(ftpOption);
        options.addOption(osOption);
        options.addOption(urOption);
        options.addOption(applicationPropertiesFileNameOption);

        final CommandLineParser commandLineParser = new DefaultParser();
        final CommandLine       commandLine;
        try {
            commandLine = commandLineParser.parse(options, args);
            if (commandLineContainsHelpOptions(commandLine)) {
                final HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp(CLI_PROGRAM_NAME, options );
            }
        } catch (Throwable e) {
            System.out.println("Exception received when trying to parse the command line");
            e.printStackTrace();
            throw e;
        }

        return commandLine;
    }

    private static void logSelectedProperties(final IOkosynkConfiguration okosynkConfiguration, final Logger logger) {
        logAllSystemProperties(okosynkConfiguration, logger);
        logAllEnvironmentVariables(okosynkConfiguration, logger);
    }

    private static void logAllSystemProperties(final IOkosynkConfiguration okosynkConfiguration, final Logger logger) {
        final StringBuffer msgStringBuffer =
            new StringBuffer()
                .append(System.lineSeparator())
                .append("================================================================================")
                .append(System.lineSeparator())
                .append("System.Properties:")
                .append(System.lineSeparator())
                .append("==================")
                .append(System.lineSeparator());

        System
            .getProperties()
            .forEach(
                (key, value) ->
                    msgStringBuffer
                        .append(key)
                        .append(": ")
                        .append(value)
                        .append(System.lineSeparator())
            );

        msgStringBuffer
            .append(System.lineSeparator())
            .append("=====================")
            .append(System.lineSeparator())
            .append("END System.Properties")
            .append(System.lineSeparator())
            .append("================================================================================")
            .append(System.lineSeparator())
        ;

        logger.debug(msgStringBuffer.toString());
    }

    private static void logAllEnvironmentVariables(final IOkosynkConfiguration okosynkConfiguration, final Logger logger) {
        // TODO: Do it!
    }

    private void startFtpServer() {

        logger.info("A test FTP server will be started");

        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        final IStartableAndStoppable ftpServerTestStarter =
            new FtpServerTestStarter(okosynkConfiguration);
        this.setFtpServerTestStarter(ftpServerTestStarter);
        getFtpServerTestStarter().start();
    }

    private void stopFtpServer() {

        final IStartableAndStoppable ftpServerTestStarter = this.getFtpServerTestStarter();
        if (ftpServerTestStarter != null) {
            ftpServerTestStarter.stop();
        }
    }

    private BatchStatus runBatches(final boolean shouldOnlyRunOs, final boolean shouldOnlyRunUr) {
        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        logSelectedProperties(okosynkConfiguration, logger);

        // TODO: Error situations.
        // TODO: Remove file.
        // TODO: Which file first?
        // TODO: Counter in file name?
        // TODO: Anyhow, do this stuff belong here?
        final BatchStatus osBatchStatus;
        if (shouldOnlyRunUr) {
            logger.info("Only running ur, not os");
            osBatchStatus = null;
        } else {
            osBatchStatus = runOs(okosynkConfiguration);
        }

        final BatchStatus urBatchStatus;
        if (shouldOnlyRunOs) {
            logger.info("Only running os, not ur");
            urBatchStatus = null;
        } else {
            urBatchStatus = runUr(okosynkConfiguration);
        }

        return (
                (
                    BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL.equals(osBatchStatus)
                    ||
                    osBatchStatus == null
                )
                &&
                (
                    BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL.equals(urBatchStatus)
                    ||
                    urBatchStatus == null
                )
            )
            ?
            BatchStatus.FULLFORT_UTEN_UVENTEDE_FEIL
            :
            BatchStatus.FEIL
            ;
    }

    private BatchStatus runOs(final IOkosynkConfiguration okosynkConfiguration) {

        return run(new OsBatchService(okosynkConfiguration));
    }

    private BatchStatus runUr(final IOkosynkConfiguration okosynkConfiguration) {

        return run(new UrBatchService(okosynkConfiguration));
    }

    private BatchStatus run(final AbstractBatchService batchService) {

        final String batchName = batchService.batchType.getName();

        logger.info("About to run batch {}...", batchName);

        final BatchStatus batchStatus = batchService.startBatchSynchronously();

        logger.info("batch {} finished with BatchStatus: {}", batchName, batchStatus);

        return batchStatus;
    }

    private IOkosynkConfiguration getOkosynkConfiguration(final String applicationPropertiesFileName) {

        final IOkosynkConfiguration okosynkConfiguration =
            OkosynkConfiguration.getInstance(applicationPropertiesFileName);

        return okosynkConfiguration;
    }

    private void setUpCertificates() {
        logger.info("About to set up certificates...");
        final Map<String, String> env = System.getenv();
        setupKeyStore(env);
        if (env.containsKey(Constants.NAV_TRUSTSTORE_PATH_KEY)) {
            System.setProperty(Constants.NAV_TRUSTSTORE_PATH_EXT_KEY    , env.get(Constants.NAV_TRUSTSTORE_PATH_KEY    ));
            System.setProperty(Constants.NAV_TRUSTSTORE_PASSWORD_EXT_KEY, env.get(Constants.NAV_TRUSTSTORE_PASSWORD_KEY));
        } else {
            setUpTestCertificates();
        }
        logger.info("Certificates successfully set up");
    }

    private void setupKeyStore(Map<String, String> env) {

        logger.info("About to set up key store...");
        final String keystore         = env.getOrDefault(Constants.SRVOKOSYNK_KEYSTORE_KEY, Constants.SRVOKOSYNK_KEYSTORE_DEFAULT_VALUE);
        final String keystorePassword = env.getOrDefault(Constants.SRVOKOSYNK_PASSWORD_KEY, Constants.SRVOKOSYNK_PASSWORD_DEFAULT_VALUE);
        System.setProperty(Constants.SRVOKOSYNK_KEYSTORE_EXT_KEY, keystore);
        System.setProperty(Constants.SRVOKOSYNK_PASSWORD_EXT_KEY, keystorePassword);
        logger.info("key store successfully set up");
    }

    private void setUpTestCertificates() {

        logger.info("About to set up test certificates...");
        try {
            TestCertificates_Copy.setupKeyAndTrustStore();
        } catch (Throwable e) {
            final String msg = "Exceptiond received when setting up test certificates.";
            logger.info(msg);
            throw new RuntimeException(msg, e);
        }
        logger.info("Test certificates successfully set up.");
    }
}
