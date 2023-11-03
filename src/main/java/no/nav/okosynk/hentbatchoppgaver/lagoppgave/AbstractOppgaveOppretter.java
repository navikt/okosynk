package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerRespons;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.MappingRegel;
import no.nav.okosynk.hentbatchoppgaver.model.Melding;
import no.nav.okosynk.model.GjelderIdType;
import no.nav.okosynk.model.Oppgave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static no.nav.okosynk.hentbatchoppgaver.model.Melding.FELTSEPARATOR;
import static no.nav.okosynk.hentbatchoppgaver.model.Melding.FORSTE_FELTSEPARATOR;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class AbstractOppgaveOppretter<T extends Melding> {
    private static final Logger log = LoggerFactory.getLogger(AbstractOppgaveOppretter.class);

    private final Mappingregelverk mappingRegelRepository;
    private final IAktoerClient aktoerClient;
    private static final Logger secureLog = LoggerFactory.getLogger("secureLog");

    protected AbstractOppgaveOppretter(
            final Mappingregelverk mappingRegelRepository,
            final IAktoerClient aktoerClient) {

        this.mappingRegelRepository = mappingRegelRepository;
        this.aktoerClient = aktoerClient;
    }

    public static String getRecordSeparator() {
        return System.getProperty("line.separator") + System.getProperty("line.separator");
    }

    public Optional<Oppgave> opprettOppgave(final List<T> meldinger) {
        meldinger.sort(getMeldingComparator());
        if (meldinger.isEmpty()) return Optional.empty();

        T melding = meldinger.get(0);
        MappingRegel mappingregel = this.mappingRegelRepository.finnRegel(melding.regelnøkkel()).orElse(null);
        if (Objects.isNull(mappingregel)) return Optional.empty();

        Oppgave.OppgaveBuilder oppgaveBuilder = new Oppgave.OppgaveBuilder();

        switch (GjelderIdType.fra(melding.getGjelderId())) {
            case BNR:
                oppgaveBuilder.withBnr(melding.getGjelderId());
                break;
            case SAMHANDLER:
                oppgaveBuilder.withSamhandlernr(melding.getGjelderId());
                break;
            case ORGANISASJON:
                oppgaveBuilder.withOrgnr(melding.getGjelderId());
                break;
            case AKTORID:
                try {
                    final AktoerRespons aktoerRespons = this.aktoerClient.hentGjeldendeAktoerId(melding.getGjelderId());
                    if (isNotBlank(aktoerRespons.getFeilmelding())) {
                        log.warn(
                                "Fikk feilmelding under henting av gjeldende aktørid for fnr/dnr angitt i inputfil, hopper over melding. - {}",
                                aktoerRespons.getFeilmelding());
                        secureLog.warn("Kunne ikke hente aktørid for: {}", melding.getGjelderId());
                    } else {
                        oppgaveBuilder.withAktoerId(aktoerRespons.getAktoerId());
                    }
                } catch (Exception e) {
                    log.error("Ukjent feil ved konverterting av FNR -> AktoerId", e);
                }
                break;
        }

        return Optional.of(oppgaveBuilder
                .withOppgavetypeKode(oppgaveTypeKode())
                .withFagomradeKode("OKO")
                .withBehandlingstema(
                        isNotBlank(mappingregel.behandlingstema()) ? mappingregel.behandlingstema() : null
                )
                .withBehandlingstype(
                        isNotBlank(mappingregel.behandlingstype()) ? mappingregel.behandlingstype() : null
                )
                .withPrioritetKode("LAV")
                .withBeskrivelse(lagSamletBeskrivelse(meldinger))
                .withAktivFra(LocalDate.now())
                .withAktivTil(LocalDate.now().plusDays(antallDagerFrist()))
                .withAnsvarligEnhetId(mappingregel.ansvarligEnhetId())
                .withLest(false)
                .withAntallMeldinger(meldinger.size())
                .build());
    }

    public String lagSamletBeskrivelse(final List<T> meldinger) {
        return meldinger.stream()
                .collect(groupingBy(m -> m.getNyesteVentestatus() + m.hashCode(), LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(this::summerOgKonsolider)
                .collect(joining(getRecordSeparator()))
                .replaceFirst(FELTSEPARATOR, FORSTE_FELTSEPARATOR);
    }

    protected abstract String summerOgKonsolider(List<T> ts);

    protected abstract String oppgaveTypeKode();

    protected abstract int antallDagerFrist();

    protected abstract Comparator<T> getMeldingComparator();

}
