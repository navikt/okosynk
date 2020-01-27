package no.nav.okosynk.batch;

public enum BatchStatus {

    STARTET(-1, false),
    READY(100, false),
    STOPPET(10, false),
    OK_ENDED_WITHOUT_UNEXPECTED_ERRORS(0, false),
    ERROR(8, true),
    ERROR_NUMBER_OF_RETRIES_EXCEEDED(919, true)
    ;

    private final int statusCode;
    private final boolean failedButRerunningMaySucceed;

    BatchStatus(
        final int statusCode,
        final boolean failedButRerunningMaySucceed) {
        this.statusCode = statusCode;
        this.failedButRerunningMaySucceed = failedButRerunningMaySucceed;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public boolean failedButRerunningMaySucceed() {
        return this.failedButRerunningMaySucceed;
    }
}