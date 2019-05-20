package no.nav.okosynk.batch;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.LinjeUnreadableException;

public class MeldingLinjeFileReaderMock
    implements IMeldingLinjeFileReader {

    @Getter(AccessLevel.PUBLIC)
    private final Status status = Status.UNSET;

    private String mockLinje;

    public MeldingLinjeFileReaderMock(final String mockLinje) {
        this.mockLinje = mockLinje;
    }

    @Override
    public List<String> read() throws LinjeUnreadableException {

        List<String> fil = new ArrayList<>();
        fil.add(mockLinje);

        return fil;
    }
}
