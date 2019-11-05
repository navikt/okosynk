package no.nav.okosynk.io.ur;

import java.util.function.Supplier;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.io.MeldingLinjeReaderWrapper;
import no.nav.okosynk.io.AbstractMeldingLinjeReaderWrapperTest;

public class UrMeldingLinjeReaderWrapperTest
    extends AbstractMeldingLinjeReaderWrapperTest {

    @Override
    protected Supplier<MeldingLinjeReaderWrapper> getCreator() {

        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();

        final Supplier<MeldingLinjeReaderWrapper> creator =
            () -> {
                return new MeldingLinjeReaderWrapper(okosynkConfiguration, Constants.BATCH_TYPE.UR);
            };

        return creator;
    }

    @Override
    protected String getFtpHostUrlKey() {
        return Constants.BATCH_TYPE.UR.getFtpHostUrlKey();
    }

    @Override
    protected String getFtpUserKey() {
        return Constants.BATCH_TYPE.UR.getFtpUserKey();
    }

    @Override
    protected String getFtpPasswordKey() {
        return Constants.BATCH_TYPE.UR.getFtpPasswordKey();
    }
}
