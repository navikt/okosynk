package no.nav.okosynk.consumer.aktoer;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdlRestClientWithFallbackToAktoerRegisteret implements IAktoerClient {

    private static final Logger log = LoggerFactory.getLogger(PdlRestClientWithFallbackToAktoerRegisteret.class);

    private final IAktoerClient pdlRestClient;
    private final IAktoerClient aktoerRestClient;

    public PdlRestClientWithFallbackToAktoerRegisteret(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {
        this.pdlRestClient = new PdlRestClient(okosynkConfiguration, batchType);
        this.aktoerRestClient = new AktoerRestClient(okosynkConfiguration, batchType);
    }

    @Override
    public AktoerRespons hentGjeldendeAktoerId(final String folkeregisterIdent) {
        try {
            return this.pdlRestClient.hentGjeldendeAktoerId(folkeregisterIdent);
        } catch (Throwable e) {
            log.warn("Failing when trying to access PDL, falling back on aktoerregisteret", e);
            return this.aktoerRestClient.hentGjeldendeAktoerId(folkeregisterIdent);
        }
    }
}
