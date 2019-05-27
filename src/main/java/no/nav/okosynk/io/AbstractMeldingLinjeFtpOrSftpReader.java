package no.nav.okosynk.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMeldingLinjeFtpOrSftpReader
    extends AbstractMeldingLinjeFileReader {

    protected abstract static class AbstractFtpOrSftpResourceContainer
        implements AbstractMeldingLinjeFileReader.IResourceContainer {

        protected InputStream getInputStream() {
            return inputStream;
        }

        protected void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        private InputStream inputStream;

        @Override
        public void free() {

            if (getInputStream() != null) {
                try {
                    getInputStream().close();
                } catch (IOException e) {
                    logger.warn("Exception when closing the input stream.", e);
                }
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(AbstractMeldingLinjeFtpOrSftpReader.class);

    protected AbstractMeldingLinjeFtpOrSftpReader(
        final IOkosynkConfiguration okosynkConfiguration,
        final Constants.BATCH_TYPE  batchType,
        final String                fullyQualifiedInputFileName
    ) {
        super(okosynkConfiguration, batchType, fullyQualifiedInputFileName);

        setStatus(IMeldingLinjeFileReader.Status.OK);

        String msg = "";
        try {
            getFtpProtocol(okosynkConfiguration);
        } catch (Throwable e) {
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
            msg +=
                System.lineSeparator()
                    + "ftp protocol is invalid. It must be either "
                    + Constants.FTP_PROTOCOL.FTP
                    + " or "
                    + Constants.FTP_PROTOCOL.SFTP
                    + "If left empty, the default value "
                    + Constants.FTP_PROTOCOL_DEFAULT_VALUE
                    + " will be used.";
        }
        if ((getFtpHostUrl(okosynkConfiguration) == null) || getFtpHostUrl(okosynkConfiguration).trim().isEmpty()) {
            msg = "ftpHostUrl er null eller tom";
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
        }
        try {
            getFtpHostPort(okosynkConfiguration);
        } catch (Throwable e) {
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
            msg += System.lineSeparator() + "ftpHostPort er null eller tom";
        }
        if ((getFtpUser(okosynkConfiguration) == null) || getFtpUser(okosynkConfiguration).trim().isEmpty()) {
            msg += System.lineSeparator() + "ftpUser er null eller tom";
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
        }
        if ((getFtpPassword(okosynkConfiguration) == null) || getFtpPassword(okosynkConfiguration).trim().isEmpty()) {
            msg += System.lineSeparator() + "ftpPassword er null eller tom";
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
        }

        if (IMeldingLinjeFileReader.Status.ERROR.equals(getStatus())) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static String getOsFtpHostUrl(final IOkosynkConfiguration okosynkConfiguration) {
        return okosynkConfiguration.getString(Constants.BATCH_TYPE.OS.getFtpHostUrlKey());
    }

    public static String getUrFtpHostUrl(final IOkosynkConfiguration okosynkConfiguration) {
        return okosynkConfiguration.getString(Constants.BATCH_TYPE.UR.getFtpHostUrlKey());
    }

    public static boolean osShouldUseSftp(final IOkosynkConfiguration okosynkConfiguration) {

        final boolean shouldUseSftp = shouldUseSftp(getOsFtpHostUrl(okosynkConfiguration));

        return shouldUseSftp;
    }

    public static boolean urShouldUseSftp(final IOkosynkConfiguration okosynkConfiguration) {

        final boolean shouldUseSftp = shouldUseSftp(getUrFtpHostUrl(okosynkConfiguration));

        return shouldUseSftp;
    }

    public static Constants.FTP_PROTOCOL getFtpProtocol(final String ftpHostUrl) throws LinjeUnreadableException {

        final Constants.FTP_PROTOCOL ftpHostProtocol;
        try {
            final URI uri = checkFtpHostUrlAndProduceUri(ftpHostUrl);
            if (uri.getScheme() == null) {
                logger.warn(
                    "ftpHostUrl has a missing protocol, so the default value "
                        + Constants.FTP_PROTOCOL_DEFAULT_VALUE
                        + " will be used"
                );
                ftpHostProtocol = Constants.FTP_PROTOCOL_DEFAULT_VALUE;
            } else {
                ftpHostProtocol = Constants.FTP_PROTOCOL.valueOf(uri.getScheme().toUpperCase());
            }
        } catch (Throwable e) {
            throw new LinjeUnreadableException("Invalid ftpHostUrl found when trying to parse the protocol: " + ftpHostUrl, e);
        }

        return ftpHostProtocol;
    }

    private static String getFtpHostServerName(final String ftpHostUrl) throws LinjeUnreadableException {

        String ftpHostServerName;
        try {
            final URI uri = checkFtpHostUrlAndProduceUri(ftpHostUrl);
            ftpHostServerName = uri.getHost();
            if (ftpHostServerName == null) {
                final String authority = uri.getAuthority();
                if (authority == null) {
                    ftpHostServerName = ftpHostUrl; // ... which we know is not null at this point
                } else {
                    ftpHostServerName = authority;
                }
            }
            final int ftpHostPort = getFtpHostPort(ftpHostUrl);
            final String ftpHostPortSuffix = ":" + ftpHostPort;
            if (ftpHostServerName.endsWith(ftpHostPortSuffix)) {
                ftpHostServerName =
                    ftpHostServerName.substring(0, ftpHostServerName.lastIndexOf(ftpHostPortSuffix));
            }

            if (ftpHostServerName.contains(":")) {
                throw new LinjeUnreadableException(
                    "Invalid ftpHostUrl: "
                        + ftpHostUrl
                        + ", parsed to give an invalid host containing a colon, indicating an erroneous port."
                );
            }

        } catch (Throwable e) {
            throw new LinjeUnreadableException("Invalid ftpHostUrl found when trying to parse the host name: " + ftpHostUrl, e);
        }

        return ftpHostServerName;
    }

    private static int getFtpHostPort(final String ftpHostUrl) throws LinjeUnreadableException {

        final int ftpHostPort;
        try {
            final URI uri = checkFtpHostUrlAndProduceUri(ftpHostUrl);
            int tempFtpHostPort = uri.getPort();
            if (tempFtpHostPort == -1) {
                final String authority = uri.getAuthority();
                if (authority != null) {
                    final String[] authorityParts = authority.split(":");
                    if (authorityParts.length > 1) {
                        try {
                            tempFtpHostPort =
                                Integer.parseInt(authorityParts[authorityParts.length - 1]);
                        } catch (NumberFormatException e) {
                            logger.warn(
                                "ftpHostUrl has an invalid or missing port, so the default value "
                                    + Constants.FTP_HOST_PORT_DEFAULT_VALUE
                                    + " will be used"
                            );
                            tempFtpHostPort = Constants.FTP_HOST_PORT_DEFAULT_VALUE;
                        }
                        ftpHostPort = tempFtpHostPort;
                    } else {
                        logger.warn(
                            "ftpHostUrl has an invalid or missing port, so the default value "
                                + Constants.FTP_HOST_PORT_DEFAULT_VALUE
                                + " will be used"
                        );
                        ftpHostPort = Constants.FTP_HOST_PORT_DEFAULT_VALUE;
                    }
                } else {
                    logger.warn(
                        "ftpHostUrl has an invalid or missing port, so the default value "
                            + Constants.FTP_HOST_PORT_DEFAULT_VALUE
                            + " will be used"
                    );
                    ftpHostPort = Constants.FTP_HOST_PORT_DEFAULT_VALUE;
                }
            } else {
                ftpHostPort = tempFtpHostPort;
            }
        } catch (Throwable e) {
            throw new LinjeUnreadableException("Invalid ftpHostUrl found when trying to parse the port: " + ftpHostUrl, e);
        }

        return ftpHostPort;
    }

    public static String getFtpInputFilePath(final String ftpHostUrl) throws LinjeUnreadableException {
        try {
            final URI uri = checkFtpHostUrlAndProduceUri(ftpHostUrl);
            return uri.getPath();
        } catch (Throwable e) {
            throw new LinjeUnreadableException("Invalid ftpHostUrl found when trying to parse the file path: " + ftpHostUrl, e);
        }
    }

    private static URI checkFtpHostUrlAndProduceUri(final String ftpHostUrl) throws LinjeUnreadableException {

        String ftpHostServerName;
        final URI uri;
        try {
            if (ftpHostUrl == null) {
                throw new NullPointerException("Host url null");
            } else if (ftpHostUrl.trim().isEmpty()){
                throw new IllegalArgumentException("Host url blank");
            } else {
                uri = new URI(ftpHostUrl);
            }

        } catch (Throwable e) {
            throw new LinjeUnreadableException("Invalid ftpHostUrl found when trying to parse the host name: " + ftpHostUrl, e);
        }

        return uri;
    }

    public static boolean shouldUseSftp(final String ftpHostUrl) {

        Constants.FTP_PROTOCOL ftpProtocol;
        try {
            ftpProtocol = getFtpProtocol(ftpHostUrl);
        } catch (LinjeUnreadableException e) {
            ftpProtocol = Constants.FTP_PROTOCOL_DEFAULT_VALUE;
        }
        final boolean shouldUseSftp = Constants.FTP_PROTOCOL.SFTP.equals(ftpProtocol);

        return shouldUseSftp;
    }

    protected String getFtpInputFilePath(final IOkosynkConfiguration okosynkConfiguration) throws LinjeUnreadableException {

        final String ftpInputFilePath = getFtpInputFilePath(this.getFtpHostUrl(okosynkConfiguration));
        return ftpInputFilePath;
    }

    @Override
    public String toString() {

        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();

        return
            "ftpHostUrl              : "
                + (this.getFtpHostUrl(okosynkConfiguration) == null
                ?
                "null"
                :
                this.getFtpHostUrl(okosynkConfiguration)) + System.lineSeparator()
                + "user                     : "
                + (this.getFtpUser(okosynkConfiguration) == null
                ?
                "null"
                :
                this.getFtpUser(okosynkConfiguration)) + System.lineSeparator()
                + "fully qualified file name: "
                + (this.getFullyQualifiedInputFileName() == null
                ?
                "null"
                :
                this.getFullyQualifiedInputFileName()) + System.lineSeparator();
    }

    protected String getFtpConnectionTimeoutInMsKey() {
        return getBatchType().getFtpConnectionTimeoutKey();
    }

    protected int getFtpDefaultConnectionTimeoutInMs() {
        return getBatchType().getFtpConnectionTimeoutDefaultValueInMs();
    }

    protected String getFtpUser(final IOkosynkConfiguration okosynkConfiguration)  {
        return okosynkConfiguration.getString(getBatchType().getFtpUserKey());
    }

    protected String getFtpPassword(final IOkosynkConfiguration okosynkConfiguration) {
        return okosynkConfiguration.getString(getBatchType().getFtpPasswordKey());
    }

    protected Constants.FTP_PROTOCOL getFtpProtocol(final IOkosynkConfiguration okosynkConfiguration) throws LinjeUnreadableException {

        final Constants.FTP_PROTOCOL ftpHostProtocol =
            getFtpProtocol(this.getFtpHostUrl(okosynkConfiguration));

        return ftpHostProtocol;
    }

    protected String getFtpHostServerName(
        final IOkosynkConfiguration okosynkConfiguration) throws LinjeUnreadableException {

        final String ftpHostServerName = getFtpHostServerName(this.getFtpHostUrl(okosynkConfiguration));
        return ftpHostServerName;
    }

    protected int getFtpHostPort(
        final IOkosynkConfiguration okosynkConfiguration) throws LinjeUnreadableException {

        final int ftpHostPort = getFtpHostPort(this.getFtpHostUrl(okosynkConfiguration));
        return ftpHostPort;
    }

    protected int getFtpConnectionTimeoutInMs(
        final IOkosynkConfiguration okosynkConfiguration) {

        int ftpConnectionTimeoutInMs;
        try {
            ftpConnectionTimeoutInMs =
                Integer.parseInt(okosynkConfiguration.getString(getFtpConnectionTimeoutInMsKey()));

        } catch (NumberFormatException e) {
            ftpConnectionTimeoutInMs = getFtpDefaultConnectionTimeoutInMs();
            logger.warn("FTP connect timeout for missing or invalid. Using default value: " + ftpConnectionTimeoutInMs);
        }

        return ftpConnectionTimeoutInMs;
    }

    protected InputStreamReader createInputStreamReader(
        final IOkosynkConfiguration                                                  okosynkConfiguration,
        final AbstractMeldingLinjeFtpOrSftpReader.AbstractFtpOrSftpResourceContainer resourceContainer)
        throws UnsupportedEncodingException {

        final InputStreamReader inputStreamReader =
            new InputStreamReader(resourceContainer.getInputStream(), getCharsetName(okosynkConfiguration));

        return inputStreamReader;
    }

    private String getFtpHostUrl(final IOkosynkConfiguration okosynkConfiguration) {

        final String ftpHostUrl =
            okosynkConfiguration.getString(getBatchType().getFtpHostUrlKey());

        return ftpHostUrl;
    }
}
