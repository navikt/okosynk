package no.nav.okosynk.hentbatchoppgaver.model;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.MappingRegel;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.Mappingregelverk;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.UrBeskrivelseInfo;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseArsaksTekst;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseBehandlendeEnhet;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseBilagsId;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseBrukerId;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseDatoForStatus;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseDatoPostert;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseGjelderId;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseGjelderIdType;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseKilde;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseMottakerId;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseNyesteVentestatus;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseOppdragsKode;
import static no.nav.okosynk.hentbatchoppgaver.parselinje.UrMeldingParser.parseTotaltNettoBelop;

public class UrMelding extends Melding {

    public final String oppdragsKode;
    public final String gjelderIdType;
    public final LocalDate datoPostert;
    public final String kilde;
    public final String bilagsId;
    public final String arsaksTekst;
    public final String mottakerId;
    public final Optional<String> navEnhet;

    public UrMelding(final String urMelding) {

        super(parseBehandlendeEnhet(urMelding),
                parseBrukerId(urMelding),
                parseDatoForStatus(urMelding),
                parseGjelderId(urMelding),
                parseNyesteVentestatus(urMelding),
                parseTotaltNettoBelop(urMelding));

        this.oppdragsKode = parseOppdragsKode(urMelding);
        this.gjelderIdType = parseGjelderIdType(urMelding);
        this.datoPostert = parseDatoPostert(urMelding);
        this.kilde = parseKilde(urMelding);
        this.bilagsId = parseBilagsId(urMelding);
        this.arsaksTekst = parseArsaksTekst(urMelding);
        this.mottakerId = parseMottakerId(urMelding);
        this.navEnhet = navEnhet();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.gjelderId, this.gjelderIdType, this.oppdragsKode, this.datoPostert, this.navEnhet);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof UrMelding otherAsUrMelding)) {
            return false;
        }

        return this.gjelderId/*...*/.equals(otherAsUrMelding.gjelderId)
                && gjelderIdType/**/.equals(otherAsUrMelding.gjelderIdType)
                && oppdragsKode/*.*/.equals(otherAsUrMelding.oppdragsKode)
                && datoPostert/*..*/.equals(otherAsUrMelding.datoPostert)
                && navEnhet/*.....*/.equals(otherAsUrMelding.navEnhet);
    }

    @Override
    public String toString() {
        return String.join(FIELD_SEPARATOR, super.toString(),
                "oppdragsKode     : " + oppdragsKode,
                "gjelderIdType    : " + gjelderIdType,
                "datoPostert      : " + datoPostert,
                "kilde            : " + kilde,
                "bilagsId         : " + bilagsId,
                "arsaksTekst      : " + arsaksTekst,
                "mottakerId       : " + mottakerId);
    }

    Optional<String> navEnhet() {
        final Mappingregelverk urMappingRegelRepository = new Mappingregelverk(Constants.BATCH_TYPE.UR.getMappingRulesPropertiesFileName());
        return urMappingRegelRepository.finnRegel(String.join(",", oppdragsKode, behandlendeEnhet)).map(MappingRegel::ansvarligEnhetId);
    }

    public UrBeskrivelseInfo urBeskrivelseInfo() {
        return new UrBeskrivelseInfo(nyesteVentestatus,
                arsaksTekst,
                datoPostert,
                bilagsId,
                hentNettoBelopSomStreng(),
                datoForStatus,
                mottakerId,
                brukerId);
    }

    @Override
    public String regelnøkkel() {
        return String.join(",", oppdragsKode, behandlendeEnhet);
    }

}
