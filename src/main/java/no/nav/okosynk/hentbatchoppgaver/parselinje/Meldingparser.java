package no.nav.okosynk.hentbatchoppgaver.parselinje;

import java.time.LocalDate;

public interface Meldingparser {
    String parseGjelderId(String melding);
    LocalDate parseDatoForStatus(String melding);
    String parseNyesteVentestatus(String melding);
    String parseBrukerId(String melding);
    double parseTotaltNettoBelop(String melding);
    String parseBehandlendeEnhet(String melding);
}
