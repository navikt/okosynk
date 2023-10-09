package no.nav.okosynk.model;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerUt;

import static java.util.Arrays.asList;

public enum GjelderIdType {
    ORGANISASJON,
    BNR,
    AKTORID,
    SAMHANDLER;
    public static final char TSS_PREFIX_1 = '8';
    private static final char TSS_PREFIX_2 = '9';
    private static final int ORGANISASJONSNUMMER_LENGDE = 9;

    public static GjelderIdType fra(String gjelderId){
        if (gjelderId.length() == ORGANISASJONSNUMMER_LENGDE) return ORGANISASJON;
        if (asList(TSS_PREFIX_1, TSS_PREFIX_2).contains(gjelderId.trim().charAt(0))) return SAMHANDLER;
        if (AktoerUt.isBnr(gjelderId)) return BNR;
        else return AKTORID;
    }
}
