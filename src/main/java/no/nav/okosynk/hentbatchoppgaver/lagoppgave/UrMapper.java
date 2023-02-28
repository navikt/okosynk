package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.model.Oppgave;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UrMapper implements IMeldingMapper<UrMelding> {

    private static final Logger logger = LoggerFactory.getLogger(UrMapper.class);

    private UrOppgaveOppretter urOppgaveOppretter;
    private UrMappingRegelRepository urMappingRegelRepository;

    public UrMapper(final IAktoerClient aktoerClient, final IOkosynkConfiguration okosynkConfiguration) {
        this.urMappingRegelRepository = new UrMappingRegelRepository();
        this.urOppgaveOppretter = new UrOppgaveOppretter(urMappingRegelRepository, aktoerClient, okosynkConfiguration);
    }

    @Override
    public List<Oppgave> lagOppgaver(final List<UrMelding> meldinger) {

        return groupMeldingerSomSkalBliOppgaver(meldinger)
                .stream()
                .map(this.urOppgaveOppretter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    Predicate<UrMelding> urMeldingSkalBliOppgave() {
        return urMelding -> urMappingRegelRepository.finnRegel(urMelding).isPresent();
    }

    Collection<List<UrMelding>> groupMeldingerSomSkalBliOppgaver(final List<UrMelding> ufiltrerteUrMeldinger) {

        final List<UrMelding> meldingerMedMappingRegel =
                ufiltrerteUrMeldinger
                        .stream()
                        .filter(urMeldingSkalBliOppgave())
                        .collect(Collectors.toList());

        logger.info("Antall meldinger som tilfredsstiller mappingregel: {}",
                meldingerMedMappingRegel.size());

        return meldingerMedMappingRegel
                .stream()
                .distinct()
                .collect(
                        Collectors
                                .groupingBy(UrMeldingFunksjonelleAggregeringsKriterier::new, Collectors.toList())
                )
                .values();
    }
}