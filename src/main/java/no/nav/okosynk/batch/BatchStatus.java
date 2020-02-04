package no.nav.okosynk.batch;

public enum BatchStatus {

    READY(100, false, false),
    STARTED(-1, false, false),
    ENDED_WITH_OK(0, false, false),
    ENDED_WITH_WARNING_BATCH_INPUT_DATA_COULD_NOT_BE_DELETED_AFTER_OK_RUN(1023, false, true),
    ENDED_WITH_WARNING_NUMBER_OF_RETRIES_EXCEEDED_NOT_FOUND(933, true, false),
    ENDED_WITH_ERROR_GENERAL(8, true, true),
    ENDED_WITH_ERROR_NUMBER_OF_RETRIES_EXCEEDED_IO(919, true, true)
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