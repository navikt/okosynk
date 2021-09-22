package no.nav.okosynk.domain.util;

public class AktoerUt {
    public static boolean isDnr(final String toBeInvestigatedForBeingADnr) {
        return
                toBeInvestigatedForBeingADnr != null
                        &&
                        toBeInvestigatedForBeingADnr.length() == 11
                        &&
                        toBeInvestigatedForBeingADnr.charAt(0) > '3';
    }
}
