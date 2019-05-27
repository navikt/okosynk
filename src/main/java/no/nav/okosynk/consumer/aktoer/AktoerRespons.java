package no.nav.okosynk.consumer.aktoer;

public class AktoerRespons {
    private final String feilmelding;
    private final String aktoerId;

    AktoerRespons(String aktoerId, String feilmelding) {
        this.aktoerId = aktoerId;
        this.feilmelding = feilmelding;
    }

    public static AktoerRespons feil(String feilmelding) {
        return new AktoerRespons(null, feilmelding);
    }

    public static AktoerRespons ok(String aktoerId) {
        return new AktoerRespons(aktoerId, null);
    }

    public String getFeilmelding() {
        return feilmelding;
    }

    public String getAktoerId() {
        return aktoerId;
    }
}
