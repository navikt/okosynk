package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerRespons;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.AktoerUt;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.model.Oppgave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.nav.okosynk.hentbatchoppgaver.lagoppgave.AbstractOppgaveOppretter.GjelderIdFelt.FEIL;
import static no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class AbstractOppgaveOppretter<T extends AbstractMelding> implements Function<List<T>, Optional<Oppgave>> {
    private static final Logger log = LoggerFactory.getLogger(AbstractOppgaveOppretter.class);

    private final AbstractMappingRegelRepository<T> mappingRegelRepository;
    private final IAktoerClient aktoerClient;

    private static final Logger secureLog = LoggerFactory.getLogger("secureLog");

    protected AbstractOppgaveOppretter(
            final AbstractMappingRegelRepository<T> mappingRegelRepository,
            final IAktoerClient aktoerClient) {

        this.mappingRegelRepository = mappingRegelRepository;
        this.aktoerClient = aktoerClient;
    }

    public static String getRecordSeparator() {
        return System.getProperty("line.separator") + System.getProperty("line.separator");
    }

    @Override
    public Optional<Oppgave> apply(final List<T> meldinger) {
        meldinger.sort(getMeldingComparator());
        if (meldinger.isEmpty()) return Optional.empty();

        T melding = meldinger.get(0);
        MappingRegel mappingregel = this.mappingRegelRepository.finnRegel(melding).orElse(null);
        if (Objects.isNull(mappingregel)) return Optional.empty();

        return Optional.of(new Oppgave.OppgaveBuilder()
                .withGjelderIdResultat(this.bestemFeltOgGjelderId(melding.gjelderId, melding.utledGjelderIdType()))
                .withOppgavetypeKode(oppgaveTypeKode())
                .withFagomradeKode("OKO")
                .withBehandlingstema(
                        isNotBlank(mappingregel.behandlingstema) ? mappingregel.behandlingstema : null
                )
                .withBehandlingstype(
                        isNotBlank(mappingregel.behandlingstype) ? mappingregel.behandlingstype : null
                )
                .withPrioritetKode("LAV")
                .withBeskrivelse(lagSamletBeskrivelse(meldinger))
                .withAktivFra(LocalDate.now())
                .withAktivTil(LocalDate.now().plusDays(antallDagerFrist()))
                .withAnsvarligEnhetId(mappingregel.ansvarligEnhetId)
                .withLest(false)
                .withAntallMeldinger(meldinger.size())
                .build());
    }

    public enum GjelderIdFelt {
        BNR,
        AKTORID,
        SAMHANDLER,
        ORGANISASJON,
        FEIL,
        INGEN_GJELDERID
    }

    public static class GjelderIdResultat {
        private final GjelderIdFelt gjelderIdFelt;
        private final String gjelderId;

        public GjelderIdResultat(GjelderIdFelt gjelderIdFelt, String gjelderId) {
            this.gjelderIdFelt = gjelderIdFelt;
            this.gjelderId = gjelderId;
        }

        public GjelderIdFelt getGjelderIdFelt() {
            return gjelderIdFelt;
        }

        public String getGjelderId() {
            return gjelderId;
        }
    }

    private GjelderIdResultat bestemFeltOgGjelderId(String gjelderId, String gjelderIdType) {

        if (isBlank(gjelderId)) {
            return new GjelderIdResultat(GjelderIdFelt.INGEN_GJELDERID, null);
        }

        if (ORGANISASJON.equals(gjelderIdType)) {
            return new GjelderIdResultat(GjelderIdFelt.ORGANISASJON, gjelderId);
        }

        if (SAMHANDLER.equals(gjelderIdType)) {
            return new GjelderIdResultat(GjelderIdFelt.SAMHANDLER, gjelderId);
        }

        if (Objects.equals(gjelderIdType, PERSON)) {
            if (isBnr(gjelderId)) {
                return new GjelderIdResultat(GjelderIdFelt.BNR, gjelderId);
            } else {
                try {
                    final AktoerRespons aktoerRespons =
                            this.aktoerClient.hentGjeldendeAktoerId(gjelderId);
                    if (isNotBlank(aktoerRespons.getFeilmelding())) {
                        log.warn(
                                "Fikk feilmelding under henting av gjeldende aktørid for fnr/dnr angitt i inputfil, hopper over melding. - {}",
                                aktoerRespons.getFeilmelding());
                        secureLog.warn("Kunne ikke hente aktørid for: {}", gjelderId);
                        return new GjelderIdResultat(FEIL, null);
                    } else {
                        return new GjelderIdResultat(GjelderIdFelt.AKTORID, aktoerRespons.getAktoerId());
                    }
                } catch (Exception e) {
                    log.error("Ukjent feil ved konverterting av FNR -> AktoerId", e);
                    return new GjelderIdResultat(FEIL, null);
                }
            }
        }
        return new GjelderIdResultat(FEIL, null);
    }

    public String lagSamletBeskrivelse(final List<T> meldinger) {
        return meldinger.stream()
                .collect(Collectors.groupingBy(m -> m.nyesteVentestatus + "" + m.hashCode()))
                .values().stream()
                .map(l -> l.stream().sorted(this.getMeldingComparator()).collect(Collectors.toList()))
                .map(this::summerOgKonsolider)
                .collect(Collectors.joining(getRecordSeparator()))
                .replaceFirst(FELTSEPARATOR, FORSTE_FELTSEPARATOR);
    }

    protected abstract String summerOgKonsolider(List<T> ts);

    protected abstract String oppgaveTypeKode();

    protected abstract int antallDagerFrist();


    protected abstract Comparator<T> getMeldingComparator();

    private boolean isBnr(String aktorNr) {
        return AktoerUt.isBnr(aktorNr);
    }
}
