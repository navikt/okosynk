package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.parselinje.OsMeldingFunksjonelleAggregeringsKriterier;
import no.nav.okosynk.model.Oppgave;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OsMapper implements IMeldingMapper<OsMelding> {
    private OsOppgaveOppretter osOppgaveOppretter;
    private OsMappingRegelRepository osMappingRegelRepository;

    public OsMapper(final IAktoerClient aktoerClient, final IOkosynkConfiguration okosynkConfiguration) {
        this.osMappingRegelRepository = new OsMappingRegelRepository();
        this.osOppgaveOppretter = new OsOppgaveOppretter(osMappingRegelRepository, aktoerClient, okosynkConfiguration);
    }

    @Override
    public List<Oppgave> lagOppgaver(final List<OsMelding> meldinger) {

        return groupMeldingerSomSkalBliOppgaver(meldinger)
                .stream()
                .map(this.osOppgaveOppretter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    Predicate<OsMelding> osMeldingSkalBliOppgave() {
        return osMelding -> osMappingRegelRepository.finnRegel(osMelding).isPresent();
    }

    Collection<List<OsMelding>> groupMeldingerSomSkalBliOppgaver(
            final List<OsMelding> ufiltrerteOsMeldinger) {

        return ufiltrerteOsMeldinger
                .stream()
                .filter(osMeldingSkalBliOppgave())
                .distinct()
                .collect(
                        Collectors
                                .groupingBy(OsMeldingFunksjonelleAggregeringsKriterier::new, Collectors.toList())
                )
                .values();
    }
}