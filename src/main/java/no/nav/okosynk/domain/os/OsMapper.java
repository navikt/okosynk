package no.nav.okosynk.domain.os;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.FakeOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.Oppgave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsMapper implements IMeldingMapper<OsMelding> {

    private static final Logger logger = LoggerFactory.getLogger(OsMapper.class);

    private OsOppgaveOppretter osOppgaveOppretter;
    private OsMappingRegelRepository osMappingRegelRepository;

    public OsMapper(AktoerRestClient aktoerRestClient) {
        osMappingRegelRepository = new OsMappingRegelRepository();
        osOppgaveOppretter = new OsOppgaveOppretter(osMappingRegelRepository, aktoerRestClient);
    }

    @Override
    public List<Oppgave> lagOppgaver(final List<OsMelding> meldinger) {

        return hentMeldingerSomSkalBliOppgaver(meldinger)
                .stream()
                .map(osOppgaveOppretter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    Predicate<OsMelding> osMeldingSkalBliOppgave() {
        return osMelding -> osMappingRegelRepository.finnRegel(osMelding).isPresent();
    }

    Collection<List<OsMelding>> hentMeldingerSomSkalBliOppgaver(final List<OsMelding> ufiltrerteOsMeldinger) {

        return ufiltrerteOsMeldinger.stream()
                .filter(osMeldingSkalBliOppgave())
                .distinct()
                .collect(Collectors.groupingBy(OsMeldingFunksjonelleAggregeringsKriterier::new, Collectors.toList()))
                .values();
    }
}
