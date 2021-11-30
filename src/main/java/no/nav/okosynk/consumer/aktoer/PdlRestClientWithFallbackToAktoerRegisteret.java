package no.nav.okosynk.consumer.aktoer;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.domain.util.AktoerUt;
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
    private final IOkosynkConfiguration okosynkConfiguration;

    public PdlRestClientWithFallbackToAktoerRegisteret(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {
        this.pdlRestClient = new PdlRestClient(okosynkConfiguration, batchType);
        this.aktoerRestClient = new AktoerRestClient(okosynkConfiguration, batchType);
        this.okosynkConfiguration = okosynkConfiguration;

        log.info("PdlRestClientWithFallbackToAktoerRegisteret created. batchType: {}", batchType);
        secureLog.info("Just making sure something is logged wih secureLog, never mind!");
    }

    @Override
    public AktoerRespons hentGjeldendeAktoerId(final String folkeregisterIdent) {

        AktoerRespons aktoerResponsFromPdl;
        try {
            aktoerResponsFromPdl = this.pdlRestClient.hentGjeldendeAktoerId(folkeregisterIdent);
        } catch (Throwable e) {
            final String msg = "Failing when trying to access PDL. The result from TPS will be preferred.";
            log.error(msg, e);
            aktoerResponsFromPdl = AktoerRespons.feil(msg);
        }

        final AktoerRespons aktoerResponsFromTps = this.aktoerRestClient.hentGjeldendeAktoerId(folkeregisterIdent);
        {
            final String aktoerIdFromPdl = aktoerResponsFromPdl.isOk() ? aktoerResponsFromPdl.getAktoerId() : "Finnes ikke";
            final String aktoerIdFromTps = aktoerResponsFromTps.isOk() ? aktoerResponsFromTps.getAktoerId() : "Finnes ikke";
            if (Objects.equals(aktoerIdFromPdl, aktoerIdFromTps)) {
                pdlTpsStatistics.incEq(folkeregisterIdent);
            } else {
                pdlTpsStatistics.incDiff(folkeregisterIdent, aktoerIdFromPdl, aktoerIdFromTps);
                final String msgBase = "Discrepancy between the aktoerIds returned from TPS and PDL";
                log.warn(msgBase);
                secureLog.warn("{}, aktoerIdFromPdl: {}, aktoerIdFromTps: {}", msgBase, aktoerIdFromPdl, aktoerResponsFromTps.getAktoerId());
            }
        }

        final AktoerRespons aktoerResponsChosen;
        String msg = "shouldPreferPdlToAktoerregisteret: " + this.okosynkConfiguration.shouldPreferPdlToAktoerregisteret();
        if (this.okosynkConfiguration.shouldPreferPdlToAktoerregisteret()) {
            msg += ", aktoerResponsFromPdl.isOk()" + aktoerResponsFromPdl.isOk();
            if (aktoerResponsFromPdl.isOk()) {
                msg = "Returning aktoerResponsFromPdl, because " + msg;
                aktoerResponsChosen = aktoerResponsFromPdl;
            } else {
                msg = "Returning aktoerResponsFromTps, because " + msg;
                aktoerResponsChosen = aktoerResponsFromTps;
            }
        } else {
            msg = "Returning aktoerResponsFromTps, because " + msg;
            aktoerResponsChosen = aktoerResponsFromTps;
        }

        return aktoerResponsChosen;
    }

    @Override
    protected void finalize() {
        report();
    }

    private void report() {
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

        public void incDiff(final String folkeregisterIdent, final String aktoerIdFromPdl, final String aktoerIdFromTps) {
            stats.get(FolkeregisterIdentType.of(folkeregisterIdent)).incDiff(folkeregisterIdent, aktoerIdFromPdl, aktoerIdFromTps);
            log.info("pdlTpsStatistics: " + this);
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
                            .map(folkeregisterIdentType -> folkeregisterIdentType.name() + ": " + stats.get(folkeregisterIdentType).toString())
                            .collect(Collectors.joining(", "));
        }
    }

    public static class DiffEq {
        private int noDiff = 0;
        private int noEq = 0;
        private String folkeregisterIdentsWithDifferences = "";

        public void incDiff(final String folkeregisterIdent, final String aktoerIdFromPdl, final String aktoerIdFromTps) {
            folkeregisterIdentsWithDifferences += (folkeregisterIdentsWithDifferences.equals("") ? "" : ", ") + "[" + folkeregisterIdent + " - " + aktoerIdFromPdl + " - " + aktoerIdFromTps + "]";
            noDiff++;
        }

        public void incEq() {
            noEq++;
        }

        @Override
        public String toString() {
            return "eq: " + noEq + ", diff: " + noDiff + ", folkeregisterIdentsWithDifferences: " + folkeregisterIdentsWithDifferences;
        }
    }
}