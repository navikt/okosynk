package no.nav.okosynk;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.exceptions.BatchStatus;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.metrics.AlertMetrics;
import org.apache.commons.cli.*;
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
    private final OkosynkConfiguration okosynkConfiguration;

    public CliMain(final String applicationPropertiesFileName) {
        okosynkConfiguration = createOkosynkConfiguration(applicationPropertiesFileName);
        setUpCertificates(okosynkConfiguration);
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public static void main(final String[] args)  {
        CliMain.runMain(args, CliMain::new);
    }

    protected static void runMain(
            final String[] args,
            final Function<String, ? extends CliMain> mainClassCreator)  {

        logger.info("===== ENTERING OKOSYNK =====");
        final CommandLine commandLine = CliMain.treatCommandLineArgs(args);
        final boolean shouldRun = !commandLine.hasOption(CLI_HELP_KEY);

        final String applicationPropertiesFileName =
                commandLine.hasOption(CLI_APPLICATION_PROPERTIES_FILENAME_KEY)
                        ? commandLine.getOptionValue(CLI_APPLICATION_PROPERTIES_FILENAME_KEY)
                        : CLI_APPLICATION_PROPERTIES_FILENAME_DEFAULT_VALUE;

        logger.info("The following properties file will be used: {}", applicationPropertiesFileName);

        try (final MainContext mainContext = new MainContext(shouldRun, commandLine, applicationPropertiesFileName)) {
            if (mainContext.shouldRun) {
                final CliMain cliMain = mainClassCreator.apply(mainContext.applicationPropertiesFileName);
                cliMain.runAllBatches();
            }
        } finally {
            logger.info("===== OKOSYNK ABOUT TO EXIT WITH EXIT STATUS 0 =====");
        }
        System.exit(0);
    }

    private static CommandLine treatCommandLineArgs(final String[] args) {

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
            if (commandLine.hasOption(CLI_HELP_KEY)) {
                final HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp(CLI_PROGRAM_NAME, options);
            }
        } catch (Exception e) {
            logger.error("Exception received when trying to parse the command line");
            throw new IllegalArgumentException(e);
        }

        return commandLine;
    }

    protected OkosynkConfiguration createOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    /**
     * The outcome of the batch jobs is neglected at this level.
     * It has already been taken care of at a deeper lever.
     */
    private void runAllBatches() {

        final String revision = createOkosynkConfiguration().getString("revision");
        logger.info("okosynk revision (as taken from pom.xml): {}", revision == null ? "Not available" : revision);

        final OkosynkConfiguration okosynkConfigurationLocal = createOkosynkConfiguration();
        final Collection<AbstractService<? extends AbstractMelding>> services = new ArrayList<>();

        if (okosynkConfigurationLocal.getRequiredString(Constants.SHOULD_RUN_OS_OR_UR_KEY).equals(Constants.BATCH_TYPE.OS.name())) {
            logger.info("Running OS");
            services.add(new OsService(okosynkConfigurationLocal));
        }
        if (okosynkConfigurationLocal.getRequiredString(Constants.SHOULD_RUN_OS_OR_UR_KEY).equals(Constants.BATCH_TYPE.UR.name())) {
            logger.info("Running UR");
            services.add(new UrService(okosynkConfigurationLocal));
        }

        final int sleepTimeBetweenRunsInMs = okosynkConfigurationLocal.getRequiredInt(Constants.BATCH_RETRY_WAIT_TIME_IN_MS_KEY);
        final int maxNumberOfRuns = okosynkConfigurationLocal.getRequiredInt(Constants.BATCH_MAX_NUMBER_OF_TRIES_KEY);
        int actualNumberOfRuns = 0;
        do {
            logger.info("About to run the batch(es) for the {}. time ...", actualNumberOfRuns + 1);
            services.stream().filter(AbstractService::shouldRun).forEach(this::runOneBatch);
            if (++actualNumberOfRuns < maxNumberOfRuns && services.stream().anyMatch(AbstractService::shouldRun)) {
                retrySleep(actualNumberOfRuns, maxNumberOfRuns, sleepTimeBetweenRunsInMs);
            } else {
                break;
            }
        } while (true);

        services.forEach(service -> AlertMetrics.getSingletonInstance(service.getOkosynkConfiguration())
                .generateCheckTheLogAlertBasedOnBatchStatus(service.getLastBatchStatus())
        );
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
            Thread.currentThread().interrupt();
        }
        logger.info("I will try re-running the batch(es)...");
    }

    private void runOneBatch(final AbstractService<? extends AbstractMelding> service) {
        final String batchName = service.getBatchType().getName();
        logger.info("About to run batch {}...", batchName);
        final BatchStatus batchStatus = service.run();
        logger.info("batch {} finished with BatchStatus: {}", batchName, batchStatus);
    }

    private OkosynkConfiguration createOkosynkConfiguration(
            final String applicationPropertiesFileName) {

        OkosynkConfiguration.createAndReplaceSingletonInstance(applicationPropertiesFileName);

        return OkosynkConfiguration.getSingletonInstance();
    }

    /**
     * Ends up with having set the following sys props:
     * <p>
     * javax.net.ssl.trustStore: /var/run/secrets/naisd.io/nav_truststore_path
     * javax.net.ssl.trustStorePassword: <whatever>
     */
    private void setUpCertificates(final OkosynkConfiguration okosynkConfiguration) {

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
