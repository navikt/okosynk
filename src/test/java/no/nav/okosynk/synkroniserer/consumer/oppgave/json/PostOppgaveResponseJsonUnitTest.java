package no.nav.okosynk.synkroniserer.consumer.oppgave.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

class PostOppgaveResponseJsonUnitTest extends AbstractOppgaveJsonUnitTest<PostOppgaveResponseJson> {

    @Override
    protected PostOppgaveResponseJson createEmptyInstance() {
        return new PostOppgaveResponseJson();
    }

    @Test
    void when_sub_instance_is_compared_to_a_subclass_then_the_result_should_be_unequal() {

        final PostOppgaveResponseJson PostOppgaveResponseJson1 = createEmptyInstance();
        final PostOppgaveResponseJson PostOppgaveResponseJson2 = new PostOppgaveResponseJson() {{
        }};

        Assertions.assertNotEquals(PostOppgaveResponseJson1, PostOppgaveResponseJson2);
    }

    @Test
    void when_selected_sub_fields_differ_then_the_result_should_be_unequal() {

        final PostOppgaveResponseJson PostOppgaveResponseJson1 = createEmptyInstance();
        fillWithAllHardCodedData(PostOppgaveResponseJson1);
        final PostOppgaveResponseJson PostOppgaveResponseJson2 = createEmptyInstance();
        fillWithAllHardCodedData(PostOppgaveResponseJson2);
        Assertions.assertEquals(PostOppgaveResponseJson1, PostOppgaveResponseJson2);
        final Collection<IdentJson> identer1 = new HashSet<>();
        {
            final IdentGruppeV2 identGruppeV2 = IdentGruppeV2.FOLKEREGISTERIDENT;
            final IdentJson identJson = new IdentJson(identGruppeV2, "xxx");
            identer1.add(identJson);
        }
        PostOppgaveResponseJson1.setIdenter(identer1);
        Assertions.assertNotEquals(PostOppgaveResponseJson1, PostOppgaveResponseJson2);

        final Collection<IdentJson> identer2 = new HashSet<>();
        {
            final IdentGruppeV2 identGruppeV2 = IdentGruppeV2.FOLKEREGISTERIDENT;
            final IdentJson identJson = new IdentJson(identGruppeV2, "xxx");
            identer2.add(identJson);
        }
        PostOppgaveResponseJson2.setIdenter(identer2);
        Assertions.assertEquals(PostOppgaveResponseJson1, PostOppgaveResponseJson2);
    }

    @Test
    void when_serializing_and_deserializing_an_empty_instance_then_the_source_and_final_target_should_equal() throws JsonProcessingException {

        final PostOppgaveResponseJson expectedPostOppgaveResponseJson = createEmptyInstance();
        final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final String serializedexpectedPostOppgaveResponseJsonString = objectMapper.writeValueAsString(expectedPostOppgaveResponseJson);
        final PostOppgaveResponseJson actualDeserializedPostOppgaveResponseJson = objectMapper.readValue(serializedexpectedPostOppgaveResponseJsonString, PostOppgaveResponseJson.class);
        Assertions.assertEquals(expectedPostOppgaveResponseJson, actualDeserializedPostOppgaveResponseJson);
    }

    @Test
    void when_serializing_and_deserializing_an_instance_then_the_source_and_final_target_should_equal() throws JsonProcessingException {

        final PostOppgaveResponseJson expectedPostOppgaveResponseJson = createEmptyInstance();
        fillWithAllHardCodedData(expectedPostOppgaveResponseJson);

        final Collection<IdentJson> identer = new HashSet<>();
        {
            final IdentGruppeV2 identGruppeV2 = IdentGruppeV2.FOLKEREGISTERIDENT;
            final IdentJson identJson = new IdentJson(identGruppeV2, "xxx");
            identer.add(identJson);
        }
        {
            final IdentGruppeV2 identGruppeV2 = IdentGruppeV2.AKTOERID;
            final IdentJson identJson = new IdentJson(identGruppeV2, "yyy");
            identer.add(identJson);
        }
        expectedPostOppgaveResponseJson.setIdenter(identer);
        setFieldsAnnotetedWithJsonIgnoreToNull(expectedPostOppgaveResponseJson);

        final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final String serializedExpectedPostOppgaveResponseJsonString = objectMapper.writeValueAsString(expectedPostOppgaveResponseJson);
        final PostOppgaveResponseJson actualDeserializedPostOppgaveResponseJson = objectMapper.readValue(serializedExpectedPostOppgaveResponseJsonString, PostOppgaveResponseJson.class);
        Assertions.assertEquals(expectedPostOppgaveResponseJson, actualDeserializedPostOppgaveResponseJson);
    }

