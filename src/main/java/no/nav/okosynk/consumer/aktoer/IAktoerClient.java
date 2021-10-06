package no.nav.okosynk.consumer.aktoer;

public interface IAktoerClient {
    AktoerRespons hentGjeldendeAktoerId(final String folkeregisterIdent);
}