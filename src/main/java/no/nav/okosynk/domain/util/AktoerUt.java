package no.nav.okosynk.domain.util;

import static org.apache.commons.lang3.StringUtils.substring;

public class AktoerUt {

    public static boolean isDnr(final String toBeInvestigatedForBeingADnr) {
        return
                toBeInvestigatedForBeingADnr != null
                        &&
                        toBeInvestigatedForBeingADnr.length() == 11
                        &&
                        toBeInvestigatedForBeingADnr.charAt(0) > '3';
    }

    public static boolean isBnr(final String toBeInvestigatedForBeingABnr) {
        int month = Integer.valueOf(substring(toBeInvestigatedForBeingABnr, 2, 4));
        return (month >= 21 && month <= 32);
    }
}
