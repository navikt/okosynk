package no.nav.okosynk.io;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMeldingLinjeFileReader
    implements IMeldingLinjeFileReader {

    protected interface IResourceContainer {
        void free();
    }

    private static final Logger logger = LoggerFactory.getLogger(AbstractMeldingLinjeFileReader.class);

    private static final Charset defaultCharset = StandardCharsets.ISO_8859_1;
    private final IOkosynkConfiguration okosynkConfiguration;
    final Constants.BATCH_TYPE  batchType;

    private static Charset getDefaultCharset() {
        return defaultCharset;
    }

    public IOkosynkConfiguration getOkosynkConfiguration() {
        return okosynkConfiguration;
    }

    public Constants.BATCH_TYPE getBatchType() {
        return batchType;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private Status status = Status.UNSET;

    public String getFullyQualifiedInputFileName() {
        return fullyQualifiedInputFileName;
    }

    private final String fullyQualifiedInputFileName;

    protected AbstractMeldingLinjeFileReader(
        final IOkosynkConfiguration okosynkConfiguration,
        final Constants.BATCH_TYPE  batchType,
        final String                fullyQualifiedInputFileName) {

        setStatus(Status.ERROR);

        if (fullyQualifiedInputFileName == null) {
            throw new NullPointerException("fullyQualifiedInputFileName er null");
        }

        if (fullyQualifiedInputFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("fullyQualifiedInputFileName er tom eller blank");
        }
        this.fullyQualifiedInputFileName = fullyQualifiedInputFileName;

        setStatus(Status.OK);

        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType            = batchType;
    }

    // =========================================================================

    @Override
    public List<String> read() throws LinjeUnreadableException {

        List<String> meldinger = null;
        final IResourceContainer resourceContainer = createResourceContainer();
        try {
            meldinger = lesMeldingerFraFil(resourceContainer);
        } catch (LinjeUnreadableException e) {
            handterThrowable(e);
        } catch (Throwable e) {
            handterThrowable(e);
        } finally {
            resourceContainer.free();
        }

        return meldinger;
    }

    protected abstract BufferedReader lagBufferedReader(
        final IOkosynkConfiguration okosynkConfiguration,
        final IResourceContainer    resources) throws Throwable;

    protected abstract IResourceContainer createResourceContainer();

    protected String getCharsetName(final IOkosynkConfiguration okosynkConfiguration) {
        return okosynkConfiguration.getString(getBatchType().getFtpCharsetNameKey(), "ISO8859_1");
    }

    protected String getDefaultCharsetName() {
        return AbstractMeldingLinjeFileReader.getDefaultCharset().name();
    }

    protected List<String> lesMeldingerFraFil(final IResourceContainer resourceContainer) throws Throwable {

        final BufferedReader bufferedReader = lagBufferedReader(this.getOkosynkConfiguration(), resourceContainer);

        final List<String> lines;
        try {
            lines = bufferedReader.lines().collect(Collectors.toList());
        } catch (Throwable e) {
            final String msg =
                  "Could not read lines from buffered reader. " + System.lineSeparator()
                + this.toString();
            throw new LinjeUnreadableException(msg, e);
        }

        return lines;
    }

    private void handterThrowable(final Throwable e) throws LinjeUnreadableException {

        logger.error(String.format(
            "Det oppsto en feil under innlesning av meldinger fra filen %s.",
            getFullyQualifiedInputFileName()), e
        );

        throw new LinjeUnreadableException(e);
    }
}
