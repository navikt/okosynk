package no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json.PdlErrorJson;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json.PdlErrorResponseJson;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json.PostPdlHentIdenterResponseJson;
import no.nav.okosynk.synkroniserer.consumer.oppgave.json.IdentGruppeV2;
import no.nav.okosynk.synkroniserer.consumer.oppgave.json.IdentJson;
import no.nav.okosynk.synkroniserer.consumer.security.OidcStsClient;
import no.nav.okosynk.synkroniserer.consumer.util.GraphQLUt;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.exceptions.FolkeregisterIdentNotFoundPdlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_OK;
import static no.nav.okosynk.config.Constants.AUTHORIZATION;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_NAV_CALL_ID_KEY;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_NAV_CONSUMER_TOKEN_KEY;
import static no.nav.okosynk.config.Constants.X_CORRELATION_ID_HEADER_KEY;

public class PdlRestClient implements IAktoerClient {

    private static final Logger log = LoggerFactory.getLogger(PdlRestClient.class);
    private static final String FILE_NAME_HENT_IDENTER_GRAPHQL_QUERY = "pdl/hentIdenter.graphql";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final IOkosynkConfiguration okosynkConfiguration;
    private final Constants.BATCH_TYPE batchType;
    private OidcStsClient oidcStsClient;

    public PdlRestClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {
        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType = batchType;

        log.info("PDL REST client created. batchType: {}", this.batchType);
    }

    static String buildHentIdenterEntityAsString(final String ident) {
        return GraphQLUt.buildQueryWrappedInJson(PdlRestClient.FILE_NAME_HENT_IDENTER_GRAPHQL_QUERY, "ident", ident);
    }

    private static PdlPersonIdentCollection hentAktivePdlIdenterFromPdl(
            final String folkeregisterIdent,
            final IOkosynkConfiguration okosynkConfiguration,
            final String selfAuthenticationToken
    ) throws FolkeregisterIdentNotFoundPdlException {
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
                        .request()
                        .header(AUTHORIZATION, "Bearer " + selfAuthenticationToken)
                        .header(HTTP_HEADER_NAV_CONSUMER_TOKEN_KEY, "Bearer " + selfAuthenticationToken)
                        .header(HTTP_HEADER_NAV_CALL_ID_KEY, correlationId)
                        .header(X_CORRELATION_ID_HEADER_KEY, correlationId)
                        .buildPost(Entity.json(hentIdenterEntityAsString));
        log.debug("Henter identer fra pdl for folkeregisterIdent...");
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
                    final Collection<PdlErrorJson> pdlErrorJsons = pdlErrorResponseJson.getErrors();
                    if (pdlErrorJsons == null) {
                        throw new IllegalStateException("Error when calling PDL. Response could not be parsed as an expected error message: " + postHentIdenterResponseAsString);
                    } else {
                        final boolean folkeregisterIdentNotFound =
                                pdlErrorJsons
                                        .stream()
                                        .anyMatch(pdlErrorJson -> pdlErrorJson.getExtensions() != null && "not_found".equals(pdlErrorJson.getExtensions().getCode()));
                        if (folkeregisterIdentNotFound) {
                            throw new FolkeregisterIdentNotFoundPdlException();
                        } else {
                            throw new IllegalStateException("Error when calling PDL. pdlErrorResponseJson: " + pdlErrorResponseJson);
                        }
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
            log.error(msg); // Change to secureLog when secureLog is working
            throw new IllegalStateException("Feil ved kall mot PDL");
        }

        return PdlRestClient.convertIdentJsonsToPdlPersonIdentCollection(aktiveIdentJsons);
    }

    private static PdlPersonIdentCollection convertIdentJsonsToPdlPersonIdentCollection(final Collection<IdentJson> identJsons) {

        final Map<IdentGruppeV2, PdlGruppe> mapFromIdentGruppeV2ToIdentGruppe = new EnumMap<>(
                Map.of(IdentGruppeV2.FOLKEREGISTERIDENT, PdlGruppe.FOLKEREGISTERIDENT,
                        IdentGruppeV2.AKTOERID, PdlGruppe.AKTORID));

        return PdlPersonIdentCollection
                .builder()
                .withPdlPersonIdenter(
                        Collections
                                .unmodifiableCollection(
                                        identJsons
                                                .stream()
                                                .filter(aktiveIdentJson -> mapFromIdentGruppeV2ToIdentGruppe.containsKey(aktiveIdentJson.getGruppe()))
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
    }

    private static OidcStsClient createOidcStsClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType
    ) {
        return new OidcStsClient(okosynkConfiguration, batchType);
    }

    @Override
    public AktoerRespons hentGjeldendeAktoerId(final String folkeregisterIdent) {

        AktoerRespons chosenAktoerRespons;
        try {
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
            chosenAktoerRespons = (aktorId == null) ? AktoerRespons.feil("Finnes ikke") : AktoerRespons.ok(aktorId);
        } catch (FolkeregisterIdentNotFoundPdlException e) {
            chosenAktoerRespons = AktoerRespons.feil("Finnes ikke");
        }
        return chosenAktoerRespons;
    }

    private String getOidcToken(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType
    ) {

        return getOidcStsClient(okosynkConfiguration, batchType).getOidcToken();
    }

    private OidcStsClient getOidcStsClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {
        if (this.oidcStsClient == null) {
            setOidcStsClient(PdlRestClient.createOidcStsClient(okosynkConfiguration, batchType));
        }
        return this.oidcStsClient;
    }

    private void setOidcStsClient(final OidcStsClient oidcStsClient) {
        this.oidcStsClient = oidcStsClient;
    }
}
