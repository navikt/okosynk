package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import no.nav.okosynk.model.Oppgave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.stream.Collectors.groupingBy;

public class UrMapper implements IMeldingMapper<UrMelding> {

    private static final Logger logger = LoggerFactory.getLogger(UrMapper.class);

    private final UrOppgaveOppretter urOppgaveOppretter;
    private final Mappingregelverk urMappingRegelRepository;

    public UrMapper(final IAktoerClient aktoerClient) {
        this.urMappingRegelRepository = new Mappingregelverk(Constants.BATCH_TYPE.UR.getMappingRulesPropertiesFileName());
        this.urOppgaveOppretter = new UrOppgaveOppretter(urMappingRegelRepository, aktoerClient);
    }

    @Override
    public List<Oppgave> lagOppgaver(final List<UrMelding> meldinger) {

        return groupMeldingerSomSkalBliOppgaver(meldinger)
                .stream()
                .map(this.urOppgaveOppretter::opprettOppgave)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    Predicate<UrMelding> urMeldingSkalBliOppgave() {
        return urMelding -> urMappingRegelRepository.finnRegel(urMelding.regelnøkkel()).isPresent();
    }

    Collection<List<UrMelding>> groupMeldingerSomSkalBliOppgaver(final List<UrMelding> ufiltrerteUrMeldinger) {

        final List<UrMelding> meldingerMedMappingRegel =
                ufiltrerteUrMeldinger.stream()
                        .filter(urMeldingSkalBliOppgave())
                        .toList();

        logger.info("Antall meldinger som tilfredsstiller mappingregel: {}",
                meldingerMedMappingRegel.size());

        return meldingerMedMappingRegel.stream().distinct()
                .collect(groupingBy(UrMeldingFunksjonelleAggregeringsKriterier::new))
                .values();
    }
}
