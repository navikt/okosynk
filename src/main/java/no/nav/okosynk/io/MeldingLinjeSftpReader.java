package no.nav.okosynk.io;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MeldingLinjeSftpReader
        implements IMeldingLinjeFileReader {

    private static final Logger logger = LoggerFactory.getLogger(MeldingLinjeSftpReader.class);

    private static final String JSCH_CHANNEL_TYPE_SFTP = "sftp";

    private final JSch javaSecureChannel;
    private final IOkosynkConfiguration okosynkConfiguration;
    private final Constants.BATCH_TYPE batchType;
    private final String fullyQualifiedInputFileName;
    private Status status = Status.UNSET;

    public MeldingLinjeSftpReader(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType,
            final String fullyQualifiedInputFileName
    ) {

        setStatus(IMeldingLinjeFileReader.Status.ERROR);

        if (okosynkConfiguration == null) {
            throw new NullPointerException("okosynkConfiguration er null");
        }

        if (batchType == null) {
            throw new NullPointerException("batchType er null");
        }

        if (fullyQualifiedInputFileName == null) {
            throw new NullPointerException("fullyQualifiedInputFileName er null");
        }

        if (fullyQualifiedInputFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("fullyQualifiedInputFileName er tom eller blank");
        }

        final String normalizedFullyQualifiedInputFileName =
                fullyQualifiedInputFileName.replace('\\', '/');

        setStatus(IMeldingLinjeFileReader.Status.OK);

        this.fullyQualifiedInputFileName = normalizedFullyQualifiedInputFileName;
        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType = batchType;
        this.javaSecureChannel = new JSch();

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
        final String ftpHostUrl = okosynkConfiguration.getFtpHostUrl(getBatchType());

        if (StringUtils.isBlank(ftpHostUrl)) {
            msg = "ftpHostUrl er null eller tom: " + String.valueOf(ftpHostUrl);
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
        }
        try {
            // TODO: Bad programming? Just see that no exception is thrown:
            MeldingLinjeSftpReader.getFtpHostPort(ftpHostUrl);
        } catch (Throwable e) {
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
            msg += System.lineSeparator() + "Cannot deduce port number from URL: " + ftpHostUrl;
        }
        final String ftpUser = okosynkConfiguration.getFtpUser(getBatchType());
        if (StringUtils.isBlank(ftpUser)) {
            msg += System.lineSeparator() + "ftpUser er null eller tom: + " + ftpUser;
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
        }
        final String ftpPassword = okosynkConfiguration.getFtpPassword(getBatchType());

        if (StringUtils.isBlank(ftpPassword)) {
            msg += System.lineSeparator() + "ftpPassword er null eller tom";
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
        }

        if (IMeldingLinjeFileReader.Status.ERROR.equals(getStatus())) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static String getFtpInputFilePath(final String ftpHostUrl)
            throws ConfigureOrInitializeOkosynkIoException {
        try {
            final URI uri = MeldingLinjeSftpReader.checkFtpHostUrlAndProduceUri(ftpHostUrl);
            return uri.getPath();
        } catch (Throwable e) {
            throw new ConfigureOrInitializeOkosynkIoException(
                    "Invalid ftpHostUrl found when trying to parse the file path: " + ftpHostUrl, e
            );
        }
    }

    private static Constants.FTP_PROTOCOL getFtpProtocol(final String ftpHostUrl)
            throws ConfigureOrInitializeOkosynkIoException {

        final Constants.FTP_PROTOCOL ftpHostProtocol;
        try {
            final URI uri = MeldingLinjeSftpReader.checkFtpHostUrlAndProduceUri(ftpHostUrl);
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
            throw new ConfigureOrInitializeOkosynkIoException(
                    "Invalid ftpHostUrl found when trying to parse the protocol: " + ftpHostUrl, e);
        }

        return ftpHostProtocol;
    }

    private static String getFtpHostServerName(final String ftpHostUrl)
            throws ConfigureOrInitializeOkosynkIoException {

        String ftpHostServerName;
        try {
            final URI uri = MeldingLinjeSftpReader.checkFtpHostUrlAndProduceUri(ftpHostUrl);
            ftpHostServerName = uri.getHost();
            if (ftpHostServerName == null) {
                final String authority = uri.getAuthority();
                if (authority == null) {
                    ftpHostServerName = ftpHostUrl; // ... which we know is not null at this point
                } else {
                    ftpHostServerName = authority;
                }
            }
            final int ftpHostPort = MeldingLinjeSftpReader.getFtpHostPort(ftpHostUrl);
            final String ftpHostPortSuffix = ":" + ftpHostPort;
            if (ftpHostServerName.endsWith(ftpHostPortSuffix)) {
                ftpHostServerName =
                        ftpHostServerName.substring(0, ftpHostServerName.lastIndexOf(ftpHostPortSuffix));
            }

            if (ftpHostServerName.contains(":")) {
                throw new ConfigureOrInitializeOkosynkIoException(
                        "Invalid ftpHostUrl: "
                                + ftpHostUrl
                                + ", parsed to give an invalid host containing a colon, "
                                + "indicating an erroneous port."
                );
            }

        } catch (Throwable e) {
            throw new ConfigureOrInitializeOkosynkIoException(
                    "Invalid ftpHostUrl found when trying to parse the host name: " + ftpHostUrl, e);
        }

        return ftpHostServerName;
    }

    private static int getFtpHostPort(final String ftpHostUrl) throws ConfigureOrInitializeOkosynkIoException {

        final int ftpHostPort;
        try {
            final URI uri = MeldingLinjeSftpReader.checkFtpHostUrlAndProduceUri(ftpHostUrl);
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
            throw new ConfigureOrInitializeOkosynkIoException(
                    "Invalid ftpHostUrl found when trying to parse the port: " + ftpHostUrl, e);
        }

        return ftpHostPort;
    }

    private static URI checkFtpHostUrlAndProduceUri(final String ftpHostUrl)
            throws ConfigureOrInitializeOkosynkIoException {

        final URI uri;
        try {
            if (ftpHostUrl == null) {
                throw new NullPointerException("Host url null");
            } else if (ftpHostUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("Host url blank");
            } else {
                uri = new URI(ftpHostUrl);
            }
        } catch (Throwable e) {
            throw new ConfigureOrInitializeOkosynkIoException(
                    "Invalid ftpHostUrl found when trying to parse the host name: " + ftpHostUrl, e);
        }

        return uri;
    }

    private static String getFtpInputFilePath(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) throws ConfigureOrInitializeOkosynkIoException {

        return MeldingLinjeSftpReader.getFtpInputFilePath(okosynkConfiguration.getFtpHostUrl(batchType));
    }

    public SftpResourceContainer createResourceContainer() {
        return new SftpResourceContainer(this.javaSecureChannel);
    }

    public IOkosynkConfiguration getOkosynkConfiguration() {
        return this.okosynkConfiguration;
    }

    @Override
    public Constants.BATCH_TYPE getBatchType() {
        return this.batchType;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    private void setStatus(final Status status) {
        this.status = status;
    }

    @Override
    /**
     * Calls resourceContainer.free(); regardless whether the method succeeds or not.
     */
    public List<String> read()
            throws IoOkosynkIoException,
            NotFoundOkosynkIoException,
            ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            EncodingOkosynkIoException {

        final List<String> meldinger;
        SftpResourceContainer resourceContainer = null;
        try {
            resourceContainer = createResourceContainer();
            meldinger = lesMeldingerFraFil(resourceContainer);
        } catch (IoOkosynkIoException | NotFoundOkosynkIoException okosynkIoException) {
            throw okosynkIoException;
        } catch (AuthenticationOkosynkIoException | ConfigureOrInitializeOkosynkIoException | EncodingOkosynkIoException e) {
            throw e;
        } finally {
            if (resourceContainer != null) {
                resourceContainer.free();
            }
        }

        return meldinger;
    }

    /**
     * Should be called after a successfull treatment of the lines read from the input file.
     * Calls resourceContainer.free(); regardless whether the method succeeds or not.
     * Never throws anything, because renaming is considered relatively harmless.
     *
     * @return <code>true</code> if OK, <code>false</code> otherwise.
     */
    @Override
    public boolean removeInputData() {

        SftpResourceContainer resourceContainer = null;
        boolean inputFileWasSuccessfullyRenamed;
        try {
            final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
            resourceContainer = createResourceContainer();
            establishSftpResources(okosynkConfiguration, resourceContainer);
            inputFileWasSuccessfullyRenamed = removeInputData(okosynkConfiguration, resourceContainer);
        } catch (Throwable e) {
            logger.error("Exception received when trying to rename the input file.", e);
            resourceContainer = null;
            inputFileWasSuccessfullyRenamed = false;
        } finally {
            if (resourceContainer != null) {
                resourceContainer.free();
                ;
            }
        }
        return inputFileWasSuccessfullyRenamed;
    }

    @Override
    public String toString() {

        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();

        return
                "Linjereader properties:" + System.lineSeparator()
                        + "=======================" + System.lineSeparator()
                        + "ftpHostUrl                 : "
                        + (
                        okosynkConfiguration.getFtpHostUrl(getBatchType()) == null
                                ?
                                "null"
                                :
                                okosynkConfiguration.getFtpHostUrl(getBatchType())


                )
                        + System.lineSeparator()
                        + "user                       : "
                        + (
                        okosynkConfiguration.getFtpUser(batchType) == null
                                ?
                                "null"
                                :
                                okosynkConfiguration.getFtpUser(batchType)
                )
                        + System.lineSeparator()
                        + "fully qualified file name  : "
                        + (
                        this.fullyQualifiedInputFileName == null
                                ?
                                "null"
                                :
                                this.fullyQualifiedInputFileName
                )
                ;
    }

    List<String> lesMeldingerFraFil(final SftpResourceContainer resourceContainer)
            throws IoOkosynkIoException,
            ConfigureOrInitializeOkosynkIoException,
            EncodingOkosynkIoException,
            AuthenticationOkosynkIoException,
            NotFoundOkosynkIoException {

        final BufferedReader bufferedReader =
                lagBufferedReader(getOkosynkConfiguration(), resourceContainer);
        final List<String> lines;
        try {
            lines = bufferedReader.lines().collect(Collectors.toList());
        } catch (Throwable e) {
            final String msg =
                    "Could not read lines from buffered reader. " + System.lineSeparator() + this;
            throw new IoOkosynkIoException(msg, e);
        }

        return lines;
    }

    Constants.FTP_PROTOCOL getFtpProtocol(final IOkosynkConfiguration okosynkConfiguration)
            throws ConfigureOrInitializeOkosynkIoException {
        return MeldingLinjeSftpReader.getFtpProtocol(okosynkConfiguration.getFtpHostUrl(getBatchType()));
    }

    String getFtpHostServerName(
            final IOkosynkConfiguration okosynkConfiguration) throws ConfigureOrInitializeOkosynkIoException {
        return MeldingLinjeSftpReader.getFtpHostServerName(okosynkConfiguration.getFtpHostUrl(getBatchType()));
    }

    int getFtpHostPort(
            final IOkosynkConfiguration okosynkConfiguration)
            throws ConfigureOrInitializeOkosynkIoException {
        return MeldingLinjeSftpReader.getFtpHostPort(okosynkConfiguration.getFtpHostUrl(getBatchType()));
    }

    private BufferedReader lagBufferedReader(
            final IOkosynkConfiguration okosynkConfiguration,
            final SftpResourceContainer resourceContainer)
            throws AuthenticationOkosynkIoException,
            ConfigureOrInitializeOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException,
            EncodingOkosynkIoException {

        establishSftpResources(okosynkConfiguration, resourceContainer);

        final BufferedReader bufferedReader =
                createBufferedReader(
                        okosynkConfiguration,
                        resourceContainer);

        return bufferedReader;
    }

    private boolean removeInputData(
            final IOkosynkConfiguration okosynkConfiguration,
            final SftpResourceContainer resourceContainer
    ) {

        boolean inputFileWasSuccessfullyRenamed;
        try {
            final ChannelSftp channelSftp = resourceContainer.getSftpChannel();
            final String home = channelSftp.getHome();
            final String inputFilePath =
                    MeldingLinjeSftpReader.getFtpInputFilePath(okosynkConfiguration, getBatchType());
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd'T'HH.mm.ss");
            final LocalDateTime now = LocalDateTime.now();
            final String formatDateTime = now.format(formatter);
            final String toFileName = inputFilePath + "." + formatDateTime;
            logger.debug("About to rename the input file.");
            channelSftp.cd(home);
            channelSftp.rename(inputFilePath, toFileName);
            logger.info("The input file is successfully renamed.");
            inputFileWasSuccessfullyRenamed = true;
        } catch (Throwable e) {
            logger.warn(
                    "Exception when trying to rename the (s)ftp input file. "
                            + "Rename will not be done, "
                            + "but the program will not be exited. This implies that "
                            + "the input file will be re-read the next time the batch is run, "
                            + "unless it has been overwritten by a new one.", e);
            inputFileWasSuccessfullyRenamed = false;
        }

        return inputFileWasSuccessfullyRenamed;
    }

    private void establishSftpResources(
            final IOkosynkConfiguration okosynkConfiguration,
            final SftpResourceContainer sftpResourceContainer)
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException {

        final Session sftpSession;
        try {
            final String sftpUser = okosynkConfiguration.getFtpUser(batchType);
            final String sftpHostServerName = this.getFtpHostServerName(okosynkConfiguration);
            final int sftpPort = this.getFtpHostPort(okosynkConfiguration);
            sftpSession =
                    sftpResourceContainer.getJavaSecureChannel().getSession(sftpUser, sftpHostServerName, sftpPort);

        } catch (JSchException e) {
            final String msg =
                    "Could not establish an sftp session. " + System.lineSeparator()
                            + this.toString();
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
            throw new ConfigureOrInitializeOkosynkIoException(msg, e);
        }
        sftpResourceContainer.setSftpSession(sftpSession);

        sftpResourceContainer.getSftpSession().setConfig("StrictHostKeyChecking", "no");
        final String sftpPassword = okosynkConfiguration.getFtpPassword(getBatchType());
        sftpResourceContainer.getSftpSession().setPassword(sftpPassword);
        try {
            sftpResourceContainer.getSftpSession().connect();
        } catch (JSchException e) {
            final String msg =
                    "Could not connect SFTP session. " + System.lineSeparator()
                            + this.toString();
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
            if ("Auth fail".equals(e.getMessage())) {
                throw new AuthenticationOkosynkIoException(msg, e);
            } else {
                throw new ConfigureOrInitializeOkosynkIoException(msg, e);
            }
        }

        try {
            final Channel channel =
                    sftpResourceContainer.getSftpSession().openChannel(JSCH_CHANNEL_TYPE_SFTP);
            sftpResourceContainer.setSftpChannel((ChannelSftp) channel);
        } catch (JSchException e) {
            final String msg =
                    "Could not run openChannel on SFTP session. " + System.lineSeparator()
                            + this.toString();
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
            throw new IoOkosynkIoException(msg, e);
        }

        try {
            sftpResourceContainer.getSftpChannel().connect();
        } catch (JSchException e) {
            final String msg =
                    "Could not connect to channel. " + System.lineSeparator()
                            + this.toString();
            setStatus(IMeldingLinjeFileReader.Status.ERROR);
            throw new IoOkosynkIoException(msg, e);
        }
    }

    private BufferedReader createBufferedReader(
            final IOkosynkConfiguration okosynkConfiguration,
            final SftpResourceContainer sftpResourceContainer)
            throws EncodingOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException {
        try {
            logger.debug("About to acquire an InputStream from the batch input file...");
            final String fullyQualifiedInputFileName = this.fullyQualifiedInputFileName;
            final InputStream inputStream =
                    sftpResourceContainer
                            .getSftpChannel()
                            .get(fullyQualifiedInputFileName);
            if (inputStream == null) {
                final String msg =
                        "InputStream instance acquired from ChannelSftp is null, "
                                + "which most probably is caused by the file not existing.";
                throw new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, msg);
            }
            sftpResourceContainer.setInputStream(inputStream);
        } catch (SftpException e) {
            final String msg;
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                msg = "Input file does not exist";
                throw new NotFoundOkosynkIoException(msg + System.lineSeparator() + this.toString(), e);
            } else {
                msg = "Could not acquire an input stream from the sftp channel.";
                throw new IoOkosynkIoException(msg + System.lineSeparator() + this.toString(), e);
            }

        }
        final InputStreamReader inputStreamReader;
        try {
            inputStreamReader =
                    createInputStreamReader(okosynkConfiguration, sftpResourceContainer);
        } catch (UnsupportedEncodingException e) {
            final String msg =
                    "Could not acquire an InputStreamReader for the input stream. "
                            + System.lineSeparator()
                            + this.toString();
            throw new EncodingOkosynkIoException(msg, e);
        }
        assert
                (inputStreamReader != null)
                :
                "Programming error: (inputStreamReader == null) after leaving the while loop.";

        return new BufferedReader(inputStreamReader);
    }

    private InputStreamReader createInputStreamReader(
            final IOkosynkConfiguration okosynkConfiguration,
            final SftpResourceContainer resourceContainer)
            throws UnsupportedEncodingException {

        return new InputStreamReader(
                resourceContainer.getInputStream(),
                okosynkConfiguration.getFtpCharsetName(getBatchType(), "ISO8859_1")
        );
    }
}