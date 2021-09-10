package no.nav.okosynk.consumer.oppgave.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import no.nav.okosynk.consumer.oppgave.json.PatchOppgaveRequestJson;
import no.nav.okosynk.consumer.oppgave.json.PatchOppgaverRequestJson;
import no.nav.okosynk.consumer.oppgave.json.PatchOppgaverRequestJson.Builder;
import org.junit.jupiter.api.Test;

public class PatchOppgaverRequestJsonUnitTest {

  @Test
  void when_fields_are_set_then_the_same_values_should_be_obtained_by_get() {

    final String expectedBehandlingstema = "BT";
    final String expectedBehandlingsttype = "BY";
    final String expectedEndretAvEnhetsnr = "ehn12";
    final Long expectedMappeId = 132435465768L;
    final String expectedTildeltEnhetsnr = "ETDN";
    final String expectedTilordnetRessurs = "TOR";
    final List<PatchOppgaveRequestJson> expectedOppgaver = new ArrayList<>();


    final PatchOppgaverRequestJson patchOppgaverRequestJson =
        new Builder()
            .withBehandlingstema(expectedBehandlingstema)
            .withBehandlingstype(expectedBehandlingsttype)
            .withEndretAvEnhetsnr(expectedEndretAvEnhetsnr)
            .withMappeId(expectedMappeId)
            .withTildeltEnhetsnr(expectedTildeltEnhetsnr)
            .withTilordnetRessurs(expectedTilordnetRessurs)
            .withOppgaver(expectedOppgaver)
            .build();

    assertEquals(expectedBehandlingstema, patchOppgaverRequestJson.getBehandlingstema());
    assertEquals(expectedBehandlingsttype, patchOppgaverRequestJson.getBehandlingstype());
    assertEquals(expectedEndretAvEnhetsnr, patchOppgaverRequestJson.getEndretAvEnhetsnr());
    assertEquals(expectedMappeId, patchOppgaverRequestJson.getMappeId());
    assertEquals(expectedTildeltEnhetsnr, patchOppgaverRequestJson.getTildeltEnhetsnr());
    assertEquals(expectedTilordnetRessurs, patchOppgaverRequestJson.getTilordnetRessurs());
    assertEquals(expectedOppgaver, patchOppgaverRequestJson.getOppgaver());
  }
}
