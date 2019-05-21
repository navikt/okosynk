package no.nav.okosynk.io.os;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.MeldingLinjeFtpReaderTestUsingRealFtpOrSftp;

abstract class AbstractOsMeldingLinjeFtpReaderTestUsingRealFtpOrSftp
    extends MeldingLinjeFtpReaderTestUsingRealFtpOrSftp {

    static {
        setFtpHostUriKey(Constants.BATCH_TYPE.OS.getFtpHostUrlKey());
        setFtpUserKey(Constants.BATCH_TYPE.OS.getFtpUserKey());
        setFtpPasswordKey(Constants.BATCH_TYPE.OS.getFtpPasswordKey());
    }
}
