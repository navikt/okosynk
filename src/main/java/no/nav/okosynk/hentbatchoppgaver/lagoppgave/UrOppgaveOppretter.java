package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;

import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

public class UrOppgaveOppretter extends AbstractOppgaveOppretter<UrMelding> {

    private static final String OPPGAVETYPE_KODE = "OKO_UR";
    private static final int ANTALL_DAGER_FRIST = 3;

    UrOppgaveOppretter(
            final UrMappingRegelRepository mappingRegelRepository,
            final IAktoerClient aktoerClient) {

        super(mappingRegelRepository, aktoerClient);
    }

    @Override
    protected Comparator<UrMelding> getMeldingComparator() {
        return comparing(urMelding -> urMelding.datoPostert, reverseOrder());
    }

    @Override
    protected String summerOgKonsolider(List<UrMelding> urMeldings) {
        return urMeldings.stream().map(UrBeskrivelseInfo::new)
                .reduce(UrBeskrivelseInfo::pluss)
                .map(UrBeskrivelseInfo::lagBeskrivelse).orElse("");
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
