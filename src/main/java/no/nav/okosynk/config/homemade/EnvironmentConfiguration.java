package no.nav.okosynk.config.homemade;

public class EnvironmentConfiguration extends Configuration {
    public EnvironmentConfiguration() {
        super(System.getenv());
        PRIORITY = 2;
    }
}
