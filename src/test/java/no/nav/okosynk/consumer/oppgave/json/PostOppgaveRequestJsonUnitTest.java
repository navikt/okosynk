package no.nav.okosynk.consumer.oppgave.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostOppgaveRequestJsonUnitTest extends AbstractOppgaveJsonUnitTest<PostOppgaveRequestJson> {

    @Override
    protected PostOppgaveRequestJson createEmptyInstance() {
        return new PostOppgaveRequestJson();
    }

    @Test
    void when_sub_instance_is_compared_to_a_subclass_then_the_result_should_be_unequal() {

        final PostOppgaveRequestJson postOppgaveRequestJson1 = new PostOppgaveRequestJson();
        final PostOppgaveRequestJson PostOppgaveRequestJson2 = new PostOppgaveRequestJson() {{
        }};

        assertFalse(postOppgaveRequestJson1.equals(PostOppgaveRequestJson2));
    }

    @Test
    void when_selected_sub_fields_differ_then_the_result_should_be_unequal() {

        final PostOppgaveRequestJson postOppgaveRequestJson1 = createEmptyInstance();
        fillWithAllHardCodedData(postOppgaveRequestJson1);
        final PostOppgaveRequestJson postOppgaveRequestJson2 = createEmptyInstance();
        fillWithAllHardCodedData(postOppgaveRequestJson2);
        assertTrue(postOppgaveRequestJson1.equals(postOppgaveRequestJson2));

        final String navPersonIdent = "10123145698";
        postOppgaveRequestJson1.setNavPersonIdent(navPersonIdent);
        assertFalse(postOppgaveRequestJson1.equals(postOppgaveRequestJson2));
        postOppgaveRequestJson2.setNavPersonIdent(navPersonIdent);
        assertTrue(postOppgaveRequestJson1.equals(postOppgaveRequestJson2));
    }

    @Test
    void when_serializing_and_deserializing_an_empty_instance_then_the_source_and_final_target_should_equal() throws JsonProcessingException {

        final PostOppgaveRequestJson postOppgaveRequestJson = new PostOppgaveRequestJson();
        fillWithAllHardCodedData(postOppgaveRequestJson);
        setFieldsAnnotetedWithJsonIgnoreToNull(postOppgaveRequestJson);
        final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final String serializedpostOppgaveRequestJsonString = objectMapper.writeValueAsString(postOppgaveRequestJson);
        final PostOppgaveRequestJson actualDeserializedFinnOppgaveResponseJson = objectMapper.readValue(serializedpostOppgaveRequestJsonString, PostOppgaveRequestJson.class);
        assertEquals(postOppgaveRequestJson, actualDeserializedFinnOppgaveResponseJson);
    }
}