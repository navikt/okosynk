package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import com.jcraft.jsch.*;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.AuthenticationOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.IoOkosynkIoException;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.NotFoundOkosynkIoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TinyFtpReader implements IMeldingLinjeFileReader {
    private static final Logger logger = LoggerFactory.getLogger(TinyFtpReader.class);
    private final FtpSettings ftpSettings;

    public TinyFtpReader(FtpSettings ftpSettings) {
        this.ftpSettings = ftpSettings;
    }

    @Override
    public List<String> read() throws AuthenticationOkosynkIoException, IoOkosynkIoException, NotFoundOkosynkIoException {
        logger.info("Leser fra {}", ftpSettings.ftpHostUrl());

        InputStream inputStream;
        try {
            inputStream = getChannelSftp().get(ftpSettings.ftpHostUrl().getPath());
        } catch (SftpException e) {
            throw new NotFoundOkosynkIoException("Fant ikke inputfil", e);
        } catch (JSchException e) {
            throw new AuthenticationOkosynkIoException("Autentiseringsfeil ved tilkobling til ftp", e);
        } catch (Exception e) {
            throw new IoOkosynkIoException("Feil ved lesing av fil", e);
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        List<String> list = bufferedReader.lines().toList();
        logger.info("Fant {} linjer", list.size());
        return list;
    }

    private ChannelSftp getChannelSftp() throws JSchException {
        JSch javaSecureChannel = new JSch();
        javaSecureChannel.addIdentity(ftpSettings.ftpUser(), ftpSettings.privateKey().getBytes(), null, null);
        javaSecureChannel.setKnownHosts(new ByteArrayInputStream(ftpSettings.hostKey().getBytes(StandardCharsets.UTF_8)));

        Session sftpSession = javaSecureChannel.getSession(ftpSettings.ftpUser(), ftpSettings.ftpHostUrl().getHost(), 22);
        sftpSession.setConfig("PreferredAuthentications", "publickey");
        sftpSession.connect();

        ChannelSftp sftpChannel = (ChannelSftp) sftpSession.openChannel("sftp");
        sftpChannel.connect();
        return sftpChannel;
    }

    @Override
    public boolean removeInputData() {
        boolean inputFileWasSuccessfullyRenamed;
        try {
            final ChannelSftp channelSftp = getChannelSftp();
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

    @Override
    public FileReaderStatus getStatus() {
        return FileReaderStatus.OK;
    }

}
