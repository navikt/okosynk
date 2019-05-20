package no.nav.okosynk.io.ur;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.MeldingLinjeFtpReaderTestUsingRealFtpOrSftp;

abstract class AbstractUrMeldingLinjeFtpReaderTestUsingRealFtpOrSftp
    extends MeldingLinjeFtpReaderTestUsingRealFtpOrSftp {

    static {
        setFTP_HOST_URI_KEY(Constants.BATCH_TYPE.UR.getFtpHostUrlKey());
        setFTP_USER_KEY(Constants.BATCH_TYPE.UR.getFtpUserKey());
        setFTP_PASSWORD_KEY(Constants.BATCH_TYPE.UR.getFtpPasswordKey());
    }
}
