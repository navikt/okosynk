package no.nav.okosynk.consumer.oppgave;

import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.AktoerRestClient;
import no.nav.okosynk.consumer.ConsumerStatistics;
import no.nav.okosynk.consumer.OidcStsClient;
import no.nav.okosynk.domain.Oppgave;
import org.apache.commons.lang3.Validate;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class OppgaveRestClient {
    private static final Logger log = LoggerFactory.getLogger(OidcStsClient.class);

    private final IOkosynkConfiguration okosynkConfiguration;
    private final AktoerRestClient aktoerRestClient;
    private final CloseableHttpClient httpClient;
    private final OidcStsClient oidcStsClient;
    private final UsernamePasswordCredentials credentials;

    public OppgaveRestClient(IOkosynkConfiguration okosynkConfiguration) {
        this.okosynkConfiguration = okosynkConfiguration;
        this.aktoerRestClient = new AktoerRestClient(okosynkConfiguration);
        this.oidcStsClient = new OidcStsClient(okosynkConfiguration);

        this.credentials = new UsernamePasswordCredentials(
                okosynkConfiguration.getRequiredString("SRVOPPGAVE_USERNAME"),
                okosynkConfiguration.getRequiredString("SRVOPPGAVE_PASSWORD")
        );

        this.httpClient = HttpClients.createDefault();
    }

    public ConsumerStatistics finnOppgaver(String fnr, Set<Oppgave> oppgaver) {
        URI uri;
        try {
            uri = new URIBuilder(this.okosynkConfiguration.getRequiredString("OPPGAVE_URL"))
                    .addParameter("statuskategori", "AAPEN")
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Klarte ikke bygge opp URI for STS kall", e);
        }

        HttpGet request = new HttpGet(uri);
        request.addHeader("X-Correlation-ID", UUID.randomUUID().toString());
        request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());
        try {
            request.addHeader(new BasicScheme(UTF_8).authenticate(credentials, request, null));
        } catch (AuthenticationException e) {
            throw new IllegalStateException(e);
        }

        try (CloseableHttpResponse response = this.httpClient.execute(request)) {

        } catch (Exception e) {
            throw new IllegalStateException("Feilet ved kall mot Oppgave API", e);
        }

        return ConsumerStatistics.zero();
    }

    public ConsumerStatistics patchOppgaver(Set<Oppgave> oppgaver, boolean ferdigstill) {
        return ConsumerStatistics.zero();
    }
}
