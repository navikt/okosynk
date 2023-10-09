package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import no.nav.okosynk.config.Constants;

public class UrMeldingLinjeSftpReaderUsingMockedSftpTest
    extends AbstractMeldingLinjeSftpReaderUsingMockedSftpTest {

  @Override
  protected Constants.BATCH_TYPE getBatchType() {
    return Constants.BATCH_TYPE.UR;
  }
}
