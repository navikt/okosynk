package no.nav.okosynk.io.os;

import java.util.function.Supplier;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.io.ConfigureOrInitializeOkosynkIoException;
import no.nav.okosynk.io.MeldingLinjeReaderWrapper;
import no.nav.okosynk.io.AbstractMeldingLinjeReaderWrapperTest;

public class OsMeldingLinjeReaderWrapperTest
    extends AbstractMeldingLinjeReaderWrapperTest {

    @Override
    protected Supplier<MeldingLinjeReaderWrapper> getCreator() {

        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();

        final Supplier<MeldingLinjeReaderWrapper> creator =
            () -> {
                try {
                    return new MeldingLinjeReaderWrapper(okosynkConfiguration, Constants.BATCH_TYPE.OS);
                } catch (ConfigureOrInitializeOkosynkIoException e) {
                    throw new RuntimeException(e);
                }
            };

        return creator;
    }

    @Override
    protected String getFtpHostUrlKey() {
        return Constants.BATCH_TYPE.OS.getFtpHostUrlKey();
    }

    @Override
    protected String getFtpUserKey() {
        return Constants.BATCH_TYPE.OS.getFtpUserKey();
    }

    @Override
    protected String getFtpPasswordKey() {
        return Constants.BATCH_TYPE.OS.getFtpPasswordKey();
    }
}
