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

import static java.util.Objects.requireNonNull;
import static no.nav.okosynk.hentbatchoppgaver.lesfrafil.FileReaderStatus.ERROR;
import static no.nav.okosynk.hentbatchoppgaver.lesfrafil.FileReaderStatus.OK;

public class MeldingLinjeSftpReader implements IMeldingLinjeFileReader {

    private static final Logger logger = LoggerFactory.getLogger(MeldingLinjeSftpReader.class);
    private static final String JSCH_CHANNEL_TYPE_SFTP = "sftp";
    private final JSch javaSecureChannel;
    private final Constants.BATCH_TYPE batchType;
    private final String fullyQualifiedInputFileName;
    private FileReaderStatus status;
    final FtpSettings ftpSettings;
    Session sftpSession = null;
    ChannelSftp sftpChannel = null;
    InputStream inputStream = null;

    public MeldingLinjeSftpReader(
            final FtpSettings ftpSettings,
            final Constants.BATCH_TYPE batchType
    ) {
        status = ERROR;

        if (ftpSettings == null) {
            throw new NullPointerException("okosynkConfiguration er null");
        }
        status = OK;

        this.ftpSettings = ftpSettings;
        this.fullyQualifiedInputFileName = ftpSettings.ftpHostUrl().getPath().replace('\\', File.separatorChar);
        this.batchType = requireNonNull(batchType);
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

        if (ERROR.equals(getStatus())) {
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public Constants.BATCH_TYPE getBatchType() {
        return this.batchType;
    }

    @Override
    public FileReaderStatus getStatus() {
        return status;
    }

    private void setStatusError() {
        this.status = ERROR;
    }

    @Override
    public List<String> read()
            throws NotFoundOkosynkIoException,
            IoOkosynkIoException,
            ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException {

        final List<String> meldinger;

        try {
            meldinger = lesMeldingerFraFil(javaSecureChannel);
        } finally {
            free();
        }

        return meldinger;
    }

    private void free() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }

            if (sftpSession != null) {
                sftpSession.disconnect();
            }

            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
        } catch (IOException e) {
            logger.error("Could not clear file reader resources", e);
        }
    }

    @Override
    public boolean removeInputData() {

        boolean inputFileWasSuccessfullyRenamed;
        try {
            establishSftpResources(javaSecureChannel);
            final ChannelSftp channelSftp = sftpChannel;
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
            logger.error("Exception received when trying to rename the input file.", e);
            inputFileWasSuccessfullyRenamed = false;
        } finally {
            free();
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

    List<String> lesMeldingerFraFil(final JSch javaSecureChannel)
            throws NotFoundOkosynkIoException,
            IoOkosynkIoException,
            ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException {

        establishSftpResources(javaSecureChannel);

        final List<String> lines;
        try (BufferedReader bufferedReader = createBufferedReader()) {
            lines = bufferedReader.lines().toList();
        } catch (NotFoundOkosynkIoException notFoundException) {
            throw notFoundException;
        } catch (Exception e) {
            final String msg =
                    "Could not read lines from buffered reader. " + System.lineSeparator() + this;
            throw new IoOkosynkIoException(msg, e);
        }

        return lines;
    }

    private void establishSftpResources(final JSch javaSecureChannel)
            throws ConfigureOrInitializeOkosynkIoException,
            AuthenticationOkosynkIoException,
            IoOkosynkIoException {
        try {
            sftpSession = javaSecureChannel
                    .getSession(
                            ftpSettings.ftpUser(),
                            ftpSettings.ftpHostUrl().getHost(),
                            ftpSettings.ftpHostUrl().getPort());
        } catch (JSchException e) {
            final String msg = "Could not establish an sftp session. " + System.lineSeparator() + this;
            setStatusError();
            throw new ConfigureOrInitializeOkosynkIoException(msg, e);
        }
        sftpSession.setConfig("StrictHostKeyChecking", "no");
        sftpSession.setPassword(ftpSettings.ftpPassword());

        try {
            sftpSession.connect();
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
            sftpChannel = (ChannelSftp) sftpSession.openChannel(JSCH_CHANNEL_TYPE_SFTP);
        } catch (JSchException e) {
            final String msg = "Could not run openChannel on SFTP session. " + System.lineSeparator() + this;
            setStatusError();
            throw new IoOkosynkIoException(msg, e);
        }

        try {
            sftpChannel.connect();
        } catch (JSchException e) {
            final String msg = "Could not connect to channel. " + System.lineSeparator() + this;
            setStatusError();
            throw new IoOkosynkIoException(msg, e);
        }
    }

    private BufferedReader createBufferedReader()
            throws EncodingOkosynkIoException,
            IoOkosynkIoException,
            NotFoundOkosynkIoException {
        try {
            logger.debug("About to acquire an InputStream from the batch input file...");
            inputStream = sftpChannel.get(fullyQualifiedInputFileName);
            if (inputStream == null) {
                final String msg =
                        "InputStream instance acquired from ChannelSftp is null, "
                                + "which most probably is caused by the file not existing.";
                throw new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, msg);
            }
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
        return new BufferedReader(setupInputStreamReader());
    }

    private InputStreamReader setupInputStreamReader() throws EncodingOkosynkIoException {
        final InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(
                    inputStream,
                    ftpSettings.ftpCharsetName()
            );
        } catch (UnsupportedEncodingException e) {
            final String msg =
                    "Could not acquire an InputStreamReader for the input stream. "
                            + System.lineSeparator()
                            + this;
            throw new EncodingOkosynkIoException(msg, e);
        }
        return inputStreamReader;
    }
}
