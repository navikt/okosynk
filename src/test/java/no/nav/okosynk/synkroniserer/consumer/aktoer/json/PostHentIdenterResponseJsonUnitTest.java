package no.nav.okosynk.synkroniserer.consumer.aktoer.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json.PostPdlHentIdenterDataResponseJson;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json.PostPdlHentIdenterHentIdenterResponseJson;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.json.PostPdlHentIdenterResponseJson;
import no.nav.okosynk.synkroniserer.consumer.oppgave.json.IdentGruppeV2;
import no.nav.okosynk.synkroniserer.consumer.oppgave.json.IdentJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;

class PostHentIdenterResponseJsonUnitTest {

    @Test
    void when_serializing_and_deserializing_then_the_actual_deserialized_should_equal_the_expected_before_serialization() throws
            JsonProcessingException {

        final Collection<IdentJson> expectedIdenter = new HashSet<>() {{
            add(new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, "10229837465"));
            add(new IdentJson(IdentGruppeV2.AKTOERID, "1029384756382"));
        }};

        final PostPdlHentIdenterResponseJson expectedPostPdlHentIdenterResponseJson =
                PostPdlHentIdenterResponseJson
                        .builder()
                        .data(
                                PostPdlHentIdenterDataResponseJson
                                        .builder()
                                        .hentIdenter(
                                                PostPdlHentIdenterHentIdenterResponseJson
                                                        .builder()
                                                        .identer(expectedIdenter)
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        final ObjectMapper objectMapper = new ObjectMapper();

        final String expectedPostHentIdenterResponseJsonAsString =
                objectMapper.writeValueAsString(expectedPostPdlHentIdenterResponseJson);

        final PostPdlHentIdenterResponseJson actualPostPdlHentIdenterResponseJson =
                objectMapper.readValue(expectedPostHentIdenterResponseJsonAsString, PostPdlHentIdenterResponseJson.class);

        Assertions.assertNotNull(actualPostPdlHentIdenterResponseJson.getData());
        Assertions.assertNotNull(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter());
        Assertions.assertNotNull(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter());
        Assertions.assertEquals(expectedIdenter.size(), actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().size());
        Assertions.assertTrue(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().containsAll(expectedIdenter));
    }

    @Test
    void when_deserializing_a_known_string_then_the_instance_should_contain_expected_values() throws JsonProcessingException {

        final ObjectMapper objectMapper = new ObjectMapper();

        final String expectedFolkeregisterIdent = "12345678901";
        final String expectedNpidIdent = "12345678901";
        final String expectedAktoeridIdent = "12345678901";
        final String expectedPostHentIdenterResponseJsonAsString = "{\n" +
                "    \"data\": {\n" +
                "        \"hentIdenter\": {\n" +
                "            \"identer\": [\n" +
                "                {\n" +
                "                    \"ident\": \"" + expectedFolkeregisterIdent + "\",\n" +
                "                    \"historisk\": false,\n" +
                "                    \"gruppe\": \"FOLKEREGISTERIDENT\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"ident\": \"" + expectedNpidIdent + "\",\n" +
                "                    \"historisk\": false,\n" +
                "                    \"gruppe\": \"NPID\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"ident\": \"" + expectedAktoeridIdent + "\",\n" +
                "                    \"historisk\": false,\n" +
                "                    \"gruppe\": \"AKTORID\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}";
        final PostPdlHentIdenterResponseJson actualPostPdlHentIdenterResponseJson =
                objectMapper.readValue(expectedPostHentIdenterResponseJsonAsString, PostPdlHentIdenterResponseJson.class);

        Assertions.assertNotNull(actualPostPdlHentIdenterResponseJson.getData());
        Assertions.assertNotNull(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter());
        Assertions.assertNotNull(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter());
        Assertions.assertEquals(3, actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().size());
        Assertions.assertTrue(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().contains(new IdentJson(IdentGruppeV2.AKTOERID, expectedAktoeridIdent)));
        Assertions.assertTrue(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().contains(new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, expectedFolkeregisterIdent)));
        Assertions.assertTrue(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().contains(new IdentJson(IdentGruppeV2.NPID, expectedNpidIdent)));
    }
}
