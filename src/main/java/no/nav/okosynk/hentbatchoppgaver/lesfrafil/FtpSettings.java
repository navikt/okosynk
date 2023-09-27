package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import java.net.URI;
import java.nio.charset.Charset;

public record FtpSettings(URI ftpHostUrl, String ftpUser, String privateKey, Charset ftpCharsetName, String hostKey) {
}
