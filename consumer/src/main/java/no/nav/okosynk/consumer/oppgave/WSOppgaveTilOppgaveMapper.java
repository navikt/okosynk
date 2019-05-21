//package no.nav.okosynk.consumer.oppgave;
//
//import no.nav.okosynk.domain.Oppgave;
//import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
//
//public class WSOppgaveTilOppgaveMapper {
//
//    public static Oppgave lagOppgave(WSOppgave wsOppgave) {
//        return new Oppgave.OppgaveBuilder()
//                .withOppgaveId(wsOppgave.getOppgaveId())
//                .withBrukerId(wsOppgave.getGjelder() != null ? wsOppgave.getGjelder().getBrukerId() : null)
//                .withBrukertypeKode(wsOppgave.getGjelder() != null ? wsOppgave.getGjelder().getBrukertypeKode() : null)
//                .withOppgavetypeKode(wsOppgave.getOppgavetype() != null ? wsOppgave.getOppgavetype().getKode() : null)
//                .withFagomradeKode(wsOppgave.getFagomrade() != null ? wsOppgave.getFagomrade().getKode() : null)
//                .withUnderkategoriKode(wsOppgave.getUnderkategori() != null ? wsOppgave.getUnderkategori().getKode() : null)
//                .withPrioritetKode(wsOppgave.getPrioritet() != null ? wsOppgave.getPrioritet().getKode() : null)
//                .withBeskrivelse(wsOppgave.getBeskrivelse())
//                .withAktivFra(wsOppgave.getAktivFra() != null ? wsOppgave.getAktivFra().toGregorianCalendar().toZonedDateTime().toLocalDate() : null)
//                .withAktivTil(wsOppgave.getAktivTil() != null ? wsOppgave.getAktivTil().toGregorianCalendar().toZonedDateTime().toLocalDate() : null)
//                .withAnsvarligEnhetId(wsOppgave.getAnsvarligEnhetId())
//                .withLest(wsOppgave.isLest())
//                .withVersjon(wsOppgave.getVersjon())
//                .withSistEndret(wsOppgave.getSporing().getEndretInfo().getDato().toGregorianCalendar().toZonedDateTime().toLocalDateTime())
//                .withMappeId(wsOppgave.getMappe() != null ? wsOppgave.getMappe().getMappeId() : null)
//                .withAnsvarligSaksbehandlerIdent(wsOppgave.getAnsvarligId())
//                .build();
//    }
//}
