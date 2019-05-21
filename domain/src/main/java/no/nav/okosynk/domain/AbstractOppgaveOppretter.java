package no.nav.okosynk.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractOppgaveOppretter<MELDINGSTYPE extends AbstractMelding>
    implements Function<List<MELDINGSTYPE>, Optional<Oppgave>> {

    private static final String fagomradeKode = "OKO";
    private static final String prioritetKode = "LAV_OKO";
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

    protected AbstractOppgaveOppretter(final AbstractMappingRegelRepository mappingRegelRepository) {
        this.mappingRegelRepository = mappingRegelRepository;
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
                    final Optional<MappingRegel> kanskjeMappingRegel =
                        this.mappingRegelRepository.finnRegel(melding);

                    return kanskjeMappingRegel
                        .map(mappingRegel ->
                            new Oppgave.OppgaveBuilder()
                                .withBrukerId(melding.gjelderId)
                                .withBrukertypeKode(melding.utledGjelderIdType())
                                .withOppgavetypeKode(oppgaveTypeKode())
                                .withFagomradeKode(getFagomradeKode())
                                .withUnderkategoriKode(mappingRegel.underkategoriKode)
                                .withPrioritetKode(getPrioritetKode())
                                .withBeskrivelse(lagSamletBeskrivelse(meldinger))
                                .withAktivFra(aktivFra)
                                .withAktivTil(aktivFra.plusDays(antallDagerFrist()))
                                .withAnsvarligEnhetId(mappingRegel.ansvarligEnhetId)
                                .withLest(false)
                                .withAntallMeldinger(meldinger.size())
                                .build())
                        .orElse(null);
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
}
