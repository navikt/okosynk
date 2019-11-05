package no.nav.okosynk.io.ur;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.io.AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp;

abstract class AbstractUrMeldingLinjeFtpReaderTestUsingRealFtpOrSftp
    extends AbstractMeldingLinjeFtpReaderTestUsingRealFtpOrSftp {

    static {
        setFtpHostUriKey(Constants.BATCH_TYPE.UR.getFtpHostUrlKey());
        setFtpUserKey(Constants.BATCH_TYPE.UR.getFtpUserKey());
        setFtpPasswordKey(Constants.BATCH_TYPE.UR.getFtpPasswordKey());
    }
}
