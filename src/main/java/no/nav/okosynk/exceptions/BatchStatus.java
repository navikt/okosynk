package no.nav.okosynk.exceptions;

public enum BatchStatus {

    READY(false, true),
    STARTED(false, true),
    ENDED_WITH_OK(false, false),
    ENDED_WITH_WARNING_BATCH_INPUT_DATA_COULD_NOT_BE_DELETED_AFTER_OK_RUN(false, true),
    ENDED_WITH_WARNING_INPUT_DATA_NOT_FOUND(true, false),
    ENDED_WITH_ERROR_GENERAL(true, true),
    ENDED_WITH_ERROR_INPUT_DATA(false, true),
    ENDED_WITH_ERROR_TOO_MANY_INPUT_DATA_LINES(false, true),
    ENDED_WITH_ERROR_CONFIGURATION(false, true)
    ;

    private final boolean failedButRerunningMaySucceed;
    private final boolean shouldAlert;

    BatchStatus(
        final boolean failedButRerunningMaySucceed,
        final boolean shouldAlert) {
        this.failedButRerunningMaySucceed = failedButRerunningMaySucceed;
        this.shouldAlert = shouldAlert;
    }

    public boolean failedButRerunningMaySucceed() {
        return this.failedButRerunningMaySucceed;
    }

    public boolean shouldAlert() {
        return this.shouldAlert;
    }
}