package no.nav.okosynk.consumer.oppgave.json;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatchOppgaverResponseJsonUnitTest {

    @Test
    void when_setting_a_value_then_get_should_retrieve_an_equal_value() {
        final int expectedFeilet = 192;
        final int expectedSuccess = 37;
        final int expectedTotalt = 1021;
        final Map<String, List<Long>> expectedData = new HashMap<>();

        final PatchOppgaverResponseJson patchOppgaverResponseJson = new PatchOppgaverResponseJson();
        patchOppgaverResponseJson.setFeilet(expectedFeilet);
        patchOppgaverResponseJson.setSuksess(expectedSuccess);
        patchOppgaverResponseJson.setTotalt(expectedTotalt);
        patchOppgaverResponseJson.setData(expectedData);

        assertEquals(expectedFeilet, patchOppgaverResponseJson.getFeilet());
        assertEquals(expectedSuccess, patchOppgaverResponseJson.getSuksess());
        assertEquals(expectedTotalt, patchOppgaverResponseJson.getTotalt());
        assertEquals(expectedData, patchOppgaverResponseJson.getData());
    }

    @Test
    void when_setting_values_then_toString_should_contain_them_all() {
        final int expectedFeilet = 87;
        final int expectedSuccess = 97;
        final int expectedTotalt = 354;
        final Map<String, List<Long>> expectedData = new HashMap<>();

        final PatchOppgaverResponseJson patchOppgaverResponseJson = new PatchOppgaverResponseJson();
        patchOppgaverResponseJson.setFeilet(expectedFeilet);
        patchOppgaverResponseJson.setSuksess(expectedSuccess);
        patchOppgaverResponseJson.setTotalt(expectedTotalt);
        patchOppgaverResponseJson.setData(expectedData);
        final String toStringValue = patchOppgaverResponseJson.toString();
        assertEquals("PatchOppgaverResponseJson[feilet=87,suksess=97,totalt=354,data={}]", toStringValue);
    }
}
