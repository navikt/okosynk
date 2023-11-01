package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.exceptions.UleseligMappingfilException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class Mappingregelverk {

    private static final Logger logger = LoggerFactory
            .getLogger(Mappingregelverk.class);

    private Properties getMappingRulesProperties() {
        return mappingRulesProperties;
    }

    private final Properties mappingRulesProperties = new Properties();

    private static final char NOKKEL_SKILLETEGN = ',';
    private static final int BEHANDLINGSTEMA_INDEKS = 0;
    private static final int BEHANDLINGSTYPE_INDEKS = 1;
    private static final int ANSVARLIG_ENHET_ID_INDEKS = 2;
    private final String mappingRulesPropertiesFileName;

    public Mappingregelverk(final String mappingRulesPropertiesFileName) {
        this.mappingRulesPropertiesFileName = mappingRulesPropertiesFileName;
        try {
            loadMappingRulesProperties(mappingRulesPropertiesFileName);
        } catch (IOException e) {
            handterIOException(e);
        }
    }

    public Optional<MappingRegel> finnRegel(final String mappingRegelKey) {

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
                mappingRulesPropertiesFileName, e);

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
