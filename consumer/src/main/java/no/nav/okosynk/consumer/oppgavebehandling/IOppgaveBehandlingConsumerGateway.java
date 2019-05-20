package no.nav.okosynk.consumer.oppgavebehandling;

import java.util.Collection;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.domain.Oppgave;

public interface IOppgaveBehandlingConsumerGateway {
    ConsumerStatistics opprettOppgaver    (final IOkosynkConfiguration okosynkConfiguration, final Collection<Oppgave> oppgaver);
    ConsumerStatistics oppdaterOppgaver   (final IOkosynkConfiguration okosynkConfiguration, final Collection<Oppgave> oppgaver);
    ConsumerStatistics ferdigstillOppgaver(                                                  final Collection<Oppgave> oppgaver);
}
