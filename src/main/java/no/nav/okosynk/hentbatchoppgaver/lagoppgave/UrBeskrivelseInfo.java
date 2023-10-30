package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import java.math.BigDecimal;
import java.time.LocalDate;

import static no.nav.okosynk.hentbatchoppgaver.model.Melding.FELTSEPARATOR;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.Util.formatAsNorwegianDate;

public class UrBeskrivelseInfo {
    private final String nyesteVentestatus;
    private final String arsaksTekst;
    private final LocalDate datoPostert;
    private final String bilagsId;
    private final String hentNettoBelopSomStreng;
    private final LocalDate datoForStatus;
    private final String mottakerId;
    private final String brukerId;

    public UrBeskrivelseInfo(String nyesteVentestatus, String arsaksTekst, LocalDate datoPostert, String bilagsId, String hentNettoBelopSomStreng, LocalDate datoForStatus, String mottakerId, String brukerId) {
        this.nyesteVentestatus = nyesteVentestatus;
        this.arsaksTekst = arsaksTekst;
        this.datoPostert = datoPostert;
        this.bilagsId = bilagsId;
        this.hentNettoBelopSomStreng = hentNettoBelopSomStreng;
        this.datoForStatus = datoForStatus;
        this.mottakerId = mottakerId;
        this.brukerId = brukerId;
    }

    public static UrBeskrivelseInfo pluss(UrBeskrivelseInfo a, UrBeskrivelseInfo b) {
        BigDecimal belopA = new BigDecimal(a.hentNettoBelopSomStreng);
        BigDecimal belopB = new BigDecimal(b.hentNettoBelopSomStreng);
        return new UrBeskrivelseInfo(
                a.nyesteVentestatus,
                a.arsaksTekst,
                a.datoPostert,
                a.bilagsId,
                belopA.add(belopB).toString(),
                a.datoForStatus,
                a.mottakerId,
                a.brukerId);
    }

    public String lagBeskrivelse() {
        return String.join(FELTSEPARATOR, nyesteVentestatus,
                arsaksTekst,
                "postert/bilagsnummer:" + formatAsNorwegianDate(datoPostert) + "/" + bilagsId,
                hentNettoBelopSomStreng + "kr",
                "statusdato:" + formatAsNorwegianDate(datoForStatus),
                "UtbTil:" + mottakerId,
                brukerId
        ).trim();
    }

}
