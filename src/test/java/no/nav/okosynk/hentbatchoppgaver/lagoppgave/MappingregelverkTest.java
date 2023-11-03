package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.MappingRegel;
import no.nav.okosynk.hentbatchoppgaver.model.Melding;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class MappingregelverkTest<MT extends Melding> {

    protected MT meldingSomSkalBliTilOppgave;
    protected MT meldingUtenMappingRegel;
    protected MT meldingWithoutBehandlingsTema;
    protected MT meldingWithoutAnsvarligEnhetId;
    protected String expectedBehandlingstema;
    protected String expectedBehandlingstype;
    protected String expectedAnsvarligenhet_id;


    @Test
    void when_melding_should_be_mapped_to_oppgave_then_correct_mappingregel_should_be_found() {
        final Optional<MappingRegel> mappingRegel = Mappingregelverk.finnRegel(this.meldingSomSkalBliTilOppgave.regelnøkkel());

        assertTrue(mappingRegel.isPresent(), "Mapping mangler");
        assertAll("Mapping skal ha riktige verdier",
                () -> assertEquals(this.expectedBehandlingstema, mappingRegel.get().behandlingstema()),
                () -> assertEquals(this.expectedBehandlingstype, mappingRegel.get().behandlingstype()),
                () -> assertEquals(this.expectedAnsvarligenhet_id, mappingRegel.get().ansvarligEnhetId())
        );
    }

    @Test
    void when_melding_should_not_be_mapped_to_oppgave_then_no_mappingregel_should_be_found() {

        final Optional<MappingRegel> mappingRegel =
                Mappingregelverk.finnRegel(meldingUtenMappingRegel.regelnøkkel());

        assertFalse(mappingRegel.isPresent(), "Mapping ble funnet for oppgave som ikke skal mappes");
    }

    @Test
    void when_mapping_properties_file_does_not_exist_then_an_io_exception_should_be_thrown() {

        final String fileName = "RubbishFileNameForNonExistingFile";

        final IOException actualIoException = assertThrows(IOException.class, () -> Mappingregelverk.init(fileName));

        final String expectedMessage = "Kunne ikke lese fra mappingRulesProperties filen " + fileName;

        assertEquals(expectedMessage, actualIoException.getMessage());
    }

    @Test
    void when_melding_is_null_then_trying_to_find_its_MappingRegel_should_throw_a_NullPointerException() {

        assertThrows(NullPointerException.class, () -> Mappingregelverk.finnRegel(null));
    }

    @Test
    void when_behandlinsTema_is_missing_then_no_MappingRegel_should_be_found() {

        final Optional<MappingRegel> actualOptionalMappingRegel =
                Mappingregelverk.finnRegel(this.meldingWithoutBehandlingsTema.regelnøkkel());

        assertFalse(actualOptionalMappingRegel.isPresent());
    }

    @Test
    void when_expectedAnsvarligenhetId_is_missing_then_no_MappingRegel_should_be_found() {

        assertFalse(Mappingregelverk.finnRegel(
                this.meldingWithoutAnsvarligEnhetId.regelnøkkel()).isPresent());
    }
}
