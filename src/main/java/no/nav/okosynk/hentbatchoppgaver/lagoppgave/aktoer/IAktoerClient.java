package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

public interface IAktoerClient {
    AktoerRespons hentGjeldendeAktoerId(final String folkeregisterIdent);
}