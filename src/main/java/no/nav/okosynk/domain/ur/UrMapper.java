package no.nav.okosynk.domain.ur;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.Oppgave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrMapper implements IMeldingMapper<UrMelding> {

    private static final Logger logger = LoggerFactory.getLogger(UrMapper.class);

    private UrOppgaveOppretter       urOppgaveOppretter;
    private UrMappingRegelRepository urMappingRegelRepository;

    public UrMapper(AktoerRestClient aktoerRestClient) {
        urMappingRegelRepository = new UrMappingRegelRepository();
        urOppgaveOppretter       = new UrOppgaveOppretter(urMappingRegelRepository);
    }

    @Override
    public List<Oppgave> lagOppgaver(final List<UrMelding> meldinger) {

        return hentMeldingerSomSkalBliOppgaver(meldinger)
                .stream()
                .map(urOppgaveOppretter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    Predicate<UrMelding> urMeldingSkalBliOppgave() {
        return urMelding -> urMappingRegelRepository.finnRegel(urMelding).isPresent();
    }

    Collection<List<UrMelding>> hentMeldingerSomSkalBliOppgaver(final List<UrMelding> ufiltrerteUrMeldinger) {

        List<UrMelding> meldingerMedMappingRegel = ufiltrerteUrMeldinger.stream()
                .filter(urMeldingSkalBliOppgave())
                .collect(Collectors.toList());

        logger.info("STATISTIKK: Antall meldinger med duplikater er {}",
                meldingerMedMappingRegel.size());

        return meldingerMedMappingRegel.stream()
                .distinct()
                .collect(Collectors.groupingBy(UrMeldingFunksjonelleAggregeringsKriterier::new, Collectors.toList()))
                .values();
    }
}
