package no.nav.okosynk.consumer.oppgave;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WSOppgaveTilOppgaveMapperTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String OPPGAVE_ID = "185587998";
    private static final String BRUKER_ID = "1234567891234";
    private static final String BRUKERTYPE_KODE = "PERSON";
    private static final String OPPGAVETYPE_KODE = "OKO_OS";
    private static final String FAGOMRADE_KODE = "BA";
    private static final String BEHANDLINGSTEMA = "ab";
    private static final String BEHANDLINGSTYPE = "ae";
    private static final String PRIORITET_KODE = "LAV_OKO";
    private static final String BESKRIVELSE = "AVVE;;   0kr;   beregningsdato/id:03.01.17/248912833;   periode:01.05.11-31.12.16;   feilkonto:J;   statusdato:06.01.17;   ;   UtbTil:05073500186;   X123456";
    private static final String ANSVARLIG_ENHET_ID = "4151";
    private final String MAPPE_ID = "EnMappeId";
    private final String ANSVARLIG_SAKSBEHANDLER_IDENT = "AnsvarligSaksbehandlerIdent";
    private static final boolean LEST = false;
    private static final int VERSJON = 1;
    private static final LocalDateTime SIST_ENDRET = LocalDateTime.of(1997, 2, 4, 7, 8, 36);

    private static final int ANT_DAGER_FRIST = 7;
    private static final LocalDate AKTIV_FRA_DATO = LocalDate.of(1997, 2, 2);
    private static final LocalDate AKTIV_TIL_DATO = AKTIV_FRA_DATO.plusDays(ANT_DAGER_FRIST);

    @Test
    void lagOppgaveSetterFelterTilRiktigVerdi() {

        enteringTestHeaderLogger.debug(null);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        OppgaveDTO oppgave = new OppgaveDTO();
        oppgave.setId(OPPGAVE_ID);
        oppgave.setAktoerId(BRUKER_ID);
        oppgave.setOppgavetype(OPPGAVETYPE_KODE);
        oppgave.setTema(FAGOMRADE_KODE);
        oppgave.setBehandlingstema(BEHANDLINGSTEMA);
        oppgave.setBehandlingstype(BEHANDLINGSTYPE);
        oppgave.setPrioritet(PRIORITET_KODE);
        oppgave.setBeskrivelse(BESKRIVELSE);
        oppgave.setAktivDato(AKTIV_FRA_DATO.format(formatter));
        oppgave.setFristFerdigstillelse(AKTIV_TIL_DATO.format(formatter));
        oppgave.setTildeltEnhetsnr(ANSVARLIG_ENHET_ID);
        oppgave.setVersjon(VERSJON);
        oppgave.setMappeId(MAPPE_ID);
        oppgave.setTilordnetRessurs(ANSVARLIG_SAKSBEHANDLER_IDENT);

        assertAll(
                () -> assertEquals(OPPGAVE_ID, oppgave.getId()),
                () -> assertEquals(BRUKER_ID, oppgave.getAktoerId()),
                () -> assertEquals(OPPGAVETYPE_KODE, oppgave.getOppgavetype()),
                () -> assertEquals(FAGOMRADE_KODE, oppgave.getTema()),
                () -> assertEquals(BEHANDLINGSTEMA, oppgave.getBehandlingstema()),
                () -> assertEquals(BEHANDLINGSTYPE, oppgave.getBehandlingstype()),
                () -> assertEquals(PRIORITET_KODE, oppgave.getPrioritet()),
                () -> assertEquals(BESKRIVELSE, oppgave.getBeskrivelse()),
                () -> assertEquals(AKTIV_FRA_DATO, oppgave.getAktivDato()),
                () -> assertEquals(AKTIV_TIL_DATO, oppgave.getFristFerdigstillelse()),
                () -> assertEquals(ANSVARLIG_ENHET_ID, oppgave.getTildeltEnhetsnr()),
                () -> assertEquals(VERSJON, oppgave.getVersjon()),
                () -> assertEquals(MAPPE_ID, oppgave.getMappeId()),
                () -> assertEquals(ANSVARLIG_SAKSBEHANDLER_IDENT, oppgave.getTilordnetRessurs())
        );
    }
}
