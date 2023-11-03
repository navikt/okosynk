package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.OsBeskrivelseInfo;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.OsMeldingFunksjonelleAggregeringsKriterier;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.model.Oppgave;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

public class OsOppgaveOppretter extends AbstractOppgaveOppretter<OsMelding> {

    public OsOppgaveOppretter(final IAktoerClient aktoerClient) {
        super(new Mappingregelverk(Constants.BATCH_TYPE.OS.getMappingRulesPropertiesFileName()), aktoerClient);
    }

    private static final String OPPGAVETYPE_KODE = "OKO_OS";
    private static final int ANTALL_DAGER_FRIST = 7;

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
    public List<Oppgave> lagOppgaver(final List<OsMelding> meldinger) {
        return groupMeldingerSomSkalBliOppgaver(meldinger)
                .stream()
                .map(this::opprettOppgave)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    Predicate<OsMelding> osMeldingSkalBliOppgave() {
        return osMelding -> new Mappingregelverk(Constants.BATCH_TYPE.OS.getMappingRulesPropertiesFileName())
                .finnRegel(osMelding.regelnøkkel()).isPresent();
    }

    Collection<List<OsMelding>> groupMeldingerSomSkalBliOppgaver(
            final List<OsMelding> ufiltrerteOsMeldinger) {

        return ufiltrerteOsMeldinger
                .stream()
                .filter(osMeldingSkalBliOppgave())
                .collect(Collectors.groupingBy(OsMeldingFunksjonelleAggregeringsKriterier::new, Collectors.toList()))
                .values();
    }
}
