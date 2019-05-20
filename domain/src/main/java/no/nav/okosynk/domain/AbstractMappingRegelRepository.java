package no.nav.okosynk.domain;

import lombok.AccessLevel;
import lombok.Getter;
import no.nav.okosynk.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public abstract class AbstractMappingRegelRepository<MELDINGSTYPE extends AbstractMelding > {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMappingRegelRepository.class);

    @Getter(AccessLevel.PROTECTED)
    private final Properties mappingRulesProperties = new Properties();

    private static final char NOKKEL_SKILLETEGN = ',';
    private static final int UNDERKATEGORI_INDEKS = 0;
    private static final int ANSVARLIG_ENHET_ID_INDEKS = 1;

    private final Constants.BATCH_TYPE batchType;

    protected AbstractMappingRegelRepository(final Constants.BATCH_TYPE batchType) {

        this.batchType = batchType;

        final String mappingRulesPropertiesFileName = getMappingRulesPropertiesFileName();
        try {
            loadMappingRulesProperties(mappingRulesPropertiesFileName);
        } catch (IOException e) {
            handterIOException(e);
        }
    }

    public Optional<MappingRegel> finnRegel(final MELDINGSTYPE melding) {

        final String mappingRegelKey = createMappingRegelKey(melding);

        final Optional<String> underkategoriKode = finnVerdiPaIndeks(mappingRegelKey, UNDERKATEGORI_INDEKS);
        final Optional<String> ansvarligEnhetId = finnVerdiPaIndeks(mappingRegelKey, ANSVARLIG_ENHET_ID_INDEKS);

        if (underkategoriKode.isPresent() && ansvarligEnhetId.isPresent()) {
            return Optional.of(new MappingRegel(underkategoriKode.get(), ansvarligEnhetId.get()));
        } else {
            return Optional.empty();
        }
    }

    protected void loadMappingRulesProperties(final String mappingRulesPropertiesFileName) throws IOException {

        final InputStream inputStream =
            this.getClass().getClassLoader().getResourceAsStream(mappingRulesPropertiesFileName);
        if (inputStream == null) {
            throw new IOException("Kunne ikke lese fra mappingRulesProperties filen " + mappingRulesPropertiesFileName);
        }
        mappingRulesProperties.load(inputStream);
    }

    protected String settSammenNokkel(final String... nokkelFelter) {

        return String.join(Character.toString(NOKKEL_SKILLETEGN), nokkelFelter);
    }

    protected Optional<String> finnVerdiPaIndeks(final String sammensattNokkel, final int indeks) {

        final Optional<String> sammensatteVerdierOptional =
            Optional.ofNullable(getMappingRulesProperties().getProperty(sammensattNokkel));

        return sammensatteVerdierOptional
            .map(
                (final String sammensatteVerdier)
                ->
                sammensatteVerdier.split(Character.toString(NOKKEL_SKILLETEGN))[indeks]);
    }

    protected abstract String createMappingRegelKey(final MELDINGSTYPE melding);

    private void handterIOException(IOException e) {
        logger.error(
              "Problemer oppsto under innlesning av mappingRulesProperties filen {}. "
            + "Applikasjonen er ute av stand til å håndtere batch som benytter seg av denne filen.",
            getMappingRulesPropertiesFileName(), e);

        throw new UleseligMappingfilException(e);
    }

    private String getMappingRulesPropertiesFileName() {

        final String mappingRulesPropertiesFileName = this.batchType.getMappingRulesPropertiesFileName();

        return mappingRulesPropertiesFileName;
    }
}
