package no.nav.okosynk.consumer.oppgave;

import no.nav.okosynk.config.IOkosynkConfiguration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class OppgaveRestClient {
    private final Client client = ClientBuilder.newClient();
    private WebTarget target = client.target("http://localhost:9998").path("resource");

    public OppgaveRestClient(IOkosynkConfiguration okosynkConfiguration) {

    }


}
