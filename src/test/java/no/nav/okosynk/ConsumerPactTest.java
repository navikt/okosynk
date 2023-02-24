package no.nav.okosynk;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.google.gson.JsonObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static no.nav.okosynk.config.Constants.AUTHORIZATION;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_ACCEPT_APPLICATION_JSON_VALUE;
import static no.nav.okosynk.config.Constants.HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY;
import static no.nav.okosynk.config.Constants.X_CORRELATION_ID_HEADER_KEY;
import static org.apache.http.HttpHeaders.ACCEPT;


@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "OppgaveResource")
class ConsumerPactTest {

    private Map<String, String> headers = MapUtils.putAll(new HashMap<>(), new String[]{
            HTTP_HEADER_CONTENT_TYPE_TOKEN_KEY, HTTP_HEADER_ACCEPT_APPLICATION_JSON_VALUE
    });

    @Pact(provider="OppgaveResource", consumer="okosynk_consumer")
    public RequestResponsePact opprett(PactDslWithProvider builder) {
        return builder
                .uponReceiving("Oppretter oppgave")
                .path("/api/v1/oppgaver")
                .headers(headers())
                .method("POST")
                .body(this::createOpprettOppgaveJson)
                .willRespondWith()
                .status(201)
                .body(new PactDslJsonBody().id())
                .toPact();
    }

    @Pact(provider="OppgaveResource", consumer="okosynk_consumer")
    public RequestResponsePact soek(PactDslWithProvider builder) {
        return builder
                .given("OS_oppgaver_er_opprettet")
                .uponReceiving("Søker etter oppgaver OS")
                .path("/api/v1/oppgaver")
                .query("opprettetAv=srvbokosynk001&tema=OKO&statuskategori=AAPEN")
                .headers(headers())
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("antallTreffTotalt", 3)
                        .eachLike("oppgaver")
                        .closeArray())

                .given("UR_oppgaver_er_opprettet")
                .uponReceiving("Søker etter oppgaver UR")
                .path("/api/v1/oppgaver")
                .query("opprettetAv=srvbokosynk002&tema=OKO&statuskategori=AAPEN")
                .headers(headers())
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .numberValue("antallTreffTotalt", 4)
                        .eachLike("oppgaver")
                        .closeArray())
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "opprett")
    void test_opprett(MockServer mockServer) throws IOException {

        HttpResponse httpResponse = Request.Post(mockServer.getUrl() + "/api/v1/oppgaver")
                .setHeader(AUTHORIZATION, "Basic srvbokosynk001")
                .setHeader(ACCEPT, HTTP_HEADER_ACCEPT_APPLICATION_JSON_VALUE)
                .setHeader(X_CORRELATION_ID_HEADER_KEY, "b8c764acfb-0a04-fd3b-c1db-bc3782890ea1cb")
                .bodyString(createOpprettOppgaveJson(), ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse();
    }

    @Test
    @PactTestFor(pactMethod = "soek")
    void test_soek(MockServer mockServer) throws IOException {
        HttpResponse httpResponse = Request.Get(mockServer.getUrl() + "/api/v1/oppgaver?opprettetAv=srvbokosynk001&tema=OKO&statuskategori=AAPEN")
                .setHeader(AUTHORIZATION, "Basic srvbokosynk001")
                .setHeader(ACCEPT, HTTP_HEADER_ACCEPT_APPLICATION_JSON_VALUE)
                .setHeader(X_CORRELATION_ID_HEADER_KEY, "b8c764acfb-0a04-fd3b-c1db-bc3782890ea1cb")
                .execute()
                .returnResponse();

        httpResponse = Request.Get(mockServer.getUrl() + "/api/v1/oppgaver?opprettetAv=srvbokosynk002&tema=OKO&statuskategori=AAPEN")
                .setHeader(AUTHORIZATION, "Basic srvbokosynk001")
                .setHeader(ACCEPT, HTTP_HEADER_ACCEPT_APPLICATION_JSON_VALUE)
                .setHeader(X_CORRELATION_ID_HEADER_KEY, "b8c764acfb-0a04-fd3b-c1db-bc3782890ea1cb")
                .execute()
                .returnResponse();


    }

    private String createOpprettOppgaveJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tildeltEnhetsnr", "4408");
        jsonObject.addProperty("opprettetAvEnhetsnr", "9999");
        jsonObject.addProperty("aktoerId", "1831212532188");
        jsonObject.addProperty("beskrivelse", "Okosynk beskrivelse");
        jsonObject.addProperty("tema", "OKO");
        jsonObject.addProperty("oppgavetype", "OKO_UR");
        jsonObject.addProperty("aktivDato", "2019-08-08");
        jsonObject.addProperty("fristFerdigstillelse", "2019-08-10");
        jsonObject.addProperty("prioritet", "LAV");

        return jsonObject.toString();
    }

    private Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, "Basic srvbokosynk001");
        headers.put(ACCEPT, HTTP_HEADER_ACCEPT_APPLICATION_JSON_VALUE);
        headers.put(X_CORRELATION_ID_HEADER_KEY, "b8c764acfb-0a04-fd3b-c1db-bc3782890ea1cb");
        return headers;
    }
}
