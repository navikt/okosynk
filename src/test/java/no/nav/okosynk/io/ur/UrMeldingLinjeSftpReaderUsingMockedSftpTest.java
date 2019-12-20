package no.nav.okosynk.io.ur;

import java.util.function.Function;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.AbstractMeldingLinjeSftpReaderUsingMockedSftpTest;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.MeldingLinjeSftpReader;

public class UrMeldingLinjeSftpReaderUsingMockedSftpTest
    extends AbstractMeldingLinjeSftpReaderUsingMockedSftpTest {

  static {
    setFtpHostUrlKey(Constants.BATCH_TYPE.UR.getFtpHostUrlKey());
    setFtpUserKey(Constants.BATCH_TYPE.UR.getFtpUserKey());
    setFtpPasswordKey(Constants.BATCH_TYPE.UR.getFtpPasswordKey());
  }

  @Override
  protected Constants.BATCH_TYPE getBatchType() {
    return Constants.BATCH_TYPE.UR;
  }
}