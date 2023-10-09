package no.nav.okosynk.hentbatchoppgaver.model;

import lombok.Getter;
import no.nav.okosynk.hentbatchoppgaver.parselinje.AbstractMeldingParser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class AbstractMelding {
    protected static final String FIELD_SEPARATOR = System.lineSeparator();
    public static final String FORSTE_FELTSEPARATOR = ";;   ";

    public static String formatAsNorwegianDate(final LocalDate dato) {
        return DateTimeFormatter.ofPattern("dd.MM.yy").format(dato);
    }

    public final String behandlendeEnhet;
    public final double totaltNettoBelop;
    public final String gjelderId;
    public final LocalDate datoForStatus;
    public final String nyesteVentestatus;
    public final String brukerId; //Dette feltet er ikke det samme som Oppgave sin "brukerId"
    public static final String FELTSEPARATOR = ";   ";

    @Getter
    private final AbstractMeldingParser parser;

    protected AbstractMelding(final String melding, final AbstractMeldingParser parser) {

        this.parser = parser;

        this.behandlendeEnhet = parser.parseBehandlendeEnhet(melding);
        this.gjelderId = parser.parseGjelderId(melding);
        this.datoForStatus = parser.parseDatoForStatus(melding);
        this.nyesteVentestatus = parser.parseNyesteVentestatus(melding);
        this.brukerId = parser.parseBrukerId(melding);
        this.totaltNettoBelop = parser.parseTotaltNettoBelop(melding);
    }

    public String hentNettoBelopSomStreng() {
        final BigDecimal bd = BigDecimal.valueOf(this.totaltNettoBelop);
        return bd.toBigInteger().toString();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractMelding otherAsAbstractMelding)) {
            return false;
        }
        return this.gjelderId.equals(otherAsAbstractMelding.gjelderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.gjelderId);
    }

    @Override
    public String toString() {
        return String.join(FIELD_SEPARATOR,
                super.toString(),
                "totaltNettoBelop : " + totaltNettoBelop,
                "gjelderId        : " + gjelderId,
                "datoForStatus    : " + datoForStatus,
                "nyesteVentestatus: " + nyesteVentestatus,
                "brukerId         : " + brukerId,
                "behandlendeEnhet : " + behandlendeEnhet
        );
    }
}
