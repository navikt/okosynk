package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.model.MappingRegel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class Mappingregelverk {
    private Mappingregelverk() {
    }

    private static final char NOKKEL_SKILLETEGN = ',';
    private static final int BEHANDLINGSTEMA_INDEKS = 0;
    private static final int BEHANDLINGSTYPE_INDEKS = 1;
    private static final int ANSVARLIG_ENHET_ID_INDEKS = 2;
    private static final Properties mappingRules = new Properties();

    public static void init(final String mappingRulesPropertiesFileName) throws IOException {
        final InputStream inputStream = Mappingregelverk.class.getClassLoader().getResourceAsStream(mappingRulesPropertiesFileName);
        try {
            mappingRules.load(inputStream);
        } catch (NullPointerException e) {
            throw new IOException("Kunne ikke lese fra mappingRulesProperties filen " + mappingRulesPropertiesFileName);
        }
    }

    public static Optional<MappingRegel> finnRegel(final String mappingRegelKey) {
        final Optional<String> behandlingstema = finnVerdiPaIndeks(mappingRegelKey, BEHANDLINGSTEMA_INDEKS);
        final Optional<String> behandlingstype = finnVerdiPaIndeks(mappingRegelKey, BEHANDLINGSTYPE_INDEKS);
        final Optional<String> ansvarligEnhetId = finnVerdiPaIndeks(mappingRegelKey, ANSVARLIG_ENHET_ID_INDEKS);

        return (behandlingstema.isEmpty() || behandlingstype.isEmpty() || ansvarligEnhetId.isEmpty()) ? Optional.empty()
                : Optional.of(
                new MappingRegel(
                        behandlingstema.orElseThrow(),
                        behandlingstype.orElseThrow(),
                        ansvarligEnhetId.orElseThrow()
                ));
    }

    private static Optional<String> finnVerdiPaIndeks(final String sammensattNokkel, final int indeks) {
        final Optional<String> sammensatteVerdierOptional = Optional.ofNullable(mappingRules.getProperty(sammensattNokkel));
        return sammensatteVerdierOptional.map(v -> v.split(Character.toString(NOKKEL_SKILLETEGN))[indeks]);
    }
}
