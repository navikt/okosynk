package no.nav.okosynk.consumer.oppgave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import no.nav.okosynk.consumer.oppgave.PatchOppgaverRequest.Builder;
import org.junit.jupiter.api.Test;

public class PatchOppgaverRequestTest {

  @Test
  void when_fields_are_set_then_the_same_values_should_be_obtained_by_get() {

    final String expectedBehandlingstema = "BT";
    final String expectedBehandlingsttype = "BY";
    final String expectedEndretAvEnhetsnr = "ehn12";
    final Long expectedMappeId = 132435465768L;
    final String expectedTildeltEnhetsnr = "ETDN";
    final String expectedTilordnetRessurs = "TOR";
    final List<PatchOppgave> expectedOppgaver = new ArrayList<>();


    final PatchOppgaverRequest patchOppgaverRequest =
        new Builder()
            .withBehandlingstema(expectedBehandlingstema)
            .withBehandlingstype(expectedBehandlingsttype)
            .withEndretAvEnhetsnr(expectedEndretAvEnhetsnr)
            .withMappeId(expectedMappeId)
            .withTildeltEnhetsnr(expectedTildeltEnhetsnr)
            .withTilordnetRessurs(expectedTilordnetRessurs)
            .withOppgaver(expectedOppgaver)
            .build();

    assertEquals(expectedBehandlingstema, patchOppgaverRequest.getBehandlingstema());
    assertEquals(expectedBehandlingsttype, patchOppgaverRequest.getBehandlingstype());
    assertEquals(expectedEndretAvEnhetsnr, patchOppgaverRequest.getEndretAvEnhetsnr());
    assertEquals(expectedMappeId, patchOppgaverRequest.getMappeId());
    assertEquals(expectedTildeltEnhetsnr, patchOppgaverRequest.getTildeltEnhetsnr());
    assertEquals(expectedTilordnetRessurs, patchOppgaverRequest.getTilordnetRessurs());
    assertEquals(expectedOppgaver, patchOppgaverRequest.getOppgaver());
  }
}
