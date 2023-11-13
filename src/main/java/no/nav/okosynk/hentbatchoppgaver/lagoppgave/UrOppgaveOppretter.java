package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.AggregeringsKriterier;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.UrBeskrivelseInfo;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import no.nav.okosynk.model.Oppgave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.stream.Collectors.groupingBy;

public class UrOppgaveOppretter extends AbstractOppgaveOppretter<UrMelding> {
    private static final Logger logger = LoggerFactory.getLogger(UrOppgaveOppretter.class);

    public UrOppgaveOppretter(final IAktoerClient aktoerClient) {
        super(Constants.BATCH_TYPE.UR, aktoerClient);
    }


    @Override
    protected String summerOgKonsolider(List<UrMelding> urMeldings) {
        return urMeldings.stream().map(UrMelding::urBeskrivelseInfo)
                .reduce(UrBeskrivelseInfo::pluss)
                .map(UrBeskrivelseInfo::lagBeskrivelse).orElse("");
    }

    public List<Oppgave> lagOppgaver(final List<UrMelding> meldinger) {

        return groupMeldingerSomSkalBliOppgaver(meldinger)
                .stream()
                .map(this::opprettOppgave)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    Predicate<UrMelding> urMeldingSkalBliOppgave() {
        return urMelding -> Mappingregelverk.finnRegel(urMelding.ruleKey()).isPresent();
    }

    Collection<List<UrMelding>> groupMeldingerSomSkalBliOppgaver(final List<UrMelding> ufiltrerteUrMeldinger) {

        final List<UrMelding> meldingerMedMappingRegel =
                ufiltrerteUrMeldinger.stream()
                        .filter(urMeldingSkalBliOppgave())
                        .toList();

        logger.info("Antall UR-meldinger som tilfredsstiller mappingregel: {}", meldingerMedMappingRegel.size());

        return meldingerMedMappingRegel.stream().distinct()
                .collect(groupingBy(AggregeringsKriterier::new))
                .values();
    }


}
