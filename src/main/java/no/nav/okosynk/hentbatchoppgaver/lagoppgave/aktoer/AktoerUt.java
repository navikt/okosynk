package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

import static org.apache.commons.lang3.StringUtils.substring;

public class AktoerUt {
    private AktoerUt() {
    }

    public static boolean isDnr(final String toBeInvestigatedForBeingADnr) {
        return
                toBeInvestigatedForBeingADnr != null
                        &&
                        toBeInvestigatedForBeingADnr.length() == 11
                        &&
                        toBeInvestigatedForBeingADnr.charAt(0) > '3';
    }

    public static boolean isBnr(final String toBeInvestigatedForBeingABnr) {
        int month = Integer.parseInt(substring(toBeInvestigatedForBeingABnr, 2, 4));
        return (month >= 21 && month <= 32);
    }
}
