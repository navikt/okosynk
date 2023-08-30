package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import no.nav.okosynk.config.Constants;

public class OsMeldingLinjeSftpReaderUsingMockedSftpTest
    extends AbstractMeldingLinjeSftpReaderUsingMockedSftpTest {

  @Override
  protected Constants.BATCH_TYPE getBatchType() {
    return Constants.BATCH_TYPE.OS;
  }
}
