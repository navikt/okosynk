package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.AggregeringsKriterier;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.OsBeskrivelseInfo;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.model.Oppgave;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class OsOppgaveOppretter extends AbstractOppgaveOppretter<OsMelding> {

    public OsOppgaveOppretter(final IAktoerClient aktoerClient) {
        super(Constants.BATCH_TYPE.OS, aktoerClient);
    }

    @Override
    protected String summerOgKonsolider(List<OsMelding> osMeldings) {
        return osMeldings.stream()
                .sorted(comparing(OsMelding::sammenligningsDato, reverseOrder()))
                .map(OsMelding::osBeskrivelseInfo)
                .reduce(OsBeskrivelseInfo::pluss)
                .map(OsBeskrivelseInfo::lagBeskrivelse).orElse("");
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
        return osMelding -> Mappingregelverk.finnRegel(osMelding.ruleKey()).isPresent();
    }

    Collection<List<OsMelding>> groupMeldingerSomSkalBliOppgaver(final List<OsMelding> ufiltrerteOsMeldinger) {
        return ufiltrerteOsMeldinger
                .stream()
                .filter(osMeldingSkalBliOppgave())
                .collect(groupingBy(AggregeringsKriterier::new, toList()))
                .values();
    }
}
