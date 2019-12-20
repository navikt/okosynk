package no.nav.okosynk.io.os;

import java.util.function.Function;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.AbstractMeldingLinjeSftpReaderUsingMockedSftpTest;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.MeldingLinjeSftpReader;

public class OsMeldingLinjeSftpReaderUsingMockedSftpTest
    extends AbstractMeldingLinjeSftpReaderUsingMockedSftpTest {

  static {
    setFtpHostUrlKey(Constants.BATCH_TYPE.OS.getFtpHostUrlKey());
    setFtpUserKey(Constants.BATCH_TYPE.OS.getFtpUserKey());
    setFtpPasswordKey(Constants.BATCH_TYPE.OS.getFtpPasswordKey());
  }

  @Override
  protected Constants.BATCH_TYPE getBatchType() {
    return Constants.BATCH_TYPE.OS;
  }
}