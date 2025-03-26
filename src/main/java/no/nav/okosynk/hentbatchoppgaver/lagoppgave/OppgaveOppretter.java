package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerRespons;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.AggregeringsKriterier;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.BeskrivelseInfo;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.MappingRegel;
import no.nav.okosynk.hentbatchoppgaver.model.Melding;
import no.nav.okosynk.model.GjelderIdType;
import no.nav.okosynk.model.Oppgave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.BeskrivelseInfo.sum;
import static no.nav.okosynk.hentbatchoppgaver.model.Melding.FELTSEPARATOR;
import static no.nav.okosynk.hentbatchoppgaver.model.Melding.FORSTE_FELTSEPARATOR;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class OppgaveOppretter {
    private static final Logger log = LoggerFactory.getLogger(OppgaveOppretter.class);
    private final IAktoerClient aktoerClient;
    private static final Logger secureLog = LoggerFactory.getLogger("secureLog");

    public OppgaveOppretter(final IAktoerClient aktoerClient) {
        this.aktoerClient = aktoerClient;
    }

    public static String getRecordSeparator() {
        return System.getProperty("line.separator") + System.getProperty("line.separator");
    }

    public Optional<Oppgave> opprettOppgave(final List<Melding> meldinger) {
        meldinger.sort(comparing(Melding::sammenligningsDato, reverseOrder()));
        if (meldinger.isEmpty()) return Optional.empty();

        Melding melding = meldinger.get(0);
        MappingRegel mappingregel = Mappingregelverk.finnRegel(melding.ruleKey()).orElse(null);
        if (Objects.isNull(mappingregel)) return Optional.empty();

        Oppgave.Builder oppgaveBuilder = Oppgave.builder();

        switch (GjelderIdType.fra(melding.getGjelderId())) {
            case BNR:
                oppgaveBuilder.bnr(melding.getGjelderId());
                break;
            case SAMHANDLER:
                oppgaveBuilder.samhandlernr(melding.getGjelderId());
                break;
            case ORGANISASJON:
                oppgaveBuilder.orgnr(melding.getGjelderId());
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
                        oppgaveBuilder.aktoerId(aktoerRespons.getAktoerId());
                    }
                } catch (Exception e) {
                    log.error("Ukjent feil ved konverterting av FNR -> AktoerId", e);
                }
                break;
        }

        return Optional.of(oppgaveBuilder
                .oppgavetypeKode(melding.batchType().getOppgaveType())
                .fagomradeKode("OKO")
                .behandlingstema(
                        isNotBlank(mappingregel.behandlingstema()) ? mappingregel.behandlingstema() : null
                )
                .behandlingstype(
                        isNotBlank(mappingregel.behandlingstype()) ? mappingregel.behandlingstype() : null
                )
                .prioritetKode("LAV")
                .beskrivelse(lagSamletBeskrivelse(meldinger))
                .aktivFra(LocalDate.now())
                .aktivTil(LocalDate.now().plusDays(melding.batchType().getAntallDagerFrist()))
                .ansvarligEnhetId(mappingregel.ansvarligEnhetId())
                .lest(false)
                .antallMeldinger(meldinger.size())
                .build());
    }

    public String lagSamletBeskrivelse(final List<Melding> meldinger) {
        return meldinger.stream()
                .collect(groupingBy(m -> m.getNyesteVentestatus() + m.hashCode(), LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(this::summerOgKonsolider)
                .collect(joining(getRecordSeparator()))
                .replaceFirst(FELTSEPARATOR, FORSTE_FELTSEPARATOR);
    }

    public String summerOgKonsolider(List<Melding> ts) {
        return ts.stream()
                .sorted(comparing(Melding::sammenligningsDato, reverseOrder()))
                .map(Melding::beskrivelseInfo)
                .reduce(sum)
                .map(BeskrivelseInfo::lagBeskrivelse).orElse("");
    }

    Predicate<Melding> meldingSkalBliOppgave() {
        return m -> Mappingregelverk.finnRegel(m.ruleKey()).isPresent();
    }

    Collection<List<Melding>> groupMeldingerSomSkalBliOppgaver(final List<Melding> ufiltrerteUrMeldinger) {
        Map<AggregeringsKriterier, List<Melding>> collect = ufiltrerteUrMeldinger
                .stream()
                .distinct()
                .filter(meldingSkalBliOppgave())
                .collect(groupingBy(AggregeringsKriterier::new));
        return collect
                .values();
    }

    public List<Oppgave> lagOppgaver(final List<Melding> meldinger) {
        Collection<List<Melding>> lists = groupMeldingerSomSkalBliOppgaver(meldinger);
        return lists
                .stream()
                .map(this::opprettOppgave)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

}
