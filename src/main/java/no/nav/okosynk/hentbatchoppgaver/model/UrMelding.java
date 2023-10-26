package no.nav.okosynk.hentbatchoppgaver.model;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.UrMappingRegelRepository;
import no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class UrMelding extends AbstractMelding {

    public final String oppdragsKode;
    public final String gjelderIdType;
    public final LocalDate datoPostert;
    public final String kilde;
    public final String bilagsId;
    public final String arsaksTekst;
    public final String mottakerId;

    public UrMelding(final String urMelding) {

        super(urMelding, new UrMeldingParser());

        final UrMeldingParser parser = (UrMeldingParser)getParser();

        this.oppdragsKode = parser.parseOppdragsKode(urMelding);
        this.gjelderIdType = parser.parseGjelderIdType(urMelding);
        this.datoPostert = parser.parseDatoPostert(urMelding);
        this.kilde = parser.parseKilde(urMelding);
        this.bilagsId = parser.parseBilagsId(urMelding);
        this.arsaksTekst = parser.parseArsaksTekst(urMelding);
        this.mottakerId = parser.parseMottakerId(urMelding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.gjelderId, this.gjelderIdType, this.oppdragsKode, this.datoPostert, this.navEnhet());
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (!(other instanceof UrMelding otherAsUrMelding)) {
            return false;
        }

        return
            this.gjelderId.equals(otherAsUrMelding.gjelderId)
                &&
            this.gjelderIdType.equals(otherAsUrMelding.gjelderIdType)
            &&
            this.oppdragsKode.equals(otherAsUrMelding.oppdragsKode)
            &&
            this.datoPostert.equals(otherAsUrMelding.datoPostert)
            &&
            skalTilSammeNavEnhet(otherAsUrMelding)
            ;
    }

    @Override
    public String toString() {
        return super.toString() + FIELD_SEPARATOR +
           "oppdragsKode     : " + oppdragsKode + FIELD_SEPARATOR +
           "gjelderIdType    : " + gjelderIdType + FIELD_SEPARATOR +
           "datoPostert      : " + datoPostert + FIELD_SEPARATOR +
           "kilde            : " + kilde + FIELD_SEPARATOR +
           "bilagsId         : " + bilagsId + FIELD_SEPARATOR +
           "arsaksTekst      : " + arsaksTekst + FIELD_SEPARATOR +
           "mottakerId       : " + mottakerId;
    }

    private boolean skalTilSammeNavEnhet(final UrMelding other) {
        return navEnhet().equals(other.navEnhet());
    }

    Optional<String> navEnhet() {
        final UrMappingRegelRepository urMappingRegelRepository = new UrMappingRegelRepository();
        return urMappingRegelRepository.finnRegel(this).map(regel -> regel.ansvarligEnhetId);
    }

}
