package no.nav.okosynk.domain.os;

import java.time.LocalDate;
import no.nav.okosynk.domain.AbstractMeldingParser;
import no.nav.okosynk.domain.DesimaltallParser;

public class OsMeldingParser extends AbstractMeldingParser {

  @Override
  public String parseGjelderId(String osMelding) {
    final String gjelderId = trimmedSubstring(osMelding, OsMeldingFormat.GJELDER_ID_START,
        OsMeldingFormat.GJELDER_ID_SLUTT);
    final int firstIndexAfter00 = 2;
    return gjelderId.startsWith("00") ? gjelderId.substring(firstIndexAfter00) : gjelderId;
  }

  @Override
  public String parseNyesteVentestatus(String osMelding) {
    return trimmedSubstring(osMelding, OsMeldingFormat.NYESTE_VENTESTATUS_START,
        OsMeldingFormat.NYESTE_VENTESTATUS_SLUTT);
  }

  @Override
  public String parseBrukerId(String osMelding) {
    return trimmedSubstring(osMelding, OsMeldingFormat.BRUKER_ID_START,
        OsMeldingFormat.BRUKER_ID_SLUTT);
  }

  @Override
  public double parseTotaltNettoBelop(String osMelding) {
    return DesimaltallParser.parse(
        trimmedSubstring(osMelding, OsMeldingFormat.TOTALT_NETTO_BELOP_START,
            OsMeldingFormat.TOTALT_NETTO_BELOP_SLUTT));
  }

  @Override
  public String parseBehandlendeEnhet(String osMelding) {
    return trimmedSubstring(osMelding, OsMeldingFormat.BEHANDLENDE_ENHET_START,
        OsMeldingFormat.BEHANDLENDE_ENHET_SLUTT);
  }

  @Override
  public LocalDate parseDatoForStatus(String osMelding) {
    return parseDatoUtenKlokkeslett(
        trimmedSubstring(osMelding, OsMeldingFormat.DATO_FOR_STATUS_START,
            OsMeldingFormat.DATO_FOR_STATUS_SLUTT));
  }

  String parseBeregningsId(String osMelding) {
    return trimmedSubstring(osMelding, OsMeldingFormat.BEREGNINGS_ID_START,
        OsMeldingFormat.BEREGNINGS_ID_SLUTT);
  }

  LocalDate parseBeregningsDato(String osMelding) {
    return parseDatoUtenKlokkeslett(
        trimmedSubstring(osMelding, OsMeldingFormat.BEREGNINGS_DATO_START,
            OsMeldingFormat.BEREGNINGS_DATO_SLUTT));
  }

  LocalDate parseForsteFomIPeriode(String osMelding) {
    return parseDatoUtenKlokkeslett(
        trimmedSubstring(osMelding, OsMeldingFormat.FORSTE_FOM_I_PERIODE_START,
            OsMeldingFormat.FORSTE_FOM_I_PERIODE_SLUTT));
  }

  LocalDate parseSisteTomIPeriode(String osMelding) {
    return parseDatoUtenKlokkeslett(
        trimmedSubstring(osMelding, OsMeldingFormat.SISTE_TOM_I_PERIODE_START,
            OsMeldingFormat.SISTE_TOM_I_PERIODE_SLUTT));
  }

  String parseFlaggFeilkonto(String osMelding) {
    return trimmedSubstring(osMelding, OsMeldingFormat.FLAGG_FEILKONTO_START,
        OsMeldingFormat.FLAGG_FEILKONTO_SLUTT);
  }

  String parseFaggruppe(String osMelding) {
    return trimmedSubstring(osMelding, OsMeldingFormat.FAGGRUPPE_START,
        OsMeldingFormat.FAGGRUPPE_SLUTT);
  }

  String parseUtbetalesTilId(String osMelding) {
    return trimmedSubstring(osMelding, OsMeldingFormat.UTBETALES_TIL_ID_START,
        OsMeldingFormat.UTBETALES_TIL_ID_SLUTT);
  }

  String parseEtteroppgjor(String osMelding) {
    return trimmedSubstring(osMelding, OsMeldingFormat.ETTEROPPGJOR_START,
        OsMeldingFormat.ETTEROPPGJOR_SLUTT);
  }
}