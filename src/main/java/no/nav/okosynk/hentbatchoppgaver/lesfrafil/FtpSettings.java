package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import java.net.URI;
import java.nio.charset.Charset;

public record FtpSettings(URI ftpHostUrl, String ftpUser, String ftpPassword, String privateKey, Charset ftpCharsetName) {
}
