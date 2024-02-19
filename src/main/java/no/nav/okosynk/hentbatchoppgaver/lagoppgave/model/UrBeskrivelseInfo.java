package no.nav.okosynk.hentbatchoppgaver.lagoppgave.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import static no.nav.okosynk.hentbatchoppgaver.model.Melding.FELTSEPARATOR;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.Util.formatAsNorwegianDate;

public class UrBeskrivelseInfo implements BeskrivelseInfo {
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

    public BeskrivelseInfo pluss(UrBeskrivelseInfo b) {
        return new UrBeskrivelseInfo(
                this.nyesteVentestatus,
                this.arsaksTekst,
                this.datoPostert,
                this.bilagsId,
                new BigDecimal(this.hentNettoBelopSomStreng).add(new BigDecimal(b.hentNettoBelopSomStreng)).toString(),
                this.datoForStatus,
                this.mottakerId,
                this.brukerId);
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
