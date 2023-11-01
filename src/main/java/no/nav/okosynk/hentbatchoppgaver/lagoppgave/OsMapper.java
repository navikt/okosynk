package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.model.Oppgave;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OsMapper implements IMeldingMapper<OsMelding> {
    private final OsOppgaveOppretter osOppgaveOppretter;
    private final OsMappingRegelRepository osMappingRegelRepository;

    public OsMapper(final IAktoerClient aktoerClient) {
        this.osMappingRegelRepository = new OsMappingRegelRepository();
        this.osOppgaveOppretter = new OsOppgaveOppretter(osMappingRegelRepository, aktoerClient);
    }

    @Override
    public List<Oppgave> lagOppgaver(final List<OsMelding> meldinger) {

        return groupMeldingerSomSkalBliOppgaver(meldinger)
                .stream()
                .map(this.osOppgaveOppretter::opprettOppgave)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    Predicate<OsMelding> osMeldingSkalBliOppgave() {
        return osMelding -> osMappingRegelRepository.finnRegel(osMelding.regelnøkkel()).isPresent();
    }

    Collection<List<OsMelding>> groupMeldingerSomSkalBliOppgaver(
            final List<OsMelding> ufiltrerteOsMeldinger) {

        return ufiltrerteOsMeldinger
                .stream()
                .filter(osMeldingSkalBliOppgave())
                .collect(
                        Collectors
                                .groupingBy(OsMeldingFunksjonelleAggregeringsKriterier::new, Collectors.toList())
                )
                .values();
    }
}
