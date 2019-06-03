package no.nav.okosynk.domain;

import no.nav.okosynk.consumer.aktoer.AktoerRespons;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.nav.okosynk.domain.AbstractMelding.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substring;

public abstract class AbstractOppgaveOppretter<MELDINGSTYPE extends AbstractMelding>
    implements Function<List<MELDINGSTYPE>, Optional<Oppgave>> {
    private static final Logger log = LoggerFactory.getLogger(AbstractOppgaveOppretter.class);
    private static final String fagomradeKode = "OKO";
    private static final String prioritetKode = "LAV";
    private static final String linjeSeparator = System.getProperty("line.separator"); // Does not need to use IOkosynkConfiguration, because "line.separator" is part of the no.nav.okosynk.io.os/java ecosystem.

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

    public static String getForsteFeltSeparator() {
        return forsteFeltSeparator;
    }

    public static String getFeltSeparator() {
        return feltSeparator;
    }

    private static final String recordSeparator = linjeSeparator + linjeSeparator;
    private static final String forsteFeltSeparator = ";;   ";
    private static final String feltSeparator = ";   ";

    private static final DateTimeFormatter NORSK_DATO_FORMAT_UTEN_KLOKKESLETT = DateTimeFormatter.ofPattern("dd.MM.yy");

    private final AbstractMappingRegelRepository mappingRegelRepository;
    private final AktoerRestClient aktoerRestClient;

    protected AbstractOppgaveOppretter(final AbstractMappingRegelRepository mappingRegelRepository, AktoerRestClient aktoerRestClient) {
        this.mappingRegelRepository = mappingRegelRepository;
        this.aktoerRestClient = aktoerRestClient;
    }

    @Override
    public Optional<Oppgave> apply(final List<MELDINGSTYPE> meldinger) {

        meldinger.sort(getMeldingComparator());

        return meldinger
            .stream()
            .findAny()
            .map(
                (final MELDINGSTYPE melding) -> {

                    final LocalDate aktivFra = LocalDate.now();
                    final Optional<MappingRegel> mappingRegel = this.mappingRegelRepository.finnRegel(melding);

                    Oppgave.OppgaveBuilder builder = new Oppgave.OppgaveBuilder();

                    if (mappingRegel.isPresent()) {
                        String gjelderId = melding.gjelderId;

                        if (isNotBlank(gjelderId)) {
                            String type = melding.utledGjelderIdType();
                            if (Objects.equals(type, PERSON)) {
                                if (erBostNr(gjelderId)) {
                                    builder.withBnr(gjelderId);
                                } else {
                                    try {
                                        AktoerRespons aktoerRespons = this.aktoerRestClient.hentGjeldendeAktoerId(gjelderId);
                                        if (isNotBlank(aktoerRespons.getFeilmelding())) {
                                            log.warn("Fikk feilmelding fra aktoerregisteret ifm. mapping av oppgave fra melding i inputfil, hopper over melding. - {}", aktoerRespons.getFeilmelding());
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
                                .withBehandlingstema(isNotBlank(mappingRegel.get().behandlingstema) ? mappingRegel.get().behandlingstema : null)
                                .withBehandlingstype(isNotBlank(mappingRegel.get().behandlingstype) ? mappingRegel.get().behandlingstype : null)
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
                });
    }

    public String lagSamletBeskrivelse(final List<MELDINGSTYPE> meldinger) {

        final List<String> beskrivelser = meldinger.stream().map(this::lagBeskrivelse).collect(Collectors.toList());

        return String.join(getRecordSeparator(), beskrivelser).replaceFirst(getFeltSeparator(), getForsteFeltSeparator());
    }

    public static String formatAsNorwegianDate(final LocalDate dato) {
        return NORSK_DATO_FORMAT_UTEN_KLOKKESLETT.format(dato);
    }

    protected abstract String oppgaveTypeKode();

    protected abstract int antallDagerFrist();

    protected abstract String lagBeskrivelse(final MELDINGSTYPE melding);

    protected abstract Comparator<MELDINGSTYPE> getMeldingComparator();

    private boolean erBostNr(String aktorNr) {
        int month = Integer.valueOf(substring(aktorNr, 2, 4));
        return (month >= 21 && month <=32);
    }
}
