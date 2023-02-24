package no.nav.okosynk.synkroniserer.consumer.oppgave.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FinnOppgaveResponseJsonUnitTest extends AbstractOppgaveJsonUnitTest<FinnOppgaveResponseJson> {

    @Override
    protected FinnOppgaveResponseJson createEmptyInstance() {
        return new FinnOppgaveResponseJson();
    }

    @Test
    void when_sub_instance_is_compared_to_a_subclass_then_the_result_should_be_unequal() {

        final FinnOppgaveResponseJson finnOppgaveResponseJson1 = createEmptyInstance();
        final FinnOppgaveResponseJson finnOppgaveResponseJson2 = new FinnOppgaveResponseJson() {{
        }};

        assertNotEquals(finnOppgaveResponseJson1, finnOppgaveResponseJson2);
    }

    @Test
    void when_selected_sub_fields_differ_then_the_result_should_be_unequal() {

        final FinnOppgaveResponseJson finnOppgaveResponseJson1 = createEmptyInstance();
        fillWithAllHardCodedData(finnOppgaveResponseJson1);
        final FinnOppgaveResponseJson finnOppgaveResponseJson2 = createEmptyInstance();
        fillWithAllHardCodedData(finnOppgaveResponseJson2);
        assertEquals(finnOppgaveResponseJson1, finnOppgaveResponseJson2);
        final Collection<IdentJson> identer1 = new HashSet<>();
        {
            final IdentGruppeV2 identGruppeV2 = IdentGruppeV2.FOLKEREGISTERIDENT;
            final IdentJson identJson = new IdentJson(identGruppeV2, "xxx");
            identer1.add(identJson);
        }
        finnOppgaveResponseJson1.setIdenter(identer1);
        assertNotEquals(finnOppgaveResponseJson1, finnOppgaveResponseJson2);

        final Collection<IdentJson> identer2 = new HashSet<>();
        {
            final IdentGruppeV2 identGruppeV2 = IdentGruppeV2.FOLKEREGISTERIDENT;
            final IdentJson identJson = new IdentJson(identGruppeV2, "xxx");
            identer2.add(identJson);
        }
        finnOppgaveResponseJson2.setIdenter(identer2);
        assertEquals(finnOppgaveResponseJson1, finnOppgaveResponseJson2);
    }

    @Test
    void when_serializing_and_deserializing_an_empty_instance_then_the_source_and_final_target_should_equal() throws JsonProcessingException {

        final FinnOppgaveResponseJson expectedFinnOppgaveResponseJson = createEmptyInstance();
        final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final String serializedexpectedFinnOppgaveResponseJsonString = objectMapper.writeValueAsString(expectedFinnOppgaveResponseJson);
        final FinnOppgaveResponseJson actualDeserializedFinnOppgaveResponseJson = objectMapper.readValue(serializedexpectedFinnOppgaveResponseJsonString, FinnOppgaveResponseJson.class);
        assertEquals(expectedFinnOppgaveResponseJson, actualDeserializedFinnOppgaveResponseJson);
    }

    @Test
    void when_serializing_and_deserializing_an_instance_then_the_source_and_final_target_should_equal() throws JsonProcessingException {

        final FinnOppgaveResponseJson expectedFinnOppgaveResponseJson = createEmptyInstance();
        fillWithAllHardCodedData(expectedFinnOppgaveResponseJson);

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
        expectedFinnOppgaveResponseJson.setIdenter(identer);
        setFieldsAnnotetedWithJsonIgnoreToNull(expectedFinnOppgaveResponseJson);

        final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final String serializedExpectedFinnOppgaveResponseJsonString = objectMapper.writeValueAsString(expectedFinnOppgaveResponseJson);
        final FinnOppgaveResponseJson actualDeserializedFinnOppgaveResponseJson = objectMapper.readValue(serializedExpectedFinnOppgaveResponseJsonString, FinnOppgaveResponseJson.class);
        assertEquals(expectedFinnOppgaveResponseJson, actualDeserializedFinnOppgaveResponseJson);
    }

    @Test
    void when_comparing_two_instances_with_different_identer_then_they_should_not_equal() {

        final FinnOppgaveResponseJson finnOppgaveResponseJson1 = createEmptyInstance();
        {
            final Collection<IdentJson> identer = new HashSet<>();
            final IdentJson identJson = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, "xxx");
            identer.add(identJson);
            finnOppgaveResponseJson1.setIdenter(identer);
        }
        final FinnOppgaveResponseJson finnOppgaveResponseJson2 = createEmptyInstance();
        {
            final Collection<IdentJson> identer = new HashSet<>();
            final IdentJson identJson = new IdentJson(IdentGruppeV2.AKTOERID, "kjnbkjnbjkbjk");
            identer.add(identJson);
            finnOppgaveResponseJson2.setIdenter(identer);
        }
        assertNotEquals(finnOppgaveResponseJson1, finnOppgaveResponseJson2);
    }

    @Test
    void when_comparing_two_instances_with_equal_identer_in_different_order_then_they_should_equal() {

        final FinnOppgaveResponseJson finnOppgaveResponseJson1 = createEmptyInstance();
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, "xxx");
            identer.add(identJson1);
            final IdentJson identJson2 = new IdentJson(IdentGruppeV2.AKTOERID, "kjnbkjnbjkbjk");
            identer.add(identJson2);
            finnOppgaveResponseJson1.setIdenter(identer);
        }
        final FinnOppgaveResponseJson finnOppgaveResponseJson2 = createEmptyInstance();
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson2 = new IdentJson(IdentGruppeV2.AKTOERID, "kjnbkjnbjkbjk");
            identer.add(identJson2);
            final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, "xxx");
            identer.add(identJson1);
            finnOppgaveResponseJson2.setIdenter(identer);
        }
        assertEquals(finnOppgaveResponseJson1, finnOppgaveResponseJson2);
    }

    @Test
    void when_comparing_two_instances_with_identer_and_no_identer_resp_then_they_should_not_equal() {
        final FinnOppgaveResponseJson finnOppgaveResponseJson1 = createEmptyInstance();
        final Collection<IdentJson> identer = new ArrayList<>();
        final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, "xxx");
        identer.add(identJson1);
        finnOppgaveResponseJson1.setIdenter(identer);
        final FinnOppgaveResponseJson finnOppgaveResponseJson2 = createEmptyInstance();
        finnOppgaveResponseJson2.setIdenter(null);
        assertNotEquals(finnOppgaveResponseJson1, finnOppgaveResponseJson2);
    }

    @Test
    void when_the_response_entity_as_string_contains_an_id_then_it_should_be_deserialized_to_the_FinnOppgaveResponseJson() throws JsonProcessingException {

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
        final FinnOppgaveResponseJson finnOppgaveResponseJson =
                objectMapper.readValue(finnOppgaverResponseJsonString, FinnOppgaveResponseJson.class);
        assertEquals(expectedId, finnOppgaveResponseJson.getId());
    }
}