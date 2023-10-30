package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Optional;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.exceptions.UleseligMappingfilException;
import no.nav.okosynk.hentbatchoppgaver.model.Melding;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMappingRegelRepositoryTest<MT extends Melding>  {

  private static final Logger enteringTestHeaderLogger =
      LoggerFactory.getLogger("EnteringTestHeader");

  protected AbstractMappingRegelRepository<MT> mappingRegelRepository;
  protected MT meldingSomSkalBliTilOppgave;
  protected MT meldingUtenMappingRegel;
  protected MT meldingWithoutBehandlingsTema;
  protected MT meldingWithoutAnsvarligEnhetId;
  protected String expectedBehandlingstema;
  protected String expectedBehandlingstype;
  protected String expectedAnsvarligenhet_id;

  @Test
  void when_melding_should_be_mapped_to_oppgave_then_correct_mappingregel_should_be_found() {

    enteringTestHeaderLogger.debug(null);

    final Optional<MappingRegel> mappingRegel =
        this.mappingRegelRepository.finnRegel(this.meldingSomSkalBliTilOppgave);

    assertTrue(mappingRegel.isPresent(), "Mapping mangler");
    assertAll("Mapping skal ha riktige verdier",
        () -> assertEquals(this.expectedBehandlingstema  , mappingRegel.get().behandlingstema),
        () -> assertEquals(this.expectedBehandlingstype  , mappingRegel.get().behandlingstype),
        () -> assertEquals(this.expectedAnsvarligenhet_id, mappingRegel.get().ansvarligEnhetId)
    );
  }

  @Test
  void when_melding_should_not_be_mapped_to_oppgave_then_no_mappingregel_should_be_found() {

    enteringTestHeaderLogger.debug(null);

    final Optional<MappingRegel> mappingRegel =
        mappingRegelRepository.finnRegel(meldingUtenMappingRegel);

    assertFalse(mappingRegel.isPresent(), "Mapping ble funnet for oppgave som ikke skal mappes");
  }

  @Test
  void when_mapping_properties_file_does_not_exist_then_an_io_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    final String fileName = "RubbishFileNameForNonExistingFile";
    final IOException actualIoException =
        assertThrows(
            IOException.class,
            () ->
                this
                    .mappingRegelRepository
                    .loadMappingRulesProperties(fileName)
        );

    final String expectedMessage = "Kunne ikke lese fra mappingRulesProperties filen " + fileName;

    assertEquals(expectedMessage, actualIoException.getMessage());
  }

  @Test
  void when_handling_an_io_exception_then_an_UleseligMappingfilException_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    final String expectedMessage = "Kxxxc ";
    final UleseligMappingfilException actualUleseligMappingfilException =
        assertThrows(
            UleseligMappingfilException.class,
            () ->
                this
                    .mappingRegelRepository
                    .handterIOException(new IOException(expectedMessage))
        );

    assertEquals(IOException.class, actualUleseligMappingfilException.getCause().getClass());
    assertEquals(expectedMessage, actualUleseligMappingfilException.getCause().getMessage());
  }

  @Test
  void when_melding_is_null_then_trying_to_find_its_MappingRegel_should_throw_a_NullPointerException() {

    enteringTestHeaderLogger.debug(null);

    assertThrows(NullPointerException.class, () -> this.mappingRegelRepository.finnRegel(null));
  }

  @Test
  void when_behandlinsTema_is_missing_then_no_MappingRegel_should_be_found() {

    enteringTestHeaderLogger.debug(null);

    final Optional<MappingRegel> actualOptionalMappingRegel =
      this.mappingRegelRepository.finnRegel(this.meldingWithoutBehandlingsTema);

    assertFalse(actualOptionalMappingRegel.isPresent());
  }

  @Test
  void when_expectedAnsvarligenhetId_is_missing_then_no_MappingRegel_should_be_found() {

    enteringTestHeaderLogger.debug(null);

    final Optional<MappingRegel> actualOptionalMappingRegel =
        this.mappingRegelRepository.finnRegel(this.meldingWithoutAnsvarligEnhetId);

    assertFalse(actualOptionalMappingRegel.isPresent());
  }
}
