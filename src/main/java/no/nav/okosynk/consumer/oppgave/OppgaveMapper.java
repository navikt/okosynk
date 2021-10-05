package no.nav.okosynk.consumer.oppgave;

import no.nav.okosynk.consumer.oppgave.json.FinnOppgaveResponseJson;
import no.nav.okosynk.consumer.oppgave.json.IdentGruppeV2;
import no.nav.okosynk.consumer.oppgave.json.IdentJson;
import no.nav.okosynk.consumer.oppgave.json.PostOppgaveRequestJson;
import no.nav.okosynk.domain.Oppgave;
import no.nav.okosynk.domain.util.AktoerUt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class OppgaveMapper {

    static final String ENHET_ID_FOR_ANDRE_EKSTERNE = "9999";
    private static final Logger logger = LoggerFactory.getLogger(OppgaveMapper.class);

    static PostOppgaveRequestJson mapFromFinnOppgaveResponseJsonToOppgave(final Oppgave oppgave) throws OppgaveMapperException_MoreThanOneActorType, OppgaveMapperException_AktivTilFraNull {

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        final PostOppgaveRequestJson postOppgaveRequestJson = new PostOppgaveRequestJson();

        final int numberOfActorTypes =
                Stream.of(oppgave.aktoerId, oppgave.bnr, oppgave.folkeregisterIdent, oppgave.orgnr, oppgave.samhandlernr)
                        .map(actor -> actor == null ? 0 : 1)
                        .reduce(0, (a, b) -> a + b);
        if (numberOfActorTypes > 1) {
            throw new OppgaveMapperException_MoreThanOneActorType();
        } else if (oppgave.aktivFra == null || oppgave.aktivTil == null) {
            throw new OppgaveMapperException_AktivTilFraNull();
        } else {
            postOppgaveRequestJson.setAktivDato(oppgave.aktivFra.format(formatter));
            postOppgaveRequestJson.setAktoerId(oppgave.aktoerId);
            postOppgaveRequestJson.setBehandlingstema(oppgave.behandlingstema);
            postOppgaveRequestJson.setBehandlingstype(oppgave.behandlingstype);
            postOppgaveRequestJson.setBeskrivelse(oppgave.beskrivelse);
            postOppgaveRequestJson.setBnr(oppgave.bnr);
            postOppgaveRequestJson.setFristFerdigstillelse(oppgave.aktivTil.format(formatter));
            postOppgaveRequestJson.setNpidOrFolkeregisterIdent(oppgave.folkeregisterIdent);
            postOppgaveRequestJson.setOppgavetype(oppgave.oppgavetypeKode);
            postOppgaveRequestJson.setOpprettetAvEnhetsnr(ENHET_ID_FOR_ANDRE_EKSTERNE);
            postOppgaveRequestJson.setOrgnr(oppgave.orgnr);
            postOppgaveRequestJson.setPrioritet(oppgave.prioritetKode);
            postOppgaveRequestJson.setSamhandlernr(oppgave.samhandlernr);
            postOppgaveRequestJson.setTema(oppgave.fagomradeKode);
            postOppgaveRequestJson.setTildeltEnhetsnr(oppgave.ansvarligEnhetId);
        }

        if (AktoerUt.isDnr(postOppgaveRequestJson.getNpidOrFolkeregisterIdent())) {
            logger.info("dnr found in PostOppgaveRequestJson: " + postOppgaveRequestJson.getNpidOrFolkeregisterIdent().substring(0, 6) + "*****");
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
                                .filter(identJson_inner -> ((identJson_inner != null) && IdentGruppeV2.FOLKEREGISTERIDENT.equals(identJson_inner.getGruppe())))
                                .findAny()
                                .orElse(new IdentJson(null, null))
                                .getIdent();
        final Oppgave oppgave =
                new Oppgave.OppgaveBuilder()
                        .withOppgaveId(finnOppgaveResponseJson.getId())
                        .withOppgavetypeKode(finnOppgaveResponseJson.getOppgavetype())
                        .withFagomradeKode(finnOppgaveResponseJson.getTema())
                        .withBehandlingstema(finnOppgaveResponseJson.getBehandlingstema())
                        .withBehandlingstype(finnOppgaveResponseJson.getBehandlingstype())
                        .withPrioritetKode(finnOppgaveResponseJson.getPrioritet())
                        .withBeskrivelse(finnOppgaveResponseJson.getBeskrivelse())
                        .withAktivFra(
                                isNotBlank(finnOppgaveResponseJson.getAktivDato()) ? LocalDate.parse(finnOppgaveResponseJson.getAktivDato()) : null)
                        .withAktivTil(isNotBlank(finnOppgaveResponseJson.getFristFerdigstillelse()) ?
                                LocalDate.parse(finnOppgaveResponseJson.getFristFerdigstillelse()) : null
                        )
                        .withAnsvarligEnhetId(finnOppgaveResponseJson.getTildeltEnhetsnr())
                        .withLest(finnOppgaveResponseJson.getStatus() != OppgaveStatus.OPPRETTET)
                        .withVersjon(finnOppgaveResponseJson.getVersjon())
                        .withSistEndret(
                                ofNullable(finnOppgaveResponseJson.getEndretTidspunkt()).orElse(finnOppgaveResponseJson.getOpprettetTidspunkt())
                        )
                        .withMappeId(finnOppgaveResponseJson.getMappeId())
                        .withAnsvarligSaksbehandlerIdent(finnOppgaveResponseJson.getTilordnetRessurs())
                        .withAktoerId(finnOppgaveResponseJson.getAktoerId())
                        .withFolkeregisterIdent(folkeregisterIdent)
                        .withSamhandlernr(finnOppgaveResponseJson.getSamhandlernr())
                        .withOrgnr(finnOppgaveResponseJson.getOrgnr())
                        .withBnr(finnOppgaveResponseJson.getBnr())

                        .build();

        if (AktoerUt.isDnr(oppgave.folkeregisterIdent)) {
            logger.info("dnr found in FinnOppgaveResponseJson: " + oppgave.folkeregisterIdent.substring(0, 6) + "*****");
        }
        return oppgave;
    }
}