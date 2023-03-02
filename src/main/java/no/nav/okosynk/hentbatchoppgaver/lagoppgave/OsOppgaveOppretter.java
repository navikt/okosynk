package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding.formatAsNorwegianDate;
import static no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding.getFeltSeparator;

public class OsOppgaveOppretter extends AbstractOppgaveOppretter<OsMelding> {

    private static final String OPPGAVETYPE_KODE = "OKO_OS";
    private static final int ANTALL_DAGER_FRIST = 7;

    private static final Comparator<OsMelding> meldingComparator = OsMelding.BEREGNINGS_DATO_COMPARATOR;

    OsOppgaveOppretter(
            final OsMappingRegelRepository mappingRegelRepository,
            final IAktoerClient aktoerClient,
            final IOkosynkConfiguration okosynkConfiguration) {

        super(mappingRegelRepository, aktoerClient, okosynkConfiguration);
    }

    @Override
    protected Comparator<OsMelding> getMeldingComparator() {
        return meldingComparator;
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
