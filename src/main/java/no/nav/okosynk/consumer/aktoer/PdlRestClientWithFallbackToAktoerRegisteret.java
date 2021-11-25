package no.nav.okosynk.consumer.aktoer;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class PdlRestClientWithFallbackToAktoerRegisteret implements IAktoerClient {

    private static final Logger log = LoggerFactory.getLogger(PdlRestClientWithFallbackToAktoerRegisteret.class);
    private static final Logger secureLog = LoggerFactory.getLogger("secureLog");

    private final IAktoerClient pdlRestClient;
    private final IAktoerClient aktoerRestClient;

    public PdlRestClientWithFallbackToAktoerRegisteret(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {
        this.pdlRestClient = new PdlRestClient(okosynkConfiguration, batchType);
        this.aktoerRestClient = new AktoerRestClient(okosynkConfiguration, batchType);

        log.info("PdlRestClientWithFallbackToAktoerRegisteret created. batchType: {}", batchType);
        secureLog.info("Just making sure something is logged wih secureLog, never mind!");
    }

    @Override
    public AktoerRespons hentGjeldendeAktoerId(final String folkeregisterIdent) {

        AktoerRespons aktoerResponsFromPdl = null;
        try {
            aktoerResponsFromPdl = this.pdlRestClient.hentGjeldendeAktoerId(folkeregisterIdent);
        } catch (Throwable e) {
            log.warn("Failing when trying to access PDL, falling back on aktoerregisteret", e);
        }

        final AktoerRespons aktoerResponsFromTps = this.aktoerRestClient.hentGjeldendeAktoerId(folkeregisterIdent);
        if (aktoerResponsFromPdl != null) {
            final String aktoerIdFromPdl = aktoerResponsFromPdl.getAktoerId();
            final String aktoerIdFromTps = aktoerResponsFromTps.getAktoerId();
            if (!Objects.equals(aktoerIdFromPdl, aktoerIdFromTps)) {
                final String msgBase = "Discrepancy between the aktoerIds returned from TPS and PDL";
                log.warn(msgBase);
                secureLog.warn("{}, aktoerIdFromPdl: {}, aktoerIdFromTps: {}", msgBase, aktoerIdFromPdl, aktoerIdFromTps);
            }
            log.info("About to return aktoerResponsFromPdl");
            return aktoerResponsFromPdl;
        } else {
            log.info("About to return aktoerResponsFromTps");
            return aktoerResponsFromTps;
        }
    }
}