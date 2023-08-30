package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import com.jcraft.jsch.*;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MeldingLinjeSftpReader implements IMeldingLinjeFileReader {

    private static final Logger logger = LoggerFactory.getLogger(MeldingLinjeSftpReader.class);
    private static final String JSCH_CHANNEL_TYPE_SFTP = "sftp";
    private final JSch javaSecureChannel;
    private final Constants.BATCH_TYPE batchType;
    private final String fullyQualifiedInputFileName;
    private Status status;
    final FtpSettings ftpSettings;

    public MeldingLinjeSftpReader(
            final FtpSettings ftpSettings,
            final Constants.BATCH_TYPE batchType
    ) {
        this.status = IMeldingLinjeFileReader.Status.ERROR;

        if (ftpSettings == null) {
            throw new NullPointerException("okosynkConfiguration er null");
        }
        this.status = IMeldingLinjeFileReader.Status.OK;

        this.ftpSettings = ftpSettings;
        this.fullyQualifiedInputFileName = ftpSettings.ftpHostUrl().getPath().replace('\\', File.separatorChar);
        this.batchType = Objects.requireNonNull(batchType);
        this.javaSecureChannel = new JSch();

        String msg = "";

        final String ftpUser = ftpSettings.ftpUser();
        if (StringUtils.isBlank(ftpUser)) {
            msg += System.lineSeparator() + "ftpUser er null eller tom: + " + ftpUser;
            setStatusError();
        }
        final String ftpPassword = ftpSettings.ftpPassword();
        if (StringUtils.isBlank(ftpPassword)) {
            msg += System.lineSeparator() + "ftpPassword er null eller tom";
            setStatusError();
        }

        if (IMeldingLinjeFileReader.Status.ERROR.equals(getStatus())) {
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public Constants.BATCH_TYPE getBatchType() {
        return this.batchType;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    private void setStatusError() {
        this.status = IMeldingLinjeFileReader.Status.ERROR;
    }

    @Override
    public List<String> read()
            throws IoOkosynkIoException,
            ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            NotFoundOkosynkIoException{

        final List<String> meldinger;
        SftpResourceContainer resourceContainer = null;
        try {
            resourceContainer = new SftpResourceContainer(javaSecureChannel);
            meldinger = lesMeldingerFraFil(resourceContainer);
        } finally {
            if (resourceContainer != null) {
                resourceContainer.free();
            }
        }

        return meldinger;
    }

    @Override
    public boolean removeInputData() {

        SftpResourceContainer resourceContainer = null;
        boolean inputFileWasSuccessfullyRenamed;
        try {
            resourceContainer = new SftpResourceContainer(this.javaSecureChannel);
            establishSftpResources(resourceContainer);
            inputFileWasSuccessfullyRenamed = removeInputData(resourceContainer);
        } catch (Exception e) {
            logger.error("Exception received when trying to rename the input file.", e);
            resourceContainer = null;
            inputFileWasSuccessfullyRenamed = false;
        } finally {
            if (resourceContainer != null) {
                resourceContainer.free();
            }
        }
        return inputFileWasSuccessfullyRenamed;
    }

    @Override
    public String toString() {
        return String.format("""
                        Linjereader properties:
                        =======================
                        ftpHostUrl                 : %s
                        user                       : %s
                        fully qualified file name  : %s
                        """,
                ftpSettings.ftpHostUrl(),
                ftpSettings.ftpUser(),
                fullyQualifiedInputFileName
        );
    }

    List<String> lesMeldingerFraFil(final SftpResourceContainer resourceContainer)
            throws IoOkosynkIoException,
            ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            NotFoundOkosynkIoException {

        establishSftpResources(resourceContainer);

        final List<String> lines;
        try (BufferedReader bufferedReader = createBufferedReader(resourceContainer)) {
            lines = bufferedReader.lines().collect(Collectors.toList());
        } catch (IoOkosynkIoException | NotFoundOkosynkIoException okosynkIoException) {
            throw okosynkIoException;
        } catch (Exception e) {
            final String msg =
                    "Could not read lines from buffered reader. " + System.lineSeparator() + this;
            throw new IoOkosynkIoException(msg, e);
        }

        return lines;
    }

    private boolean removeInputData(
            final SftpResourceContainer resourceContainer
    ) {
        boolean inputFileWasSuccessfullyRenamed;
        try {
            final ChannelSftp channelSftp = resourceContainer.getSftpChannel();
            final String home = channelSftp.getHome();
            final String inputFilePath = ftpSettings.ftpHostUrl().getPath();
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd'T'HH.mm.ss");
            final LocalDateTime now = LocalDateTime.now();
            final String formatDateTime = now.format(formatter);
            final String toFileName = inputFilePath + "." + formatDateTime;
            logger.debug("About to rename the input file.");
            channelSftp.cd(home);
            channelSftp.rename(inputFilePath, toFileName);
            logger.info("The input file is successfully renamed.");
            inputFileWasSuccessfullyRenamed = true;
        } catch (Exception e) {
            logger.warn("""
                    Exception when trying to rename the (s)ftp input file. \
                    Rename will not be done, \
                    but the program will be exited. This implies that \
                    the input file will be re-read the next time the batch is run, \
                    unless it has been overwritten by a new one.
                    """, e);
            inputFileWasSuccessfullyRenamed = false;
        }

        return inputFileWasSuccessfullyRenamed;
    }

    private void establishSftpResources(final SftpResourceContainer sftpResourceContainer)
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException {

        final Session sftpSession;
        try {
            sftpSession = sftpResourceContainer.getJavaSecureChannel()
                    .getSession(
                            ftpSettings.ftpUser(),
                            ftpSettings.ftpHostUrl().getHost(),
                            ftpSettings.ftpHostUrl().getPort());
        } catch (JSchException e) {
            final String msg = "Could not establish an sftp session. " + System.lineSeparator() + this;
            setStatusError();
            throw new ConfigureOrInitializeOkosynkIoException(msg, e);
        }
        sftpResourceContainer.setSftpSession(sftpSession);

        sftpResourceContainer.getSftpSession().setConfig("StrictHostKeyChecking", "no");

        sftpResourceContainer.getSftpSession().setPassword(ftpSettings.ftpPassword());
        try {
            sftpResourceContainer.getSftpSession().connect();
        } catch (JSchException e) {
            final String msg = "Could not connect SFTP session. " + System.lineSeparator() + this;
            setStatusError();
            if ("Auth fail".equals(e.getMessage())) {
                throw new AuthenticationOkosynkIoException(msg, e);
            } else {
                throw new ConfigureOrInitializeOkosynkIoException(msg, e);
            }
        }

        try {
            final Channel channel = sftpResourceContainer.getSftpSession().openChannel(JSCH_CHANNEL_TYPE_SFTP);
            sftpResourceContainer.setSftpChannel((ChannelSftp) channel);
        } catch (JSchException e) {
            final String msg = "Could not run openChannel on SFTP session. " + System.lineSeparator() + this;
            setStatusError();
            throw new IoOkosynkIoException(msg, e);
        }

        try {
            sftpResourceContainer.getSftpChannel().connect();
        } catch (JSchException e) {
            final String msg = "Could not connect to channel. " + System.lineSeparator() + this;
            setStatusError();
            throw new IoOkosynkIoException(msg, e);
        }
    }

    private BufferedReader createBufferedReader(final SftpResourceContainer sftpResourceContainer)
            throws EncodingOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException {
        try {
            logger.debug("About to acquire an InputStream from the batch input file...");
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
                throw new NotFoundOkosynkIoException(msg + System.lineSeparator() + this, e);
            } else {
                msg = "Could not acquire an input stream from the sftp channel.";
                throw new IoOkosynkIoException(msg + System.lineSeparator() + this, e);
            }

        }
        final InputStreamReader inputStreamReader;
        try {

            inputStreamReader = new InputStreamReader(
                    sftpResourceContainer.getInputStream(),
                    ftpSettings.ftpCharsetName()
            );
        } catch (UnsupportedEncodingException e) {
            final String msg =
                    "Could not acquire an InputStreamReader for the input stream. "
                            + System.lineSeparator()
                            + this;
            throw new EncodingOkosynkIoException(msg, e);
        }

        return new BufferedReader(inputStreamReader);
    }

}
