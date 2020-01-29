package no.nav.okosynk.batch;

public enum BatchStatus {

    READY(100, false, false),
    STARTET(-1, false, false),
    OK_ENDED_WITHOUT_UNEXPECTED_ERRORS(0, false, false),
    ERROR(8, true, true),
    ERROR_NUMBER_OF_RETRIES_EXCEEDED(919, true, true)
    ;

    private final int statusCode;
    private final boolean failedButRerunningMaySucceed;
    private final boolean shouldAlert;

    BatchStatus(
        final int statusCode,
        final boolean failedButRerunningMaySucceed,
        final boolean shouldAlert) {
        this.statusCode = statusCode;
        this.failedButRerunningMaySucceed = failedButRerunningMaySucceed;
        this.shouldAlert = shouldAlert;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public boolean failedButRerunningMaySucceed() {
        return this.failedButRerunningMaySucceed;
    }

    public boolean shouldAlert() {
        return this.shouldAlert;
    }
}