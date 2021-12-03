package no.nav.okosynk.batch;

import java.util.ArrayList;
import java.util.List;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.io.IMeldingLinjeFileReader;

public class MeldingLinjeFileReaderMock
    implements IMeldingLinjeFileReader {

    @Override
    public Status getStatus() {
        return status;
    }

    private final Status status = Status.UNSET;

    private String mockLinje;

    public MeldingLinjeFileReaderMock(final String mockLinje) {
        this.mockLinje = mockLinje;
    }

    @Override
    public List<String> read() throws ConfigureOrInitializeOkosynkIoException {

        List<String> fil = new ArrayList<>();
        fil.add(mockLinje);

        return fil;
    }

    @Override
    public boolean removeInputData() {
        // Intentionally doing nothing
        return true;
    }

    @Override
    public Constants.BATCH_TYPE getBatchType() {
        return Constants.BATCH_TYPE.UR;
    }
}
