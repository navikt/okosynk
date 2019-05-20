package no.nav.okosynk.consumer.oppgavebehandling;

import no.nav.okosynk.consumer.util.DatoKonverterer;
import no.nav.okosynk.consumer.util.DatoKonverteringException;
import no.nav.okosynk.domain.Oppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgave;

public class OppgaveTilWSOpprettOppgaveMapper {

    public static WSOpprettOppgave lagWSOpprettOppgave(Oppgave oppgave) throws DatoKonverteringException {
        return new WSOpprettOppgave()
                .withBrukerId(oppgave.brukerId)
                .withBrukertypeKode(oppgave.brukertypeKode)
                .withOppgavetypeKode(oppgave.oppgavetypeKode)
                .withFagomradeKode(oppgave.fagomradeKode)
                .withUnderkategoriKode(oppgave.underkategoriKode)
                .withPrioritetKode(oppgave.prioritetKode)
                .withBeskrivelse(oppgave.beskrivelse)
                .withAktivFra(DatoKonverterer.konverterLocalDateTilXMLGregorianCalendar(oppgave.aktivFra))
                .withAktivTil(DatoKonverterer.konverterLocalDateTilXMLGregorianCalendar(oppgave.aktivTil))
                .withAnsvarligEnhetId(oppgave.ansvarligEnhetId)
                .withLest(oppgave.lest);
    }
}
