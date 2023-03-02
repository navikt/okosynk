package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.IOkosynkConfiguration;
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

import static no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class AbstractOppgaveOppretter<T extends AbstractMelding>
        implements Function<List<T>, Optional<Oppgave>> {

    private static final Logger log = LoggerFactory.getLogger(AbstractOppgaveOppretter.class);
    private static final String fagomradeKode = "OKO";
    private static final String prioritetKode = "LAV";
    private static final String linjeSeparator = System.getProperty(
            "line.separator"); // Does not need to use IOkosynkConfiguration, because "line.separator" is part of the no.nav.okosynk.io.os/java ecosystem.
    private static final String recordSeparator = linjeSeparator + linjeSeparator;

    private final IOkosynkConfiguration okosynkConfiguration;
    private final AbstractMappingRegelRepository mappingRegelRepository;
    private final IAktoerClient aktoerClient;

    private static final Logger secureLog = LoggerFactory.getLogger("secureLog");

    protected AbstractOppgaveOppretter(
            final AbstractMappingRegelRepository mappingRegelRepository,
            final IAktoerClient aktoerClient,
            final IOkosynkConfiguration okosynkConfiguration) {

        this.okosynkConfiguration = okosynkConfiguration;
        this.mappingRegelRepository = mappingRegelRepository;
        this.aktoerClient = aktoerClient;
    }

    public static String getFagomradeKode() {
        return fagomradeKode;
    }

    public static String getPrioritetKode() {
        return prioritetKode;
    }

    public static String getLinjeSeparator() {
        return linjeSeparator;
    }

    public static String getRecordSeparator() {
        return recordSeparator;
    }

    @Override
    public Optional<Oppgave> apply(final List<T> meldinger) {

        meldinger.sort(getMeldingComparator());

        return meldinger
                .stream()
                .findAny()
                .map(
                        getMeldingstypeOppgaveFunction(meldinger)
                );
    }

    // Under construction
    private Function<T, Oppgave> getMeldingstypeOppgaveFunction(List<T> meldinger) {
        return (final T melding) -> {

            final LocalDate aktivFra = LocalDate.now();
            final Optional<MappingRegel> mappingRegel =
                    this.mappingRegelRepository.finnRegel(melding);

            Oppgave.OppgaveBuilder builder = new Oppgave.OppgaveBuilder();

            if (mappingRegel.isPresent()) {
                final String gjelderId = melding.gjelderId;
                if (isNotBlank(gjelderId)) {
                    final String type = melding.utledGjelderIdType();
                    if (Objects.equals(type, PERSON)) {
                        if (isBnr(gjelderId)) {
                            builder.withBnr(gjelderId);
                        } else {
                            try {
                                final AktoerRespons aktoerRespons =
                                        this.aktoerClient.hentGjeldendeAktoerId(gjelderId);
                                if (isNotBlank(aktoerRespons.getFeilmelding())) {
                                    log.warn(
                                            "Fikk feilmelding under henting av gjeldende aktørid for fnr/dnr angitt i inputfil, hopper over melding. - {}",
                                            aktoerRespons.getFeilmelding());
                                    secureLog.warn("Kunne ikke hente aktørid for: {}", gjelderId);
                                    return null;
                                } else {
                                    builder.withAktoerId(aktoerRespons.getAktoerId());
                                }
                            } catch (Exception e) {
                                log.error("Ukjent feil ved konverterting av FNR -> AktoerId", e);
                                return null;
                            }
                        }
                    } else if (Objects.equals(type, SAMHANDLER)) {
                        builder.withSamhandlernr(gjelderId);
                    } else if (Objects.equals(type, ORGANISASJON)) {
                        builder.withOrgnr(gjelderId);
                    }
                }

                return builder
                        .withOppgavetypeKode(oppgaveTypeKode())
                        .withFagomradeKode(getFagomradeKode())
                        .withBehandlingstema(
                                isNotBlank(mappingRegel.get().behandlingstema) ? mappingRegel.get().behandlingstema : null)
                        .withBehandlingstype(
                                isNotBlank(mappingRegel.get().behandlingstype) ? mappingRegel.get().behandlingstype : null)
                        .withPrioritetKode(getPrioritetKode())
                        .withBeskrivelse(lagSamletBeskrivelse(meldinger))
                        .withAktivFra(aktivFra)
                        .withAktivTil(aktivFra.plusDays(antallDagerFrist()))
                        .withAnsvarligEnhetId(mappingRegel.get().ansvarligEnhetId)
                        .withLest(false)
                        .withAntallMeldinger(meldinger.size())
                        .build();

            } else {
                return null;
            }
        };
    }

    public String lagSamletBeskrivelse(final List<T> meldinger) {
        return meldinger.stream()
                .map(AbstractMelding::lagBeskrivelse)
                .collect(Collectors.joining(getRecordSeparator()))
                .replaceFirst(getFeltSeparator(), getForsteFeltSeparator());
    }

    protected abstract String oppgaveTypeKode();

    protected abstract int antallDagerFrist();


    protected abstract Comparator<T> getMeldingComparator();

    private boolean isBnr(String aktorNr) {
        return AktoerUt.isBnr(aktorNr);
    }
}
