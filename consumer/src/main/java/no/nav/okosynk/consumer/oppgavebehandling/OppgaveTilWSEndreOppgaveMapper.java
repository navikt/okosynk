//package no.nav.okosynk.consumer.oppgavebehandling;
//
//import no.nav.okosynk.consumer.util.DatoKonverterer;
//import no.nav.okosynk.domain.Oppgave;
//import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSEndreOppgave;
//
//public class OppgaveTilWSEndreOppgaveMapper {
//
//    public static WSEndreOppgave lagWSEndreOppgave(Oppgave oppgave) {
//        return new WSEndreOppgave()
//                .withOppgaveId(oppgave.oppgaveId)
//                .withBrukerId(oppgave.brukerId)
//                .withBrukertypeKode(oppgave.brukertypeKode)
//                .withOppgavetypeKode(oppgave.oppgavetypeKode)
//                .withUnderkategoriKode(oppgave.underkategoriKode)
//                .withFagomradeKode(oppgave.fagomradeKode)
//                .withPrioritetKode(oppgave.prioritetKode)
//                .withAktivFra(DatoKonverterer.konverterLocalDateTilXMLGregorianCalendar(oppgave.aktivFra))
//                .withAktivTil(DatoKonverterer.konverterLocalDateTilXMLGregorianCalendar(oppgave.aktivTil))
//                .withAnsvarligEnhetId(oppgave.ansvarligEnhetId)
//                .withBeskrivelse(oppgave.beskrivelse)
//                .withLest(oppgave.lest)
//                .withVersjon(oppgave.versjon)
//                .withMappeId(oppgave.mappeId)
//                .withAnsvarligId(oppgave.ansvarligSaksbehandlerIdent);
//    }
//}
