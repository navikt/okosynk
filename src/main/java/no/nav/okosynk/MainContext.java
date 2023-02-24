package no.nav.okosynk;

import org.apache.commons.cli.CommandLine;

public class MainContext {
    public final boolean shouldRun;
    public final CommandLine commandLine;
    final String applicationPropertiesFileName;

    public MainContext(boolean shouldRun, CommandLine commandLine, String applicationPropertiesFileName) {
        this.shouldRun = shouldRun;
        this.commandLine = commandLine;
        this.applicationPropertiesFileName = applicationPropertiesFileName;
    }
}
