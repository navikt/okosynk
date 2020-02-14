package no.nav.okosynk.io.ur;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.AbstractMeldingLinjeSftpReaderUsingMockedSftpTest;

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