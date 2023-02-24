package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import no.nav.okosynk.config.Constants;

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