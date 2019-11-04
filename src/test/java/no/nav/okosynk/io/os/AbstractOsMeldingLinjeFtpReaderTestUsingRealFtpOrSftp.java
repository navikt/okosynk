package no.nav.okosynk.io.os;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp;

abstract class AbstractOsMeldingLinjeFtpReaderTestUsingRealFtpOrSftp
    extends AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp {

    static {
        setFtpHostUriKey(Constants.BATCH_TYPE.OS.getFtpHostUrlKey());
        setFtpUserKey(Constants.BATCH_TYPE.OS.getFtpUserKey());
        setFtpPasswordKey(Constants.BATCH_TYPE.OS.getFtpPasswordKey());
    }
}
