package no.nav.okosynk.synkroniserer.consumer.oppgave;

import java.util.List;

import static java.util.Arrays.asList;

public enum OppgaveStatus {
    OPPRETTET,
    AAPNET,
    UNDER_BEHANDLING,
    FERDIGSTILT,
    FEILREGISTRERT;

    public static List<OppgaveStatus> aapnet() {
        return asList(OPPRETTET, AAPNET, UNDER_BEHANDLING);
    }

    public static List<OppgaveStatus> avsluttet() {
        return asList(FERDIGSTILT, FEILREGISTRERT);
    }
}

