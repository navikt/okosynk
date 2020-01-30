package no.nav.okosynk.batch;

import java.util.ArrayList;
import java.util.List;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.OkosynkIoException;

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
    public List<String> read() throws OkosynkIoException {

        List<String> fil = new ArrayList<>();
        fil.add(mockLinje);

        return fil;
    }

    @Override
    public boolean renameInputFile() {
        // Intentionally doing nothing
        return true;
    }
}
