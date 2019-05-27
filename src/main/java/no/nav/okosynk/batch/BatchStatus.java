package no.nav.okosynk.batch;

public enum BatchStatus {

    STARTET(-1),
    FULLFORT_UTEN_UVENTEDE_FEIL(0),
    FEIL(8),
    STOPPET(10),
    READY(100),
    FULLFORT_MED_UVENTEDE_FEIL(371)
    ;

    private final int statusKode;

    BatchStatus(int statusKode) {
        this.statusKode = statusKode;
    }

    public int getStatusKode() {
        return statusKode;
    }
}
