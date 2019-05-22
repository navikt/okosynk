package no.nav.okosynk.consumer;

import no.nav.okosynk.config.IOkosynkConfiguration;

public class AktoerRestClient {
    private final IOkosynkConfiguration okosynkConfiguration;
    private final OidcStsClient oidcStsClient;

    public AktoerRestClient(IOkosynkConfiguration okosynkConfiguration) {
        this.okosynkConfiguration = okosynkConfiguration;
        this.oidcStsClient = new OidcStsClient(okosynkConfiguration);
    }

    public String hentAktoerIdForFnr(String fnr) {
        return fnr;
    }
}
