package no.nav.okosynk.io;

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.io.os.OsMeldingLinjeReaderWrapperTest;
import no.nav.okosynk.io.ur.UrMeldingLinjeReaderWrapperTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MeldingLinjeReaderWrapperTest {
    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    @Getter(AccessLevel.PROTECTED)
    private final IOkosynkConfiguration okosynkConfiguration =
        new FakeOkosynkConfiguration();

    @Test
    public void testSuccessfulCreationUsingFtp() {

        enteringTestHeaderLogger.debug(null);

        setAllSystemPropertiesSoThatAllReadersCouldBeCreatedWithoutError();

        final MeldingLinjeReaderWrapper actualWrappingMeldingLinjeFileReader =
            getCreator().get();
        final IMeldingLinjeFileReader actualWrappedMeldingLinjeFileReader =
            actualWrappingMeldingLinjeFileReader.getWrappedMeldingLinjeFileReader();
        final String actualTestClassname = this.getClass().getName();
        final String actualWrappingMeldingLinjeFileReaderClassname =
            actualWrappingMeldingLinjeFileReader.getClass().getName();
        final String actualWrappedMeldingLinjeFileReaderClassname =
            actualWrappedMeldingLinjeFileReader.getClass().getName();

        Assertions.assertNotNull(actualWrappingMeldingLinjeFileReader);
        Assertions.assertNotNull(actualWrappedMeldingLinjeFileReader);
        Assertions.assertTrue(
            (
                MeldingLinjeReaderWrapper.class.getName().equals(actualWrappingMeldingLinjeFileReaderClassname)
                &&
                MeldingLinjeFtpReader.class.getName().equals(actualWrappedMeldingLinjeFileReaderClassname)
                &&
                OsMeldingLinjeReaderWrapperTest.class.getName().equals(actualTestClassname)
            )
            ||
            (
                MeldingLinjeReaderWrapper.class.getName().equals(actualWrappingMeldingLinjeFileReaderClassname)
                &&
                MeldingLinjeFtpReader.class.getName().equals(actualWrappedMeldingLinjeFileReaderClassname)
                &&
                UrMeldingLinjeReaderWrapperTest.class.getName().equals(actualTestClassname)
            ),
            System.lineSeparator()
            + "Wrong wrapper <Os/Ur>MeldingLinjeReaderWrapperTest created, "
            + "and/or wrong <Os/Ur>UrMeldingLinjeFtpReader created" + System.lineSeparator()
            + "actualWrappedMeldingLinjeFileReaderClassname: "
            + actualWrappedMeldingLinjeFileReaderClassname + System.lineSeparator()
            + "this.getClass().getName(): "
            + this.getClass().getName() + System.lineSeparator()
        );
    }

    @Test
    public void testSuccessfulCreationUsingSftp() {

        enteringTestHeaderLogger.debug(null);

        setAllSystemPropertiesSoThatAllReadersCouldBeCreatedWithoutError();
        setFtpHostUrl("sftp://a.b:12001/some.path/myFile.txt");

        final MeldingLinjeReaderWrapper actualWrappingMeldingLinjeFileReader =
            getCreator().get();
        final IMeldingLinjeFileReader actualWrappedMeldingLinjeFileReader =
            actualWrappingMeldingLinjeFileReader.getWrappedMeldingLinjeFileReader();
        final String actualTestClassname = this.getClass().getName();
        final String actualWrappingMeldingLinjeFileReaderClassname =
            actualWrappingMeldingLinjeFileReader.getClass().getName();
        final String actualWrappedMeldingLinjeFileReaderClassname =
            actualWrappedMeldingLinjeFileReader.getClass().getName();

        Assertions.assertNotNull(actualWrappingMeldingLinjeFileReader);
        Assertions.assertNotNull(actualWrappedMeldingLinjeFileReader);
        Assertions.assertTrue(
            (
                MeldingLinjeReaderWrapper.class.getName().equals(actualWrappingMeldingLinjeFileReaderClassname)
                &&
                MeldingLinjeSftpReader.class.getName().equals(actualWrappedMeldingLinjeFileReaderClassname)
                &&
                OsMeldingLinjeReaderWrapperTest.class.getName().equals(actualTestClassname)
            )
            ||
            (
                MeldingLinjeReaderWrapper.class.getName().equals(actualWrappingMeldingLinjeFileReaderClassname)
                &&
                MeldingLinjeSftpReader.class.getName().equals(actualWrappedMeldingLinjeFileReaderClassname)
                &&
                UrMeldingLinjeReaderWrapperTest.class.getName().equals(actualTestClassname)
            ),
            System.lineSeparator()
            + "Wrong wrapper <Os/Ur>MeldingLinjeReaderWrapperTest created, "
            + "and/or wrong <Os/Ur>UrMeldingLinjeFtpReader created" + System.lineSeparator()
            + "actualWrappedMeldingLinjeFileReaderClassname: "
            + actualWrappedMeldingLinjeFileReaderClassname + System.lineSeparator()
            + "this.getClass().getName(): "
            + this.getClass().getName() + System.lineSeparator()
        );
    }

    private void setAllSystemPropertiesSoThatAllReadersCouldBeCreatedWithoutError() {
        setFtpHostUrl("ftp://a.b:12001/some.path/myFile.txt");
        setFtpUser("SomeUser");
        setFtpPassword("SomePwd");
    }

    protected abstract Supplier<MeldingLinjeReaderWrapper> getCreator();
    protected abstract String getFtpHostUrlKey();
    protected abstract String getFtpUserKey();
    protected abstract String getFtpPasswordKey();

    private void setFtpHostUrl(final String ftpHostUrl) {
        final String ftpHostUrlKey = getFtpHostUrlKey();
        this.okosynkConfiguration.setSystemProperty(ftpHostUrlKey, ftpHostUrl);
    }

    private void setFtpUser(final String ftpUser) {
        final String ftpUserKey = getFtpUserKey();
        this.okosynkConfiguration.setSystemProperty(ftpUserKey, ftpUser);
    }

    private void setFtpPassword(final String ftpPassword) {
        final String ftpPasswordKey = getFtpPasswordKey();
        this.okosynkConfiguration.setSystemProperty(ftpPasswordKey, ftpPassword);
    }

}
