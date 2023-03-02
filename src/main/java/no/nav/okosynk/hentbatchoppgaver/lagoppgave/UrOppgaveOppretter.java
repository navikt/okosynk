package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding.formatAsNorwegianDate;
import static no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding.getFeltSeparator;

public class UrOppgaveOppretter extends AbstractOppgaveOppretter<UrMelding> {

    private static final String OPPGAVETYPE_KODE = "OKO_UR";
    private static final int ANTALL_DAGER_FRIST = 3;

    private static final Comparator<UrMelding> MELDINGCOMPARATOR = UrMelding.DATO_POSTERT_COMPARATOR;

    UrOppgaveOppretter(
            final UrMappingRegelRepository mappingRegelRepository,
            final IAktoerClient aktoerClient,
            final IOkosynkConfiguration okosynkConfiguration) {

        super(mappingRegelRepository, aktoerClient, okosynkConfiguration);
    }

    @Override
    protected Comparator<UrMelding> getMeldingComparator() {
        return MELDINGCOMPARATOR;
    }

    @Override
    protected String oppgaveTypeKode() {
        return OPPGAVETYPE_KODE;
    }

    @Override
    protected int antallDagerFrist() {
        return ANTALL_DAGER_FRIST;
    }
}
