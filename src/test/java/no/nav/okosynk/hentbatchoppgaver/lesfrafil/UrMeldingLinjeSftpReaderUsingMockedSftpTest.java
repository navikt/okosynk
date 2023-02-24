package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import no.nav.okosynk.config.Constants;

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