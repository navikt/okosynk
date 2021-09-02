package no.nav.okosynk.consumer.oppgave;

import no.nav.okosynk.domain.Oppgave;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class OppgaveMapper {

    static final String ENHET_ID_FOR_ANDRE_EKSTERNE = "9999";

    static OppgaveDto map(final Oppgave oppgave) throws OppgaveMapperException_MoreThanOneActorType, OppgaveMapperException_AktivTilFraNull {

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        final OppgaveDto oppgaveDto = new OppgaveDto();

        final int numberOfActorTypes =
                Stream.of(oppgave.aktoerId, oppgave.bnr, oppgave.navPersonIdent, oppgave.orgnr, oppgave.samhandlernr)
                        .map(actor -> actor == null ? 0 : 1)
                        .reduce(0, (a, b) -> a + b);
        if (numberOfActorTypes > 1) {
            throw new OppgaveMapperException_MoreThanOneActorType();
        } else if (oppgave.aktivFra == null || oppgave.aktivTil == null) {
            throw new OppgaveMapperException_AktivTilFraNull();
        } else {
            oppgaveDto.setAktivDato(oppgave.aktivFra.format(formatter));
            oppgaveDto.setAktoerId(oppgave.aktoerId);
            oppgaveDto.setBehandlingstema(oppgave.behandlingstema);
            oppgaveDto.setBehandlingstype(oppgave.behandlingstype);
            oppgaveDto.setBeskrivelse(oppgave.beskrivelse);
            oppgaveDto.setBnr(oppgave.bnr);
            oppgaveDto.setFristFerdigstillelse(oppgave.aktivTil.format(formatter));
            oppgaveDto.setNavPersonIdent(oppgave.navPersonIdent);
            oppgaveDto.setOppgavetype(oppgave.oppgavetypeKode);
            oppgaveDto.setOpprettetAvEnhetsnr(ENHET_ID_FOR_ANDRE_EKSTERNE);
            oppgaveDto.setOrgnr(oppgave.orgnr);
            oppgaveDto.setPrioritet(oppgave.prioritetKode);
            oppgaveDto.setSamhandlernr(oppgave.samhandlernr);
            oppgaveDto.setTema(oppgave.fagomradeKode);
            oppgaveDto.setTildeltEnhetsnr(oppgave.ansvarligEnhetId);
        }

        return oppgaveDto;
    }

    static Oppgave map(final OppgaveDto oppgaveDto) {
        return new Oppgave.OppgaveBuilder()
                .withOppgaveId(oppgaveDto.getId())
                .withAktoerId(oppgaveDto.getAktoerId())
                .withSamhandlernr(oppgaveDto.getSamhandlernr())
                .withOrgnr(oppgaveDto.getOrgnr())
                .withBnr(oppgaveDto.getBnr())
                .withOppgavetypeKode(oppgaveDto.getOppgavetype())
                .withFagomradeKode(oppgaveDto.getTema())
                .withBehandlingstema(oppgaveDto.getBehandlingstema())
                .withBehandlingstype(oppgaveDto.getBehandlingstype())
                .withPrioritetKode(oppgaveDto.getPrioritet())
                .withBeskrivelse(oppgaveDto.getBeskrivelse())
                .withAktivFra(
                        isNotBlank(oppgaveDto.getAktivDato()) ? LocalDate.parse(oppgaveDto.getAktivDato())
                                : null)
                .withAktivTil(isNotBlank(oppgaveDto.getFristFerdigstillelse()) ? LocalDate
                        .parse(oppgaveDto.getFristFerdigstillelse()) : null)
                .withAnsvarligEnhetId(oppgaveDto.getTildeltEnhetsnr())
                .withLest(oppgaveDto.getStatus() != OppgaveStatus.OPPRETTET)
                .withVersjon(oppgaveDto.getVersjon())
                .withSistEndret(
                        ofNullable(oppgaveDto.getEndretTidspunkt()).orElse(oppgaveDto.getOpprettetTidspunkt()))
                .withMappeId(oppgaveDto.getMappeId())
                .withAnsvarligSaksbehandlerIdent(oppgaveDto.getTilordnetRessurs())
                .build();
    }
}