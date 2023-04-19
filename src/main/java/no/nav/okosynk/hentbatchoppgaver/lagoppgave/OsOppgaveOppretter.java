package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;

import java.util.Comparator;
import java.util.List;

public class OsOppgaveOppretter extends AbstractOppgaveOppretter<OsMelding> {

    private static final String OPPGAVETYPE_KODE = "OKO_OS";
    private static final int ANTALL_DAGER_FRIST = 7;
    private static final Comparator<OsMelding> meldingComparator = OsMelding.BEREGNINGS_DATO_COMPARATOR;

    OsOppgaveOppretter(
            final OsMappingRegelRepository mappingRegelRepository,
            final IAktoerClient aktoerClient) {

        super(mappingRegelRepository, aktoerClient);
    }

    @Override
    protected Comparator<OsMelding> getMeldingComparator() {
        return meldingComparator;
    }

    @Override
    protected String summerOgKonsolider(List<OsMelding> osMeldings) {
        return osMeldings.stream()
                .sorted(this.getMeldingComparator())
                .map(OsBeskrivelseInfo::new)
                .reduce(OsBeskrivelseInfo::pluss)
                .map(OsBeskrivelseInfo::lagBeskrivelse).orElse("");
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
