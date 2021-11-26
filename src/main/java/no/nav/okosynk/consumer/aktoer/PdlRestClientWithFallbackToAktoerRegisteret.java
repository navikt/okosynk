package no.nav.okosynk.consumer.aktoer;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.domain.util.AktoerUt;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PdlRestClientWithFallbackToAktoerRegisteret implements IAktoerClient {

    private static final Logger log = LoggerFactory.getLogger(PdlRestClientWithFallbackToAktoerRegisteret.class);
    private static final Logger secureLog = LoggerFactory.getLogger("secureLog");

    private final IAktoerClient pdlRestClient;
    private final IAktoerClient aktoerRestClient;
    private final PdlTpsStatistics pdlTpsStatistics = new PdlTpsStatistics();

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
            if (!(e instanceof NotImplementedException)) {
                log.error("Crash against PDL: ", e);
            }
        }

        final AktoerRespons aktoerResponsFromTps = this.aktoerRestClient.hentGjeldendeAktoerId(folkeregisterIdent);
        if (aktoerResponsFromPdl != null) {
            final String aktoerIdFromPdl = aktoerResponsFromPdl.getAktoerId();
            final String aktoerIdFromTps = aktoerResponsFromTps.getAktoerId();
            if (Objects.equals(aktoerIdFromPdl, aktoerIdFromTps)) {
                pdlTpsStatistics.incEq(folkeregisterIdent);
            } else {
                pdlTpsStatistics.incDiff(folkeregisterIdent);
                final String msgBase = "Discrepancy between the aktoerIds returned from TPS and PDL";
                log.warn(msgBase);
                secureLog.warn("{}, aktoerIdFromPdl: {}, aktoerIdFromTps: {}", msgBase, aktoerIdFromPdl, aktoerIdFromTps);
            }
            log.info("About to return aktoerResponsFromPdl");
            return aktoerResponsFromPdl;
        } else {
            pdlTpsStatistics.incDiff(folkeregisterIdent);
            log.info("About to return aktoerResponsFromTps");
            return aktoerResponsFromTps;
        }
    }

    @Override
    public void finalize() {
        log.info("pdlTpsStatistics: " + pdlTpsStatistics);
    }

    public enum FolkeregisterIdentType {
        FNR, BNR, DNR;

        public static FolkeregisterIdentType of(final String folkeregisterIdent) {
            if (AktoerUt.isBnr(folkeregisterIdent)) return BNR;
            if (AktoerUt.isDnr(folkeregisterIdent)) return DNR;
            return FNR;
        }
    }

    public static class PdlTpsStatistics {

        private final Map<FolkeregisterIdentType, DiffEq> stats = new HashMap<>();

        PdlTpsStatistics() {
            stats.put(FolkeregisterIdentType.BNR, new DiffEq());
            stats.put(FolkeregisterIdentType.DNR, new DiffEq());
            stats.put(FolkeregisterIdentType.FNR, new DiffEq());
        }

        public void incDiff(final String folkeregisterIdent) {
            stats.get(FolkeregisterIdentType.of(folkeregisterIdent)).incDiff();
        }

        public void incEq(final String folkeregisterIdent) {
            stats.get(FolkeregisterIdentType.of(folkeregisterIdent)).incEq();
        }

        @Override
        public String toString() {
            return
                    stats
                            .keySet()
                            .stream()
                            .map(folkeregisterIdentType -> folkeregisterIdentType.name() + ": " + stats.toString())
                            .collect(Collectors.joining(", "));
        }
    }

    public static class DiffEq {
        int noDiff = 0;
        int noEq = 0;

        public void incDiff() {
            noDiff++;
        }

        public void incEq() {
            noEq++;
        }

        @Override
        public String toString() {
            return "eq: " + noEq + ", diff: " + noDiff;
        }
    }
}