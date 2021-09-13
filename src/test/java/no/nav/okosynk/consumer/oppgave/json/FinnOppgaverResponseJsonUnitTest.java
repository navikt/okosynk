package no.nav.okosynk.consumer.oppgave.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.stream.Collectors;

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

    @Test
    void when_the_response_entity_as_string_contains_aktoerId_and_identer_then_it_should_be_deserialized_to_the_FinnOppgaverResponseJson_as_expected() throws JsonProcessingException {

        final String expectedAktoerId1 = "1000004764602";
        final String expectedFolkeregisterIdent1 = "19027627938";
        final String expectedAktoerId2 = "1000031054701";
        final String expectedFolkeregisterIdent2 = "10058343531";
        final String finnOppgaverResponseJsonString = "{\n" +
                "    \"antallTreffTotalt\": 7801,\n" +
                "    \"oppgaver\":\n" +
                "    [\n" +
                "        {\n" +
                "            \"id\": 338263349,\n" +
                "            \"tildeltEnhetsnr\": \"4151\",\n" +
                "            \"endretAvEnhetsnr\": \"9999\",\n" +
                "            \"opprettetAvEnhetsnr\": \"9999\",\n" +
                "            \"aktoerId\": \"" + expectedAktoerId1 + "\",\n" +
                "            \"identer\":\n" +
                "            [\n" +
                "                {\n" +
                "                    \"ident\": \"" + expectedFolkeregisterIdent1 + "\",\n" +
                "                    \"gruppe\": \"FOLKEREGISTERIDENT\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"ident\": \"" + expectedAktoerId1 + "\",\n" +
                "                    \"gruppe\": \"AKTOERID\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"beskrivelse\": \"24;;   Manuell retur - fra bank;   postert/bilagsnummer:22.12.20/001655494;   1054kr;   statusdato:22.12.20;   UtbTil:19027627938;   ASL2960\",\n" +
                "            \"tema\": \"OKO\",\n" +
                "            \"behandlingstema\": \"ab0270\",\n" +
                "            \"oppgavetype\": \"OKO_UR\",\n" +
                "            \"versjon\": 3,\n" +
                "            \"opprettetAv\": \"okosynkur\",\n" +
                "            \"endretAv\": \"okosynkur\",\n" +
                "            \"prioritet\": \"LAV\",\n" +
                "            \"status\": \"OPPRETTET\",\n" +
                "            \"metadata\":\n" +
                "            {},\n" +
                "            \"fristFerdigstillelse\": \"2021-07-20\",\n" +
                "            \"aktivDato\": \"2021-07-17\",\n" +
                "            \"opprettetTidspunkt\": \"2021-07-17T06:04:25.819+02:00\",\n" +
                "            \"endretTidspunkt\": \"2021-09-13T09:45:44.848+02:00\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 338263350,\n" +
                "            \"tildeltEnhetsnr\": \"4151\",\n" +
                "            \"endretAvEnhetsnr\": \"9999\",\n" +
                "            \"opprettetAvEnhetsnr\": \"9999\",\n" +
                "            \"aktoerId\": \"" + expectedAktoerId2 + "\",\n" +
                "            \"identer\":\n" +
                "            [\n" +
                "                {\n" +
                "                    \"ident\": \"" + expectedFolkeregisterIdent2 + "\",\n" +
                "                    \"gruppe\": \"FOLKEREGISTERIDENT\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"ident\": \"" + expectedAktoerId2 + "\",\n" +
                "                    \"gruppe\": \"AKTOERID\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"beskrivelse\": \"09;;   MOTTATT FRA FORSYSTEM;   postert/bilagsnummer:06.04.21/660550819;   18712kr;   statusdato:07.04.21;   UtbTil:00888920412;   K2300462\",\n" +
                "            \"tema\": \"OKO\",\n" +
                "            \"oppgavetype\": \"OKO_UR\",\n" +
                "            \"behandlingstype\": \"ae0215\",\n" +
                "            \"versjon\": 3,\n" +
                "            \"opprettetAv\": \"okosynkur\",\n" +
                "            \"endretAv\": \"okosynkur\",\n" +
                "            \"prioritet\": \"LAV\",\n" +
                "            \"status\": \"OPPRETTET\",\n" +
                "            \"metadata\":\n" +
                "            {},\n" +
                "            \"fristFerdigstillelse\": \"2021-07-20\",\n" +
                "            \"aktivDato\": \"2021-07-17\",\n" +
                "            \"opprettetTidspunkt\": \"2021-07-17T06:04:25.916+02:00\",\n" +
                "            \"endretTidspunkt\": \"2021-09-13T09:44:55.589+02:00\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        final ObjectMapper objectMapper =
                new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final FinnOppgaverResponseJson finnOppgaverResponseJson =
                objectMapper.readValue(finnOppgaverResponseJsonString, FinnOppgaverResponseJson.class);
        assertNotNull(finnOppgaverResponseJson.getFinnOppgaveResponseJsons());
        assertEquals(2, finnOppgaverResponseJson.getFinnOppgaveResponseJsons().size());
        assertEquals(2,
                finnOppgaverResponseJson
                        .getFinnOppgaveResponseJsons()
                        .stream()
                        .filter(
                                finnOppgaveResponseJson ->
                                        finnOppgaveResponseJson.getAktoerId() != null &&
                                                finnOppgaveResponseJson.getIdenter().size() == 2
                        )
                        .collect(Collectors.toSet())
                        .size()
        );

        final Collection<Collection<IdentJson>> actualIdentJsonsCollection =
                finnOppgaverResponseJson
                        .getFinnOppgaveResponseJsons()
                        .stream()
                        .map(finnOppgaveResponseJson -> finnOppgaveResponseJson.getIdenter())
                        .collect(Collectors.toSet());
        assertEquals(2, actualIdentJsonsCollection
                .stream()
                .filter(
                        identJsons ->
                                identJsons
                                        .stream()
                                        .filter(
                                                identJson ->
                                                        (
                                                                IdentGruppeV2.FOLKEREGISTERIDENT.equals(identJson.getGruppe())
                                                                        &&
                                                                        expectedFolkeregisterIdent1.equals(identJson.getIdent())
                                                        )
                                                        ||
                                                        (
                                                                IdentGruppeV2.AKTOERID.equals(identJson.getGruppe())
                                                                        &&
                                                                        expectedAktoerId1.equals(identJson.getIdent())
                                                        )
                                                        ||
                                                        (
                                                                IdentGruppeV2.FOLKEREGISTERIDENT.equals(identJson.getGruppe())
                                                                        &&
                                                                        expectedFolkeregisterIdent2.equals(identJson.getIdent())
                                                        )
                                                        ||
                                                        (
                                                                IdentGruppeV2.AKTOERID.equals(identJson.getGruppe())
                                                                        &&
                                                                        expectedAktoerId2.equals(identJson.getIdent())
                                                        )
                                        )
                                        .collect(Collectors.toSet())
                                        .size() == 2
                )
                .collect(Collectors.toSet())
                .size())
        ;
    }
}