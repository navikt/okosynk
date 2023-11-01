package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;

import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

public class OsOppgaveOppretter extends AbstractOppgaveOppretter<OsMelding> {

    private static final String OPPGAVETYPE_KODE = "OKO_OS";
    private static final int ANTALL_DAGER_FRIST = 7;

    OsOppgaveOppretter(
            final Mappingregelverk mappingRegelRepository,
            final IAktoerClient aktoerClient) {

        super(mappingRegelRepository, aktoerClient);
    }

    @Override
    protected Comparator<OsMelding> getMeldingComparator() {
        return comparing(OsMelding::getBeregningsDato, reverseOrder());
    }

    @Override
    protected String summerOgKonsolider(List<OsMelding> osMeldings) {
        return osMeldings.stream()
                .sorted(this.getMeldingComparator())
                .map(OsMelding::osBeskrivelseInfo)
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
