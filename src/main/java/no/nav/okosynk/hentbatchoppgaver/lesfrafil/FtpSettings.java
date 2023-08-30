package no.nav.okosynk.hentbatchoppgaver.lesfrafil;

import java.net.URI;

public record FtpSettings(URI ftpHostUrl, String ftpUser, String ftpPassword, String ftpCharsetName ) {
}
