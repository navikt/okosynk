package no.nav.okosynk.cli;

import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.batch.BatchStatus;
import no.nav.okosynk.batch.os.OsService;
import no.nav.okosynk.batch.ur.UrService;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.consumer.security.AzureAdClient;
import no.nav.okosynk.domain.AbstractMelding;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class CliMain {

    private static final Logger logger = LoggerFactory.getLogger(CliMain.class);

    private static final String CLI_PROGRAM_NAME = "java -jar okosynk.jar";

    private static final String CLI_APPLICATION_PROPERTIES_FILENAME_KEY = "p";
    private static final String CLI_APPLICATION_PROPERTIES_FILENAME_LONG_KEY = "propFile";
    private static final String CLI_APPLICATION_PROPERTIES_FILENAME_DESCRIPTION = "The name of a file from which the application should read properties";
    private static final String CLI_APPLICATION_PROPERTIES_FILENAME_ARG_NAME = "applicationPropertiesFileName";
    private static final String CLI_APPLICATION_PROPERTIES_FILENAME_DEFAULT_VALUE = "okosynk.properties";

    private static final String CLI_HELP_KEY = "h";
    private static final String CLI_HELP_LONG_KEY = "help";
    private static final String CLI_HELP_DESCRIPTION = "Print this message";

    private static final String CLI_OS_ONLY_KEY = "o";
    private static final String CLI_OS_ONLY_LONG_KEY = "onlyOs";
    private static final String CLI_OS_ONLY_DESCRIPTION = "Only run os batch";

    private static final String CLI_UR_ONLY_KEY = "u";
    private static final String CLI_UR_ONLY_LONG_KEY = "onlyUr";
    private static final String CLI_UR_ONLY_DESCRIPTION = "Only run ur batch";

    private final IOkosynkConfiguration okosynkConfiguration;

    public CliMain(final String applicationPropertiesFileName) {

        // TODO: This instance of IOkosynkConfiguration should maybe be injected around.
        final IOkosynkConfiguration okosynkConfiguration =
                getOkosynkConfiguration(applicationPropertiesFileName);
        this.okosynkConfiguration = okosynkConfiguration;
        setUpCertificates();
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public static void main(final String[] args) throws Exception {
        CliMain.runMain(args, CliMain::new);
    }

    protected static void runMain(
            final String[] args,
            final Function<String, ? extends CliMain> mainClassCreator) throws ParseException {

        final MainContext mainContext = preMain(args);
        try {
            if (mainContext.shouldRun) {
                final CliMain cliMain = mainClassCreator.apply(mainContext.applicationPropertiesFileName);
                cliMain.runAllBatches(
                        CliMain.shouldOnlyRunOs(mainContext.commandLine),
                        CliMain.shouldOnlyRunUr(mainContext.commandLine));
            }
        } finally {
            CliMain.postMain();
        }
        System.exit(0);
    }

    private static MainContext preMain(final String[] args) throws ParseException {

        logger.info("===== ENTERING OKOSYNK =====");
        final CommandLine commandLine = CliMain.treatCommandLineArgs(args);
        final boolean shouldRun = !CliMain.commandLineContainsHelpOptions(commandLine);
        final String applicationPropertiesFileName;
        if (CliMain.commandLineContainsPropertyFileName(commandLine)) {
            applicationPropertiesFileName = commandLine
                    .getOptionValue(CLI_APPLICATION_PROPERTIES_FILENAME_KEY);
        } else {
            applicationPropertiesFileName = CLI_APPLICATION_PROPERTIES_FILENAME_DEFAULT_VALUE;
        }
        logger.info("The following properties file will be used: {}", applicationPropertiesFileName);

        final MainContext mainContext = new MainContext(shouldRun, commandLine, applicationPropertiesFileName);

        return mainContext;
    }

    private static void postMain() {
        logger.info("===== OKOSYNK ABOUT TO EXIT WITH EXIT STATUS 0 =====");
    }

    private static int getRetryWaitTimeInMilliseconds(
            final IOkosynkConfiguration okosynkConfiguration
    ) {
        return okosynkConfiguration
                .getRequiredInt(Constants.BATCH_RETRY_WAIT_TIME_IN_MS_KEY);
    }

    private static int getMaxNumberOfReadTries(
            final IOkosynkConfiguration okosynkConfiguration
    ) {
        return okosynkConfiguration
                .getRequiredInt(Constants.BATCH_MAX_NUMBER_OF_TRIES_KEY);
    }

    private static boolean commandLineContainsHelpOptions(final CommandLine commandLine) {
        return commandLine.hasOption(CLI_HELP_KEY);
    }

    private static boolean commandLineContainsPropertyFileName(final CommandLine commandLine) {
        return commandLine.hasOption(CLI_APPLICATION_PROPERTIES_FILENAME_KEY);
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
        options.addOption(osOption);
        options.addOption(urOption);
        options.addOption(applicationPropertiesFileNameOption);

        final CommandLineParser commandLineParser = new DefaultParser();
        final CommandLine commandLine;
        try {
            commandLine = commandLineParser.parse(options, args);
            if (commandLineContainsHelpOptions(commandLine)) {
                final HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp(CLI_PROGRAM_NAME, options);
            }
        } catch (Throwable e) {
            System.out.println("Exception received when trying to parse the command line");
            e.printStackTrace();
            throw e;
        }

        return commandLine;
    }

    protected void preRunAllBatches() {
        final String revision = getOkosynkConfiguration().getString("revision");
        logger.info("okosynk revision (as taken from pom.xml): {}", revision == null ? "Not available" : revision);

        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
        final AzureAdClient azureAdClient = new AzureAdClient(okosynkConfiguration);
    }

    protected void postRunAllBatches() {
    }

    protected IOkosynkConfiguration getOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    /**
     * The outcome of the batch jobs is neglected at this level.
     * It has already been taken care of at a deeper lever.
     *
     * @param shouldOnlyRunOs Self explanatory
     * @param shouldOnlyRunUr Self explanatory
     */
    private void runAllBatches(
            final boolean shouldOnlyRunOs,
            final boolean shouldOnlyRunUr) {

        preRunAllBatches();

        try {
            final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
            final Collection<AbstractService<? extends AbstractMelding>> services = new ArrayList<>();
            if (!shouldOnlyRunUr) {
                services.add(new OsService(okosynkConfiguration));
            }
            if (!shouldOnlyRunOs) {
                services.add(new UrService(okosynkConfiguration));
            }
            final int sleepTimeBetweenRunsInMs = CliMain.getRetryWaitTimeInMilliseconds(okosynkConfiguration);
            final int maxNumberOfRuns = CliMain.getMaxNumberOfReadTries(okosynkConfiguration);
            int actualNumberOfRuns = 0;
            do {
                logger.info("About to run the batch(es) for the {}. time ...", actualNumberOfRuns + 1);
                services
                        .stream()
                        .forEach(
                                service -> {
                                    if (service.shouldRun()) {
                                        runOneBatch(service);
                                    }
                                }
                        );
                actualNumberOfRuns++;
                if (
                        actualNumberOfRuns < maxNumberOfRuns
                                &&
                                // A new run may potentially succeed:
                                services
                                        .stream()
                                        .map(AbstractService::shouldRun)
                                        .reduce(false, ((b1, b2) -> b1 || b2))
                ) {
                    retrySleep(actualNumberOfRuns, maxNumberOfRuns, sleepTimeBetweenRunsInMs);
                } else {
                    break;
                }
            } while (true);

            services
                    .stream()
                    .forEach(
                            service ->
                                    service
                                            .getAlertMetrics()
                                            .generateCheckTheLogAlertBasedOnBatchStatus(service.getLastBatchStatus())
                    );
        } finally {
            postRunAllBatches();
        }
    }

    private void retrySleep(final int actualNumberOfRuns, final int maxNumberOfRuns, final int sleepTimeBetweenRunsInMs) {
        try {
            final String msg =
                    System.lineSeparator()
                            + "I have tried running the batch {} times, "
                            + "and I will not give up until I have tried {} times."
                            + System.lineSeparator()
                            + "I will try again in {} ms. Until then, I will take a nap."
                            + System.lineSeparator();
            logger.warn(msg, actualNumberOfRuns, maxNumberOfRuns, sleepTimeBetweenRunsInMs);
            Thread.sleep(sleepTimeBetweenRunsInMs);
            logger.debug("Good morning, I just woke up again!");
        } catch (InterruptedException ex) {
            logger.warn(
                    "Ooooops, of unknown reasons, "
                            + "I was woken up by \"something\" before {} ms had passed.",
                    sleepTimeBetweenRunsInMs
            );
        }
        logger.info("I will try re-running the batch(es)...");
    }

    private void runOneBatch(final AbstractService<? extends AbstractMelding> service) {
        final String batchName = service.getBatchType().getName();
        logger.info("About to run batch {}...", batchName);
        final BatchStatus batchStatus = service.run();
        logger.info("batch {} finished with BatchStatus: {}", batchName, batchStatus);
    }

    private IOkosynkConfiguration getOkosynkConfiguration(
            final String applicationPropertiesFileName) {

        final IOkosynkConfiguration okosynkConfiguration =
                OkosynkConfiguration.getInstance(applicationPropertiesFileName);

        return okosynkConfiguration;
    }

    /**
     * Ends up with having set the following sys props:
     * no.nav.modig.security.appcert.keystore: /var/run/secrets/naisd.io/srvokosynk_keystore
     * no.nav.modig.security.appcert.password: ytX7G6r51d
     * <p>
     * javax.net.ssl.trustStore: /var/run/secrets/naisd.io/nav_truststore_path
     * javax.net.ssl.trustStorePassword: 467792be15c4a8807681fd2d5c9c1748
     */
    private void setUpCertificates() {

        logger.info("About to set up certificates...");

            final Map<String, String> env = System.getenv();
        setupKeyStore(env);
        if (env.containsKey(Constants.NAV_TRUSTSTORE_PATH_KEY)) {
            System.setProperty(Constants.NAV_TRUSTSTORE_PATH_EXT_KEY,
                    env.get(Constants.NAV_TRUSTSTORE_PATH_KEY));
            System.setProperty(Constants.NAV_TRUSTSTORE_PASSWORD_EXT_KEY,
                    env.get(Constants.NAV_TRUSTSTORE_PASSWORD_KEY));
        } else {
            final String msg = "The environment variable NAV_TRUSTSTORE_PATH is not set by NAISERATOR";
            logger.error(msg);
            //throw new RuntimeException(msg);
        }

        logger.info("Certificates successfully set up");
    }

    private void setupKeyStore(Map<String, String> env) {

        logger.info("About to set up key store...");

        // TODO: MODIG-OPPRYDDING: Remove if working without:
        //final String keystore = env.getOrDefault(Constants.SRVOKOSYNK_KEYSTORE_KEY,
        //        Constants.SRVOKOSYNK_KEYSTORE_DEFAULT_VALUE);
        //final String keystorePassword = env.getOrDefault(Constants.SRVOKOSYNK_PASSWORD_KEY,
        //        Constants.SRVOKOSYNK_PASSWORD_DEFAULT_VALUE);

        // TODO: MODIG-OPPRYDDING: Remove if working without:
        //System.setProperty(Constants.SRVOKOSYNK_KEYSTORE_EXT_KEY, keystore);

        // TODO: MODIG-OPPRYDDING: Remove if working without:
        //System.setProperty(Constants.SRVOKOSYNK_PASSWORD_EXT_KEY, keystorePassword);

        logger.info("key store successfully set up");
    }
}