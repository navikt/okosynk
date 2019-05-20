package no.nav.okosynk.consumer.oppgave;

import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.domain.Oppgave;

import java.util.Collection;

public interface IOppgaveConsumerGateway {
     ConsumerStatistics finnOppgaver(final String opprettetAv, final Collection<Oppgave> funneOppgaver);
}
