package no.nav.okosynk;

import io.vavr.Function1;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
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

    private final IOkosynkConfiguration okosynkConfiguration;

    public CliMain(final String applicationPropertiesFileName) {

        final IOkosynkConfiguration okosynkConfiguration =
                createOkosynkConfiguration(applicationPropertiesFileName);
        this.okosynkConfiguration = okosynkConfiguration;
        setUpCertificates(okosynkConfiguration);
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public static void main(final String[] args) throws Exception {
        CliMain.runMain(args, CliMain::new);
    }

    protected static void runMain(
            final String[] args,
            final Function1<String, ? extends CliMain> mainClassCreator) throws ParseException {

        final MainContext mainContext = preMain(args);
        try {
            if (mainContext.shouldRun) {
                final CliMain cliMain =
                        mainClassCreator.apply(mainContext.applicationPropertiesFileName);
                cliMain.runAllBatches();
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

    private static CommandLine treatCommandLineArgs(final String[] args) throws ParseException {

        final Option helpOption =
                Option
                        .builder(CLI_HELP_KEY)
                        .longOpt(CLI_HELP_LONG_KEY)
                        .desc(CLI_HELP_DESCRIPTION)
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
        options.addOption(applicationPropertiesFileNameOption);

        final CommandLineParser commandLineParser = new DefaultParser();
        final CommandLine commandLine;
        try {
            commandLine = commandLineParser.parse(options, args);
            if (commandLineContainsHelpOptions(commandLine)) {
                final HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp(CLI_PROGRAM_NAME, options);
            }
        } catch (Exception e) {
            logger.error("Exception received when trying to parse the command line");
            e.printStackTrace();
            throw e;
        }

        return commandLine;
    }

    protected void preRunAllBatches() {
        final String revision = createOkosynkConfiguration().getString("revision");
        logger.info("okosynk revision (as taken from pom.xml): {}", revision == null ? "Not available" : revision);
    }

    protected void postRunAllBatches() {
    }

    protected IOkosynkConfiguration createOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    /**
     * The outcome of the batch jobs is neglected at this level.
     * It has already been taken care of at a deeper lever.
     */
    private void runAllBatches() {

        preRunAllBatches();

        try {
            final IOkosynkConfiguration okosynkConfigurationLocal = createOkosynkConfiguration();
            final Collection<AbstractService<? extends AbstractMelding>> services = new ArrayList<>();

            if (okosynkConfigurationLocal.shouldRun(Constants.BATCH_TYPE.OS)) {
                logger.info("Running OS");
                services.add(new OsService(okosynkConfigurationLocal));
            } else {
                logger.info("Not running OS");
            }
            if (okosynkConfigurationLocal.shouldRun(Constants.BATCH_TYPE.UR)) {
                logger.info("Running UR");
                services.add(new UrService(okosynkConfigurationLocal));
            } else {
                logger.info("Not running UR");
            }
            final int sleepTimeBetweenRunsInMs = CliMain.getRetryWaitTimeInMilliseconds(okosynkConfigurationLocal);
            final int maxNumberOfRuns = CliMain.getMaxNumberOfReadTries(okosynkConfigurationLocal);
            int actualNumberOfRuns = 0;
            do {
                logger.info("About to run the batch(es) for the {}. time ...", actualNumberOfRuns + 1);
                services
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
                                services.stream().anyMatch(AbstractService::shouldRun)
                ) {
                    retrySleep(actualNumberOfRuns, maxNumberOfRuns, sleepTimeBetweenRunsInMs);
                } else {
                    break;
                }
            } while (true);

            services
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

    private IOkosynkConfiguration createOkosynkConfiguration(
            final String applicationPropertiesFileName) {

        OkosynkConfiguration.createAndReplaceSingletonInstance(applicationPropertiesFileName);
        final IOkosynkConfiguration okosynkConfiguration = OkosynkConfiguration.getSingletonInstance();

        return okosynkConfiguration;
    }

    /**
     * Ends up with having set the following sys props:
     * <p>
     * javax.net.ssl.trustStore: /var/run/secrets/naisd.io/nav_truststore_path
     * javax.net.ssl.trustStorePassword: <whatever>
     */
    private void setUpCertificates(final IOkosynkConfiguration okosynkConfiguration) {

        logger.info("About to set up certificates...");
        final Map<String, String> env = System.getenv();

        if (env.containsKey(Constants.NAV_TRUSTSTORE_PATH_KEY)) { // If running under NAIS/K8S
            final String navTrustStorePath = okosynkConfiguration.getNavTrustStorePath();
            final String navTrustStorePassword = okosynkConfiguration.getNavTrustStorePassword();
            System.setProperty(Constants.NAV_TRUSTSTORE_PATH_EXT_KEY, navTrustStorePath);
            System.setProperty(Constants.NAV_TRUSTSTORE_PASSWORD_EXT_KEY, navTrustStorePassword);
            logger.info("Certificates successfully set up");
        } else {
            // This is OK if running locally in a test environment
            logger.error("The environment variable {} is not set.", Constants.NAV_TRUSTSTORE_PATH_KEY);
        }
    }
}