    @Test
    void when_comparing_two_instances_with_different_identer_then_they_should_not_equal() {

        final PostOppgaveResponseJson PostOppgaveResponseJson1 = createEmptyInstance();
        {
            final Collection<IdentJson> identer = new HashSet<>();
            final IdentJson identJson = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, "xxx");
            identer.add(identJson);
            PostOppgaveResponseJson1.setIdenter(identer);
        }
        final PostOppgaveResponseJson PostOppgaveResponseJson2 = createEmptyInstance();
        {
            final Collection<IdentJson> identer = new HashSet<>();
            final IdentJson identJson = new IdentJson(IdentGruppeV2.AKTOERID, "kjnbkjnbjkbjk");
            identer.add(identJson);
            PostOppgaveResponseJson2.setIdenter(identer);
        }
        Assertions.assertNotEquals(PostOppgaveResponseJson1, PostOppgaveResponseJson2);
    }

    @Test
    void when_comparing_two_instances_with_equal_identer_in_different_order_then_they_should_equal() {

        final PostOppgaveResponseJson PostOppgaveResponseJson1 = createEmptyInstance();
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, "xxx");
            identer.add(identJson1);
            final IdentJson identJson2 = new IdentJson(IdentGruppeV2.AKTOERID, "kjnbkjnbjkbjk");
            identer.add(identJson2);
            PostOppgaveResponseJson1.setIdenter(identer);
        }
        final PostOppgaveResponseJson PostOppgaveResponseJson2 = createEmptyInstance();
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson2 = new IdentJson(IdentGruppeV2.AKTOERID, "kjnbkjnbjkbjk");
            identer.add(identJson2);
            final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, "xxx");
            identer.add(identJson1);
            PostOppgaveResponseJson2.setIdenter(identer);
        }
        Assertions.assertEquals(PostOppgaveResponseJson1, PostOppgaveResponseJson2);
    }

    @Test
    void when_comparing_two_instances_with_identer_and_no_identer_resp_then_they_should_not_equal() {
        final PostOppgaveResponseJson PostOppgaveResponseJson1 = createEmptyInstance();
        final Collection<IdentJson> identer = new ArrayList<>();
        final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, "xxx");
        identer.add(identJson1);
        PostOppgaveResponseJson1.setIdenter(identer);
        final PostOppgaveResponseJson PostOppgaveResponseJson2 = createEmptyInstance();
        PostOppgaveResponseJson2.setIdenter(null);
        Assertions.assertNotEquals(PostOppgaveResponseJson1, PostOppgaveResponseJson2);
    }

    @Test
    void when_the_response_entity_as_string_contains_an_id_then_it_should_be_deserialized_to_the_PostOppgaveResponseJson() throws JsonProcessingException {

        final String expectedId = "339032492";
        final String finnOppgaverResponseJsonString =
                "        {\n" +
                        "            \"id\": " + expectedId + ",\n" +
                        "            \"tildeltEnhetsnr\": \"4819\",\n" +
                        "            \"endretAvEnhetsnr\": \"9999\",\n" +
                        "            \"opprettetAvEnhetsnr\": \"9999\",\n" +
                        "            \"aktoerId\": \"1000023209046\",\n" +
                        "            \"beskrivelse\": \"AVVE;REGNSKAP;   345kr;   beregningsdato/id:19.07.21/429386336;   periode:01.05.21-31.05.21;   feilkonto: ;   statusdato:19.07.21;   ;   UtbTil:06105630008;   A126804\",\n" +
                        "            \"tema\": \"OKO\",\n" +
                        "            \"oppgavetype\": \"OKO_TBK\",\n" +
                        "            \"behandlingstype\": \"ae0216\",\n" +
                        "            \"versjon\": 36,\n" +
                        "            \"opprettetAv\": \"okosynkos\",\n" +
                        "            \"endretAv\": \"okosynkos\",\n" +
                        "            \"prioritet\": \"LAV\",\n" +
                        "            \"status\": \"OPPRETTET\",\n" +
                        "            \"metadata\":\n" +
                        "            {},\n" +
                        "            \"fristFerdigstillelse\": \"2021-07-21\",\n" +
                        "            \"aktivDato\": \"2021-07-20\",\n" +
                        "            \"opprettetTidspunkt\": \"2021-07-20T06:02:32.05+02:00\",\n" +
                        "            \"endretTidspunkt\": \"2021-09-07T06:02:06.742+02:00\"\n" +
                        "        }";
        final ObjectMapper objectMapper =
                new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final PostOppgaveResponseJson PostOppgaveResponseJson =
                objectMapper.readValue(finnOppgaverResponseJsonString, PostOppgaveResponseJson.class);
        Assertions.assertEquals(expectedId, PostOppgaveResponseJson.getId());
    }

    @Test
    void shouldBeAbleToCreateObjectFromJson() {
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Assertions.assertDoesNotThrow(() ->
                objectMapper.readValue("""
                        {
                            "id": "some-id",
                            "opprettetAv": "creator",
                            "versjon": 1,
                            "endretAvEnhetsnr": "unit-number",
                            "status": "FERDIGSTILT",
                            "endretTidspunkt": "2021-07-20T06:02:32.05+02:00",
                            "opprettetTidspunkt": "2021-07-20T06:02:32.05+02:00",
                            "ferdigstiltTidspunkt": "2021-07-20T06:02:32.05+02:00",
                            "identer": [
                                {
                                    "ident": "12345",
                                    "gruppe": "AKTORID"
                                },
                                {
                                    "ident": "67890",
                                    "gruppe": "AKTOERID"
                                }
                            ]
                        }
                        """, PostOppgaveResponseJson.class)
        );
    }
}
