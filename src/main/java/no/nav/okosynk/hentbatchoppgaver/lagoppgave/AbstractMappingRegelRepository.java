package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.exceptions.UleseligMappingfilException;
import no.nav.okosynk.hentbatchoppgaver.model.Melding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMappingRegelRepository<T extends Melding> {

  private static final Logger logger = LoggerFactory
      .getLogger(AbstractMappingRegelRepository.class);

  private Properties getMappingRulesProperties() {
    return mappingRulesProperties;
  }

  private final Properties mappingRulesProperties = new Properties();

  private static final char NOKKEL_SKILLETEGN = ',';
  private static final int BEHANDLINGSTEMA_INDEKS = 0;
  private static final int BEHANDLINGSTYPE_INDEKS = 1;
  private static final int ANSVARLIG_ENHET_ID_INDEKS = 2;
  private final Constants.BATCH_TYPE batchType;

  protected AbstractMappingRegelRepository(final Constants.BATCH_TYPE batchType) {

    final String mappingRulesPropertiesFileName =
        AbstractMappingRegelRepository.getMappingRulesPropertiesFileName(batchType);
    try {
      loadMappingRulesProperties(mappingRulesPropertiesFileName);
    } catch (IOException e) {
      handterIOException(e);
    }
    this.batchType = batchType;
  }

  private static String getMappingRulesPropertiesFileName(final Constants.BATCH_TYPE batchType) {
    return batchType.getMappingRulesPropertiesFileName();
  }

  public Optional<MappingRegel> finnRegel(final T melding) {

    final String mappingRegelKey = createMappingRegelKey(melding);

    final Optional<String> behandlingstema =
        finnVerdiPaIndeks(mappingRegelKey, BEHANDLINGSTEMA_INDEKS);
    final Optional<String> behandlingstype =
        finnVerdiPaIndeks(mappingRegelKey, BEHANDLINGSTYPE_INDEKS);
    final Optional<String> ansvarligEnhetId =
        finnVerdiPaIndeks(mappingRegelKey, ANSVARLIG_ENHET_ID_INDEKS);

    final Optional<MappingRegel> optionalMappingRegel;
    if (
        behandlingstema.isPresent()
        &&
        behandlingstype.isPresent()
        &&
        ansvarligEnhetId.isPresent()) {

      optionalMappingRegel =
          Optional
              .of(
                new MappingRegel(
                    behandlingstema.get(),
                    behandlingstype.get(),
                    ansvarligEnhetId.get()
                )
              );
    } else {
      optionalMappingRegel = Optional.empty();
    }

    return optionalMappingRegel;
  }

  protected String settSammenNokkel(final String... nokkelFelter) {
    return String.join(Character.toString(NOKKEL_SKILLETEGN), nokkelFelter);
  }

  protected abstract String createMappingRegelKey(final T melding);

  void loadMappingRulesProperties(final String mappingRulesPropertiesFileName)
      throws IOException {

    final InputStream inputStream =
        this.getClass().getClassLoader().getResourceAsStream(mappingRulesPropertiesFileName);
    if (inputStream == null) {
      throw new IOException(
          "Kunne ikke lese fra mappingRulesProperties filen " + mappingRulesPropertiesFileName);
    }
    this.mappingRulesProperties.load(inputStream);
  }

  void handterIOException(IOException e) {

    logger.error(
          "Problemer oppsto under innlesning av mappingRulesProperties filen {}. "
        + "Applikasjonen er ute av stand til å håndtere batch som benytter seg av denne filen.",
        getMappingRulesPropertiesFileName(this.batchType), e);

    throw new UleseligMappingfilException(e);
  }

  private Optional<String> finnVerdiPaIndeks(final String sammensattNokkel, final int indeks) {

    final Optional<String> sammensatteVerdierOptional =
        Optional.ofNullable(getMappingRulesProperties().getProperty(sammensattNokkel));

    return sammensatteVerdierOptional
        .map(
            (final String sammensatteVerdier)
            ->
            sammensatteVerdier.split(Character.toString(NOKKEL_SKILLETEGN))[indeks]
        );
  }
}
