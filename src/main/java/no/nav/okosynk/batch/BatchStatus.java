package no.nav.okosynk.batch;

import no.nav.okosynk.cli.ExitStatus;

public enum BatchStatus {

    STARTET(-1, ExitStatus.OK, false),
    FULLFORT_UTEN_UVENTEDE_FEIL(0, ExitStatus.OK, false),
    FEIL(8, ExitStatus.OK, true),
    STOPPET(10, ExitStatus.OK, false),
    READY(100, ExitStatus.OK, false),
    FEIL_NUMBER_OF_RETRIES_EXCEEDED(919, ExitStatus.ERROR, false)
    ;

    private final int statusCode;
    private final boolean failedButRerunningMaySucceed;

    // The exit status with which the
    // main method returns back to
    // the calling environment
    // (e.g. the operating system)
    // Ref.:
    // https://kubernetes.io/docs/concepts/workloads/controllers/jobs-run-to-completion/#handling-pod-and-container-failures
    // https://kubernetes.io/docs/concepts/workloads/pods/pod-lifecycle/#example-states
    final ExitStatus exitStatus;

    BatchStatus(
        final int statusCode,
        final ExitStatus exitCode,
        final boolean failedButRerunningMaySucceed) {
        this.statusCode = statusCode;
        this.exitStatus = exitCode;
        this.failedButRerunningMaySucceed = failedButRerunningMaySucceed;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public ExitStatus getExitStatus() {
        return this.exitStatus;
    }

    public boolean failedButRerunningMaySucceed() {
        return this.failedButRerunningMaySucceed;
    }
}