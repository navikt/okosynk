//package no.nav.okosynk.consumer.oppgave;
//
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.GregorianCalendar;
//import javax.xml.datatype.DatatypeConfigurationException;
//import javax.xml.datatype.DatatypeFactory;
//import javax.xml.datatype.XMLGregorianCalendar;
//import no.nav.okosynk.consumer.util.DatoKonverterer;
//import no.nav.okosynk.domain.Oppgave;
//import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.*;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//class WSOppgaveTilOppgaveMapperTest {
//
//    private static final Logger enteringTestHeaderLogger =
//        LoggerFactory.getLogger("EnteringTestHeader");
//
//    private static final String OPPGAVE_ID = "185587998";
//    private static final String BRUKER_ID = "10108000398";
//    private static final String BRUKERTYPE_KODE = "PERSON";
//    private static final String OPPGAVETYPE_KODE = "OKO_OS";
//    private static final String FAGOMRADE_KODE = "BA";
//    private static final String UNDERKATEGORI_KODE = "BA";
//    private static final String PRIORITET_KODE = "LAV_OKO";
//    private static final String BESKRIVELSE = "AVVE;;   0kr;   beregningsdato/id:03.01.17/248912833;   periode:01.05.11-31.12.16;   feilkonto:J;   statusdato:06.01.17;   ;   UtbTil:05073500186;   X123456";
//    private static final String ANSVARLIG_ENHET_ID = "4151";
//    private final String MAPPE_ID = "EnMappeId";
//    private final String ANSVARLIG_SAKSBEHANDLER_IDENT = "AnsvarligSaksbehandlerIdent";
//    private static final boolean LEST = false;
//    private static final int VERSJON = 1;
//    private static final LocalDateTime SIST_ENDRET = LocalDateTime.of(1997, 2, 4, 7, 8, 36);
//
//    private static final int ANT_DAGER_FRIST = 7;
//    private static final LocalDate AKTIV_FRA_DATO = LocalDate.of(1997, 2, 2);
//    private static final LocalDate AKTIV_TIL_DATO = AKTIV_FRA_DATO.plusDays(ANT_DAGER_FRIST);
//
//    private static XMLGregorianCalendar aktivFraXMLGregorianCalendar;
//    private static XMLGregorianCalendar aktivTilXMLGregorianCalendar;
//
//    @BeforeAll
//    static void settOppXMLGregorianCalendarFelter() throws DatatypeConfigurationException {
//        GregorianCalendar aktivFraGregorianCalendar = GregorianCalendar.from(AKTIV_FRA_DATO.atStartOfDay(ZoneId.systemDefault()));
//        GregorianCalendar aktivTilGregorianCalendar = GregorianCalendar.from(AKTIV_TIL_DATO.atStartOfDay(ZoneId.systemDefault()));
//
//        aktivFraXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(aktivFraGregorianCalendar);
//        aktivTilXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(aktivTilGregorianCalendar);
//    }
//
//    @Test
//    void lagOppgaveSetterFelterTilRiktigVerdi() {
//
//        enteringTestHeaderLogger.debug(null);
//
//        WSOppgave wsOppgave = new WSOppgave()
//                .withOppgaveId(OPPGAVE_ID)
//                .withGjelder(new WSBruker()
//                        .withBrukerId(BRUKER_ID)
//                        .withBrukertypeKode(BRUKERTYPE_KODE))
//                .withOppgavetype(new WSOppgavetype().withKode(OPPGAVETYPE_KODE))
//                .withFagomrade(new WSFagomrade().withKode(FAGOMRADE_KODE))
//                .withUnderkategori(new WSUnderkategori().withKode(UNDERKATEGORI_KODE))
//                .withPrioritet(new WSPrioritet().withKode(PRIORITET_KODE))
//                .withBeskrivelse(BESKRIVELSE)
//                .withAktivFra(aktivFraXMLGregorianCalendar)
//                .withAktivTil(aktivTilXMLGregorianCalendar)
//                .withAnsvarligEnhetId(ANSVARLIG_ENHET_ID)
//                .withLest(LEST)
//                .withVersjon(VERSJON)
//                .withSporing(new WSSporing().withEndretInfo(new WSSporingsdetalj().withDato(DatoKonverterer.konverterLocalDateTimeTilXMLGregorianCalendar(SIST_ENDRET))))
//                .withMappe(new WSMappe().withMappeId(MAPPE_ID))
//                .withAnsvarligId(ANSVARLIG_SAKSBEHANDLER_IDENT);
//
//        Oppgave oppgave = WSOppgaveTilOppgaveMapper.lagOppgave(wsOppgave);
//
//        assertAll(
//                () -> assertEquals(OPPGAVE_ID, oppgave.oppgaveId),
//                () -> assertEquals(BRUKER_ID, oppgave.brukerId),
//                () -> assertEquals(BRUKERTYPE_KODE, oppgave.brukertypeKode),
//                () -> assertEquals(OPPGAVETYPE_KODE, oppgave.oppgavetypeKode),
//                () -> assertEquals(FAGOMRADE_KODE, oppgave.fagomradeKode),
//                () -> assertEquals(UNDERKATEGORI_KODE, oppgave.underkategoriKode),
//                () -> assertEquals(PRIORITET_KODE, oppgave.prioritetKode),
//                () -> assertEquals(BESKRIVELSE, oppgave.beskrivelse),
//                () -> assertEquals(AKTIV_FRA_DATO, oppgave.aktivFra),
//                () -> assertEquals(AKTIV_TIL_DATO, oppgave.aktivTil),
//                () -> assertEquals(ANSVARLIG_ENHET_ID, oppgave.ansvarligEnhetId),
//                () -> assertEquals(LEST, oppgave.lest),
//                () -> assertEquals(VERSJON, oppgave.versjon),
//                () -> assertEquals(SIST_ENDRET, oppgave.sistEndret),
//                () -> assertEquals(MAPPE_ID, oppgave.mappeId),
//                () -> assertEquals(ANSVARLIG_SAKSBEHANDLER_IDENT, oppgave.ansvarligSaksbehandlerIdent)
//        );
//    }
//}
