package no.nav.okosynk.consumer.oppgave.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FinnOppgaverResponseJsonUnitTest {

    @Test
    void when_the_response_entity_as_string_contains_an_id_then_it_should_be_deserialized_to_the_FinnOppgaverResponseJson() throws JsonProcessingException {

        final String expectedId = "339032492";
        final String finnOppgaverResponseJsonString = "{\n" +
                "    \"antallTreffTotalt\": 3332,\n" +
                "    \"oppgaver\":\n" +
                "    [\n" +
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
                "        }\n" +
                "    ]\n" +
                "}";
        final ObjectMapper objectMapper =
                new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final FinnOppgaverResponseJson finnOppgaverResponseJson =
                objectMapper.readValue(finnOppgaverResponseJsonString, FinnOppgaverResponseJson.class);
        assertNotNull(finnOppgaverResponseJson.getFinnOppgaveResponseJsons());
        assertEquals(1, finnOppgaverResponseJson.getFinnOppgaveResponseJsons().size());
        assertEquals(expectedId, finnOppgaverResponseJson.getFinnOppgaveResponseJsons().stream().findAny().get().getId());
    }
}