package no.nav.okosynk.batch;

import no.nav.okosynk.cli.ExitStatus;

public enum BatchStatus {

    STARTET(-1, ExitStatus.OK),
    FULLFORT_UTEN_UVENTEDE_FEIL(0, ExitStatus.OK),
    FEIL(8, ExitStatus.OK),
    STOPPET(10, ExitStatus.OK),
    READY(100, ExitStatus.OK),
    FULLFORT_MED_UVENTEDE_FEIL(371, ExitStatus.OK),
    FEIL_NUMBER_OF_RETRIES_EXCEEDED(919, ExitStatus.ERROR)
    ;

    private final int statusCode;

    // The exit status with which the
    // main method returns back to
    // the calling environment
    // (e.g. the operating system)
    // Ref.:
    // https://kubernetes.io/docs/concepts/workloads/controllers/jobs-run-to-completion/#handling-pod-and-container-failures
    // https://kubernetes.io/docs/concepts/workloads/pods/pod-lifecycle/#example-states
    final ExitStatus exitStatus;

    BatchStatus(final int statusCode, final ExitStatus exitCode) {
        this.statusCode = statusCode;
        this.exitStatus = exitCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ExitStatus getExitStatus() {
        return exitStatus;
    }
}