package no.nav.okosynk.hentbatchoppgaver.model;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;


import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.UrMappingRegelRepository;
import no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser;

public class UrMelding extends AbstractMelding {

    public static final Comparator<UrMelding> DATO_POSTERT_COMPARATOR =
        comparing(urMelding -> urMelding.datoPostert, reverseOrder());

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
        return Objects.hash(super.hashCode(), this.gjelderIdType, this.oppdragsKode, this.datoPostert, new UrMappingRegelRepository().finnRegel(this));
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (!super.equals(other)) {
            return false;
        }

        if (!(other instanceof UrMelding)) {
            return false;
        }

        final UrMelding otherAsUrMelding = (UrMelding)other;

        return
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

        final UrMappingRegelRepository urMappingRegelRepository = new UrMappingRegelRepository();

        return urMappingRegelRepository
                .finnRegel(this)
                .map(
                    regel
                    ->
                    regel.ansvarligEnhetId
                )
                .equals(
                    urMappingRegelRepository
                        .finnRegel(other)
                        .map(
                            otherRegel
                            ->
                            otherRegel.ansvarligEnhetId
                        )
                );
    }

    @Override
    public String lagBeskrivelse() {
        return Stream.of(nyesteVentestatus,
                        arsaksTekst,
                        "postert/bilagsnummer:" + formatAsNorwegianDate(datoPostert) + "/" + bilagsId,
                        hentNettoBelopSomStreng() + "kr",
                        "statusdato:" + formatAsNorwegianDate(datoForStatus),
                        "UtbTil:" + mottakerId,
                        brukerId)
                .collect(Collectors.joining(getFeltSeparator()))
                .trim();
    }

}
