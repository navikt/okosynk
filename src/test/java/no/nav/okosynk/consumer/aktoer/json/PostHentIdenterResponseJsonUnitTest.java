package no.nav.okosynk.consumer.aktoer.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.okosynk.consumer.oppgave.json.IdentGruppeV2;
import no.nav.okosynk.consumer.oppgave.json.IdentJson;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostHentIdenterResponseJsonUnitTest {

    @Test
    void when_serializing_and_deserializing_then_the_actual_deserialized_should_equal_the_expected_before_serialization() throws
            JsonProcessingException {

        final Collection<IdentJson> expectedIdenter = new HashSet<IdentJson>() {{
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

        assertNotNull(actualPostPdlHentIdenterResponseJson.getData());
        assertNotNull(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter());
        assertNotNull(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter());
        assertEquals(expectedIdenter.size(), actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().size());
        assertTrue(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().containsAll(expectedIdenter));
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

        assertNotNull(actualPostPdlHentIdenterResponseJson.getData());
        assertNotNull(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter());
        assertNotNull(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter());
        assertEquals(3, actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().size());
        assertTrue(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().contains(new IdentJson(IdentGruppeV2.AKTOERID, expectedAktoeridIdent)));
        assertTrue(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().contains(new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, expectedFolkeregisterIdent)));
        assertTrue(actualPostPdlHentIdenterResponseJson.getData().getHentIdenter().getIdenter().contains(new IdentJson(IdentGruppeV2.NPID, expectedNpidIdent)));
    }
}
