package no.nav.okosynk.io.os;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.MeldingLinjeFtpReaderTestUsingRealFtpOrSftp;

abstract class AbstractOsMeldingLinjeFtpReaderTestUsingRealFtpOrSftp
    extends MeldingLinjeFtpReaderTestUsingRealFtpOrSftp {

    static {
        setFTP_HOST_URI_KEY(Constants.BATCH_TYPE.OS.getFtpHostUrlKey());
        setFTP_USER_KEY(Constants.BATCH_TYPE.OS.getFtpUserKey());
        setFTP_PASSWORD_KEY(Constants.BATCH_TYPE.OS.getFtpPasswordKey());
    }
}
