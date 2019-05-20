package no.nav.okosynk.consumer.oppgavebehandling;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import no.nav.okosynk.domain.Oppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgave;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OppgaveTilWSOpprettOppgaveMapperTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final String BRUKER_ID = "10108000398";
    private static final String BRUKERTYPE_KODE = "PERSON";
    private static final String OPPGAVETYPE_KODE = "OKO_OS";
    private static final String FAGOMRADE_KODE = "BA";
    private static final String UNDERKATEGORI_KODE = "BA";
    private static final String PRIORITET_KODE = "LAV_OKO";
    private static final String BESKRIVELSE = "AVVE;;   0kr;   beregningsdato/id:03.01.17/248912833;   periode:01.05.11-31.12.16;   feilkonto:J;   statusdato:06.01.17;   ;   UtbTil:05073500186;   X123456";
    private static final int ANT_DAGER_FRIST = 7;
    private static final LocalDate AKTIV_FRA = LocalDate.of(1995, 1, 12);
    private static final LocalDate AKTIV_TIL = AKTIV_FRA.plusDays(ANT_DAGER_FRIST);
    private static final String ANSVARLIG_ENHET_ID = "4151";
    private static final boolean LEST = false;

    @Test
    void lagWSOpprettOppgaveSetterFelterTilRiktigVerdi() {

        enteringTestHeaderLogger.debug(null);

        Oppgave oppgave = new Oppgave.OppgaveBuilder()
                .withBrukerId(BRUKER_ID)
                .withBrukertypeKode(BRUKERTYPE_KODE)
                .withOppgavetypeKode(OPPGAVETYPE_KODE)
                .withFagomradeKode(FAGOMRADE_KODE)
                .withUnderkategoriKode(UNDERKATEGORI_KODE)
                .withPrioritetKode(PRIORITET_KODE)
                .withBeskrivelse(BESKRIVELSE)
                .withAktivFra(AKTIV_FRA)
                .withAktivTil(AKTIV_TIL)
                .withLest(LEST)
                .withAnsvarligEnhetId(ANSVARLIG_ENHET_ID)
                .build();

        WSOpprettOppgave wsOpprettOppgave = OppgaveTilWSOpprettOppgaveMapper.lagWSOpprettOppgave(oppgave);

        assertAll(
                () -> assertEquals(BRUKER_ID, wsOpprettOppgave.getBrukerId()),
                () -> assertEquals(BRUKERTYPE_KODE, wsOpprettOppgave.getBrukertypeKode()),
                () -> assertEquals(OPPGAVETYPE_KODE, wsOpprettOppgave.getOppgavetypeKode()),
                () -> assertEquals(FAGOMRADE_KODE, wsOpprettOppgave.getFagomradeKode()),
                () -> assertEquals(UNDERKATEGORI_KODE, wsOpprettOppgave.getUnderkategoriKode()),
                () -> assertEquals(PRIORITET_KODE, wsOpprettOppgave.getPrioritetKode()),
                () -> assertEquals(BESKRIVELSE, wsOpprettOppgave.getBeskrivelse()),
                () -> assertEquals(AKTIV_FRA, wsOpprettOppgave.getAktivFra().toGregorianCalendar().toZonedDateTime().toLocalDate()),
                () -> assertEquals(AKTIV_TIL, wsOpprettOppgave.getAktivTil().toGregorianCalendar().toZonedDateTime().toLocalDate()),
                () -> assertEquals(ANSVARLIG_ENHET_ID, wsOpprettOppgave.getAnsvarligEnhetId()),
                () -> assertEquals(LEST, wsOpprettOppgave.isLest())
        );
    }
}
