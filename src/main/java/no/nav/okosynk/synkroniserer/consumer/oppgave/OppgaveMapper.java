package no.nav.okosynk.synkroniserer.consumer.oppgave;

import no.nav.okosynk.synkroniserer.consumer.oppgave.json.FinnOppgaveResponseJson;
import no.nav.okosynk.synkroniserer.consumer.oppgave.json.IdentGruppeV2;
import no.nav.okosynk.synkroniserer.consumer.oppgave.json.IdentJson;
import no.nav.okosynk.synkroniserer.consumer.oppgave.json.PostOppgaveRequestJson;
import no.nav.okosynk.model.Oppgave;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class OppgaveMapper {
    private OppgaveMapper() {
    }

    static final String ENHET_ID_FOR_ANDRE_EKSTERNE = "9999";

    static PostOppgaveRequestJson mapFromFinnOppgaveResponseJsonToOppgave(final Oppgave oppgave) throws OppgaveMapperException_MoreThanOneActorType, OppgaveMapperException_AktivTilFraNull {

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        final PostOppgaveRequestJson postOppgaveRequestJson = new PostOppgaveRequestJson();

        final int numberOfActorTypes =
                Stream.of(oppgave.aktoerId(), oppgave.bnr(), oppgave.folkeregisterIdent(), oppgave.orgnr(), oppgave.samhandlernr())
                        .map(actor -> actor == null ? 0 : 1)
                        .reduce(0, Integer::sum);
        if (numberOfActorTypes > 1) {
            throw new OppgaveMapperException_MoreThanOneActorType();
        } else if (oppgave.aktivFra() == null || oppgave.aktivTil() == null) {
            throw new OppgaveMapperException_AktivTilFraNull();
        } else {
            postOppgaveRequestJson.setAktivDato(oppgave.aktivFra().format(formatter));
            postOppgaveRequestJson.setAktoerId(oppgave.aktoerId());
            postOppgaveRequestJson.setBehandlingstema(oppgave.behandlingstema());
            postOppgaveRequestJson.setBehandlingstype(oppgave.behandlingstype());
            postOppgaveRequestJson.setBeskrivelse(oppgave.beskrivelse());
            postOppgaveRequestJson.setBnr(oppgave.bnr());
            postOppgaveRequestJson.setFristFerdigstillelse(oppgave.aktivTil().format(formatter));
            postOppgaveRequestJson.setOppgavetype(oppgave.oppgavetypeKode());
            postOppgaveRequestJson.setOpprettetAvEnhetsnr(ENHET_ID_FOR_ANDRE_EKSTERNE);
            postOppgaveRequestJson.setOrgnr(oppgave.orgnr());
            postOppgaveRequestJson.setPrioritet(oppgave.prioritetKode());
            postOppgaveRequestJson.setSamhandlernr(oppgave.samhandlernr());
            postOppgaveRequestJson.setTema(oppgave.fagomradeKode());
            postOppgaveRequestJson.setTildeltEnhetsnr(oppgave.ansvarligEnhetId());
        }

        return postOppgaveRequestJson;
    }

    static Oppgave mapFromFinnOppgaveResponseJsonToOppgave(final FinnOppgaveResponseJson finnOppgaveResponseJson) {

        final Collection<IdentJson> identer = finnOppgaveResponseJson.getIdenter();
        final String folkeregisterIdent =
                (identer == null)
                        ?
                        null
                        :
                        identer
                                .stream()
                                .filter(identJsonInner -> ((identJsonInner != null) && IdentGruppeV2.FOLKEREGISTERIDENT.equals(identJsonInner.getGruppe())))
                                .findAny()
                                .orElse(new IdentJson(null, null))
                                .getIdent();

        return Oppgave.builder()
                .oppgaveId(finnOppgaveResponseJson.getId())
                .oppgavetypeKode(finnOppgaveResponseJson.getOppgavetype())
                .fagomradeKode(finnOppgaveResponseJson.getTema())
                .behandlingstema(finnOppgaveResponseJson.getBehandlingstema())
                .behandlingstype(finnOppgaveResponseJson.getBehandlingstype())
                .prioritetKode(finnOppgaveResponseJson.getPrioritet())
                .beskrivelse(finnOppgaveResponseJson.getBeskrivelse())
                .aktivFra(isNotBlank(finnOppgaveResponseJson.getAktivDato()) ? LocalDate.parse(finnOppgaveResponseJson.getAktivDato()) : null)
                .aktivTil(isNotBlank(finnOppgaveResponseJson.getFristFerdigstillelse()) ? LocalDate.parse(finnOppgaveResponseJson.getFristFerdigstillelse()) : null)
                .ansvarligEnhetId(finnOppgaveResponseJson.getTildeltEnhetsnr())
                .lest(finnOppgaveResponseJson.getStatus() != OppgaveStatus.OPPRETTET)
                .versjon(finnOppgaveResponseJson.getVersjon())
                .sistEndret(ofNullable(finnOppgaveResponseJson.getEndretTidspunkt()).orElse(finnOppgaveResponseJson.getOpprettetTidspunkt()))
                .mappeId(finnOppgaveResponseJson.getMappeId())
                .ansvarligSaksbehandlerIdent(finnOppgaveResponseJson.getTilordnetRessurs())
                .aktoerId(finnOppgaveResponseJson.getAktoerId())
                .folkeregisterIdent(folkeregisterIdent)
                .samhandlernr(finnOppgaveResponseJson.getSamhandlernr())
                .orgnr(finnOppgaveResponseJson.getOrgnr())
                .bnr(finnOppgaveResponseJson.getBnr())

                .build();
    }
}
