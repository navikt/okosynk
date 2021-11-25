package no.nav.okosynk.consumer.aktoer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.json.PdlErrorResponseJson;
import no.nav.okosynk.consumer.aktoer.json.PostPdlHentIdenterResponseJson;
import no.nav.okosynk.consumer.oppgave.json.IdentGruppeV2;
import no.nav.okosynk.consumer.oppgave.json.IdentJson;
import no.nav.okosynk.consumer.security.OidcStsClient;
import no.nav.okosynk.consumer.util.GraphQLUt;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_OK;
import static no.nav.okosynk.config.Constants.*;

public class PdlRestClient implements IAktoerClient {

    private static final Logger log = LoggerFactory.getLogger(PdlRestClient.class);
    private static final Logger secureLog = LoggerFactory.getLogger("secureLog");
    private static final String FILE_NAME_HENT_IDENTER_GRAPHQL_QUERY = "pdl/hentIdenter.graphql";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final IOkosynkConfiguration okosynkConfiguration;
    private final Constants.BATCH_TYPE batchType;
    private final boolean shouldAlwaysThrow;
    private OidcStsClient oidcStsClient;

    public PdlRestClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {
        this(okosynkConfiguration, batchType, !okosynkConfiguration.shouldPreferPdlToAktoerregisteret());
    }

    PdlRestClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType,
            final boolean shouldAlwaysThrow) {
        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType = batchType;
        this.shouldAlwaysThrow = shouldAlwaysThrow;

        log.info("PDL REST client created. batchType: {}, shouldAlwaysThrow: {}", this.batchType, this.shouldAlwaysThrow);
    }

    static String buildHentIdenterEntityAsString(final String ident) {
        final String hentIdenterEntityAsString =
                GraphQLUt.buildQueryWrappedInJson(PdlRestClient.FILE_NAME_HENT_IDENTER_GRAPHQL_QUERY, "ident", ident);
        return hentIdenterEntityAsString;
    }

    private static PdlPersonIdentCollection hentAktivePdlIdenterFromPdl(
            final String folkeregisterIdent,
            final IOkosynkConfiguration okosynkConfiguration,
            final String selfAuthenticationToken
    ) {
        final String hentIdenterEntityAsStringPotentiallyWithNewLines =
                PdlRestClient.buildHentIdenterEntityAsString(folkeregisterIdent);
        final String hentIdenterEntityAsString =
                hentIdenterEntityAsStringPotentiallyWithNewLines.replace('\n', ' ');

        final Client jerseyHttpClient = ClientBuilder.newBuilder().build();
        final String pdlUrl = okosynkConfiguration.getPdlUrl();
        final String correlationId = UUID.randomUUID().toString();
        final Invocation invocation =
                jerseyHttpClient
                        .target(pdlUrl)
                        .request(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + selfAuthenticationToken)
                        .header(HTTP_HEADER_NAV_CONSUMER_TOKEN_KEY, "Bearer " + selfAuthenticationToken)
                        .header(HTTP_HEADER_NAV_CALL_ID_KEY, correlationId)
                        .header(X_CORRELATION_ID_HEADER_KEY, correlationId)
                        .buildPost(Entity.text(hentIdenterEntityAsString));
        log.info("Henter identer fra pdl for folkeregisterIdent...");
        final Response postHentIdenterResponse = invocation.invoke();
        final String postHentIdenterResponseAsString = postHentIdenterResponse.readEntity(String.class);
        final Collection<IdentJson> aktiveIdentJsons;
        if (HTTP_OK == postHentIdenterResponse.getStatus()) {
            try {
                final PostPdlHentIdenterResponseJson postPdlHentIdenterResponseJson =
                        PdlRestClient.objectMapper.readValue(postHentIdenterResponseAsString, PostPdlHentIdenterResponseJson.class);
                if (postPdlHentIdenterResponseJson.getData().getHentIdenter() == null) {
                    final PdlErrorResponseJson pdlErrorResponseJson =
                            PdlRestClient.objectMapper.readValue(postHentIdenterResponseAsString, PdlErrorResponseJson.class);
                    if (pdlErrorResponseJson.getErrors() == null) {
                        throw new IllegalStateException("Error when calling PDL. Response could not be parsed as an expected error message: " + postHentIdenterResponseAsString);
                    } else {
                        throw new IllegalStateException("Error when calling PDL. pdlErrorResponseJson: " + pdlErrorResponseJson);
                    }
                } else {
                    aktiveIdentJsons = postPdlHentIdenterResponseJson
                            .getData()
                            .getHentIdenter()
                            .getIdenter();
                }
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Exception received when trying to parse the response", e);
            }

        } else {
            final String msg = String.format("Feil ved kall mot PDL for folkeregisterIdentOrAktorId: %s, Status: %d, postHentIdenterResponseAsString: %s",
                    folkeregisterIdent,
                    postHentIdenterResponse.getStatus(),
                    postHentIdenterResponseAsString);
            secureLog.warn(msg);
            throw new IllegalStateException("Feil ved kall mot PDL");
        }
        final PdlPersonIdentCollection pdlPersonIdentCollection =
                PdlRestClient.convertIdentJsonsToPdlPersonIdentCollection(aktiveIdentJsons);

        return pdlPersonIdentCollection;
    }

    private static PdlPersonIdentCollection convertIdentJsonsToPdlPersonIdentCollection(final Collection<IdentJson> identJsons) {

        final Map<IdentGruppeV2, PdlGruppe> mapFromIdentGruppeV2ToIdentGruppe =
                new HashMap<IdentGruppeV2, PdlGruppe>() {{
                    put(IdentGruppeV2.FOLKEREGISTERIDENT, PdlGruppe.FOLKEREGISTERIDENT);
                    put(IdentGruppeV2.AKTOERID, PdlGruppe.AKTORID);
                }};

        final PdlPersonIdentCollection pdlPersonIdentCollection =
                PdlPersonIdentCollection
                        .builder()
                        .withPdlPersonIdenter(
                                Collections
                                        .unmodifiableCollection(
                                                identJsons
                                                        .stream()
                                                        .filter(aktiveIdentJson -> mapFromIdentGruppeV2ToIdentGruppe.keySet().contains(aktiveIdentJson.getGruppe()))
                                                        .map(
                                                                aktivIdentJson ->
                                                                        PdlPersonIdent
                                                                                .builder()
                                                                                .withIdent(aktivIdentJson.getIdent())
                                                                                .withGruppe(mapFromIdentGruppeV2ToIdentGruppe.get(aktivIdentJson.getGruppe()))
                                                                                .build()
                                                        )
                                                        .collect(Collectors.toSet())
                                        )
                        )
                        .build();
        return pdlPersonIdentCollection;
    }

    private static OidcStsClient createOidcStsClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType
    ) {
        return new OidcStsClient(okosynkConfiguration, batchType);
    }

    @Override
    public AktoerRespons hentGjeldendeAktoerId(final String folkeregisterIdent) {

        if (this.shouldAlwaysThrow) {
            throw new NotImplementedException();
        }

        final PdlPersonIdentCollection pdlPersonIdentCollection =
                PdlRestClient.hentAktivePdlIdenterFromPdl(
                        folkeregisterIdent,
                        this.okosynkConfiguration,
                        getOidcToken(this.okosynkConfiguration, this.batchType)
                );

        final String aktorId = pdlPersonIdentCollection
                .extractGjeldendeAktorIdPdlPersonIdent()
                .orElseGet(() -> PdlPersonIdent.builder().withGruppe(null).withIdent(null).build())
                .getIdent();

        return aktorId == null ?
                AktoerRespons.feil("Finnes ikke")
                :
                AktoerRespons.ok(aktorId);
    }

    private String getOidcToken(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType
    ) {
        final OidcStsClient oidcStsClient = getOidcStsClient(okosynkConfiguration, batchType);
        final String oidcToken = oidcStsClient.getOidcToken();

        return oidcToken;
    }

    private OidcStsClient getOidcStsClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {
        if (this.oidcStsClient == null) {
            final OidcStsClient oidcStsClient =
                    PdlRestClient.createOidcStsClient(okosynkConfiguration, batchType);
            setOidcStsClient(oidcStsClient);
        }
        return this.oidcStsClient;
    }

    private void setOidcStsClient(final OidcStsClient oidcStsClient) {
        this.oidcStsClient = oidcStsClient;
    }
}