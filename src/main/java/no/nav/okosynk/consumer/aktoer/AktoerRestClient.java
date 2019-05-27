package no.nav.okosynk.consumer.aktoer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class AktoerRestClient {
    private static final Logger log = LoggerFactory.getLogger(AktoerRestClient.class);
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String NAV_CALLID = "Nav-Call-Id";
    private static final String NAV_PERSONIDENTER = "Nav-Personidenter";
    private final CloseableHttpClient httpClient;
    private final IOkosynkConfiguration okosynkConfiguration;
    private final OidcStsClient oidcStsClient;
    private final String consumerId;

    public AktoerRestClient(IOkosynkConfiguration okosynkConfiguration, Constants.BATCH_TYPE batchType) {
        this.okosynkConfiguration = okosynkConfiguration;
        this.consumerId = this.okosynkConfiguration.getString(batchType.getBatchBrukerKey(), batchType.getBatchBrukerDefaultValue());
        this.oidcStsClient = new OidcStsClient(okosynkConfiguration, batchType);
        this.httpClient = HttpClients.createDefault();
        log.info("Aktoerregister REST client bygd opp for {}", this.consumerId);
    }

    public AktoerRespons hentGjeldendeAktoerId(String fnr) {
        URI uri;
        try {
             uri = new URIBuilder(this.okosynkConfiguration.getRequiredString("AKTOERREGISTER_API_URL"))
                    .addParameter("identgruppe", "AktoerId")
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed while building aktoerregister endpoint URI", e);
        }

        HttpUriRequest request = new HttpGet(uri);
        String oidcToken = getOidcToken();
        request.addHeader(AUTHORIZATION, "Bearer " + oidcToken);
        request.addHeader(NAV_CALLID, String.valueOf(UUID.randomUUID()));
        request.addHeader(NAV_PERSONIDENTER, fnr);
        request.addHeader(NAV_CONSUMER_ID, this.consumerId);
        request.addHeader(ACCEPT, APPLICATION_JSON.getMimeType());


        try (CloseableHttpResponse response = this.httpClient.execute(request)) {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HTTP_OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getEntity().getContent());
                JsonNode aktoerResponse = jsonNode.get(fnr);
                AktoerIdent aktoerIdent = objectMapper.treeToValue(aktoerResponse, AktoerIdent.class);

                if (isNotBlank(aktoerIdent.getFeilmelding())) {
                    return AktoerRespons.feil(String.format("Mottok feilmelding fra aktoerregisteret: %s", aktoerIdent.getFeilmelding()));
                } else {
                    List<AktoerIdentEntry> identer = aktoerIdent.getIdenter();
                    if (identer != null && !identer.isEmpty()) {
                        if (identer.size() == 1) {
                            AktoerIdentEntry entry = identer.get(0);
                            if (!entry.isGjeldende()) {
                                return AktoerRespons.feil("Spurte etter kun gjeldene identer men mottok gyldig respons hvor ident ikke er satt til gyldig");
                            } else {
                                return AktoerRespons.ok(entry.getIdent());
                            }
                        } else {
                            return AktoerRespons.feil(String.format("Forventet kun en aktoerId i respnsen men fikk: %s", identer.size()));
                        }
                    } else {
                        return AktoerRespons.feil("Fant ingen identer i responsen fra aktoerregisteret som, responsen innholdt ingen feilmeldinger.");
                    }
                }
            } else if (statusLine.getStatusCode() == HTTP_NOT_FOUND) {
                return AktoerRespons.feil("Fant ikke forespurt FNR i aktoerregisteret");
            } else {
                return AktoerRespons.feil(String.format("Feil ved kall mot Aktoerregister - %s %s", statusLine.getStatusCode(), statusLine.getReasonPhrase()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Feil ved kall mot Aktoerregister API", e);
        }
    }

    private String getOidcToken() {
        return this.oidcStsClient.getOidcToken();
    }
}
