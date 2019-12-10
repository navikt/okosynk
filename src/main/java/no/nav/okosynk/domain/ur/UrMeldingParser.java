package no.nav.okosynk.domain.ur;

import java.time.LocalDate;

import no.nav.okosynk.domain.AbstractMeldingParser;
import no.nav.okosynk.domain.DesimaltallParser;

public class UrMeldingParser extends AbstractMeldingParser {

  @Override
  public String parseGjelderId(String urMelding) {
    final String gjelderId = trimmedSubstring(urMelding, UrMeldingFormat.GJELDER_ID_KOLONNE_START,
        UrMeldingFormat.GJELDER_ID_KOLONNE_SLUTT);
    final int firstIndexAfter00 = 2;
    return gjelderId.startsWith("00") ? gjelderId.substring(firstIndexAfter00) : gjelderId;
  }

  @Override
  public String parseNyesteVentestatus(String urMelding) {
    return trimmedSubstring(urMelding, UrMeldingFormat.NYESTE_VENTESTATUS_KOLONNE_START,
        UrMeldingFormat.NYESTE_VENTESTATUS_KOLONNE_SLUTT);
  }

  @Override
  public String parseBrukerId(String urMelding) {
    return trimmedSubstring(urMelding, UrMeldingFormat.BRUKER_ID_KOLONNE_START,
        UrMeldingFormat.BRUKER_ID_KOLONNE_SLUTT);
  }

  @Override
  public double parseTotaltNettoBelop(String urMelding) {
    return DesimaltallParser.parse(
        trimmedSubstring(urMelding, UrMeldingFormat.TOTALT_NETTO_BELOP_KOLONNE_START,
            UrMeldingFormat.TOTALT_NETTO_BELOP_KOLONNE_SLUTT));
  }

  @Override
  public String parseBehandlendeEnhet(String urMelding) {
    return trimmedSubstring(urMelding, UrMeldingFormat.BEHANDLENDE_ENHET_KOLONNE_START,
        UrMeldingFormat.BEHANDLENDE_ENHET_KOLONNE_SLUTT);
  }

  @Override
  public LocalDate parseDatoForStatus(String urMelding) {
    return parseDatoMedKlokkeslett(
        trimmedSubstring(urMelding, UrMeldingFormat.DATO_FOR_STATUS_KOLONNE_START,
            UrMeldingFormat.DATO_FOR_STATUS_KOLONNE_SLUTT)).toLocalDate();
  }

  String parseGjelderIdType(String urMelding) {
    return trimmedSubstring(urMelding, UrMeldingFormat.GJELDER_ID_TYPE_KOLONNE_START,
        UrMeldingFormat.GJELDER_ID_TYPE_KOLONNE_SLUTT);
  }

  String parseOppdragsKode(String urMelding) {
    return trimmedSubstring(urMelding, UrMeldingFormat.OPPDRAGS_KODE_KOLONNE_START,
        UrMeldingFormat.OPPDRAGS_KODE_KOLONNE_SLUTT);
  }

  String parseKilde(String urMelding) {
    return trimmedSubstring(urMelding, UrMeldingFormat.KILDE_KOLONNE_START,
        UrMeldingFormat.KILDE_KOLONNE_SLUTT);
  }

  LocalDate parseDatoPostert(String urMelding) {
    return parseDatoUtenKlokkeslett(
        trimmedSubstring(urMelding, UrMeldingFormat.DATO_POSTERT_KOLONNE_START,
            UrMeldingFormat.DATO_POSTERT_KOLONNE_SLUTT));
  }

  String parseBilagsId(String urMelding) {
    return trimmedSubstring(urMelding, UrMeldingFormat.BILAGS_ID_KOLONNE_START,
        UrMeldingFormat.BILAGS_ID_KOLONNE_SLUTT);
  }

  String parseArsaksTekst(String urMelding) {
    return trimmedSubstring(urMelding, UrMeldingFormat.ARSAKS_TEKST_KOLONNE_START,
        UrMeldingFormat.ARSAKS_TEKST_KOLONNE_SLUTT);
  }

  String parseMottakerId(String urMelding) {
    return trimmedSubstring(urMelding, UrMeldingFormat.MOTTAKER_ID_KOLONNE_START,
        UrMeldingFormat.MOTTAKER_ID_KOLONNE_SLUTT);
  }
}
