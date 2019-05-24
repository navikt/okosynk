package no.nav.okosynk.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public abstract class AbstractMelding {

    protected static final String FIELD_SEPARATOR = System.lineSeparator();

    public  static final char   TSS_PREFIX_1               = '8';

    private static final char   TSS_PREFIX_2               = '9';
    private static final int    ORGANISASJONSNUMMER_LENGDE = 9;
    public static final String ORGANISASJON               = "ORGANISASJON";
    public static final String PERSON                     = "PERSON";
    public static final String SAMHANDLER                 = "SAMHANDLER";

    public  final String    behandlendeEnhet;
    public  final double    totaltNettoBelop;
    public  final String    gjelderId;
    public  final LocalDate datoForStatus;
    public  final String    nyesteVentestatus;
    public  final String    brukerId; //Dette feltet er ikke det samme som Oppgave sin "brukerId"

    public MeldingParser getParser() {
        return parser;
    }

    private final MeldingParser parser;

    protected AbstractMelding(final String melding, final MeldingParser parser) {

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

    public String utledGjelderIdType(){

        if (gjelderTss()) {
            return SAMHANDLER;
        } else if (gjelderOrganisasjon()) {
            return ORGANISASJON;
        } else {
            return PERSON;
        }
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (!(other instanceof AbstractMelding)) {
            return false;
        }

        final AbstractMelding otherAsAbstractMelding = (AbstractMelding)other;

        return this.gjelderId.equals(otherAsAbstractMelding.gjelderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.gjelderId);
    }

    @Override
    public String toString() {

        final String s =
            super.toString() + FIELD_SEPARATOR +
                "totaltNettoBelop : " + totaltNettoBelop + FIELD_SEPARATOR +
                "gjelderId        : " + gjelderId + FIELD_SEPARATOR +
                "datoForStatus    : " + datoForStatus + FIELD_SEPARATOR +
                "nyesteVentestatus: " + nyesteVentestatus + FIELD_SEPARATOR +
                "brukerId         : " + brukerId + FIELD_SEPARATOR +
                "behandlendeEnhet : " + behandlendeEnhet;

        return s;
    }

    private boolean gjelderTss() {
        return
            gjelderIkkeOrganisasjon()
            &&
            (
                this.gjelderId.startsWith(String.valueOf(TSS_PREFIX_1))
                ||
                this.gjelderId.startsWith(String.valueOf(TSS_PREFIX_2))
            );
    }

    private boolean gjelderOrganisasjon() {
        return gjelderId.length() == ORGANISASJONSNUMMER_LENGDE;
    }

    private boolean gjelderIkkeOrganisasjon() {
        return !gjelderOrganisasjon();
    }
}
